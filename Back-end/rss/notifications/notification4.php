<?php
include("../connect_db.php");

if(!isset($_GET['wp'])) {
	$wp = "AndLogo";
}

$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);

$t = $rss->channel->item[2]->title;
$l = $rss->channel->item[2]->link;

if($tiletype != "2") {
if($usetwitter) {
	$site_html= get_meta_tags($l);
	$i = $site_html["twitter:image"];
	$d = str_replace("'", "(apos)", html_entity_decode($site_html["twitter:description"], ENT_QUOTES, "UTF-8"));
} else {
	$site_html= file_get_contents($l);
	$matches=null;
	preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+property="(og:description)"\s+content="([^"]*)~i', $site_html, $matches);
	$i = $matches[2][1];
	$d = str_replace("'", "(apos)", html_entity_decode($matches[4][0], ENT_QUOTES, "UTF-8"));
}
}

if($tiletype == "2") {
for($i = 0; $i <= 7; $i++) {
	$t[] = $rss->channel->item[$i]->title;
	$d[] = $rss->channel->item[$i]->description;
}
}

if($tiletype != "2") {
$url_arr = explode('/', $i);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('images/'.$name)) {
  include("create_image.php");
  resizeImage($i);
}
}

$i = 'http://apps.aloogle.net/blogapp/rss/notifications/images/'.$name;

if($tiletype != "2") {
$tile = new SimpleXMLElement('<tile>
<visual lang="pt-BR" version="2">
<binding template="TileSquare150x150PeekImageAndText04" fallback="TileSquarePeekImageAndText04" branding="logo">
<image id="1" src="'.$i.'" alt="alt text"/>
<text id="1">'.$t.'</text>
</binding>  
<binding template="TileWide310x150PeekImage01" fallback="TileWidePeekImage01" branding="name'.$wp.'">
<image id="1" src="'.$i.'" alt="alt text"/>
<text id="1">'.$t.'</text>
<text id="2">'.$d.'</text>
</binding>
<binding template="TileSquare310x310ImageAndTextOverlay02" branding="name'.$wp.'">
<image id="1" src="'.$i.'" alt="alt text"/>
<text id="1">'.$t.'</text>
<text id="2">'.$d.'</text>
</binding>
</visual>
</tile>');
} else {
$tile = new SimpleXMLElement('<tile>
<visual lang="pt-BR" version="2">
<binding template="TileSquareText02" branding="name'.$wp.'">
<text id="1">'.$t[2].'</text>
<text id="2">'.$d[2].'</text>
</binding>
<binding template="TileWideText09" branding="name'.$wp.'">
<text id="1">'.$t[2].'</text>
<text id="2">'.$d[2].'</text>
</binding>
<binding template="TileSquare310x310TextList03" branding="name'.$wp.'">
<text id="1">'.$t[4].'</text>
<text id="2">'.$d[4].'</text>
<text id="3">'.$t[5].'</text>
<text id="4">'.$d[5].'</text>
<text id="5"> </text>
<text id="6"> </text>
</binding>
</visual>
</tile>');
}
Header('Content-type: text/xml');
print($tile->asXML());
?>