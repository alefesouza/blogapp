<?php
$id = $_GET['id'];

$json = file_get_contents('https://www.googleapis.com/youtube/v3/playlistItems?playlistId='.$id.'&key=&part=snippet&maxResults=5');
$site = json_decode($json);

for($i = 0; $i < 5; $i++) {
  $t[$i] = $site->items[$i]->snippet->title;
  
  $de = explode(".", $site->items[$i]->snippet->description);
  $d[$i] = $de[0];
  
  $image[$i] = $site->items[$i]->snippet->thumbnails->medium->url;
}

$imagemedium = $site->items[0]->snippet->thumbnails->medium->url;
$imagebig = $site->items[0]->snippet->thumbnails->high->url;

$tile = new SimpleXMLElement('<tile>
<visual lang="pt-BR" version="2">
<binding template="TileSquare150x150PeekImageAndText04" fallback="TileSquarePeekImageAndText04" branding="logo">
<image id="1" src="'.$imagemedium.'" alt="alt text"/>
<text id="1">Último vídeo: '.$t[0].'</text>
</binding>
<binding template="TileWide310x150PeekImage02" fallback="TileWidePeekImage02" branding="nameAndLogo">
<image id="1" src="'.$imagebig.'" alt="alt text"/>
<text id="1">Últimos vídeos</text>
<text id="2">'.$t[0].'</text>
<text id="3">'.$t[1].'</text>
<text id="4">'.$t[2].'</text>
<text id="5">'.$t[3].'</text>
</binding>
<binding template="TileSquare310x310SmallImagesAndTextList05" branding="nameAndLogo">
<image id="1" src="'.$image[0].'" alt="alt text"/>
<image id="2" src="'.$image[1].'" alt="alt text"/>
<image id="3" src="'.$image[2].'" alt="alt text"/>
<text id="1">Últimos vídeos</text>
<text id="2">'.$t[0].'</text>
<text id="3">'.$d[0].'</text>
<text id="4">'.$t[1].'</text>
<text id="5">'.$d[1].'</text>
<text id="6">'.$t[2].'</text>
<text id="7">'.$d[2].'</text>
</binding>
</visual>
</tile>');
Header('Content-type: text/xml');
print($tile->asXML());
?>
