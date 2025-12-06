<?
include("connect_db.php");

echo "{ \"buttons\": [ ";

if($_GET["platform"] == "windows" && $_GET["build"] == "wp") {
  $titles = array("site", "artigos", "notícias", "podcasts", "a série", "spin-offs", "detonados", "traduções", "mídia");
  $links = array("site", "artigos", "noticias", "podcast", "jogos", "spinoffs", "detonados", "traducoes", "midia");
  
  for($i = 0; $i < count($titles); $i++) { ?>

{ "title": <? echo json_encode($titles[$i]); ?>, "link": <? echo json_encode($links[$i]); ?> }<? if($i < count($titles) - 1) { echo ", "; }
                    }
} else if($_GET["platform"] == "windows" && version_compare($_GET["build"], '10', '>=')) {
  $titles = array("Site", "Artigos", "Notícias", "Podcasts", "A série", "Spin-offs", "Detonados", "Traduções", "Mídia", "Facebook", "Twitter", "YouTube");
  
  if(version_compare($_GET["version"], '1.9.3.0', '>=')) {
  $icons = array("Home", "Page", "Copy", "Microphone", "ms-appx:///Assets/Images/triforce.png", "GoToStart", "Mappin", "Character", "Play", "ms-appx:///Assets/Images/facebook.png", "ms-appx:///Assets/Images/twitter.png", "ms-appx:///Assets/Images/youtube.png");
  } else {
  $icons = array("Home", "Page", "Copy", "Microphone", "ms-appx:///Assets/Images/triforce.png", "GoToStart", "Mappin", "Character", "Play", "World", "World", "World");
  }

  $links = array("http://zelda.com.br", "http://zelda.com.br/artigos", "http://zelda.com.br/noticias", "http://zelda.com.br/podcast", "http://zelda.com.br/jogos", "http://zelda.com.br/spinoffs", "http://zelda.com.br/detonados", "http://zelda.com.br/traducoes", "http://zelda.com.br/midia", "http://facebook.com/hyrulelegends", "http://twitter.com/hyrulelegends", "http://youtube.com/user/hyrulelegends");
  
  for($i = 0; $i < count($titles); $i++) { ?>
{ "title": <? echo json_encode($titles[$i]); ?>,

<? if(strpos($icons[$i], "ms-appx:///") !== false || strpos($icons[$i], "http://") !== false) { $icon = "Holder"; $image = $icons[$i]; } else { $icon = $icons[$i]; $image = "ms-appx:///Assets/Images/Triforce.png"; } ?>
"icon": <? echo json_encode($icon); ?>, "link": <? echo json_encode($links[$i]); ?>, "image": <? echo json_encode($image); ?> }<? if($i < count($titles) - 1) { echo ", "; }
                                         }
}

echo " ] }";
?>