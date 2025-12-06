<? 
$json = file_get_contents('http://'.$_GET['domain'].'/feeds/posts/summary?alt=json');
$site = json_decode($json);

$categorias = $site->feed->category;

echo "{ \"categories\": { \"total\": ".count($categorias).", \"categories\": [";

for($i=0; $i < count($categorias); $i++) {
	echo "{ \"name\": \"".$categorias[$i]->term."\", \"id\": \"".rawurlencode($categorias[$i]->term)."\", \"icon\": \"\" }";
	
	if($i<count($categorias) - 1) {
		echo",";
	}
}

echo "] } }"
?>