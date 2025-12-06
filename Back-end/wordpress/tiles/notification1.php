<?php
$id = $_GET['id'];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/'.$id.'/posts');
$site = json_decode($json);

$contagem = 0;
foreach($site->posts as $post) {
	$title[$contagem] = $post->title;
	$link[$contagem] = $post->URL;
	$contagem = $contagem + 1;
}

for($i = 0; $i < 3; $i ++) {
	$t[$i] = $title[$i];
	$tags = get_meta_tags($link[$i]);
	$d[$i] = $tags['twitter:description'];
	if($d[$i] == "") {
		$d[$i] = " ";
	}
}

$tile = new SimpleXMLElement('<tile>
<visual lang="pt-BR" version="2">
<binding template="TileSquare150x150Text02" fallback="TileSquareBlock" branding="logo">
<text id="1">'.$t[0].'</text>
<text id="2">'.$d[0].'</text>
</binding>
<binding template="TileWide310x150Text09" branding="nameAndLogo">
<text id="1">'.$t[0].'</text>
<text id="2">'.$d[0].'</text>
</binding>
<binding template="TileSquare310x310TextList03" branding="nameAndLogo">
<text id="1">'.$t[0].'</text>
<text id="2">'.$d[0].'</text>
<text id="3">'.$t[1].'</text>
<text id="4">'.$d[1].'</text>
<text id="5">'.$t[2].'</text>
<text id="6">'.$d[2].'</text>
</binding>
</visual>
</tile>');
Header('Content-type: text/xml');
print($tile->asXML());
?>