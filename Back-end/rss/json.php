<?
include("connect_db.php");

$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);
$contagem = 0;

if($type == "feedburner") {
	foreach($rss->channel->item as $entrada) {
		$titles[$contagem] = $entrada->title;
		$posts[$contagem] = $entrada->children('content', true)->encoded;
		$links[$contagem] = $entrada->link;
		$authors[$contagem] = $entrada->children('dc', true)->creator;
		$dates[$contagem] = $entrada->pubDate;
		$comentarioss[$contagem] = $entrada->children('slash', true)->comments;
		$contagem += 1;
	}
} else {
	foreach($rss->channel->item as $entrada) {
		$titles[$contagem] = $entrada->title;
		$posts[$contagem] = $entrada->description;
		$links[$contagem] = $entrada->link;
		$authors[$contagem] = $entrada->children('dc', true)->creator;
		$dates[$contagem] = $entrada->pubDate;
		$contagem += 1;
	}
}

$css = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='zeldacombr' AND oque='postactivitycss'") or die ("ERROR: ".mysql_error());
$infocss = mysqli_fetch_array($css);
$js = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='zeldacombr' AND oque='postactivityjs'") or die ("ERROR: ".mysql_error());
$infojs = mysqli_fetch_array($js);

$finalJson = array("header" => $header, "posts" => array());

for($i=0;$i<=9;$i++) {
	$titulo = $titles[$i];
	$titulo = str_replace("'", "(apos)", html_entity_decode(preg_replace('/\s+/', ' ', trim($titulo)), ENT_QUOTES, "UTF-8"));
	$post = $posts[$i];
	$post = trim(preg_replace('/\s+/', ' ', $post));

	if($usetwitter) {
		$site_html= get_meta_tags($links[$i]);
		$imagem = $site_html["twitter:image"];
		$description = str_replace("'", "(apos)", html_entity_decode($site_html["twitter:description"], ENT_QUOTES, "UTF-8"));
	} else {
		$site_html= file_get_contents($links[$i]);
		$matches=null;
		preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+property="(og:description)"\s+content="([^"]*)~i', $site_html, $matches);
		$imagem = $matches[2][1];
		$description = str_replace("'", "(apos)", html_entity_decode($matches[4][0], ENT_QUOTES, "UTF-8"));
	}

	$author = $authors[$i];
	$date = substr($dates[$i], 5, -15);

	if($usefbcomments) {
		$jsonfb = file_get_contents('https://graph.facebook.com/v2.6/?fields=og_object{comments}&id='.$links[$i].'&access_token=');
		$commentsfb = json_decode($jsonfb);
		$comentarios = count($commentsfb->og_object->comments->data);
	} else {
		$comentarios = $comentarioss[$i];
	}

	if(json_encode($description) == "null") { $description = ""; }

	$post = str_replace("'", "(apos)", '<html><head><base href="'.$base.'" /><meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" /><style>'.$infocss['value'].'</style></head><body><p class="title">'.$titulo.'</p><p class="authordate">Por '.$author.' - '.$date.'</p>'.$post.'</body><script>'.$infojs['value'].'</script></html>');

	$finalJson["posts"][] = array("title" => (string)$titulo, "description" => (string)$description, "image" => (string)$imagem, "author" => (string)$author, "date" => (string)$date, "url" => (string)$hl_link[$i][0], "comments" => (string)$comentarios, "post" => (string)$post);
}

echo json_encode($finalJson);
?>
