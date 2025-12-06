<?php
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];
$systembuild = $_GET["systembuild"];
$what = $_GET["what"];
if($what == "fcategory") { $what = "category"; }

if(version_compare($systembuild, '10', '>=')) {
	$systembuild = 10;
}
$cache = "../cache/".$blogappid."/notification/notification1.".$systembuild.".xml";

if(!file_exists($cache) || $_GET["action"] == "update" || isset($what)) {
include("../connect_db.php");
include("create_image.php");
$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

$jsonurl = 'https://public-api.wordpress.com/rest/v1.1/sites/'.$blogid.'/posts/?number=9';
if(isset($what)) {
	$jsonurl .= "&".$what."=".$_GET["value"];
}
$json = file_get_contents($jsonurl);
$site = json_decode($json);

foreach($site->posts as $p) {
$l = $p->URL;
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
$name = explode('?', $name)[0];
if(strpos($name, '.') === false) {
	$name .= ".jpg";
}
$dir = '../cache/'.$blogappid.'/images/'.$name;
if (!file_exists($dir)) {
    if(!is_dir(dirname($dir)))
        mkdir(dirname($dir).'/', 0777, TRUE);
  resizeImage($i);
}

$image[] = 'http://apps.aloogle.net/blogapp/wordpress/v2/cache/'.$blogappid.'/images/'.$name;
}

$images = "";

foreach($image as $imagem) {
	$images .= "<image src='".$imagem."' />";
}

if($_GET["systembuild"] == "wp") {
for($i = 0; $i < 3; $i ++) {
	$t[$i] = $titles[$i];
	if($d[$i] == "") {
		$d[$i] == " ";
	}
}

$xml = '<tile>
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
</tile>';
} else {
$xml = "<tile>
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
</tile>";
}

if(!isset($what)) {
	file_force_contents($cache, $xml);
}
} else {
$xml = file_get_contents($cache);
}

Header('Content-type: text/xml');
$tile = new SimpleXMLElement($xml);
print($tile->asXML());
?>