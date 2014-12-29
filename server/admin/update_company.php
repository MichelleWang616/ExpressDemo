<!DOCTYPE HTML>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>快递公司信息</title>
<style type="text/css">
html{height:100%}
body{height:100%;margin:0px;padding:0px}
#container{height:100%}
</style>
<script type="text/javascript"
src="http://webapi.amap.com/maps?v=1.3&key=yourkey">
</script>
<script type="text/javascript">
    function initialize() {
        var position = new AMap.LngLat(121.531214, 31.220658);
        var mapObj = new AMap.Map("map_container");
        mapObj.setCenter(position);
        mapObj.setZoom(16);
        var listener = AMap.event.addListener(mapObj, "click", function (e) {
            lnglat = e.lnglat;
            if (null == expressMarker) {
                expressMarker = new AMap.Marker({
                    map: mapObj,
                    position: e.lnglat,
                    icon: "icon/0.png",
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
    }
    var expressMarker = null;
    var lnglat;
    function onCompanyChange()
    {
        document.getElementById("company_logo").innerHTML = "<img src='icon/" + document.getElementById("company").value + ".jpg'  height='24px'/>";
    }

	function updateInfo()
	{
		var name=document.getElementById("name").value;
		var phone_num1=document.getElementById("phone_num1").value;
		var phone_num2=document.getElementById("phone_num2").value;
		var company=document.getElementById("company").value;
		var site_id=document.getElementById("site_id").value

		alert(name+phone_num1+phone_num2+company+site_id);
	}
</script>
</head>

<body onload="initialize()">
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="400px" style="text-align: left">
			<tr>
				<td>名称</td>
				<td>
					<input type="text" id="display_name" value="" style="width:120px">
				</td>
			</tr>
			<tr>
				<td>内部名</td>
				<td>
					<input type="text" id="internal_name" value="" style="width:120px">
				</td>
			</tr>
			<tr>
				<td>公司Logo</td>
				<td>
					<input type="text" id="logo" value="" style="width:120px">
				</td>
			</tr>
        </table>
    </center>
</body>
</html>
