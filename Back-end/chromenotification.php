<?php
if($_GET['site'] == "acasadocogumelo" || $_GET['site'] == "zeldacombr") {
$dbi = mysqli_connect("localhost","","","aloog304_notificacoes");
$dbi -> set_charset("utf8");
if($_GET['site'] == "acasadocogumelo") {
$tabela = mysqli_query($dbi, "SELECT * FROM blogappchrome WHERE site='acasadocogumelo'"); }
if($_GET['site'] == "zeldacombr") {
$tabela = mysqli_query($dbi, "SELECT * FROM blogappchrome WHERE site='zeldacombr'"); }
$info = mysqli_fetch_array($tabela);

$titulo = addcslashes($info['titulo'], '"');
$descricao = addcslashes($info['descricao'], '"');

if($_GET['list'] == "true") {
	echo $info['titulos'];
} else {
	echo '{"titulo" : "'.$titulo.'",';
	echo '"descricao" : "'.$descricao.'",';
	echo '"link" : "'.$info['link'].'",';
	echo '"imagem" : "'.$info['imagem'].'"}';
}}
?>
