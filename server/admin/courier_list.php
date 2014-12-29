<?php
require_once('../core/util.php');

$conn = connectDB();
$courierList = getAllCourierList();
disconnectDB($conn);
//echo count($courierList). "<hr>";
//var_dump($courierList);
?>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>快递员列表</title>
</head>

<body>
<br>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="800px" style="text-align: left">
			<tr>
                <td colspan="8">
                    <b>快递员列表</b><hr />
                </td>
            </tr>
			<tr>
			<th>头像</th>
			<th>ID</th>
			<th>姓名</th>
			<th>电话</th>
			<th>公司</th>
			<th>站点</th>
			<th>登记时间</th>
			<th>操作</th>
            </tr>
			<?php
				foreach($courierList as $courier)
				{
					$id = $courier['id'];
					echo "<tr><td><img src='../resource/avatar/01.png' width='32px'></td>"
						. "<td>" . $id . "</td>"
						. "<td>" . $courier['name'] . "</td>"
						. "<td>" . $courier['phone'] . "</td>"
						. "<td><img src='../resource/icon/company/" . $courier['company_id'] . ".jpg' height='16px'>" . $courier['company_name'] . "</td>"
						. "<td>" . $courier['site_name'] . "</td>"
						. "<td>" . $courier['reg_time'] . "</td>"
						. "<td><a href='update_courier.php?id=" . $id . "'>修改</a></td>"
						. "</tr>";
				}
			?>
        </table>
    </center>
</body>
</html>
