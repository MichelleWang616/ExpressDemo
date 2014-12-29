<?php

require_once('../core/util.php');

if (!isset($_GET['op']))
{
	$result['result'] = 'error';
    $result['message'] = 'Wrong arg(s).';
    echo json_encode($result);
	exit(0);
}

$op = $_GET['op'];
if (empty($op))
{
	$result['result'] = 'error';
    $result['message'] = 'Wrong arg(s).';
    echo json_encode($result);
	exit(0);
}

switch ($op)
{
	case "GetAllCompanyList":
		$conn = connectDB();
		$company_list = getAllCompanyListWithEncoding();
		disconnectDB($conn);
		echo encodeQueryResult2Json($company_list);
		break;
    case "GetSiteListByCompanyId":
        $company_id = $_POST['company_id'];
        
        $conn = connectDB();
		$courier_list = getSiteListByCompanyIdWithEncoding($company_id);
		disconnectDB($conn);
        
        echo encodeQueryResult2Json($courier_list);
        break;
    case "GetMyHistoryList":
        $device_id = $_POST['device_id'];

        $conn = connectDB();
		    $history_list = GetMyHistoryList($device_id);
		    disconnectDB($conn);

        echo encodeQueryResult2Json($history_list);
        break;
    case "GetProperSiteList":
        $company_id = $_POST['company_id'];
        $location = $_POST['location'];
        $max_distance = $_POST['max_distance'];
        
        $conn = connectDB();
		$site_list = getProperSiteList($location, $max_distance, $company_id);
		disconnectDB($conn);
        
        echo encodeQueryResult2Json($site_list);
        break;
    case "GetProperCourierList":
        //var_dump($_POST);
        $device_id = $_POST['device_id'];
        $user_name = $_POST['user_name'];
        $from_address = $_POST['from_address'];
        $to_address = $_POST['to_address'];
        
        $location = $_POST['location'];
        $max_distance = $_POST['max_distance'];
        
        $street = $_POST['street'];
        $street_num = $_POST['street_number'];
        
        $company_id = $_POST['company_id'];
        
        $comments = $_POST['comments'];
        
        $conn = connectDB();
		$courier_list = getProperCourierList($device_id, $user_name, $location, $max_distance, $street, $street_num, $company_id, $from_address, $to_address, $comments);
		disconnectDB($conn);
        
        echo encodeQueryResult2Json($courier_list);
        break;
    case "UpdateSiteInfo":
        $company_id = $_POST['company_id'];
        $site_name = $_POST['site_name'];
        $site_phone = $_POST['site_phone'];
        $site_location = $_POST['site_location'];
        
        $record[DB_SITE_COMPANYID] = $company_id;
        $record[DB_SITE_NAME] = $site_name;
        $record[DB_SITE_PHONE] = $site_phone;
        $record[DB_SITE_LOCATION] = $site_location;
        
		$conn = connectDB();
        addOrUpdateSite($record);
		disconnectDB($conn);
		break;
    case "UpdateCourierInfo":
        $id = $_POST['id'];
        $name = $_POST['name'];
        $phone = $_POST['phone'];
        $company_id = $_POST['company_id'];
        $siteId = $_POST['site_id'];
        $location = $_POST['location'];
        $address_list = $_POST['address_list'];
        
        $courierRecord[DB_COURIER_ID] = $id;
        $courierRecord[DB_COURIER_NAME] = $name;
        $courierRecord[DB_COURIER_PHONE] = $phone;
        $courierRecord[DB_COURIER_COMPANYID] = $company_id;
        $courierRecord[DB_COURIER_SITEID] = $siteId;
        
        $conn = connectDB();
        addOrUpdateCourier($courierRecord, $location, $address_list);
		disconnectDB($conn);
		break;
    case "UpdateRequestInfo":
        $id = $_POST['id'];
        $status = $_POST['status'];
        $courier_id = $_POST['courier_id'];
        
        $conds[DB_REQUESTLOG_ID] = $id;
        
        $values[DB_REQUESTLOG_STATUS] = $status;
        $values[DB_REQUESTLOG_COURIERID] = $courier_id;
        
        $conn = connectDB();
        updateRecord(DB_TABLE_REQUESTLOG, $conds, $values);
        disconnectDB($conn);
        $result['result'] = 'ok';
        echo json_encode($result);
        break;
	default:
        $result['result'] = 'ok';
        $result['message'] = $op . " is not supported yet, if you want this urgently, please contact Administrator.";
        echo json_encode($result);
		break;
}

?>