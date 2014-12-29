<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>GetMyHistoryList</title>
<script type="text/javascript">
	function go()
	{
        var device_id = document.getElementById("device_id").value;

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
	    xmlhttp.open("POST","../api/?op=GetMyHistoryList",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send("device_id=" + device_id);
	}
</script>
</head>

<body>
<br>
<b>GetMyHistoryList</b>
<hr/>
<b>URL:</b>http://www.intalker.com/express/demo/api/?op=GetMyHistoryList
<br/>
<br/>
<b>Post Params:</b>
<table border="0px" cellspacing="0px" cellpadding="0px">
<tr><td>Device ID:</td><td><input type="text" id="device_id" value="abc123" style="width:240px"></td></tr>
</table>
<button type="button" onclick="go()">Send Request</button>
<hr/>
<div id="result"></div>
</body>
</html>
