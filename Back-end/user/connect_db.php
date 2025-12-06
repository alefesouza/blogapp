<?php
header('Content-Type: text/html; charset=utf-8');

session_start();

$dbi = mysqli_connect("localhost","","","aloog304_blogapp");
$dbi -> set_charset("utf8");

$token = $_SESSION['token'];
$sqlvalues = mysqli_query($dbi, "SELECT * FROM login WHERE token='$token'") or die ("ERROR: ".mysql_error());
$info = mysqli_fetch_array($sqlvalues);

$userid = $info['userid'];

if (mysqli_connect_errno()) {
  echo "Não foi possível conectar a base de dados: " . mysqli_connect_error();
}
?>
