<?
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];
$link = urldecode($_GET["link"]);
if(substr($link, -1) == "/") { $link = substr($link, 0, -1); }

include("connect_db.php");

$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

$object = explode("/", $link);
$object = $object[count($object) - 1];

if(strpos($link, "/tag/") !== false) {
  $json = file_get_contents("https://public-api.wordpress.com/rest/v1.1/sites/".$blogid."/tags/slug:".$object);
  $categories = json_decode($json);
  
  echo json_encode(array("title" => $categories->name, "value" => $categories->slug));
} else if(strpos($link, "/category/") !== false) {
  $json = file_get_contents("https://public-api.wordpress.com/rest/v1.1/sites/".$blogid."/categories/slug:".$object);
  $categories = json_decode($json);
  
  echo json_encode(array("title" => $categories->name, "value" => $categories->slug));
} else if(strpos($link, "s=") !== false) {
  $parts = parse_url($link);
  parse_str($parts['query'], $query);
  
  echo json_encode(array("title" => $query['s'], "value" => $query['s']));
} else {
  $json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/'.$blogid.'/posts/slug:'.$object);
  $post = json_decode($json);
  $postid = $post->ID;
  echo file_get_contents("http://apps.aloogle.net/blogapp/wordpress/v2/post.php?blogappid=".$blogappid."&platform=".$platform."&postid=".$postid);
}
?>