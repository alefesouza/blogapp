<?php
if(!isset($_GET['wp'])) {
	$wp = "AndLogo";
}

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);

$t = $hl_rss->channel->item[1]->title;
$l = $hl_rss->channel->item[1]->link;

$site_html= file_get_contents($l);
$matches=null;
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+name="(description)"\s+content="([^"]*)~i', $site_html, $matches);
$d = html_entity_decode($matches[4][1], ENT_QUOTES, "UTF-8");
$i = $matches[2][0];

$url_arr = explode('/', $i);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('images/'.$name)) {
  include("create_image.php");
  resizeImage($i);
}

$i = 'http://apps.aloogle.net/blogapp/zeldacombr/notifications/images/'.$name;

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
Header('Content-type: text/xml');
print($tile->asXML());
?>