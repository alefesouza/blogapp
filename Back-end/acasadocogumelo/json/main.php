<?
if(isset($_GET['token']) && $_GET['token'] != "") {
	$token = '&pageToken='.$_GET['token'];
}

if(isset($_GET['search']) && $_GET['search'] != "") {
	$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts/search?key=&q='.urlencode($_GET['search']).$token);
} else if(isset($_GET['label']) && $_GET['label'] != "") {
	$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts?key=&labels='.urlencode($_GET['label']).$token);
} else {
	$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts?key='.$token);
}
$site = json_decode($json);

echo "{ \"token\" : \"".$site->nextPageToken."\", \"noticias\": [";

for($i=0; $i < count($site->items); $i++) {
$titulo = addslashes($site->items[$i]->title);
$link = addslashes($posts->url);
$id = $site->items[$i]->id;
$descricao = addslashes(html_entity_decode(trim(strip_tags($site->items[$i]->content)), 1,"UTF-8"));
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
preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->items[$i]->content, $urls);
$urls = $urls[1];
$imagem = addslashes($urls[0]);
$url = $site->items[$i]->url;
	echo "{ \"id\": \"".$id."\", \"titulo\": \"".$titulo."\", \"descricao\": \"".$d."\", \"imagem\": \"".$imagem."\", \"url\": \"".$url."\" }";
	if($i < count($site->items) - 1) {
		echo",";
	}
}

	echo "] }";
?>
