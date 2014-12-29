<?php

require_once('../config/path.cfg');
require_once($product_config_path . 'host_conn.cfg');
require_once($product_config_path . 'db_schema.cfg');

header("Content-Type:text/html;charset=utf-8");
date_default_timezone_set('PRC');
function guid()
{
    mt_srand((double)microtime()*10000);
    $charid = strtolower(md5(uniqid(rand(), true)));
    $hyphen = chr(45);// "-"
    $uuid = //chr(123)// "{"
           //.
           substr($charid, 0, 8).$hyphen
           .substr($charid, 8, 4).$hyphen
           .substr($charid,12, 4).$hyphen
           .substr($charid,16, 4).$hyphen
           .substr($charid,20,12);
           //.chr(125);// "}"
    return $uuid;
}

function connectDB()
{
	$con = mysql_connect(DB_URL, DB_USER, DB_PWD);
	mysql_query("SET NAMES 'UTF8'");
	mysql_select_db(DB_NAME, $con);
	return $con;
}

function disconnectDB($con)
{
    mysql_close($con);
}

function calcLineDistance($longitude1, $latitude1, $longitude2, $latitude2)
{
    $theta = $longitude1 - $longitude2;
    $miles = (sin(deg2rad($latitude1)) * sin(deg2rad($latitude2))) + (cos(deg2rad($latitude1)) * cos(deg2rad($latitude2)) * cos(deg2rad($theta)));
    $miles = acos($miles);
    $miles = rad2deg($miles);
    $miles = $miles * 60 * 1.1515;
    $meters = $miles * 1609.344;
    return $meters;
}

function getAllCourierList()
{
  $sql = "select courier.id, courier.name, courier.phone, companyinfo.company_id, companyinfo.display_name as company_name, companyinfo.site_name, courier.reg_time
  from courier inner join (select site.name as site_name, site.phone as site_phone, site.id as site_id, company.id as company_id, company.display_name from company inner join site on company.id=site.company_id) as companyinfo on courier.site_id=companyinfo.site_id";
  $result = mysql_query($sql);
	if(mysql_num_rows($result) <= 0)
	{
		return NULL;
	}
	return queryResultToArray($result);
}

function getAllCourierListForDistanceCompare($long, $lati)
{
  $courierList = getAllCourierList();
  $result = array();
  foreach($courierList as $courier)
  {
    $courier['name'] = urlencode($courier['name']);
    $courier['company_name'] = urlencode($courier['company_name']);
    $courier['site_name'] = urlencode($courier['site_name']);
  
    $latestLocation = getLatestLocationByCourierId($courier[DB_COURIER_ID]);
    $arr = explode(",", $latestLocation);
    $latestLong = $arr[0];
    $latestLati = $arr[1];
    
    $courier['line_distance'] = calcLineDistance($latestLong, $latestLati, $long, $lati);
    $result[] = $courier;
  }
  unset($courierList);
  usort($result, "sortDistanceAsc");
  return $result;
}

function getAllSiteListForDistanceCompare($long, $lati)
{
  $siteList = getAllSiteList();
  $result = array();
  foreach($siteList as $site)
  {
    $site['name'] = urlencode($site['name']);
    $site['company_name'] = urlencode($site['company_name']);

    $siteLocation = $site[DB_SITE_LOCATION];
    $arr = explode(",", $siteLocation);
    $siteLong = $arr[0];
    $siteLati = $arr[1];

    $site['line_distance'] = calcLineDistance($siteLong, $siteLati, $long, $lati);
    $result[] = $site;
  }
  unset($siteList);
  usort($result, "sortDistanceAsc");
  return $result;
}

function getProperSiteList($location, $max_distance, $company_id)
{
    $filterDistance = $max_distance > 0;
    $filterCompany = $company_id > 0;
    
    $arr = explode(",", $location);
    $long = $arr[0];
    $lati = $arr[1];
    
    $allSiteList = getAllSiteListForDistanceCompare($long, $lati);
    $resultList = array();
    foreach($allSiteList as $site)
    {
        if ($filterCompany)
        {
            if ($site['company_id'] != $company_id)
            {
                continue;
            }
        }
        
        if ($filterDistance)
        {
            if ($site['line_distance'] > $max_distance)
            {
                break;
            }
        }
        
        //pass
        $resultList[] = $site;
    }
    
    unset($allSiteList);
    return $resultList;
}

function getProperCourierList($device_id, $user_name, $location, $max_distance, $street, $street_num, $company_id, $from_address, $to_address, $comments)
{
    $filterDistance = $max_distance > 0;
    $filterStreet = strlen($street) > 0;
    $filterCompany = $company_id > 0;
    //Search db
    $arr = explode(",", $location);
    $long = $arr[0];
    $lati = $arr[1];
    
    $allCourierList = getAllCourierListForDistanceCompare($long, $lati);
    $resultList = array();
    foreach($allCourierList as $courier)
    {
        if ($filterCompany)
        {
            if ($courier['company_id'] != $company_id)
            {
                continue;
            }
        }
        
        if ($filterDistance)
        {
            if ($courier['line_distance'] > $max_distance)
            {
                break;
            }
        }
        
        if ($filterStreet)
        {
            $addressList = getAddressListByCourierId($courier[DB_COURIER_ID]);
            $canReach = false;
            foreach ($addressList as $address)
            {
                if (strcmp($street, $address['street']) == 0)
                {
                    if ($street_num >= $address['min_number'] && $street_num <= $address['max_number'])
                    {
                        $canReach = true;
                        break;
                    }
                }
            }
            if (!$canReach)
            {
                continue;
            }
        }

        //pass
        $resultList[] = $courier;
    }
    unset($allCourierList);

    //Put into request log
    $logRecord[DB_REQUESTLOG_USERDEVICEID] = $device_id;
    $logRecord[DB_REQUESTLOG_USERNAME] = $user_name;
    $logRecord[DB_REQUESTLOG_FROMADDRESS] = $from_address;
    $logRecord[DB_REQUESTLOG_TOADDRESS] = $to_address;
    $logRecord[DB_REQUESTLOG_LOCATION] = $location;
    $logRecord[DB_REQUESTLOG_MAXDISTANCE] = $max_distance;
    $logRecord[DB_REQUESTLOG_STREET] = $street;
    $logRecord[DB_REQUESTLOG_STREETNUM] = $street_num;
    $logRecord[DB_REQUESTLOG_SPECIFIEDCOMPANYID] = $company_id;
    $logRecord[DB_REQUESTLOG_COMMENTS] = $comments;
    insertRecord(DB_TABLE_REQUESTLOG, $logRecord);
    
    $result['request_id'] = mysql_insert_id();
    $result['courier_list'] = $resultList;
    return $result;
}

function getMyHistoryList($device_id)
{
  $sql = "select * from " . DB_TABLE_REQUESTLOG . " where " . wrapCol(DB_REQUESTLOG_USERDEVICEID) . "=" . wrapStr($device_id);
  $result = mysql_query($sql);
  $arr = queryResultToArray($result);
  $encodedArr = array();
  foreach ($arr as $item)
  {
    $item[DB_REQUESTLOG_USERNAME] = urlencode($item[DB_REQUESTLOG_USERNAME]);
    $item[DB_REQUESTLOG_FROMADDRESS] = urlencode($item[DB_REQUESTLOG_FROMADDRESS]);
    $item[DB_REQUESTLOG_TOADDRESS] = urlencode($item[DB_REQUESTLOG_TOADDRESS]);
    $item[DB_REQUESTLOG_STREET] = urlencode($item[DB_REQUESTLOG_STREET]);
    $item[DB_REQUESTLOG_COMMENTS] = urlencode($item[DB_REQUESTLOG_COMMENTS]);
    $encodedArr[] = $item;
  }
  return $encodedArr;
}

function getSiteListByCompanyIdWithEncoding($company_id)
{
  $sql = "select * from " . DB_TABLE_SITE . " where " . wrapCol(DB_SITE_COMPANYID) . "=" . wrapStr($company_id);
  $result = mysql_query($sql);
  $arr = queryResultToArray($result);
  $encodedArr = array();
  foreach ($arr as $item)
  {
    $item[DB_SITE_NAME] = urlencode($item[DB_SITE_NAME]);
    $encodedArr[] = $item;
  }
  return $encodedArr;
}

function getSiteById($id)
{
    //$sql = "select * from site inner join company on site.commpany_id=company.id where " . wrapCol(DB_SITE_ID) . "=" . wrapStr($id);
    $sql = "select site.id, site.name, site.phone, site.location, site.company_id as company_id, site.location, company.internal_name as company_internalname from site inner join company on site.company_id=company.id where site.id=" . wrapStr($id);
    //echo $sql;
	$result = mysql_query($sql);
	if(mysql_num_rows($result) <= 0)
	{
		return NULL;
	}
	return queryResultToStruct($result);
}

function getCourierById($id)
{
	$sql = "select * from " . DB_TABLE_COURIER . " where " . wrapCol(DB_COURIER_ID) . "=" . wrapStr($id);
	$result = mysql_query($sql);
	if(mysql_num_rows($result) <= 0)
	{
		return NULL;
	}
	return queryResultToStruct($result);
}

function getLatestLocationByCourierId($id)
{
	$sql = "select * from " . DB_TABLE_COURIER_LOCATION . " where " . wrapCol(DB_CL_COURIERID) . "=" . wrapStr($id) . " order by time desc limit 1";
	$result = mysql_query($sql);
	$location = NULL;
	while($row = mysql_fetch_array($result))
	{
		$location = $row[DB_CL_LOCATION];
		break;
	}
	return $location;
}

function getCompanyInternalNameById($id)
{
	$sql = "select * from " . DB_TABLE_COMPANY . " where " . wrapCol(DB_COMPANY_ID) . "=" . wrapStr($id);
	$result = mysql_query($sql);
	$internalName = NULL;
	while($row = mysql_fetch_array($result))
	{
		$internalName = $row[DB_COMPANY_INTERNALNAME];
		break;
	}
	return $internalName;
}

function getAllCompanyListWithEncoding()
{
  $sql = "select * from " . DB_TABLE_COMPANY . " where 1=1";
  $result = mysql_query($sql);
  $arr = queryResultToArray($result);
  $encodedArr = array();
  foreach ($arr as $item)
  {
    $displayName = $item['display_name'];
    unset($item['display_name']);
    $item['display_name'] = urlencode($displayName);
    $encodedArr[] = $item;
  }
  usort($encodedArr, "sortCompany");
  return $encodedArr;
}

function getAllStreet()
{
  $sql = "select street, count(*) as courier_count from courier_street_map group by street";
  $result = mysql_query($sql);
  return queryResultToArray($result);
}

function getAllRequestList()
{
  $sql = "select * from " . DB_TABLE_REQUESTLOG . " where 1=1 order by id asc";
  $result = mysql_query($sql);
  return queryResultToArray($result);
}

function getAllCompanyList()
{
  $sql = "select * from " . DB_TABLE_COMPANY . " where 1=1";
  $result = mysql_query($sql);
  $arr = queryResultToArray($result);
  usort($arr, "sortCompany");
  return $arr;
}

function sortCompany($a, $b)
{
	$displayNameA = $a[DB_COMPANY_INTERNALNAME];
	$displayNameB = $b[DB_COMPANY_INTERNALNAME];
	return strcmp($displayNameA, $displayNameB);
}

function sortDistanceAsc($a, $b)
{
	$dist1 = $a['line_distance'];
	$dist2 = $b['line_distance'];
	if ($dist1 > $dist2)
  {
    return 1;
  }
  else if ($dist1 < $dist2)
  {
    return -1;
  }
  else
  {
    return 0;
  }
}

function sortSiteByName($a, $b)
{
	$displayNameA = $a[DB_SITE_NAME];
	$displayNameB = $b[DB_SITE_NAME];
	return strcmp($displayNameA, $displayNameB);
}

function getCompanySelectHtml($width, $callbackFunctionName, $selectedId='___')
{
	$html = '<select id="company" style="width:' . $width . '" onchange="' . $callbackFunctionName . '">';
    $html .= '<option value="-1">请选择快递公司</option>';
	$companyList = getAllCompanyList();
	foreach($companyList as $company)
	{
		$id = $company[DB_COMPANY_ID];
		$selectedAttribute = "";
		if (strcmp($id, $selectedId) == 0)
		{
			$selectedAttribute = ' selected="selected"';
		}
        //$html .= '<option tag="' . $company[DB_COMPANY_ID] . '" value="' . $internalName . '"' . $selectedAttribute . '>' . $company[DB_COMPANY_DISPLAYNAME] . '</option>';
		$html .= '<option value="' . $id . '"' . $selectedAttribute . '>' . $company[DB_COMPANY_DISPLAYNAME] . '</option>';
	}
	$html .= '</select>';
	return $html;
}

function getSiteListByCompanyId($id)
{
  $sql = "select * from " . DB_TABLE_SITE . " where " . wrapCol(DB_SITE_COMPANYID) . "=" . wrapStr($id);
  $result = mysql_query($sql);
  $arr = queryResultToArray($result);
  usort($arr, "sortSiteByName");
  return $arr;
}

function getSiteSelectHtml($companyid, $width, $selectedSiteId=-1)
{
	$html = '<select id="site" style="width:' . $width . '">';
    $html .= '<option value="-1">请选择站点</option>';
	$siteList = getSiteListByCompanyId($companyid);
	foreach($siteList as $site)
	{
        $siteId = $site[DB_SITE_ID];
		//$internal_name = $company[DB_COMPANY_INTERNALNAME];
		$selectedAttribute = "";
		if (strcmp($siteId, $selectedSiteId) == 0)
		{
			$selectedAttribute = ' selected="selected"';
		}
		$html .= '<option value="' . $siteId . '"' . $selectedAttribute . '>' . $site[DB_SITE_NAME] . '</option>';
	}
	$html .= '</select>';
	return $html;
}

function getAddressListByCourierId($cid)
{
    $sql = "select * from " . DB_TABLE_COURIER_STREET_MAP . " where " . wrapCol(DB_CSM_COURIERID) . "=" . wrapStr($cid);
    $result = mysql_query($sql);
    $arr = queryResultToArray($result);
    return $arr;
}

function getAllSiteList()
{
    //$sql = 'select * from site where 1=1 order by company_id';
    $sql = 'select site.id, site.company_id, company.display_name as company_name, site.name, site.phone, site.location from site inner join company on site.company_id=company.id where 1=1 order by company_id';
    $result = mysql_query($sql);
    return queryResultToArray($result);
}

function getCompanySiteMap()
{
    $allSiteList = getAllSiteList();
    $map = array();
    foreach($allSiteList as $site)
    {
        $map[$site[DB_SITE_COMPANYID]][] = $site;
    }
    return $map;
}

function addOrUpdateSite($data)
{
    $companyId = $data[DB_SITE_COMPANYID];
    $name = $data[DB_SITE_NAME];
    if (hasRecordOfTwoConds(DB_TABLE_SITE, DB_SITE_COMPANYID, $companyId, DB_SITE_NAME, $name))
	{
	    $conds[DB_SITE_COMPANYID] = $companyId;
        $conds[DB_SITE_NAME] = $name;

	    $record[DB_SITE_PHONE] = $data[DB_SITE_PHONE];
	    $record[DB_SITE_LOCATION] = $data[DB_SITE_LOCATION];

	    updateRecord(DB_TABLE_SITE, $conds, $record);
        echo "update";
	}
	else
	{
		insertRecord(DB_TABLE_SITE, $data);
        echo "add";
    }
}

function addOrUpdateCourier($courierRecord, $location, $addressList)
{
    $id = $courierRecord[DB_COURIER_ID];

    $record[DB_COURIER_NAME] = $courierRecord[DB_COURIER_NAME];
    $record[DB_COURIER_PHONE] = $courierRecord[DB_COURIER_PHONE];
    $record[DB_COURIER_COMPANYID] = $courierRecord[DB_COURIER_COMPANYID];
    $record[DB_COURIER_SITEID] = $courierRecord[DB_COURIER_SITEID];

    if (hasRecordOf(DB_TABLE_COURIER, DB_COURIER_ID, $id))
    {
        $conds[DB_COURIER_ID] = $id;
        updateRecord(DB_TABLE_COURIER, $conds, $record);
        echo "update";
    }
    else
    {
        insertRecord(DB_TABLE_COURIER, $record);
        $id = mysql_insert_id();
        echo "add_" . $id;
    }
    
    updateCourierLocation($id, $location);
    updateAddressList($id, $addressList);
}

function updateCourierLocation($cid, $location)
{
    $record[DB_CL_COURIERID] = $cid;
    $record[DB_CL_LOCATION] = $location;
    
    insertRecord(DB_TABLE_COURIER_LOCATION, $record);
}

function updateAddressList($cid, $addressList)
{
    $sql = "delete from " . DB_TABLE_COURIER_STREET_MAP . " where " . wrapCol(DB_CSM_COURIERID) . "=" . wrapStr($cid);
	mysql_query($sql);

    $arr = explode("|", $addressList);
    $count = count($arr);
    for ($i = 1; $i < $count - 2; $i += 3)
    {
        $street = $arr[$i];
        $minnum = $arr[$i + 1];
        $maxnum = $arr[$i + 2];
        $record[DB_CSM_COURIERID] = $cid;
        $record[DB_CSM_STREET] = $street;
        $record[DB_CSM_MINNUMBER] = $minnum;
        $record[DB_CSM_MAXNUMBER] = $maxnum;
        insertRecord(DB_TABLE_COURIER_STREET_MAP, $record);
    }
}
// ignore below

function queryResultToArray($result)
{
	$tempArray = array();
	if(mysql_num_rows($result) > 0)
	{
		while($row = mysql_fetch_assoc($result))
		{
			unset($item);
			foreach ($row as $key=>$val)
			{
				$item[$key] = $val;
			}
			$tempArray[] = $item;
		}
	}
	return $tempArray;
}

function queryResultToStruct($result)
{
	$tempObj = NULL;
	if(mysql_num_rows($result) > 0)
	{
		$row = mysql_fetch_assoc($result);
		foreach ($row as $key=>$val)
		{
			$tempObj[$key] = $val;
		}
	}
	return $tempObj;
}

function encodeQueryResult2Json($arr)
{
	$encodedStr = "[]";
	if(count($arr) > 0)
	{
		$encodedStr = urldecode(json_encode($arr));
	}
	return $encodedStr;
}

function wrapStr($str, $avoidSpecialChar=true)
{
	if ($avoidSpecialChar)
	{
		return "'" . htmlspecialchars($str, ENT_QUOTES) . "'";
	}
	else
	{
		return "'" . $str . "'";
	}
    //return "'" . $str . "'";
}

function wrapCol($str)
{
    return "`" . $str . "`";
}

function hasRecordOf($tableName, $key, $val)
{
	$sql = "select * from " . $tableName . " where " . wrapCol($key) . "=" . wrapStr($val);
	$result = mysql_query($sql);
	if(mysql_num_rows($result) > 0)
	{
		return true;
	}
	return false;
}

//improve later, should be able to process multi key-val conditions.
function hasRecordOfTwoConds($tableName, $key1, $val1, $key2, $val2)
{
	$sql = "select * from " . $tableName . " where " . wrapCol($key1) . "=" . wrapStr($val1) . " and " . wrapCol($key2) . "=" . wrapStr($val2);
	$result = mysql_query($sql);
	if(mysql_num_rows($result) > 0)
	{
		return true;
	}
	return false;
}

function insertRecord($tableName, $record, $avoidSpecialChar=true)
{
    $keys = "";
    $vals = "";
    foreach($record as $key=>$val)
    {
        if(strlen($keys) == 0)
        {
            $keys = wrapCol($key);
            $vals = wrapStr($val, $avoidSpecialChar);
        }
        else
        {
            $keys .= "," . wrapCol($key);
            $vals .= "," . wrapStr($val, $avoidSpecialChar);
        }
    }
    $sql = "insert into " . $tableName . "(" . $keys . ") values(" . $vals . ")";
    //echo $sql;//exit();
    $result = mysql_query($sql);
    return $result;
}

function updateRecord($tableName, $conditions, $newValues)
{
    $condition = "";
    $newValue = "";
    foreach($conditions as $key=>$val)
    {
        if(strlen($condition) == 0)
        {
            $condition = $key . "=" . wrapStr($val);
        }
        else
        {
            $condition .= " and " . $key . "=" . wrapStr($val);
        }
    }
    foreach($newValues as $key=>$val)
    {
        if(strlen($newValue) == 0)
        {
            $newValue = $key . "=" . wrapStr($val);
        }
        else
        {
            $newValue .= ", " . $key . "=" . wrapStr($val);
        }
    }
    $sql = "update " . $tableName . " set " . $newValue . " where " . $condition;
    //echo $sql;//exit();
    $result = mysql_query($sql);
    return $result;
}

?>