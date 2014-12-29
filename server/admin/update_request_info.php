<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>UpdateRequestInfo</title>
<script type="text/javascript">
	function go()
	{
        var id = document.getElementById("id").value;
        var status = document.getElementById("status").value;

        var courier_id = document.getElementById("courier_id").value;

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
	    xmlhttp.open("POST","../api/?op=UpdateRequestInfo",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send("id=" + id
                     + "&status=" + status
                     + "&courier_id=" + courier_id);
	}
</script>
</head>

<body>
<br>
<b>UpdateRequestInfo</b>
<hr/>
<b>URL:</b>http://www.intalker.com/express/demo/api/?op=UpdateRequestInfo
<br/>
<br/>
<b>Post Params:</b>
<table border="0px" cellspacing="0px" cellpadding="0px">
<tr><td>Request ID:</td><td><input type="text" id="id" value="1" style="width:122px"></td></tr>
<tr><td>Status:</td><td><select id="status" style="width:126px"><option value="open" selected="selected">open</option><option value="pause">pause</option><option value="close">close</option></select></td></tr>
<tr><td>Courier ID:</td><td><input type="text" id="courier_id" value="-1" style="width:122px"></td></tr>
</table>
<button type="button" onclick="go()">Send Request</button>
<hr/>
<div id="result"></div>
</body>
</html>
