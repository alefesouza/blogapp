<?php
$feedurl = "http://www.windowsclub.com.br/feed";

if(!isset($_GET['wp'])) {
	$wp = "AndLogo";
}

$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);

$t = $rss->channel->item[3]->title;
$d = $rss->channel->item[3]->description;
$feedlink = $rss->channel->item[3]->link;

$article = file_get_contents($feedlink);

preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $article, $urls);

$i = $urls[1][1];

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