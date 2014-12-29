<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>GetSiteListByCompanyId</title>
<script type="text/javascript">
	function go()
	{
        var company_id = document.getElementById("company_id").value;

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
	    xmlhttp.open("POST","../api/?op=GetSiteListByCompanyId",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send("company_id=" + company_id);
	}
</script>
</head>

<body>
<br>
<b>GetSiteListByCompanyId</b>
<hr/>
<b>URL:</b>http://www.intalker.com/express/demo/api/?op=GetSiteListByCompanyId
<br/>
<br/>
<b>Post Params:</b>
<table border="0px" cellspacing="0px" cellpadding="0px">
<tr><td>Company ID:</td><td><input type="text" id="company_id" value="1" style="width:240px"></td></tr>
</table>
<button type="button" onclick="go()">Send Request</button>
<hr/>
<div id="result"></div>
</body>
</html>
