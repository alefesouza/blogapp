<?
	session_start();
    unset($_SESSION['token']);
	header("location: login.php");
?>