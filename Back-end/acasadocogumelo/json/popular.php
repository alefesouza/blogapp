<?php
$dbi = mysqli_connect("localhost","","","aloog304_blogapp");
$dbi -> set_charset("utf8");

if (mysqli_connect_errno()) {
  echo "Não foi possível conectar a base de dados: " . mysqli_connect_error();
}
echo "{ \"token\" : \"\", \"noticias\": [";
$contagem = 0;
$result = mysqli_query($dbi, "SELECT * FROM acasadocogumelo");
while ($row = mysqli_fetch_array($result)) {
	echo "{ \"id\": \"".$row['postid']."\", \"titulo\": \"".$row['titulo']."\", \"descricao\": \"".$row['descricao']."\", \"imagem\": \"".$row['imagem']."\", \"url\": \"".$row['url']."\", \"data\": \"".$row['data']."\" }";
	$contagem = $contagem + 1;
	if($contagem < 7) {
		echo",";
	}
}
echo "] }";
?>
