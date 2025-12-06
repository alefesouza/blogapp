<?
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];

include('connect_db.php');

$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

$q = $_GET['q'];
$number = strlen($q);

$json = file_get_contents('https://public-api.wordpress.com/rest/v1/sites/'.$blogid.'/tags?number=1000&fields=ID,name&search='.$q);
$site = json_decode($json);

$categoria = $site->tags;
foreach($categoria as $categ) {
	if(0 === stripos($categ->name, $q)) {
		$categorias[] = $categ->name;
	}
}

$tags = array("tags" => array());

if(count($categorias) > 0) {
	foreach($categorias as $tag) {
		$tags["tags"][] = array("tag" => $tag);
	}
}
echo json_encode($tags);
?>