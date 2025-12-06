<?php
$dbi = mysqli_connect("localhost","","","aloog304_blogapp");
$dbi -> set_charset("utf8");

if (mysqli_connect_errno()) {
  echo "Não foi possível conectar a base de dados: " . mysqli_connect_error();
}
?>
