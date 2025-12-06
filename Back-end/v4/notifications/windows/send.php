<?php 
include_once 'wns.php';

$url_arr = explode('/', $image);
$ct = count($url_arr);
$name = $url_arr[$ct-1];

include("create_image.php");
resizeImage($image);

$image = 'http://apps.aloogle.net/blogapp/v4/notifications/windows/images/'.$name;

notify_wns_users($title, $description, $image);
?>