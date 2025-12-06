<?php
include("../connect_db.php");

include("create_image.php");

$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);
$contagem = 0;
if($type == "feedburner") {
	foreach($rss->channel->item as $entrada) {
		$titles[$contagem] = $entrada->title;
		$links[$contagem] = $entrada->link;
		$contagem++;
	}
} else {
	foreach($rss->channel->item as $entrada) {
		$titles[$contagem] = $entrada->title;
		$links[$contagem] = $entrada->link;
		$contagem++;
	}
}

foreach($links as $l) {
if($usetwitter) {
	$site_html= get_meta_tags($l);
	$i = $site_html["twitter:image"];
	$d[] = str_replace("'", "(apos)", html_entity_decode($site_html["twitter:description"], ENT_QUOTES, "UTF-8"));
} else {
	$site_html= file_get_contents($l);
	$matches=null;
	preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+property="(og:description)"\s+content="([^"]*)~i', $site_html, $matches);
	$i = $matches[2][1];
	$description = str_replace("'", "(apos)", html_entity_decode($matches[4][0], ENT_QUOTES, "UTF-8"));
	$d[] = $description;
}

$url_arr = explode('/', $i);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('images/'.$name)) {
  resizeImage($i);
}

$image[] = 'http://apps.aloogle.net/blogapp/rss/notifications/images/'.$name;
}

$images = "";

foreach($image as $imagem) {
	$images .= "<image src='".$imagem."' />";
}

if(isset($_GET["build"]) && $_GET["build"] == "wp") {
for($i = 0; $i < 3; $i ++) {
	$t[$i] = $titles[$i];
	if($d[$i] == "") {
		$d[$i] == " ";
	}
}

$tile = new SimpleXMLElement('<tile>
<visual lang="pt-BR" version="2">
<binding template="TileSquare150x150Text02" fallback="TileSquareBlock" branding="name">
<text id="1">'.$t[0].'</text>
<text id="2">'.$d[0].'</text>
</binding>
<binding template="TileWide310x150Text09" branding="name'.$wp.'">
<text id="1">'.$t[0].'</text>
<text id="2">'.$d[0].'</text>
</binding>
<binding template="TileSquare310x310TextList03" branding="name'.$wp.'">
<text id="1">'.$t[0].'</text>
<text id="2">'.$d[0].'</text>
<text id="3">'.$t[1].'</text>
<text id="4">'.$d[1].'</text>
<text id="5">'.$t[2].'</text>
<text id="6">'.$d[2].'</text>
</binding>
</visual>
</tile>');
} else {
$tile = new SimpleXMLElement("<tile>
<visual>
<binding template='TileMedium' hint-presentation='photos'>
".$images."
</binding>
<binding template='TileWide' hint-presentation='photos'>
".$images."
</binding>
<binding template='TileLarge' hint-presentation='photos'>
".$images."
</binding>
</visual>
</tile>");
}

Header('Content-type: text/xml');
print($tile->asXML());
?>