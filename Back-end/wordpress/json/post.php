<?
include('connect_db.php');

$posts = file_get_contents("http://alefesouza.com/blogapp.php?mode=post&id=".$_GET["id"]);

echo $posts;
?>