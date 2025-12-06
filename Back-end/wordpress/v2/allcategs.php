<?
$blogappid = $_GET["blogappid"];
$platform = $_GET["platform"];

include('connect_db.php');

$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1/sites/'.$blogid.'/categories/?number=100'.$page);
$site = json_decode($json);

$categorias = $site->categories;
$categoriasstotal = $site->found;

$categories = array("categories" => array("total" => $categoriasstotal, "categories" => array()));

if(count($categorias) > 0) {
	foreach($categorias as $categoria) {
		$categories["categories"]["categories"][] = array("title" => $categoria->name, "icon" => $categoria->slug, "icon" => "");
	}
}

echo json_encode($categories);
?>