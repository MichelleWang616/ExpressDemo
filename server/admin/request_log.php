<?php
require_once('../core/util.php');

$conn = connectDB();
$requestList = getAllRequestList();
disconnectDB($conn);
//echo count($courierList). "<hr>";
//var_dump($courierList);
?>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>用户请求列表</title>
</head>

<body>
<br>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="100%" style="text-align: left;">
			<tr>
                <td colspan="14">
                    <b>用户请求列表</b><hr />
                </td>
            </tr>
			<tr>
			<th>ID</th>
			<th>设备码</th>
			<th>姓名</th>
			<th>发件地址</th>
			<th>收件地址</th>
            <th>位置</th>
			<th>距离范围</th>
            <th>街道</th>
            <th>门牌</th>
            <th>快递公司</th>
            <th>备注</th>
            <th>提交时间</th>
            <th>状态</th>
            <th>响应快递员ID</th>
            </tr>
			<?php
				foreach($requestList as $request)
				{
					echo "<tr><td>" . $request['id'] . "</td>"
                        . "<td>" . $request['user_device_id'] . "</td>"
                        . "<td>" . $request['user_name'] . "</td>"
                        . "<td>" . $request['from_address'] . "</td>"
                        . "<td>" . $request['to_address'] . "</td>"
                        . "<td>" . $request['location'] . "</td>"
                        . "<td>" . $request['max_distance'] . "</td>"
                        . "<td>" . $request['street'] . "</td>"
                        . "<td>" . $request['street_number'] . "</td>"
                        . "<td>" . $request['specified_company_id'] . "</td>"
                        . "<td>" . $request['comments'] . "</td>"
                        . "<td>" . $request['time'] . "</td>"
                        . "<td>" . $request['status'] . "</td>"
                        . "<td>" . $request['courier_id'] . "</td>"
						. "</tr>";
				}
			?>
        </table>
    </center>
</body>
</html>
