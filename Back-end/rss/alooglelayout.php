<?
include("check.php");

header('Content-Type: text/html; charset=utf-8');
$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);
$contagem = 0;
foreach($rss->channel->item as $entrada) {
	$title[$contagem] = $entrada->title;
	$link[$contagem] = $entrada->link;
	$contagem = $contagem + 1;
}
?>
<html>
<head>
<title>Hyrule Legends - Zelda.com.br</title>
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, user-scalable=no"/>
<meta charset="utf-8">
<meta name="application-name" content="Zelda.com.br" />
<meta name="msapplication-square70x70logo" content="imagens/smalltile.png" />
<meta name="msapplication-square150x150logo" content="imagens/mediumtile.png" />
<meta name="msapplication-wide310x150logo" content="imagens/widetile.png" />
<meta name="msapplication-square310x310logo" content="imagens/largetile.png" />
<meta name="msapplication-allowDomainApiCall" content="true" />
<meta name="msapplication-allowDomainMetaTags" content="true" />
<meta name="msapplication-TileImage" content="imagens/tileimage.png" />
<meta name="msapplication-TileColor" content="#1b5c1f" />
<meta name="msapplication-tooltip" content="Iniciar o Zelda.com.br" />
<meta name="msapplication-starturl" content="http://zelda.com.br" />
<meta name="msapplication-navbutton-color" content="#1b5c1f" />
<meta name="msapplication-notification" content="frequency=60;polling-uri=http://apps.aloogle.net/blogapp/zeldacombr/notifications/notification1.php?build=wp; polling-uri2=http://apps.aloogle.net/blogapp/zeldacombr/notifications/notification2.php?build=wp; polling-uri3=http://apps.aloogle.net/blogapp/zeldacombr/notifications/notification3.php?build=wp; polling-uri4=http://apps.aloogle.net/blogapp/zeldacombr/notifications/notification4.php?build=wp; polling-uri5=http://apps.aloogle.net/blogapp/zeldacombr/notifications/notification5.php?build=wp" />
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.6/material.indigo-pink.min.css" />
<link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<style>
body {
	margin: 0px;
	padding: 0px;
	background: #e5e5e5;
}
	
img {
	display: block;
	margin-left: auto;
	margin-right: auto;
}
	
.mdl-card {
	cursor: pointer;
}

.mdl-card:hover {
	background-color: #eeeeee;
}

.mdl-card:active {
	background-color: #dddddd;
}
</style>
</head>
<body>
<img src="<? echo $header; ?>" width="100%">
<div class="demo-grid-ruler mdl-grid">
<?
for($i=0;$i<=9;$i++) {
$titulo = $title[$i];
$titulo = trim(preg_replace('/\s+/', ' ', $titulo));
$descricao = trim(preg_replace('/\s+/', ' ', $descricao));

$site_html= file_get_contents($link[$i]);
$matches=null;
preg_match_all('~<\s*meta\s+property="(og:image)"\s+content="([^"]*)|<\s*meta\s+name="(description)"\s+content="([^"]*)~i', $site_html, $matches);
$imagem = $matches[2][0];
$description = html_entity_decode($matches[4][1], ENT_QUOTES, "UTF-8");

$jsonfb = file_get_contents('http://graph.facebook.com/comments?id='.$link[$i]);
$commentsfb = json_decode($jsonfb);
$comentarios = count($commentsfb->data); ?>
	<div class="mdl-card mdl-shadow--2dp mdl-cell mdl-cell--6-col mdl-cell--4-col-tablet mdl-cell--4-col-phone" onclick="window.open('alooglepost.php?post=<? echo $i; ?>&url=<? echo urlencode(str_replace("http://", "", $link[$i])); ?>&blogapp=<? echo $blogapp; ?>', '_self');">
  <div class="mdl-card__title">
    <h2 style="color: #2e7d32;" class="mdl-card__title-text"><? echo html_entity_decode($titulo, ENT_QUOTES, "UTF-8"); ?></h2>
  </div>
		<? if($imagem != "") {
echo "<img src=\"".$imagem."\" style=\"width: 100%; max-width: 600;\"><br>";
	} ?>
  <div class="mdl-card__supporting-text">
			<? 
    echo $description; ?>
  </div>
  <div class="mdl-card__actions mdl-card--border" align="center" style="margin-top: auto;">
      <? echo $comentarios." comentário"; if($comentarios > 1) echo "s"; ?>
  </div>
</div>
<? } ?>
</div>
</body>
</html>