<?
include('connect_db.php');

$id = $_GET['id'];

$posts = file_get_contents("http://alefesouza.com/blogapp.php?mode=categories&page=".$_GET["page"]);

echo $posts;
?>