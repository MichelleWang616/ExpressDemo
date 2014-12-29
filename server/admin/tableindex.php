<?php
require_once('../core/util.php');
/*
$userInfo = loginCheck();
if (NULL == $userInfo)
{
	exit(0);
}
*/
$isDev = false;
if(isset($_GET['role']))
{
    if (strcmp($_GET['role'], "dev") == 0)
    {
        $isDev = true;
    }
}

?>

<br>
<font face="verdana">
  <b>后台管理</b>
</font>
<hr>
<br>
<font face="verdana">
添加
</font>
<br>
<font size="2px">
<a target="showframe" href="update_courier.php">快递员</a><br>
<a target="showframe" href="update_site.php">快递站点</a><br>
</font>

<br>
<font face="verdana">
浏览/修改
</font>
<br>
<font size="2px">
<a target="showframe" href="courier_list.php">快递员</a><br>
<a target="showframe" href="site_list.php">快递站点</a><br>
<a target="showframe" href="company_list.php">快递公司</a><br>
<a target="showframe" href="street_list.php">街道</a><br>
</font>

<br>
<font face="verdana">
日志
</font>
<br>
<font size="2px">
<a target="showframe" href="request_log.php">用户请求日志</a><br>
</font>

<?php

if ($isDev)
{
echo '
<br>
<hr/>
<font face="verdana">
API Sample
</font>
<br>
<font size="2px">
<a target="showframe" href="get_all_company_list.php">GetAllCompanyList</a><br>
<a target="showframe" href="get_site_list_by_company_id.php">GetSiteListByCompanyId</a><br>
<a target="showframe" href="get_proper_site_list.php">GetProperSiteList</a><br>
<a target="showframe" href="get_proper_courier_list.php">GetProperCourierList</a><br>
<a target="showframe" href="update_request_info.php">UpdateRequestInfo</a><br>
<a target="showframe" href="get_my_history_list.php">GetMyHistoryList</a><br>
</font>
';
}

?>