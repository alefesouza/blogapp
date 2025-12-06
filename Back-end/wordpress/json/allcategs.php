<?
$id = $_GET['id'];

$posts = file_get_contents("http://alefesouza.com/blogapp.php?mode=allcategs");

echo "{ \"categories\": $posts }"
?>