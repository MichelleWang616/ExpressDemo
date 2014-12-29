<?php
require_once('../core/util.php');

$conn = connectDB();
$streetList = getAllStreet();
disconnectDB($conn);

?>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>街道列表</title>
</head>

<body>
<br>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="800px" style="text-align: left;">
			<tr>
                <td colspan="2">
                    <b>街道列表</b><hr />
                </td>
            </tr>
			<tr>
			<th>街道名称</th>
			<th>快递员数</th>
            </tr>
			<?php
				foreach($streetList as $street)
				{
					echo "<tr><td>" . $street["street"] . "</td>"
						. "<td>" . $street["courier_count"] . "</td>"
						. "</tr>";
				}
			?>
        </table>
    </center>
</body>
</html>
