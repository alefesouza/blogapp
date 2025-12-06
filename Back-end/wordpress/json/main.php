<?
include('connect_db.php');

$id = $_GET['id'];
$number = $_GET['number'];

if(isset($_GET['search']) && $_GET['search'] != "") {
	$search = '&search='.$_GET['search'];
}

if(isset($_GET['label']) && $_GET['label'] != "") {
	$category = '&category='.$_GET['category'];
}

if(isset($_GET['tag']) && $_GET['tag'] != "") {
	$tag = '&tag='.$_GET['tag'];
}

if(isset($_GET['page']) && $_GET['page'] != "") {
	$page = '&page='.$_GET['page'];
}

$site = json_decode($json);

$posts = file_get_contents("http://alefesouza.com/blogapp.php?mode=main&number=".$_GET["number"]."$search$tag$category$page");

echo "{ \"posts\": $posts }";
?>