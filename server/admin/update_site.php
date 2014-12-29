<?php
require_once('../core/util.php');

$site_id = NULL;
$isAdding = true;
if (isset($_GET['id']))
{
    $site_id = $_GET['id'];
}

$site_name = "";
$site_phone = "";
$site_location = "121.499617,31.23986";
//$site_company_internalname = "unknown";
$company_id = -1;

$conn = connectDB();
if (!empty($site_id))
{
    $isAdding = false;
    $siteInfo = getSiteById($site_id);

    $site_name = $siteInfo['name'];
    $site_phone = $siteInfo['phone'];
    $site_location = $siteInfo['location'];
    //$site_company_internalname = $siteInfo['company_internalname'];
    $company_id = $siteInfo['company_id'];
}
$companySelHtml = getCompanySelectHtml("126px", "onCompanyChange()", $company_id);
disconnectDB($conn);
    
?>

<!DOCTYPE HTML>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>快递站点信息</title>
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
    function initialize() {
        var position = new AMap.LngLat(<?php echo $site_location; ?>);
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
            document.getElementById("site_location").innerHTML = lnglat.toString();
            //Center current pointer
            //mapObj.setCenter(lnglat);
        });
    }
    var expressMarker = null;
    var lnglat;
    function onCompanyChange()
    {
        document.getElementById("company_logo").innerHTML = "<img src='../resource/icon/company/" + document.getElementById("company").value + ".jpg'  height='24px'/>";
    }

	function updateInfo()
	{
        var company_id = document.getElementById("company").value;
        if (company_id == "-1")
        {
            alert("必须选择一个快递公司!");
            document.getElementById("company").focus();
            return;
        }
        var site_name = document.getElementById("site_name").value;
        if (site_name.length <= 0)
        {
            alert("站点名称不能为空!");
            document.getElementById("site_name").focus();
            return;
        }
        var site_phone = document.getElementById("site_phone").value;
        if (site_phone.length <= 0)
        {
            alert("电话号码不能为空!");
            document.getElementById("site_phone").focus();
            return;
        }
        var site_location = document.getElementById("site_location").innerHTML;
        
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
                isAdding = xmlhttp.responseText == "add";
                if (isAdding)
                {
                    //alert("站点信息创建成功!");
                    //window.location = "add_site_completed.php";
                    document.getElementById("tip").innerHTML="<br>站点信息<b>创建</b>成功!";
                }
                else
                {
                    //alert("站点信息更新成功!");
                    document.getElementById("tip").innerHTML="<br>站点信息<b>更新</b>成功!";
                }
		    }
	    }
	    xmlhttp.open("POST","../api/?op=UpdateSiteInfo",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send("company_id=" + company_id
                     + "&site_name=" + encodeURIComponent(site_name)
                     + "&site_phone=" + encodeURIComponent(site_phone)
                     + "&site_location=" + site_location);
	}
</script>
</head>

<body onload="initialize()">
<br>
<div id="debug"></div>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="400px" style="text-align: left">
			<tr>
                <td colspan="2">
                    <b>快递站点信息:</b><hr />
                </td>
            </tr>
			<tr>
				<td><div id="company_logo"><img src="../resource/icon/company/<?php echo $company_id; ?>.jpg" height="24px"/></div></td>
				<td>
                    <?php echo $companySelHtml; ?>
				</td>
			</tr>
			<tr>
				<td>站点名称</td>
				<td>
					<input type="text" id="site_name" value="<?php echo $site_name; ?>" style="width:272px">
				</td>
			</tr>
            <tr>
				<td>联系电话</td>
				<td>
					<input type="text" id="site_phone" value="<?php echo $site_phone; ?>" style="width:122px">
				</td>
			</tr>
			<tr>
                <td>
                    站点地址
                </td>
                <td>
                    <div id="site_location"><?php echo $site_location; ?></div>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <div id="map_container" style="width:396px; height:256px"></div>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center">
                    <div id="tip"></div>
					<br />
					<button type="button" onclick="updateInfo()">提交</button>
                </td>
            </tr>
        </table>
    </center>
</body>
</html>
