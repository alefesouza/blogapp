<?
$hl_feed = file_get_contents('http://www.zelda.com.br/rss.xml');
$hl_rss = new SimpleXmlElement($hl_feed);
$hl_contagem = 0;
foreach($hl_rss->channel->item as $hl_entrada) {
	$hl_title[$hl_contagem] = $hl_entrada->title;
	$hl_description[$hl_contagem] = $hl_entrada->description;
	$hl_link[$hl_contagem] = $hl_entrada->link;
	$hl_contagem = $hl_contagem + 1;
}
?>
<html>
<head>
<meta name="viewport" content="width=device-width, height=device-height,  initial-scale=1.0, user-scalable=no;user-scalable=0;"/>
<style>
body {
	font-family: Arial;
   margin: 0px;
   padding: 0px;
	background: #fafafa;
}

section {
	padding: 10px;
	text-align: center;
}

section:hover {
	background: #eeeeee;
	cursor: pointer;
}
</style>
</head>
<body>
<img src="majora.jpg" width="100%"><br>
<section onclick="window.open('http://www.zelda.com.br/redes-sociais/facebook', '_self');">
Capas para Facebook
</section>
<section onclick="window.open('http://www.zelda.com.br/musica', '_self');">
M&uacute;sicas
</section>
<section onclick="window.open('http://www.zelda.com.br/portal-do-tempo', '_self');">
Portal do Tempo
</section>
<section onclick="window.open('http://www.zelda.com.br/quadrinhos', '_self');">
Quadrinhos
</section>
<section onclick="window.open('http://www.zelda.com.br/tipografia', '_self');">
Tipografia
</section>
<section onclick="window.open('http://www.zelda.com.br/wallpapers', '_self');">
Wallpapers
</section>
</body>
</html>