<?php
include("create_image.php");

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);
$hl_contagem = 0;
foreach($hl_rss->channel->item as $hl_entrada) {
	$hl_title[$hl_contagem] = $hl_entrada->title;
	$hl_link[$hl_contagem] = $hl_entrada->link;
	$hl_contagem = $hl_contagem + 1;
}
	
foreach($hl_link as $l) {
$site_html= file_get_contents($l);
$matches=null;
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)~i', $site_html, $matches);
$i = $matches[2][0];

$url_arr = explode('/', $i);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('images/'.$name)) {
  resizeImage($i);
}

$image[] = 'http://apps.aloogle.net/blogapp/zeldacombr/notifications/images/'.$name;
}

if(isset($_GET["build"]) && version_compare($_GET["build"], '10.0.10586', '>=')) {
$tile = new SimpleXMLElement("<tile>
<visual>
<binding template='TileMedium' hint-presentation='people' branding='logo'>
<image src='".$image[0]."'/>
<image src='".$image[1]."'/>
<image src='".$image[2]."'/>
<image src='".$image[3]."'/>
<image src='".$image[4]."'/>
<image src='".$image[5]."'/>
<image src='".$image[6]."'/>
<image src='".$image[7]."'/>
<image src='".$image[8]."'/>
</binding>
<binding template='TileWide' hint-presentation='people'>
<image src='".$image[0]."'/>
<image src='".$image[1]."'/>
<image src='".$image[2]."'/>
<image src='".$image[3]."'/>
<image src='".$image[4]."'/>
<image src='".$image[5]."'/>
<image src='".$image[6]."'/>
<image src='".$image[7]."'/>
<image src='".$image[8]."'/>
<image src='".$image[9]."'/>
</binding>
<binding template='TileLarge' hint-presentation='people'>
<image src='".$image[0]."'/>
<image src='".$image[1]."'/>
<image src='".$image[2]."'/>
<image src='".$image[3]."'/>
<image src='".$image[4]."'/>
<image src='".$image[5]."'/>
<image src='".$image[6]."'/>
<image src='".$image[7]."'/>
<image src='".$image[8]."'/>
<image src='".$image[9]."'/>
</binding>
</visual>
</tile>");
} else if(isset($_GET["build"]) && $_GET["build"] == "wp") {
for($i = 0; $i < 3; $i ++) {
	$t[$i] = $hl_title[$i];
	$tags = get_meta_tags($hl_link[$i]);
	$d[$i] = $tags['description'];
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
<image src='".$image[0]."'/>
<image src='".$image[1]."'/>
<image src='".$image[2]."'/>
<image src='".$image[3]."'/>
<image src='".$image[4]."'/>
<image src='".$image[5]."'/>
<image src='".$image[6]."'/>
<image src='".$image[7]."'/>
<image src='".$image[8]."'/>
<image src='".$image[9]."'/>
</binding>
<binding template='TileWide' hint-presentation='photos'>
<image src='".$image[0]."'/>
<image src='".$image[1]."'/>
<image src='".$image[2]."'/>
<image src='".$image[3]."'/>
<image src='".$image[4]."'/>
<image src='".$image[5]."'/>
<image src='".$image[6]."'/>
<image src='".$image[7]."'/>
<image src='".$image[8]."'/>
<image src='".$image[9]."'/>
</binding>
<binding template='TileLarge' hint-presentation='photos'>
<image src='".$image[0]."'/>
<image src='".$image[1]."'/>
<image src='".$image[2]."'/>
<image src='".$image[3]."'/>
<image src='".$image[4]."'/>
<image src='".$image[5]."'/>
<image src='".$image[6]."'/>
<image src='".$image[7]."'/>
<image src='".$image[8]."'/>
<image src='".$image[9]."'/>
</binding>
</visual>
</tile>");
}

Header('Content-type: text/xml');
print($tile->asXML());
?>