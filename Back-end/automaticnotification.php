<?php
$dbi = mysqli_connect("localhost","","","");
$dbi -> set_charset("utf8");
$tabela = mysqli_query($dbi, "SELECT * FROM blogapp WHERE site='acasadocogumelo'");
$info = mysqli_fetch_array($tabela);

$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts?key=');
$site = json_decode($json);

$APPLICATION_ID = "";
$REST_API_KEY = "";

$id = $site->items[0]->id;

$checkVars = array("", "...", "…");
if($info["thingid"] != $id) {
if($id != "") {
$titulo = addslashes($site->items[0]->title);
$descricao = addslashes(html_entity_decode(trim(strip_tags($site->items[0]->content)), 1,"UTF-8"));
$descricao = explode("\n", $descricao);
if(trim($descricao[0]) != "") {
	$desc = trim($descricao[0]);
} else if(trim($descricao[1]) != "") {
	$desc = trim($descricao[1]);
} else if(trim($descricao[2]) != "") {
	$desc = trim($descricao[2]);
} else if(trim($descricao[3]) != "") {
	$desc = trim($descricao[3]);
}
$d = $desc;
if(!isset($desc)) { $desc = ""; }
$d = substr($desc, 0, -1);
$link = addslashes($site->items[0]->url);
preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->items[0]->content, $urls);
$urls = $urls[1];
$imagem = addslashes($urls[0]);

$data = array(
	'where' => '{}',
	'data' => array(
		'action' => 'com.acasadocogumelo.cogumelonoticias.UPDATE_STATUS',
        'id' => ''.$id,
		'tipo' => '0',
		'barra' => $titulo.' - Cogumelo Notícias',
		'titulo' => 'Cogumelo Notícias',
		'texto' => ''.$titulo,
		'titulogrande' => ''.$titulo,
		'textogrande' => $d.'.',
		'sumario' => 'Cogumelo Notícias',
		'url' => ''.$link,
		'imagem' => ''.$imagem,
	),
);

$_data = json_encode($data);

$ch = curl_init();

$arr = array();
array_push($arr, "X-Parse-Application-Id: " . $APPLICATION_ID);
array_push($arr, "X-Parse-REST-API-Key: " . $REST_API_KEY);
array_push($arr, "Content-Type: application/json");

curl_setopt($ch, CURLOPT_HTTPHEADER, $arr);
curl_setopt($ch, CURLOPT_URL, 'https://api.parse.com/1/push');
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $_data);

curl_exec($ch);
curl_close($ch);

mysqli_query($dbi, "UPDATE blogapp SET thingid='$id' WHERE site='acasadocogumelo'");
}}
?>
<?
$hl_tabela = mysqli_query($dbi, "SELECT * FROM blogapp WHERE site='zeldacombr'");
$hl_info = mysqli_fetch_array($hl_tabela);

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);

$hl_title = $hl_rss->channel->item[0]->title;
$hl_link = $hl_rss->channel->item[0]->link;

$hl_APPLICATION_ID = "";
$hl_REST_API_KEY = "";

$hl_titulo = $hl_title;
$hl_titulo = trim(preg_replace('/\s+/', ' ', $hl_titulo));

if($hl_info["thingid"] != $hl_titulo) {
if($hl_titulo != "") {
$site_html= file_get_contents($hl_link);
$matches=null;
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+name="(description)"\s+content="([^"]*)~i', $site_html, $matches);
$imagem = $matches[2][0];
$description = html_entity_decode($matches[4][1], ENT_QUOTES, "UTF-8");
$hl_modif_title = str_replace("'", "\'", $hl_titulo);

$hl_data = array(
	'where' => '{ "deviceType": "android" }',
	'data' => array(
		'action' => 'net.aloogle.zeldacombr.UPDATE_STATUS',
		'barra' => $hl_titulo.' - Hyrule Legends',
		'titulo' => 'Hyrule Legends',
		'texto' => ''.$hl_titulo,
		'titulogrande' => ''.$hl_titulo,
		'textogrande' => ''.$description,
		'sumario' => 'Hyrule Legends',
		'url' => ''.$hl_link,
	),
);

$hl__data = json_encode($hl_data);

$hl_ch = curl_init();

$hl_arr = array();
array_push($hl_arr, "X-Parse-Application-Id: " . $hl_APPLICATION_ID);
array_push($hl_arr, "X-Parse-REST-API-Key: " . $hl_REST_API_KEY);
array_push($hl_arr, "Content-Type: application/json");

curl_setopt($hl_ch, CURLOPT_HTTPHEADER, $hl_arr);
curl_setopt($hl_ch, CURLOPT_URL, 'https://api.parse.com/1/push');
curl_setopt($hl_ch, CURLOPT_POST, true);
curl_setopt($hl_ch, CURLOPT_POSTFIELDS, $hl__data);

curl_exec($hl_ch);
curl_close($hl_ch);

$hl_data = array(
	'where' => '{ "deviceType": { "$in": [ "winrt", "winphone" ] } }',
	'data' => array(
		'title' => ''.$hl_titulo,
		'alert' => ''.$description,
	),
);

$hl__data = json_encode($hl_data);

$hl_ch = curl_init();

$hl_arr = array();
array_push($hl_arr, "X-Parse-Application-Id: " . $hl_APPLICATION_ID);
array_push($hl_arr, "X-Parse-REST-API-Key: " . $hl_REST_API_KEY);
array_push($hl_arr, "Content-Type: application/json");

curl_setopt($hl_ch, CURLOPT_HTTPHEADER, $hl_arr);
curl_setopt($hl_ch, CURLOPT_URL, 'https://api.parse.com/1/push');
curl_setopt($hl_ch, CURLOPT_POST, true);
curl_setopt($hl_ch, CURLOPT_POSTFIELDS, $hl__data);

curl_exec($hl_ch);
curl_close($hl_ch);

if(isset($_POST['agoraindo'])) {
include("zeldacombr/notifications/create_image.php");

$url_arr = explode('/', $imagem);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('zeldacombr/notifications/images/'.$name)) {
  resizeImage($imagem);
}

$imagem = 'http://apps.aloogle.net/blogapp/zeldacombr/notifications/images/'.$name;

$xml = base64_encode('<?xml version="1.0" encoding="utf-8"?><toast activationType="foreground" scenario="reminder" duration="long"><visual><binding template="ToastGeneric"><text>'.$hl_title.'</text><text>'.$description.'</text><image placement="inline" src="'.$imagem.'" /></binding></visual></toast>');
}

mysqli_query($dbi, "UPDATE blogapp SET thingid='$hl_modif_title' WHERE site='zeldacombr'");
	}
}
?>
