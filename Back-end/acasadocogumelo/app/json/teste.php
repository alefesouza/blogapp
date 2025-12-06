<?
$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts/'.$_GET['id'].'?key=');
$site = json_decode($json);

$id = $site->id;
$titulo = trim(addslashes($site->title));
$descricao = addslashes(html_entity_decode(trim(strip_tags($site->content)), 1,"UTF-8"));
$descricao = explode("\n", $descricao);
if(trim($descricao[0]) != "") {
	$desc = trim($descricao[0]);
} else if(trim($descricao[1])) {
	$desc = trim($descricao[1]);
} else if(trim($descricao[2])) {
	$desc = trim($descricao[2]);
} else if(trim($descricao[3])) {
	$desc = trim($descricao[3]);
}
$desc = explode(".", $desc);
if(strlen($desc[0]) > 20) {
	$d = $desc[0];
} else {
	$d = $desc[0].$desc[1];
}
preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->content, $urls);
$urls = $urls[1];
$imagem = addslashes($urls[0]);
$url = $site->url;
$autor = $site->author->displayName;
$data = str_replace("T", " ", substr($site->published, 0, -6));
$data2 = date_parse($site->published);
include("month.php");
$descricao = html_entity_decode(trim($site->content), 1,"UTF-8");
$descricao = preg_split( '#<img (.*?)>|<iframe (.*?)></iframe>#s', $descricao);
preg_match_all('~<img.*?src=["\']+(.*?)["\']+|<iframe.*?src=["\']+(.*?)["\']+~', html_entity_decode(trim(str_replace('"', '\'', $site->content)), 1,"UTF-8"), $urls);
echo "{ \"titulo\": \"".$titulo."\", \"descricao\": \"".$d."\", \"url\": \"".$url."\", \"imagem\": \"".$imagem."\", \"autor\": \"".$autor."\", \"data\": \"".$data."\", \"data2\": \"".$data2["day"]." ".$month." ".$data2["year"]."\", ";
	echo "\"textos\": [";
	for($i=0; $i < count($descricao); $i++) {
		if(ctype_space(strip_tags($descricao[$i]))) {
			$descricao[$i] = "";
		}
		echo "{ \"texto\": ".str_replace('<\/a><\/div>\n<div style=\"text-align: center;\">\n<br \/><\/div>\n', '', json_encode($descricao[$i]))." }";
		if($i<count($descricao) - 1) {
			echo",";
		}
	}
	echo "] ,";
	echo "\"imagens\": [";
	for($i=0; $i < count($urls[1]); $i++) {
		echo "{ \"imagem\": \"".$urls[1][$i]."\" }";
	if($i<count($urls[1]) - 1) {
		echo",";
	}
	}
	echo "] ,";
	echo "\"iframes\": [";
	for($i=0; $i < count($urls[2]); $i++) {
		if($urls[2][$i] != "") {
		if(substr($urls[2][$i], 0, 4) != "http") {
			$urls[2][$i] = "http:".$urls[2][$i];
		}}
		echo "{ \"iframe\": \"".$urls[2][$i]."\" }";
	if($i<count($urls[2]) - 1) {
		echo",";
	}
	}
	echo "] ,";
	echo "\"tags\": [";
	for($i=0; $i < count($site->labels); $i++) {
		echo "{ \"tag\": \"".$site->labels[$i]."\" }";
	if($i<count($site->labels) - 1) {
		echo",";
	}
	}
	echo "] }";
?>
