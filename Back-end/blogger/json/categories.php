<?
include('connect_db.php');
$id = $_GET['id'];

$icon = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='$id' AND oque='categoryicon'") or die ("ERROR: ".mysql_error());
$infoicon = mysqli_fetch_array($icon);
if($infoicon['value'] != "") {
	$valueicon = $infoicon['value'];
}

$page = $_GET['page'];
if(!isset($page)) {
$links = mysqli_query($dbi, "SELECT * FROM lateral_links WHERE userid='$id'") or die ("ERROR: ".mysql_error());
$infolinks = mysqli_fetch_array($links);

$categories = mysqli_query($dbi, "SELECT * FROM lateral_categorias WHERE userid='$id'") or die ("ERROR: ".mysql_error());
$infocategories = mysqli_fetch_array($categories);
if($infolinks['json'] == "") { $links = "{ \"name\": \"Links\", \"links\": [] }"; } else { $links = $infolinks['json']; }
if($infocategories['json'] == "") { $categories = "{ \"name\": \"Categorias principais\", \"featuredcategories\": [] }"; } else { $categories = $infocategories['json']; }
$config = "\"links\": ".$links.", \"featuredcategories\": ".$categories;
} else {
	$page = '&page='.$_GET['page'];
}

echo "{ ".$config." }"
?>