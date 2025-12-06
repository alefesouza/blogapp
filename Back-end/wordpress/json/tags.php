<?
$id = $_GET['id'];
$q = $_GET['q'];
$number = strlen($q);
if($number > 2) {
	$posts = file_get_contents("http://alefesouza.com/blogapp.php?mode=tags&q=$q");
	
	echo "{ \"categories\": $posts }";
} else {
	echo "{ \"categories\": [] }";
}
?>