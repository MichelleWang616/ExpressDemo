<?php
require_once('../core/util.php');
/*
if (loginCheck() == NULL)
{
	exit(0);
}
*/

$params = "";
$isFirst = true;
foreach ($_GET as $key=>$val)
{
	if ($isFirst)
	{
		$params .= "?" . $key . "=" . $val;
		$isFirst = false;
	}
	else
	{
		$params .= "&" . $key . "=" . $val;
	}
}

?>
<html>


<frameset cols="200,*">
<frame src="tableindex.php<?php echo $params; ?>">
<frame src="about.php" name="showframe">

</frameset>


</html>