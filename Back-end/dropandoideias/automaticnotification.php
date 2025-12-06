<?php
$dbi = mysqli_connect("localhost","","","aloog304_notificacoes");
$dbi -> set_charset("utf8");
$di_tabela = mysqli_query($dbi, "SELECT * FROM blogapp WHERE site='dropandoideias'");
$di_info = mysqli_fetch_array($di_tabela);

$di_json = file_get_contents('https://public-api.wordpress.com/rest/v1/sites/95564318/posts/?number=1');
$di_site = json_decode($di_json);

$di_APPLICATION_ID = "";
$di_REST_API_KEY = "";

$di_id = ''.$di_site->posts[0]->ID;

$checkVars = array("", "...", "…");
if($di_info["thingid"] != $di_id && $di_info["lastid"] != $di_id) {
if($di_id != "") {
$di_titulo = trim(addslashes(html_entity_decode($di_site->posts[0]->title)));
$di_descricao = preg_replace('/<script[^>]+\>(.|\s)*?<\/script>/', '', $di_site->posts[0]->content);
$di_descricao = trim(addslashes(html_entity_decode(strip_tags($di_descricao), 1,"UTF-8")));
$di_descricao = explode("\n", $di_descricao);
for($i = 0; $i < count($di_descricao); $i++) {
	if(!in_array($di_descricao[$i], $checkVars)) {
		$di_desc = trim($di_descricao[$i]);
	}
	if(isset($di_desc)) { break; }
}
if(!isset($di_desc)) { $di_desc = ""; }
$di_d = $di_desc;
$di_link = addslashes($di_site->posts[0]->URL);

$di_data = array(
	'where' => '{}',
	'data' => array(
		'action' => 'net.aloogle.dropandoideias.UPDATE_STATUS',
		'id' => ''.$di_id,
		'tipo' => '0',
		'barra' => $di_titulo.' - Dropando Ideias',
		'titulo' => 'Novo post! - Dropando Ideias',
		'texto' => ''.$di_titulo,
		'titulogrande' => ''.$di_titulo,
		'textogrande' => ''.$di_d,
		'sumario' => 'Dropando Ideias',
		'url' => ''.$di_link,
	),
);

$di__data = json_encode($di_data);

$di_ch = curl_init();

$di_arr = array();
array_push($di_arr, "X-Parse-Application-Id: " . $di_APPLICATION_ID);
array_push($di_arr, "X-Parse-REST-API-Key: " . $di_REST_API_KEY);
array_push($di_arr, "Content-Type: application/json");

curl_setopt($di_ch, CURLOPT_HTTPHEADER, $di_arr);
curl_setopt($di_ch, CURLOPT_URL, 'https://api.parse.com/1/push');
curl_setopt($di_ch, CURLOPT_POST, true);
curl_setopt($di_ch, CURLOPT_POSTFIELDS, $di__data);

curl_exec($di_ch);
curl_close($di_ch);

$lastid = $di_info["thingid"];
mysqli_query($dbi, "UPDATE blogapp SET thingid='$di_id', lastid='$lastid' WHERE site='dropandoideias'");
}}
?>
<?
$diy_tabela = mysqli_query($dbi, "SELECT * FROM blogapp WHERE site='dropandoideiasyoutube'");
$diy_info = mysqli_fetch_array($diy_tabela);

$diy_json = file_get_contents('https://www.googleapis.com/youtube/v3/playlistItems?playlistId=UUJW-3SY48ok-9_uonxzKe0A&key=&part=snippet&maxResults=1');
$diy_site = json_decode($diy_json);

$diy_APPLICATION_ID = "";
$diy_REST_API_KEY = "";

$diy_titulo = addslashes($diy_site->items[0]->snippet->title);
$diy_descricao = addslashes(html_entity_decode(trim(strip_tags($diy_site->items[0]->snippet->description)), 1,"UTF-8"));
$diy_id = $diy_site->items[0]->snippet->resourceId->videoId;

if($diy_id != $diy_info['thingid'] && $diy_id != $diy_info['lastid']) {
if($diy_id != "") {
$diy_data = array(
	'where' => '{}',
	'data' => array(
		'action' => 'net.aloogle.dropandoideias.UPDATE_STATUS',
		'id' => $diy_id,
		'tipo' => '2',
		'barra' => $diy_titulo.' - Dropando Ideias',
		'titulo' => 'Novo vídeo! - Dropando Ideias',
		'texto' => ''.$diy_titulo,
		'titulogrande' => ''.$diy_titulo,
		'textogrande' => ''.$diy_descricao,
		'sumario' => 'Dropando Ideias',
		'url' => 'http://youtube.com/watch?v='.$diy_id,
	),
);

$diy__data = json_encode($diy_data);

$diy_ch = curl_init();

$diy_arr = array();
array_push($diy_arr, "X-Parse-Application-Id: " . $diy_APPLICATION_ID);
array_push($diy_arr, "X-Parse-REST-API-Key: " . $diy_REST_API_KEY);
array_push($diy_arr, "Content-Type: application/json");

curl_setopt($diy_ch, CURLOPT_HTTPHEADER, $diy_arr);
curl_setopt($diy_ch, CURLOPT_URL, 'https://api.parse.com/1/push');
curl_setopt($diy_ch, CURLOPT_POST, true);
curl_setopt($diy_ch, CURLOPT_POSTFIELDS, $diy__data);

curl_exec($diy_ch);
curl_close($diy_ch);

$lastid = $diy_info['thingid'];
mysqli_query($dbi, "UPDATE blogapp SET thingid='$diy_id', lastid='$lastid' WHERE site='dropandoideiasyoutube'");
	}
}
?>
