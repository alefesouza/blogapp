<?
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];
$postid = $_GET["postid"];

include('connect_db.php');

$css = mysqli_query($dbi, "SELECT * FROM extras WHERE blogappid=$blogappid AND oque='postcss';") or die ("ERROR: ".mysql_error());
$infocss = mysqli_fetch_array($css);
$js = mysqli_query($dbi, "SELECT * FROM extras WHERE blogappid=$blogappid AND oque='postjs';") or die ("ERROR: ".mysql_error());
$infojs = mysqli_fetch_array($js);

$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];
$base = $infoblogapp["site"];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/'.$blogid.'/posts/'.$postid);
$post = json_decode($json);

$posts = array("header" => "http://apps.aloogle.net/blogapp/wordpress/v2/headers/".$blogappid.".png", "posts" => array());

$titulo = trim(addslashes(html_entity_decode($post->title)));
$id = $post->ID;
$url = $post->URL;
$author = $post->author->name;
$content = $post->content;
	
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+property="(og:description)"\s+content="([^"]*)~i', file_get_contents($url), $matches);
$imagem = $matches[2][1];
$descricao = str_replace("'", "(apos)", html_entity_decode($matches[4][0], ENT_QUOTES, "UTF-8"));

$comentarios = $post->discussion->comment_count;
$data2 = date_parse($post->date);
include("month.php");
$date = $data2["day"]." ".$month." ".$data2["year"];

if($usefbcomments) {
$jsonfb = file_get_contents('http://graph.facebook.com/comments?id='.$url);
$commentsfb = json_decode($jsonfb);
$comentarios += count($commentsfb->data);
}

$tags = "";

foreach ($post->tags as $key=>$val) {
	$tag = $post->tags->$key->slug;
	$tags .= '<a href="http://apps.aloogle.net/blogapp/start?tag='.$tag.'&title='.$key.'" class="tag">'.$key.'</a>, ';
}
if($tags != "") {
	$tags = "<font class=\"tagscall\">Tags:</font> ".$tags;
}
$tags = substr($tags, 0, -2);

if($_GET["platform"] == "windows") {
	$top = "<p class=\"title\">".$titulo."</p><p class=\"authordate\">Por ".$author." - ".$date."</p>";
}

$posts["posts"][] = array("id" => $id, "title" => $titulo, "description" => $descricao, "image" => $imagem, "author" => $author, "url" => $url, "comments" => $comentarios, "date" => $date, "post" => str_replace("'", "(apos)", "<html><head><base href=\"".$base."\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\" /><style>".$infocss['value']."</style></head><body>".$top.$content."<p>".$tags."</p></body><script>".$infojs['value']."</script></html>"));

echo json_encode($posts);
?>