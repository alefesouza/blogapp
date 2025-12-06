<?
header('Content-Type: text/html; charset=utf-8');

$post = intval($_GET['post']);

$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);

$hl_title = $hl_rss->channel->item[$post]->title;
$hl_descricao = $hl_rss->channel->item[$post]->description;
$hl_link = $hl_rss->channel->item[$post]->link;
$hl_author = $hl_rss->channel->item[$post]->children('dc', true)->creator;
$hl_date = $hl_rss->channel->item[$post]->pubDate;

$hl_titulo = $hl_title;
$hl_titulo = trim(preg_replace('/\s+/', ' ', $hl_titulo));
$hl_descricao = trim(preg_replace('/\s+/', ' ', $hl_descricao));

$author = $hl_author;
$date = substr($hl_date, 5, -15);?>
<html>
<head>
<title><? echo $hl_title; ?></title>
<base href="http://zelda.com.br">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, user-scalable=no"/>
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.6/material.indigo-pink.min.css" />
<link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<style>
body {
	margin-top: 10;
}
	
img {
	display: block;
	margin-left: auto;
	margin-right: auto;
	max-width: 100%;
	height: auto !important;
}
  
a {
	color: #1e5e23;
}
	
.authordate {
	font-style: italic;
	color: #424f58;
}
	
@media screen and (max-width: 1024px) {
	.mdl-layout__header {
  	  display: flex;
	}
}
</style>
</head>
<body>
<main style="padding-left: 15px;">
    <h2 style="color: #2e7d32;" class="mdl-card__title-text" id="eventbodytitle"><? echo $hl_titulo; ?></h2>
	<p class="authordate">Por <? echo $author.' - '.$date; ?></p>
	
<p id="description" style="margin-top: 15px;"><? echo $hl_descricao; ?></p>
	<hr>
	
<div style="float: right; cursor: pointer; color: #1e5e23;" onclick="window.open('<? echo $hl_link; ?>', '_self');">
	<font style="font-weight: bold;">Ver post no site</font>
	
	<button class="mdl-button mdl-js-button mdl-button--icon">
	<i class="material-icons">open_in_new</i>
	</button></div>
</main>
<script src="https://storage.googleapis.com/code.getmdl.io/1.0.6/material.min.js"></script>
</body>
</html>