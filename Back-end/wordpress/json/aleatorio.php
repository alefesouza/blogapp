<?
include('connect_db.php');

$gid = $_GET['id'];

$posts = file_get_contents("http://alefesouza.com/blogapp.php?mode=random");

echo $posts;
?>