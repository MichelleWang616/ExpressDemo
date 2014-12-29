<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>GetProperCourierList</title>
<script type="text/javascript">
	function go()
	{
        var device_id = document.getElementById("device_id").value;
        var user_name = document.getElementById("user_name").value;
        
        var location = document.getElementById("location").value;
        var max_distance = document.getElementById("max_distance").value;
        
        var from_address = document.getElementById("from_address").value;
        var to_address = document.getElementById("to_address").value;
        
        var street = document.getElementById("street").value;
        var street_number = document.getElementById("street_number").value;
        
        var company_id = document.getElementById("company_id").value;
        
        var comments = document.getElementById("comments").value;

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
			    document.getElementById("result").innerHTML=xmlhttp.responseText;
		    }
	    }
	    xmlhttp.open("POST","../api/?op=GetProperCourierList",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send("device_id=" + device_id
                     + "&user_name=" + encodeURIComponent(user_name)
                     + "&location=" + encodeURIComponent(location)
                     + "&max_distance=" + max_distance
                     + "&from_address=" + encodeURIComponent(from_address)
                     + "&to_address=" + encodeURIComponent(to_address)
                     + "&street=" + encodeURIComponent(street)
                     + "&street_number=" + street_number
                     + "&company_id=" + company_id
                     + "&comments=" + encodeURIComponent(comments));
	}
</script>
</head>

<body>
<br>
<b>GetProperCourierList</b>
<hr/>
<b>URL:</b>http://www.intalker.com/express/demo/api/?op=GetProperCourierList
<br/>
<br/>
<b>Post Params:</b>
<table border="0px" cellspacing="0px" cellpadding="0px">
<tr><td>User Device ID:</td><td><input type="text" id="device_id" value="abc123" style="width:240px"></td></tr>
<tr><td>User Name:</td><td><input type="text" id="user_name" value="小明" style="width:240px"></td></tr>
<tr><td>Current Location:</td><td><input type="text" id="location" value="121.5258,31.219821" style="width:240px"></td></tr>
<tr><td>Max Line Distance:</td><td><input type="text" id="max_distance" value="400" style="width:240px">米 (-1表示不限)</td></tr>
<tr><td>From Address:</td><td><input type="text" id="from_address" value="上海市浦东新区潍坊九村1号楼101室" style="width:240px"></td></tr>
<tr><td>To Address:</td><td><input type="text" id="to_address" value="北京市故宫博物院" style="width:240px"></td></tr>
<tr><td>From Street:</td><td><input type="text" id="street" value="浦电路" style="width:240px">(留空表示不限)</td></tr>
<tr><td>Street Number:</td><td><input type="text" id="street_number" value="200" style="width:240px">号/弄</td></tr>
<tr><td>Company ID:</td><td><input type="text" id="company_id" value="-1" style="width:240px">(-1表示不限)</td></tr>
<tr><td>Comments:</td><td><input type="text" id="comments" value="收件员必须要帅" style="width:240px"></td></tr>
</table>
<button type="button" onclick="go()">Send Request</button>
<hr/>
<div id="result"></div>
</body>
</html>
