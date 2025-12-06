<?php
$connect = mysql_connect("localhost","","");
$db = mysql_select_db("aloog304_notificacoes");
mysql_set_charset('utf8', $connect);
mysql_query("OPTIMIZE TABLE 'blogapp'");
mysql_close($connect);
?>
<?php
$connect = mysql_connect("localhost","","");
$db = mysql_select_db("aloog304_notificacoes");
mysql_set_charset('utf8', $connect);
mysql_query("OPTIMIZE TABLE 'blogappchrome'");
mysql_close($connect);
?>
