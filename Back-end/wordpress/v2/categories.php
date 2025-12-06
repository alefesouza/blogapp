<?
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];
$cache = "cache/".$blogappid."/categories.json";

if(!file_exists($cache) || $_GET["action"] == "update" || isset($_GET["page"])) {
include('connect_db.php');

$page = $_GET['page'];

$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

if(!isset($page)) {
$lateral = mysqli_query($dbi, "SELECT * FROM lateral WHERE blogappid=$blogappid") or die ("ERROR: ".mysql_error());

$links = array("name" => "Links", "links" => array());
$fcategorias = array("name" => "Categorias principais", "fcategories" => array());

while($row = mysqli_fetch_array($lateral)) {
	$title = $row["title"];
	$icon = $row["icon"];
	$value = $row["value"];
	
	if($row["tipo"] == "0") {
		$links["links"][] = array("title" => $title, "icon" => $icon, "value" => $value);
	} else {
		$fcategorias["fcategories"][] = array("title" => $title, "icon" => $icon, "value" => $value);
	}
}
} else {
	$page = '&page='.$_GET['page'];
}

$json = file_get_contents('https://public-api.wordpress.com/rest/v1/sites/'.$blogid.'/categories/?number=15'.$page);
$site = json_decode($json);

$categorias = $site->categories;
$categoriastotal = $site->found;
$received = count($categorias);
if(!isset($_GET['page'])) { $pagecount = 1; } else { $pagecount = $_GET['page']; }
$more = $categoriastotal - (($pagecount - 1) * 15) - $received;

$jsonfinish = array("links" => $links, "fcategories" => $fcategorias, "categories" => array("total" => $categoriastotal, "received" => $received, "more" => $more, "categories" => array()));

foreach($categorias as $categoria) {
	$idc = $categoria->slug;
	$name = $categoria->name;
	
	$jsonfinish["categories"]["categories"][] = array("title" => $name, "icon" => "", "value" => $idc);
}

if(!isset($_GET["page"])) {
file_force_contents($cache, json_encode($jsonfinish));
}

echo json_encode($jsonfinish);
} else {
echo file_get_contents($cache);
}
?>