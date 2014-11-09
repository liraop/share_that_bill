<?

$databasehost = "sql4.freesqldatabase.com";
$databasename = "sql457251";
$databaseusername ="sql457251";
$databasepassword = "wX2*aK7%";

$con = mysql_connect($databasehost,$databaseusername,$databasepassword);

if(!$con){

	die('Could not connect: ' .mysql_error());

} else {

	print("gg");
}

?>