<?php
include("create_image.php");

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);

$hl_title = $hl_rss->channel->item[0]->title;
$hl_link = $hl_rss->channel->item[0]->link;

$site_html= file_get_contents($hl_link);
$matches=null;
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+name="(description)"\s+content="([^"]*)~i', $site_html, $matches);
$imagem = $matches[2][0];
$description = html_entity_decode($matches[4][1], ENT_QUOTES, "UTF-8");

$url_arr = explode('/', $imagem);
$ct = count($url_arr);
$name = $url_arr[$ct-1];
if (!file_exists('images/'.$name)) {
  resizeImage($imagem);
}

$imagem = 'http://apps.aloogle.net/blogapp/zeldacombr/notifications/images/'.$name;

define('PW_AUTH', 'mJ4t946HSXf0FOPo6VxWJS6d8BKl4kYlyMMlL0yWEFWnCWPBbxncJlQ2tkQh3yPjrM2dMnMAzl3Iz2RQcaJa');
define('PW_APPLICATION', '3463D-1D17E');

$url = 'https://cp.pushwoosh.com/json/1.3/createMessage';

$xml = base64_encode('<?xml version="1.0" encoding="utf-8"?><toast activationType="foreground" scenario="reminder" duration="long"><visual><binding template="ToastGeneric"><text>'.$hl_title.'</text><text>'.$description.'</text><image placement="inline" src="'.$imagem.'" /></binding></visual></toast>');

$data = array(
                  'application' => PW_APPLICATION,
                  'auth' => PW_AUTH,
                  'notifications' => array(
                          array(
                              'send_date' => 'now',
                              'ignore_user_timezone' => true,
                              'wns_content' => array( 'en' => $xml ),
                              'wns_type' => 'Toast', // 'Tile' | 'Toast' | 'Badge' | 'Raw'
                              'wns_tag' => 'myTag',
                          )
                  ),
          );

$request = json_encode(['request' => $data], JSON_HEX_QUOT | JSON_HEX_TAG);

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
curl_setopt($ch, CURLOPT_ENCODING, 'gzip, deflate');
curl_setopt($ch, CURLOPT_HEADER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $request);

$response = curl_exec($ch);
$info = curl_getinfo($ch);
curl_close($ch);
?>