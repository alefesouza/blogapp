<?php
include("connect_db.php");
if (isset($token)) { header("location:sendnotification.php"); } else { header("location:login.php"); }
?>