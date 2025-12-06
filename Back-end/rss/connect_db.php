<?php
$dbi = mysqli_connect("localhost","","","aloog304_blogapp");
$dbi -> set_charset("utf8");

if (mysqli_connect_errno()) {
  echo "Não foi possível conectar a base de dados: " . mysqli_connect_error();
}

ini_set('user_agent','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36');

$blogapp = $_GET["blogapp"];

if($blogapp == "windowsclubbr") {
		$blogapp = $_GET["blogapp"];
		$feedurl = "http://www.windowsclub.com.br/feed";
		$tiletype = "2"; // 2 = sem imagens
} else {
$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE blogapp='$blogapp';") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);

$feedurl = $infoblogapp["userid"];
$base = $infoblogapp["site"];
$arguments = json_decode($infoblogapp["arguments"]);

$usefbcomments = $arguments->usefbcomments;
$usetwitter = $arguments->usetwittertags;
$type = $arguments->type;

if(isset($arguments->headers->{$_GET["platform"]})) {
  $header = $arguments->headers->{$_GET["platform"]};
} else {
  $header = $arguments->headers->all;
}
}
?>
