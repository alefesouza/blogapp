<?php
$dbi = mysqli_connect("localhost","c01ae9f379cf","6vAz5FnPoAJ0tct","winclub");
$dbi -> set_charset("utf8");

mysqli_query($dbi, "CREATE TABLE IF NOT EXISTS notification_users (id int auto_increment primary key, device varchar(255), platform varchar(50))") or die("ERROR: ".mysqli_error($dbi));

define("SID","ms-app://s-1-15-2-3527009781-1148255299-3792287610-3109339278-760729356-1393678575-1747842411");
define("CLIENT_SECRET",urlencode("aVhFEIkaBK4bHnJGzl5eOWjOo/mAv+Vt"));
$ChannelUri = $_POST['ChannelUri'];
?>