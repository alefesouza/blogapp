<?
include("connect_db.php");

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);
$hl_contagem = 0;
foreach($hl_rss->channel->item as $hl_entrada) {
	$hl_title[$hl_contagem] = $hl_entrada->title;
	$hl_description[$hl_contagem] = $hl_entrada->description;
	$hl_link[$hl_contagem] = $hl_entrada->link;
	$hl_author[$hl_contagem] = $hl_entrada->children('dc', true)->creator;
	$hl_date[$hl_contagem] = $hl_entrada->pubDate;
	$hl_contagem = $hl_contagem + 1;
}

$css = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='zeldacombr' AND oque='postactivitycss'") or die ("ERROR: ".mysql_error());
$infocss = mysqli_fetch_array($css);
$js = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='zeldacombr' AND oque='postactivityjs'") or die ("ERROR: ".mysql_error());
$infojs = mysqli_fetch_array($js);

$header = "http://apps.aloogle.net/blogapp/zeldacombr/";

if($_GET['platform'] == "ios") {
  $header .= "majora.png";
} else if($_GET['platform'] == "windows") {
  $header .= "zeldaii.png";
} else if(!isset($_GET["platform"])) {
  $header .= "ocarina.jpg";
}

$finalJson = array("header" => $header, "posts" => array());

for($i=0;$i<=9;$i++) {
$hl_titulo = $hl_title[$i];
$hl_titulo = str_replace("'", "(apos)", html_entity_decode(preg_replace('/\s+/', ' ', trim($hl_titulo)), ENT_QUOTES, "UTF-8"));
$hl_descricao = $hl_description[$i];
$hl_descricao = trim(preg_replace('/\s+/', ' ', $hl_descricao));

$site_html= file_get_contents($hl_link[$i]);
$matches=null;
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+name="(description)"\s+content="([^"]*)~i', $site_html, $matches);
$imagem = $matches[2][0];
$description = str_replace("'", "(apos)", html_entity_decode($matches[4][1], ENT_QUOTES, "UTF-8"));

$author = $hl_author[$i];
$date = substr($hl_date[$i], 5, -15);

$jsonfb = file_get_contents('https://graph.facebook.com/v2.6/?fields=og_object{comments}&id='.$hl_link[$i].'&access_token=');
$commentsfb = json_decode($jsonfb);
$comentarios = count($commentsfb->og_object->comments->data);

if(json_encode($description) == "null") { $description = ""; }
$post = str_replace("'", "(apos)", '<html><head><base href="http://zelda.com.br" /><meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" /><style>'.$infocss['value'].'</style></head><body><p class="title">'.$hl_titulo.'</p><p class="authordate">Por '.$author.' - '.$date.'</p>'.$hl_descricao.'</body><script>'.$infojs['value'].'</script></html>');

$finalJson["posts"][] = array("title" => $hl_titulo, "description" => $description, "image" => $imagem, "author" => (string)$author, "date" => $date, "url" => (string)$hl_link[$i][0], "comments" => $comentarios, "post" => $post);
}

echo json_encode($finalJson);
?>
