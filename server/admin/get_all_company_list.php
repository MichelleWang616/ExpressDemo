<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>GetAllCompanyList</title>
<script type="text/javascript">
	function go()
	{
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
	    xmlhttp.open("POST","../api/?op=GetAllCompanyList",true);
	    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	    xmlhttp.send();
	}
</script>
</head>

<body>
<br>
<b>GetAllCompanyList</b>
<hr/>
<b>URL:</b>http://www.intalker.com/express/demo/api/?op=GetAllCompanyList
<br>
<button type="button" onclick="go()">Send Request</button>
<hr/>
<div id="result"></div>
</body>
</html>
