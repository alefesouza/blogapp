<?
include('connect_db.php');
$id = $_GET['id'];
$pageToken = $_GET['token'];
if(!isset($pageToken)) {
$links = mysqli_query($dbi, "SELECT * FROM lateral_links WHERE userid='$id'") or die ("ERROR: ".mysql_error());
$infolinks = mysqli_fetch_array($links);

$playlists = mysqli_query($dbi, "SELECT * FROM lateral_categorias WHERE userid='$id'") or die ("ERROR: ".mysql_error());
$infoplaylists = mysqli_fetch_array($playlists);
if($infolinks['json'] == "") { $links = "{ \"links\": [] }"; } else { $links = $infolinks['json']; }
if($infoplaylists['json'] == "") { $playlists = "{ \"playlists\": [] }"; } else { $playlists = $infoplaylists['json']; }
$config = ", \"links\": ".$links.", \"featuredplaylists\": ".$playlists;
} else {
	$token = '&pageToken='.$_GET['token'];
}
$json = file_get_contents('https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId='.$id.'&maxResults=25&key='.$token);
$site = json_decode($json);

$items = $site->items;
$token = $site->nextPageToken;
$playliststotal = $site->pageInfo->totalResults;

echo "{ \"token\": \"".$token."\"".$config.", \"playlists\": { \"total\": ".$playliststotal.", \"playlists\": [";
for($i = 0; $i < count($items); $i++) {
	echo "{ \"id\": \"".$items[$i]->id."\", \"titulo\": \"".$items[$i]->snippet->title."\" }";
	if($i < count($items) - 1) { echo ", "; }
}
echo "] } }";
?>
