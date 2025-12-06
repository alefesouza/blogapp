<?
include("check.php");

header('Content-Type: text/html; charset=utf-8');

$post = intval($_GET['post']);

$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);

$title = $rss->channel->item[$post]->title;
$descricao = $rss->channel->item[$post]->description;
$link = $rss->channel->item[$post]->link;
$author = $rss->channel->item[$post]->children('dc', true)->creator;
$date = $rss->channel->item[$post]->pubDate;

$titulo = $title;
$titulo = trim(preg_replace('/\s+/', ' ', $titulo));
$descricao = trim(preg_replace('/\s+/', ' ', $descricao));

$author = $author;
$date = substr($date, 5, -15);?>
<html>
<head>
<title><? echo $title; ?></title>
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
    <h2 style="color: #2e7d32;" class="mdl-card__title-text" id="eventbodytitle"><? echo $titulo; ?></h2>
	<p class="authordate">Por <? echo $author.' - '.$date; ?></p>
	
<p id="description" style="margin-top: 15px;"><? echo $descricao; ?></p>
	<hr>
	
<div style="float: right; cursor: pointer; color: #1e5e23;" onclick="window.open('<? echo $link; ?>', '_self');">
	<font style="font-weight: bold;">Ver post no site</font>
	
	<button class="mdl-button mdl-js-button mdl-button--icon">
	<i class="material-icons">open_in_new</i>
	</button></div>
</main>
<script src="https://storage.googleapis.com/code.getmdl.io/1.0.6/material.min.js"></script>
</body>
</html>