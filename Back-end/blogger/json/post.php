<?
include('connect_db.php');

$blogid = $_GET['blogid'];

$css = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='$blogid' AND oque='postactivitycss'") or die ("ERROR: ".mysql_error());
$infocss = mysqli_fetch_array($css);
$js = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='$blogid' AND oque='postactivityjs'") or die ("ERROR: ".mysql_error());
$infojs = mysqli_fetch_array($js);

$icon = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='$blogid' AND oque='categoryicon'") or die ("ERROR: ".mysql_error());
$infoicon = mysqli_fetch_array($icon);
if($infoicon['value'] != "") {
	$valueicon = $infoicon['value'];
}

$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/'.$blogid.'/posts/'.$_GET['id'].'?key=');
$site = json_decode($json);

$id = $site->id;
$titulo = trim(addslashes($site->title));
$descricao = addslashes(html_entity_decode(trim(strip_tags($site->content)), 1,"UTF-8"));
$descricao = explode("\n", $descricao);
if(trim($descricao[0]) != "") {
	$desc = trim($descricao[0]);
} else if(trim($descricao[1])) {
	$desc = trim($descricao[1]);
} else if(trim($descricao[2])) {
	$desc = trim($descricao[2]);
} else if(trim($descricao[3])) {
	$desc = trim($descricao[3]);
}
$desc = explode(".", $desc);
if(strlen($desc[0]) > 20) {
	$d = $desc[0];
} else {
	$d = $desc[0].$desc[1];
}
preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->content, $urls);
$urls = $urls[1];
$imagem = addslashes($urls[0]);
$url = $site->url;
$autor = $site->author->displayName;
$data = str_replace("T", " ", substr($site->published, 0, -6));
$data2 = date_parse($site->published);
include("month.php");
$descricao = html_entity_decode(trim($site->content), 1,"UTF-8");
$descricao = preg_split( '#<img (.*?)>|<iframe (.*?)></iframe>#s', $descricao);
$content = preg_replace('/<script[^>]+\>(.|\s)*?<\/script>/', '', $site->content);
$jsonfb = file_get_contents('http://graph.facebook.com/comments?id='.$url);
$commentsfb = json_decode($jsonfb);
$comentarios = $site->replies->totalItems + count($commentsfb->data);

foreach ($site->labels as $tag) {
	$tags .= '<a href="http://apps.aloogle.net/blogapp/start?tag='.$tag.'&title='.$tag.'" class="tag">'.$tag.'</a>, ';
}
if($tags != "") {
	$tags = "<font class=\"tagscall\">Tags:</font> ".$tags;
}
	$tags = substr($tags, 0, -2);
echo "{ \"title\": \"".$titulo."\", \"description\": ".$d.", \"url\": \"".$url."\", \"image\": \"".$imagem."\", \"author\": \"".$autor."\", \"date\": \"".$data."\", \"date2\": \"".$data2["day"]." ".$month." ".$data2["year"]."\", \"comments\": \"".$comentarios."\", \"categoryicon\": \"".str_replace("valuehere", $categoria, $valueicon)."\",";
	echo "\"content\": ".json_encode('<html><head><meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" /><style>'.$infocss['value'].'</style></head><body>'.$content.'<p>'.$tags.'</p></body><script>'.$infojs['value'].'</script></html>')." }";
?>
