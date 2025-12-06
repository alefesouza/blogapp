<?
$json = file_get_contents('https://www.googleapis.com/youtube/v3/channels?id='.$_GET['id'].'&key=&part=snippet,brandingSettings');
$site = json_decode($json);

$items = $site->items;

echo "{ \"thumbnails\": [ \"".$items[0]->snippet->thumbnails->default->url."\", \"".$items[0]->snippet->thumbnails->medium->url."\", \"".$items[0]->snippet->thumbnails->high->url."\" ], \"images\": [ \"".$items[0]->brandingSettings->image->bannerMobileLowImageUrl."\", \"".$items[0]->brandingSettings->image->bannerMobileMediumHdImageUrl."\", \"".$items[0]->brandingSettings->image->bannerMobileHdImageUrl."\", \"".$items[0]->brandingSettings->image->bannerMobileExtraHdImageUrl."\" ] }";

?>
