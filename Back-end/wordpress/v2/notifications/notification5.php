<?php
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];
$systembuild = $_GET["systembuild"];
$what = $_GET["what"];
if($what == "fcategory") { $what = "category"; }

if(version_compare($systembuild, '10', '>=')) {
	$systembuild = 10;
}

$cache = "../cache/".$blogappid."/notification/notification5.".$systembuild.".xml";

if(!file_exists($cache) || $_GET["action"] == "update" || isset($what)) {
include("../connect_db.php");
$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

$jsonurl = 'https://public-api.wordpress.com/rest/v1.1/sites/'.$blogid.'/posts/?number='.$number;
if(isset($what)) {
	$jsonurl .= "&".$what."=".$_GET["value"];
}
$json = file_get_contents($jsonurl);
$site = json_decode($json);

if(!isset($_GET['wp'])) {
	$wp = "AndLogo";
}

$t = $site->posts[3]->title;
$l = $site->posts[3]->URL;

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
  include("create_image.php");
  resizeImage($i);
}

$i = 'http://apps.aloogle.net/blogapp/wordpress/v2/cache/'.$blogappid.'/images/'.$name;

$xml = '<tile>
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
</tile>';

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