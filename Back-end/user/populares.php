<?
include("connect_db.php");
include("checklogin.php");

if($info['popular'] != "true") {
	header("location:index.php");
} ?>
<div id="hide"><?

$blogapp = $info['blogapp'];

$dbi = mysqli_connect("localhost","","","aloog304_blogapp");
$dbi -> set_charset("utf8");

if(isset($_POST['enviar'])) {
for($i=1; $i <= 7; $i++) {
	$post = $_POST['post'.$i];
	$json = file_get_contents('https://www.googleapis.com/blogger/v3/blogs/5261320232708018923/posts/'.$post.'?key=');
	$site = json_decode($json);
	
$titulo = addslashes($site->title);
$link = addslashes($posts->url);
$id = $site->id;
$descricao = addslashes(html_entity_decode(trim(strip_tags($site->content)), 1,"UTF-8"));
$descricao = split("\n", $descricao);
if(trim($descricao[0]) != "") {
	$desc = trim($descricao[0]);
} else if(trim($descricao[1])) {
	$desc = trim($descricao[1]);
} else if(trim($descricao[2])) {
	$desc = trim($descricao[2]);
} else if(trim($descricao[3])) {
	$desc = trim($descricao[3]);
}
$desc = explode(".", $desc);
if(strlen($desc[0]) > 20) {
	$d = $desc[0].".";
} else {
	$d = $desc[0].$desc[1].".";
}
preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $site->content, $urls);
$urls = $urls[1];
$imagem = addslashes($urls[0]);
$url = $site->url;

mysqli_query($dbi, "UPDATE $blogapp SET postid='$id',titulo='$titulo',descricao='$d',imagem='$imagem',url='$url',data='' WHERE id='$i'");
}
$enviado = "Posts atualizados";
}

$popular = mysqli_query($dbi, "SELECT * FROM $blogapp");

while ($row = mysqli_fetch_array($popular)) {
    $postpopular[] = $row['postid'];
}
?>
</div>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar populares - Aloogle BlogApp</title>
	  
    <link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.min.css">
    <link rel="stylesheet" href="styles.css">
<link href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.3.0/css/material.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.3.0/css/ripples.min.css" rel="stylesheet">
<script src="/libs/jquery/jquery-2.1.3.min.js"></script>

<script>
window.onload = function() {
	<? for($i=0; $i < 7; $i++) { ?>
	document.getElementById("post<? echo $i + 1; ?>").value = "<? echo $postpopular[$i]; ?>";
	<? } ?>
}
</script>

<style>
.mdl-layout__content {
	background-color: #e5e5e5;
	padding: 50px;
	padding-top: 35px;
}

  .demo-card-square.mdl-card {
	  width: 100%;
	  height: auto;
	display: block;
  }
</style>
  </head>
  <body>
<button class="mdl-button mdl-js-button mdl-button--fab help" style="background-color: #d23f31;" id="help">
  <i class="mdl-color-text--white material-icons">help</i>
</button>
    <div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
      <header class="demo-header mdl-layout__header mdl-color--primary mdl-color--grey-100 mdl-color-text--white">
        <div class="mdl-layout__header-row">
          <span class="mdl-layout-title">Editar populares - <? echo $info['nome']; ?></span>
        </div>
      </header>
      <div class="demo-drawer mdl-layout__drawer mdl-color--white mdl-color-text--black">
        <nav class="demo-navigation mdl-navigation mdl-color--white" style="margin-top: 70px;">
			<a class="mdl-navigation__link" href="sendnotification.php"><i class="mdl-color-text--black material-icons">volume_up</i>Enviar notificação</a>
		  <? if($info['popular'] == "true") { ?>
			<a class="mdl-navigation__link selected" href=""><i class="mdl-color-text--black material-icons">grade</i><b>Editar populares</b></a>
			<? } ?>
          <a class="mdl-navigation__link" href="logout.php"><i class="mdl-color-text--black material-icons">exit_to_app</i>Sair</a>
        </nav>
      </div>
      <main class="mdl-layout__content">
		  <div class="mdl-card mdl-shadow--2dp demo-card-square">
 			 <div class="mdl-card__supporting-text">
				 
<form class="form-horizontal" method="post" action="">
    <fieldset>
		<? for($i=0; $i < 7; $i++) { ?>
        <div class="form-group">
            <label for="post<? echo $i + 1; ?>" class="col-lg-2 control-label">Post <? echo $i + 1; ?></label>
            <div class="col-lg-10">
                <input name="post<? echo $i + 1; ?>" type="text" class="form-control" id="post<? echo $i + 1; ?>"></div>
            </div>
		<? } ?>
            <div class="col-lg-10 col-lg-offset-2">
                <button type="reset" class="btn btn-default">Apagar tudo</button>
                <button type="submit" name="enviar" class="btn btn-primary">Enviar</button>
				<? echo $enviado; ?>
            </div>
        </div>
    </fieldset>
</form>
	</div>
	  </div>
      </main>
    </div>
	<script src="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.min.js"></script>
	<script>
$(function() {
	var toshow = true;
	$help=$("#help");
	$help.click(function() {  alert(<? echo json_encode("Entre em um post popular, aperte CTRL+U (ou clique com o botão direito e vá em \"Exibir código fonte da página\"), aperte CTRL+F e digite \"comments/default\", no primeiro resultado copie os números antes desse resultado (esse é o ID do post) e cole na posição que deve ficar no aplicativo."); ?>); });
})
	</script>
</script>
  </body>
</html>
