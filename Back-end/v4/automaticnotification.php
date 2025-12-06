<?php
include("connect_db.php");

$tabela = mysqli_query($dbi, "SELECT * FROM login") or die ("ERROR: -1".mysql_error());

while($row = mysqli_fetch_array($tabela)) {
  $blogappid = $row["id"];
  $tipo = $row["tipo"];
  $apiurl = $row["apiurl"];
  
  $notifsql = mysqli_query($dbi, "SELECT * FROM notifications WHERE blogappid=$blogappid") or die ("ERROR: -1".mysql_error());
  $notifinfo = mysqli_fetch_array($notifsql);
  
  $json = file_get_contents($apiurl);
  $site = json_decode($json);
  
  $checkVars = array("", "...", "…");
  
  if($tipo == "blogger") {
    $id = $site->items[0]->id;

    if($notifinfo["thingid"] != $id) {
      if($id != "") {
        $title = addslashes($site->items[0]->title);
        $description = addslashes(html_entity_decode(trim(strip_tags($site->items[0]->content)), 1,"UTF-8"));
        $description = explode("\n", $description);
        
        if(trim($descricao[0]) != "") {
          $desc = trim($descricao[0]);
        } else if(trim($descricao[1]) != "") {
          $desc = trim($descricao[1]);
        } else if(trim($descricao[2]) != "") {
          $desc = trim($descricao[2]);
        } else if(trim($descricao[3]) != "") {
          $desc = trim($descricao[3]);
        }
        
        $description = $desc;
        
        if(!isset($desc)) { $desc = ""; }
        $description = substr($desc, 0, -1);
        $link = addslashes($site->items[0]->url);
        preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->items[0]->content, $urls);
        $urls = $urls[1];
        $image = addslashes($urls[0]);
      } else {
        continue;
      }
    } else {
      continue;
    }
  } else if($tipo == "wordpress") {
    $id = ''.$site->posts[0]->ID;
    
    if($notifinfo["thingid"] != $id && $notifinfo["lastid"] != $id) {
      if($id != "") {
        $title = trim(addslashes(html_entity_decode($site->posts[0]->title)));
        $description = preg_replace('/<script[^>]+\>(.|\s)*?<\/script>/', '', $site->posts[0]->content);
        $description = trim(addslashes(html_entity_decode(strip_tags($description), 1,"UTF-8")));
        $link = addslashes($site->posts[0]->URL);
      } else {
        continue;
      }
    } else {
      continue;
    }
  } else if($tipo == "xml") {
    $feed = file_get_contents($apiurl);
    $rss = new SimpleXmlElement($feed);

    $title = $rss->channel->item[0]->title;
    $link = $rss->channel->item[0]->link;

    $title = trim(preg_replace('/\s+/', ' ', $title));
    echo $notifinfo["thingid"]."<br>";
    
    if($notifinfo["thingid"] != $title) {
      if($title != "") {
        $site_html= file_get_contents($link);
        $matches=null;
        preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+name="(description)"\s+content="([^"]*)~i', $site_html, $matches);
        $image = $matches[2][0];
        $description = html_entity_decode($matches[4][1], ENT_QUOTES, "UTF-8");
        $id = str_replace("'", "\'", $title);
      } else {
        continue;
      }
    } else {
      continue;
    }
  }

  /*
  if(strpos($row['platforms'], 'android') !== false) {
    $data = array(
      'where' => '{ "deviceType": "android" }',
      'data' => array(
        'action' => ''.$row["action"],
        'id' => ''.$id,
        'tipo' => '0',
        'barra' => $title.' - '.$row["nome"],
        'titulo' => ''.$row["nome"],
        'texto' => ''.$title,
        'titulogrande' => ''.$title,
        'textogrande' => $description.'.',
        'sumario' => ''.$row["nome"],
        'url' => ''.$link,
        'imagem' => ''.$image,
      ),
    ); 

    $_data = json_encode($data);

    $ch = curl_init();

    $arr = array();
    array_push($arr, "X-Parse-Application-Id: " . $APPLICATION_ID);
    array_push($arr, "X-Parse-REST-API-Key: " . $REST_API_KEY);
    array_push($arr, "Content-Type: application/json");

    curl_setopt($ch, CURLOPT_HTTPHEADER, $arr);
    curl_setopt($ch, CURLOPT_URL, 'https://api.parse.com/1/push');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $_data);

    curl_exec($ch);
    curl_close($ch);
  }
  */
  
  if(strpos($row['platforms'], 'windows') !== false) {
    include("notifications/windows/send.php");
  }

  mysqli_query($dbi, "UPDATE notifications SET thingid='$id', lastid='$lastid' WHERE blogappid='$blogappid'");
}
?>