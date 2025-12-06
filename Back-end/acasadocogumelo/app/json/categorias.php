<? 
$json = file_get_contents('http://www.acasadocogumelo.com/feeds/posts/summary?alt=json');
$site = json_decode($json);

$categorias = $site->feed->category;

echo "{ \"categorias\": [";

for($i=0; $i < count($categorias); $i++) {
	echo "{ \"categoria\": \"".$categorias[$i]->term."\" }";
	
	if($i<count($categorias) - 1) {
		echo",";
	}
}

echo "]}"
?>