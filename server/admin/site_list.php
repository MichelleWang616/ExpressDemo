<?php
require_once('../core/util.php');

$conn = connectDB();
$siteList = getAllSiteList();
disconnectDB($conn);
//echo count($courierList). "<hr>";
//var_dump($courierList);
?>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>快递站点列表</title>
</head>

<body>
<br>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="800px" style="text-align: left;">
			<tr>
                <td colspan="5">
                    <b>快递站点列表</b><hr />
                </td>
            </tr>
			<tr>
			<th>ID</th>
			<th>公司</th>
			<th>站点名称</th>
			<th>联系电话</th>
			<th>操作</th>
            </tr>
			<?php
				foreach($siteList as $site)
				{
                    $id = $site['id'];
					echo "<tr><td>" . $id . "</td>"
						. "<td><img src='../resource/icon/company/" . $site['company_id'] . ".jpg' height='16px'>" . $site['company_name'] . "</td>"
						. "<td>" . $site['name'] . "</td>"
						. "<td>" . $site['phone'] . "</td>"
						. "<td><a href='update_site.php?id=" . $id . "'>修改</a></td>"
						. "</tr>";
				}
			?>
        </table>
    </center>
</body>
</html>
