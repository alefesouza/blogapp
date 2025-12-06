<?
$id = $_GET['id'];
$blogid = $_GET['blogid'];

$comments = file_get_contents('http://apps.aloogle.net/blogapp/wordpress/json/comentarios/'.$blogid.'.php?id='.$id);
echo $comments;
?>