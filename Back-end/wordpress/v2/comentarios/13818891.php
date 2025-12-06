<?
$id = $_GET['id'];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/13818891/posts/'.$id.'/replies?order=ASC');
$site = json_decode($json);

$comentarios = $site->comments;
?>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<link rel="stylesheet" href="http://vidadesuporte.com.br/wp-content/themes/vdsuporte/style.css?ver=4.2.2">
<style>
body {
	background: #ffffff;
}
</style>
</head>
<body>
<div class="list-comments" id="listacome">
<? for($i=0; $i < count($comentarios); $i++) { ?>
		<div class="comment even thread-even depth-1" id="comment-<? echo $comentarios[$i]->ID; ?>">
				<div class="comment-author vcard">
			<img alt="" src="<? echo $comentarios[$i]->author->avatar_URL; ?>" srcset="<? echo $comentarios[$i]->author->avatar_URL; ?>" class="avatar avatar-60 photo grav-hashed grav-hijack" height="60" width="60" id="grav-<? echo $comentarios[$i]->ID; ?>">			<cite class="fn"><? echo $comentarios[$i]->author->name; ?></cite> <span class="says">disse:</span>		</div>
		
		<div class="comment-meta commentmetadata">
			<?
			$data = str_replace("T", " ", substr($comentarios[$i]->date, 0, -9));
			$data = DateTime::createFromFormat('Y-m-d H:i', $data);
			echo $data->format("d/m/Y H:i"); ?></a>		</div>

		<p><? echo $comentarios[$i]->content; ?></p>

		
		</div>
<? } ?>
</div>
</body>
</html>