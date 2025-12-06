<?
if(isset($_GET['token'])) {
	$token = '&pageToken='.$_GET['token'];
} else { $token = ''; }
$json = file_get_contents('https://www.googleapis.com/youtube/v3/search?channelId='.$_GET['id'].'&key=&part=snippet&maxResults=10&type=video&q='.$_GET['q'].$token);
$site = json_decode($json);

$items = $site->items;

echo "{ \"token\": \"".$site->nextPageToken."\", \"videos\": [ ";
for($i = 0; $i < count($items); $i++) {
$jsonv = file_get_contents('https://www.googleapis.com/youtube/v3/videos?id='.$items[$i]->id->videoId.'&key=&part=statistics');
$sitev = json_decode($jsonv);
	echo "{ \"id\": \"".$items[$i]->id->videoId."\", \"titulo\": \"".$items[$i]->snippet->title."\", \"likes\": \"".$sitev->items[0]->statistics->likeCount."\", \"visualizacoes\": \"".$sitev->items[0]->statistics->viewCount."\" }";
	if($i < count($items) - 1) { echo ", "; }
}
echo " ] }";
?>
