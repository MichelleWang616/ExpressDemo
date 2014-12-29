<?php
require_once('../core/util.php');

$courierId = NULL;
$isAdding = true;
if (isset($_GET['id']))
{
    $courierId = $_GET['id'];
}

$courierName = "";
$location = "121.499617,31.23986";
//$curCompanyInternalName = "unknown";
$curCompanyId = -1;
$curSiteId = -1;
$phone = "";
$addressList = array();

$conn = connectDB();
$companySiteMap = getCompanySiteMap();
if (!empty($courierId))
{
    $isAdding = false;
    $courierInfo = getCourierById($courierId);
    $courierName = $courierInfo[DB_COURIER_NAME];
    $phone = $courierInfo[DB_COURIER_PHONE];
    $curCompanyId = $courierInfo[DB_COURIER_COMPANYID];
    //$curCompanyInternalName = getCompanyInternalNameById($curCompanyId);
    $curSiteId = $courierInfo[DB_COURIER_SITEID];
    $location = getLatestLocationByCourierId($courierId);
    $addressList = getAddressListByCourierId($courierId);
}
$companySelHtml = getCompanySelectHtml("126px", "onCompanyChange()", $curCompanyId);
$siteSelHtml = getSiteSelectHtml($curCompanyId, '126px', $curSiteId);
disconnectDB($conn);
//echo $location;
?>

<!DOCTYPE HTML>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>快递员信息</title>
<style type="text/css">
html{height:100%}
body{height:100%;margin:0px;padding:0px}
#container{height:100%}
</style>
<script type="text/javascript"
src="http://webapi.amap.com/maps?v=1.3&key=yourkey">
</script>
<script type="text/javascript">
    var isAdding = <?php echo $isAdding ? "true" : "false"; ?>;
    var companySiteMap = null;
    var courierId = <?php echo $isAdding ? "-1" : $courierId; ?>;
    function initCompanySiteMapData()
    {
        companySiteMap = {
            <?php
                foreach($companySiteMap as $companyId=>$siteList)
                {
                    echo $companyId . ":[";
                        foreach($siteList as $site)
                        {
                            echo "{site_id:'" . $site[DB_SITE_ID] . "', site_name:'" . $site[DB_SITE_NAME] . "'},";
                        }
                    echo "],";
                }
            ?>
        };
    }
    function initAddressListData()
    {
        var addressList = new Array();
        <?php
        $index = 0;
        foreach($addressList as $address)
        {
            {
                echo "var street={street:'" . $address["street"] . "', minnum:" . $address["min_number"] . ", maxnum:" . $address["max_number"] . "};";
                echo "addressList[" . $index . "] = street;";
            }
            ++$index;
        }
        ?>
        updateAddressListHtml(addressList);
    }
    
    function updateAddressListHtml(addressList)
    {
        var html = "";
        for(var i = 0; i < addressList.length; ++i)
        {
            var address = addressList[i];
            html += '<div id="address"><input type="text" name="street" value="' + address.street + '" style="width:80px">&nbsp;';
            html += '<input type="text" name="minnum" value="' + address.minnum + '" style="width:40px">(号/弄)-';
            html += '<input type="text" name="maxnum" value="' + address.maxnum + '" style="width:40px">(号/弄)<button type="button" onclick="deleteStreet(' + i + ')">删除</button></div>';
        }
        document.getElementById("address_list").innerHTML = html;
    }
    
    function initialize() {
        var position = new AMap.LngLat(<?php echo $location; ?>);
        var mapObj = new AMap.Map("map_container");
        mapObj.setCenter(position);
        mapObj.setZoom(16);

		expressMarker = new AMap.Marker({
                    map: mapObj,
                    position: position,
                    icon: "../resource/icon/0.png",
                    offset: new AMap.Pixel(-10, -34)
                });

        var listener = AMap.event.addListener(mapObj, "click", function (e) {
            lnglat = e.lnglat;
            if (null == expressMarker) {
                expressMarker = new AMap.Marker({
                    map: mapObj,
                    position: e.lnglat,
                    icon: "../resource/icon/0.png",
                    offset: new AMap.Pixel(-10, -34)
                });
            }
            else {
                expressMarker.setPosition(e.lnglat);
            }
            document.getElementById("cur_location").innerHTML = lnglat.toString();
            //Center current pointer
            //mapObj.setCenter(lnglat);
        });

		//document.getElementById("company_logo").innerHTML = "<img src='icon/" + <?php echo "'" . $curCompanyId . "'"; ?> + ".jpg'  height='24px'/>";
        initAddressListData();
        initCompanySiteMapData();
    }
    var expressMarker = null;
    var lnglat;
    function onCompanyChange()
    {
        var company_id = document.getElementById("company").value;
        document.getElementById("company_logo").innerHTML = "<img src='../resource/icon/company/" + company_id + ".jpg'  height='24px'/>";
        //companySiteMap
        var siteOptionListStr = "<option value='-1'>请选择站点</option>";
        var siteList = companySiteMap[company_id];
        if (null != siteList)
        {
            //alert(siteList.length);
            for (var i = 0; i < siteList.length; ++i)
            {
                var site = siteList[i];
                siteOptionListStr += "<option value='" + site["site_id"] + "'>" + site["site_name"] + "</option>";
            }
        }
        //alert(siteOptionListStr);
        document.getElementById("site").innerHTML = siteOptionListStr;
    }

    function extractAddressList(emptyCheck)
    {
        var addressList = new Array();
        
        var addressUIList = document.getElementById("address_list");
        var children = addressUIList.children;
        for(var i = 0; i < children.length; ++i)
        {
            var address = children[i].childNodes;
            var street = address[0].value;
            if (emptyCheck && street.length <=0)
            {
                alert("地址信息不完整!");
                address[0].focus();
                return null;
            }
            var minnum = address[2].value;
            if (emptyCheck && minnum.length <=0)
            {
                alert("地址信息不完整!");
                address[2].focus();
                return null;
            }
            var maxnum = address[4].value;
            if (emptyCheck && maxnum.length <=0)
            {
                alert("地址信息不完整!");
                address[4].focus();
                return null;
            }
            //alert(street + ":" + minnum + "-" + maxnum);
            var addressItem = {'street': street, 'minnum': minnum, 'maxnum': maxnum};
            addressList[i] = addressItem;
        }
        return addressList;
    }
    
    function deleteStreet(index)
    {
        var addressList = extractAddressList(false);
        addressList.splice(index, 1);
        updateAddressListHtml(addressList);
    }
    
    function addStreet()
    {
        var addressList = extractAddressList(false);
        addressList[addressList.length] = {'street': '', 'minnum': '', 'maxnum': ''};
        updateAddressListHtml(addressList);
    }
    
	function updateInfo()
	{
        var name = document.getElementById("name").value;
        if (name.length <= 0)
        {
            alert("姓名不能为空!");
            document.getElementById("name").focus();
            return;
        }
        
        var phone = document.getElementById("phone").value;
        if (phone.length <= 0)
        {
            alert("电话号码不能为空!");
            document.getElementById("phone").focus();
            return;
        }
        var company_id = document.getElementById("company").value;
        if (company_id == "-1")
        {
            alert("必须选择一个快递公司!");
            document.getElementById("company").focus();
            return;
        }
        var site_id = document.getElementById("site").value;
        if (site_id == "-1")
        {
            alert("必须选择一个快递站点!");
            document.getElementById("site").focus();
            return;
        }
        var addressList = extractAddressList(true);
        if (null == addressList)
        {
            return;
        }
        //prepare address data
        var addressData = addressList.length + "|";
        for (var i = 0; i < addressList.length; ++i)
        {
            var address = addressList[i];
            addressData += address['street'] + "|" + address['minnum'] + "|" + address['maxnum'] + "|";
        }
        
        var cur_location = document.getElementById("cur_location").innerHTML;

        var xmlhttp;
	    if (window.XMLHttpRequest)
	    {
		    xmlhttp=new XMLHttpRequest();
	    }
	    else
	    {// code for IE6, IE5
		    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    }
        xmlhttp.onreadystatechange=function()
	    {
		    if (xmlhttp.readyState==4 && xmlhttp.status==200)
		    {
			    //document.getElementById("tip").innerHTML=xmlhttp.responseText;
                var resultStr = xmlhttp.responseText;
				isAdding = resultStr.substr(0, 3)=="add";
				if (isAdding)
				{
					courierId = resultStr.substr(4);
				}
                /*document.getElementById("tip").innerHTML=xmlhttp.responseText;
                */
                if (isAdding)
                {
                    document.getElementById("tip").innerHTML="<br>快递员信息<b>创建</b>成功!";
                }
                else
                {
                    document.getElementById("tip").innerHTML="<br>快递员信息<b>更新</b>成功!";
                }
                
		    }
	    }
	    xmlhttp.open("POST","../api/?op=UpdateCourierInfo",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send("id=" + courierId
                     + "&name=" + encodeURIComponent(name)
                     + "&phone=" + phone
                     + "&company_id=" + company_id
                     + "&site_id=" + site_id
                     + "&location=" + cur_location
                     + "&address_list=" + encodeURIComponent(addressData));
	}
</script>
</head>

<body onload="initialize()">
<br>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="400px" style="text-align: left">
            <tr>
                <td colspan="3">
                    <b>快递员信息:</b><hr />
                </td>
            </tr>
            <tr>
                <td rowspan="4" style="width:120px;">
                    <img src="../resource/avatar/01.png" />
                </td>
                <td style="width:80px;">姓名</td>
                <td>
                    <input type="text" id="name" value="<?php echo $courierName; ?>" style="width:120px">
                </td>
            </tr>
            <tr>
                <td>电话</td>
                <td>
                    <input type="text" id="phone" value="<?php echo $phone; ?>" style="width:120px">
                </td>
            </tr>
            <tr>
                <td><div id="company_logo"><img src="../resource/icon/company/<?php echo $curCompanyId; ?>.jpg" height="24px"/></div></td>
                <td>
					<?php echo $companySelHtml; ?>
                </td>
            </tr>
            <tr>
                <td>所属站点</td>
                <td>
                    <?php echo $siteSelHtml; ?>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <hr />
                    <b>负责街区:</b>
                    <br />
                    <div id="address_list">
                    </div>
                    <button type="button" onclick="addStreet()">添加街区</button>
                    <hr />
                </td>
            </tr>
            <tr>
                <td>
                    <b>当前位置:</b>
                </td>
                <td colspan="2">
                    <div id="cur_location"><?php echo $location; ?></div>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <div id="map_container" style="width:396px; height:256px"></div>
                </td>
            </tr>
            <tr>
                <td colspan="3" style="text-align: center">
                <div id="tip"></div>
				<br />
                <button type="button" onclick="updateInfo()">提交</button>
                </td>
            </tr>
        </table>
    </center>
</body>
</html>
