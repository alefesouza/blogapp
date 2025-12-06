<?
$id = $_GET['id'];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/95564318/posts/'.$id.'/replies?order=ASC');
$site = json_decode($json);

$comentarios = $site->comments;
?>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<link rel="stylesheet" href="http://dropandoideias.com/wp-content/themes/dropideias/style.css">
<link rel='stylesheet' id='commentluv_style-css'  href='http://dropandoideias.com/wp-content/plugins/commentluv/css/commentluv.css?ver=4.2.2' type='text/css' media='all' />
<link href='http://fonts.googleapis.com/css?family=Dosis:400,500' rel='stylesheet' type='text/css' />
<link href='http://fonts.googleapis.com/css?family=Roboto:400,400italic,500,700,500italic' rel='stylesheet' type='text/css' />
<link href='http://fonts.googleapis.com/css?family=Oswald:400,700' rel='stylesheet' type='text/css' />
<link href='http://fonts.googleapis.com/css?family=Noticia+Text:400,400italic,700,700italic' rel='stylesheet' type='text/css' />
<link href='http://fonts.googleapis.com/css?family=Droid+Serif:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
</head>
<body>
<div id="comments" class="commentwrap">
<h1>Faça seu comentário!</h1> <br>

<div id="respond"> <br>


<form action="http://dropandoideias.com/wp-comments-post.php" method="post" id="commentform">


<div class="space"></div>

<p class="comment-form-author">
<input type="text" class="campo_nome" name="author" placeholder="Nome" id="author" value="" size="22" tabindex="1" aria-required="true">
</p>


<p class="comment-form-email">
<input type="text" name="email" placeholder="Email" class="campo_email" value="" size="22" tabindex="2" aria-required="true">
</p>

<p class="comment-form-url">
<input type="text" name="url" class="campo_url" placeholder="Site/Blog" id="url" value="" size="22" tabindex="3">
</p>
	
	
<p class="comment-form-comment">
<textarea name="comment" placeholder="Comentário" id="comment" class="campo_comentario" cols="78" rows="10" tabindex="4"></textarea></p>
<span class="center"></span>

<p><input name="submit" type="submit" id="submit" tabindex="5" value="Enviar Comentário"><input type="hidden" name="comment_post_ID" value="<? echo $id; ?>" id="comment_post_ID">
<input type="hidden" name="comment_parent" id="comment_parent" value="0">
</p></form>

	<ol class="commentlist"><!-- #comment-## -->
<? for($i=0; $i < count($comentarios); $i++) { ?>
	<li id="comment-<? echo $comentarios[$i]->ID; ?>" class="comment even thread-even depth-1">
		<div class="comment-author"> 
        <div class="avatar_reply">
			<img alt="" src="<? echo $comentarios[$i]->author->avatar_URL; ?>" srcset="<? echo $comentarios[$i]->author->avatar_URL; ?>" class="avatar avatar-55 photo" height="55" width="55">
		</div>
	<div class="bubble">
      <div class="autor"><a href="http://www.eagorafe.com" rel="external nofollow" class="url"><? echo $comentarios[$i]->author->name; ?></a> </div><br>
			<div class="comment-time">
			<?
			$data = str_replace("T", " ", substr($comentarios[$i]->date, 0, -9));
			$data = DateTime::createFromFormat('Y-m-d H:i', $data);
			echo $data->format("d/m/Y H:i"); ?></div>
		<div class="commententry"><? echo $comentarios[$i]->content; ?>
	</div>
    
	</div></div></li><!-- #comment-## -->
<? } ?>
	</ol>
	
	
</div></div>
</body>
</html>