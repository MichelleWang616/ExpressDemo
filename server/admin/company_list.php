<?php
require_once('../core/util.php');

$conn = connectDB();
$companyList = getAllCompanyList();
disconnectDB($conn);
?>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>快递公司列表</title>
</head>

<body>
<br>
    <center>
        <table border="0px" cellspacing="0px" cellpadding="0px" width="400px" style="text-align: left">
			<tr>
                <td colspan="2">
                    <b>快递公司列表</b><hr />
                </td>
            </tr>
			<?php
				foreach($companyList as $company)
				{
                    $id = $company[DB_COMPANY_ID];
					echo "<tr><td><img src='../resource/icon/company/" . $id . ".jpg' width='28px'></td>"
						. "<td>" . $company[DB_COMPANY_DISPLAYNAME] . "</td></tr>";
				}
			?>
        </table>
    </center>
</body>
</html>
