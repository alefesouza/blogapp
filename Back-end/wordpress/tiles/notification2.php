<?php
include("create_image.php");

$id = $_GET['id'];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/'.$id.'/posts');
$site = json_decode($json);

$t = $site->posts[0]->title;
$l = $site->posts[0]->URL;

$tags = get_meta_tags($l);
$d = $tags['twitter:description'];
if($d == "") {
  $d = " ";
}

$i = $tags['twitter:image'];

$url_arr = explode('/', $i);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('images/'.$name)) {
  resizeImage($i);
}

$i = 'http://apps.aloogle.net/blogapp/wordpress/tiles/images/'.$name;

$tile = new SimpleXMLElement('<tile>
<visual lang="pt-BR" version="2">
<binding template="TileSquare150x150PeekImageAndText04" fallback="TileSquarePeekImageAndText04" branding="logo">
<image id="1" src="'.$i.'" alt="alt text"/>
<text id="1">'.$t.'</text>
</binding>  
<binding template="TileWide310x150PeekImage01" fallback="TileWidePeekImage01" branding="nameAndLogo">
<image id="1" src="'.$i.'" alt="alt text"/>
<text id="1">'.$t.'</text>
<text id="2">'.$d.'</text>
</binding>
<binding template="TileSquare310x310ImageAndTextOverlay02" branding="nameAndLogo">
<image id="1" src="'.$i.'" alt="alt text"/>
<text id="1">'.$t.'</text>
<text id="2">'.$d.'</text>
</binding>  
</visual>
</tile>');
Header('Content-type: text/xml');
print($tile->asXML());
?>