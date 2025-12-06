<?php
$dbi = mysqli_connect("localhost","","","");
$dbi -> set_charset("utf8");
$tabela = mysqli_query($dbi, "SELECT * FROM blogappchrome WHERE site='acasadocogumelo'");
$info = mysqli_fetch_array($tabela);

$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts?key=');
$site = json_decode($json);

$titulo = addslashes($site->items[0]->title);
$descricao = substr(addslashes(html_entity_decode(trim(preg_replace('/\s+/', ' ', strip_tags($site->items[0]->content))), 1,"UTF-8")), 0, 350).'...';
$link = addslashes($site->items[0]->url);

preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->items[0]->content, $urls);
$urls = $urls[1];
$imagem = addslashes(str_replace('s1600', 's72-c', $urls[0]));
$id = $site->items[0]->id;
$titulos = '[{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $site->items[0]->title))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $site->items[1]->title))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $site->items[2]->title))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $site->items[3]->title))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $site->items[4]->title))).'"}]';
if($info['id'] != $id) {
	mysqli_query($dbi, "UPDATE blogappchrome SET titulo='$titulo',descricao='$descricao',link='$link',imagem='$imagem',id='$id',titulos='$titulos' WHERE site='acasadocogumelo'");
}
?>
<?php
$hl_tabela = mysqli_query($dbi, "SELECT * FROM blogappchrome WHERE site='zeldacombr'");
$hl_info = mysqli_fetch_array($hl_tabela);

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);
$hl_contagem = 0;
foreach($hl_rss->channel->item as $hl_entrada) {
	$hl_title[$hl_contagem] = $hl_entrada->title;
	$hl_description[$hl_contagem] = $hl_entrada->description;
	$hl_link[$hl_contagem] = $hl_entrada->link;
	$hl_contagem = $hl_contagem + 1;
}

$hl_titulo = addslashes(trim(preg_replace('/\s+/', ' ', $hl_title[0])));
$hl_descricao = trim(strip_tags(preg_replace('/\s+/', ' ', $hl_description[0])));
$zelda = $hl_link[0];
preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $hl_description[0], $hl_urls);
$hl_urls = $hl_urls[1];
$hl_imagem = addslashes(str_replace('medium', 'thumbnail', $hl_urls[0]));
$hl_titulos = '[{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $hl_title[0]))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $hl_title[1]))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $hl_title[2]))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $hl_title[3]))).'"},{"titulo" : "'.addslashes(trim(preg_replace('/\s+/', ' ', $hl_title[4]))).'"}]';

if($hl_info['titulo'] != $hl_titulo) {
	mysqli_query($dbi, "UPDATE blogappchrome SET titulo='$hl_titulo',descricao='$hl_descricao',link='$zelda',imagem='$hl_imagem',id='$hl_titulo',titulos='$hl_titulos' WHERE site='zeldacombr'");
}
?>
