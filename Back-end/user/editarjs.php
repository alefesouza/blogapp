<? 
include("connect_db.php");
include("checklogin.php");

$id = $info['userid'];

if(isset($_POST['enviar'])) {
$js = $_POST['editjs'];
$js = mysqli_real_escape_string($dbi, $js);

mysqli_query($dbi, "UPDATE extras SET value='$js' WHERE userid='$id' AND oque='postactivityjs'") or die ("ERROR: ".mysql_error());

$enviado = "JS atualizado";
}

$values = mysqli_query($dbi, "SELECT * FROM extras WHERE userid='$id' AND oque='postactivityjs'") or die ("ERROR: ".mysql_error());
$infovalues = mysqli_fetch_array($values);
?>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar JS - Aloogle BlogApp</title>
	  
    <link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.indigo-light_blue.min.css">
    <link rel="stylesheet" href="styles.css">
<link href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.3.0/css/material.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.3.0/css/ripples.min.css" rel="stylesheet">
<script src="/libs/jquery/jquery-2.1.3.min.js"></script>
<script src="/libs/js/autosize/autosize.js"></script>
<script>
$(function() {
	autosize(document.querySelector('textarea'));
});
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
    <div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
      <header class="demo-header mdl-layout__header mdl-color--primary mdl-color--grey-100 mdl-color-text--white">
        <div class="mdl-layout__header-row">
          <span class="mdl-layout-title">Editar JS - <? echo $info['nome']; ?></span>
        </div>
      </header>
      <div class="demo-drawer mdl-layout__drawer mdl-color--white mdl-color-text--black">
        <nav class="demo-navigation mdl-navigation mdl-color--white" style="margin-top: 70px;">
			<a class="mdl-navigation__link" href="sendnotification.php"><i class="mdl-color-text--black material-icons">volume_up</i>Enviar notificação</a>
		  <? if($info['popular'] == "true") { ?>
          <a class="mdl-navigation__link" href="populares.php"><i class="mdl-color-text--black material-icons">grade</i>Editar populares</a>
			<? } ?>
		  <? if($info['tipo'] == "youtuber" || $info['tipo'] = "wordpress") { ?>
          <a class="mdl-navigation__link" href="links.php"><i class="mdl-color-text--black material-icons">link</i>Editar links</a>
			<? } if($info['tipo'] == "youtuber") { ?>
          <a class="mdl-navigation__link" href="playlists.php"><i class="mdl-color-text--black material-icons">video_library</i>Editar playlists</a>
			<? } if($info['tipo'] == "wordpress") { ?>
          <a class="mdl-navigation__link" href="categorias.php"><i class="mdl-color-text--black material-icons">label</i>Editar categorias</a>
          <a class="mdl-navigation__link" href="editarcss.php"><i class="mdl-color-text--black material-icons">format_paint</i>Editar CSS</a>
          <a class="mdl-navigation__link selected" href="editarjs.php"><i class="mdl-color-text--black material-icons">functions</i><b>Editar JS</b></a>
			<? } ?>
          <a class="mdl-navigation__link" href="logout.php"><i class="mdl-color-text--black material-icons">exit_to_app</i>Sair</a>
        </nav>
      </div>
      <main class="mdl-layout__content">
		  <div class="mdl-card mdl-shadow--2dp demo-card-square">
 			 <div class="mdl-card__supporting-text">
				 
			<form class="form-horizontal" method="post" action="">
    <fieldset>
        <div class="form-group">
            <label for="editjs" class="col-lg-2 control-label">JS</label>
            <div class="col-lg-10">
				<textarea name="editjs" class="form-control" id="editjs"><? echo $infovalues['value']; ?></textarea>
            </div>
        </div>
            <div class="col-lg-10 col-lg-offset-2">
                <button type="submit" name="enviar" class="btn btn-primary">Atualizar</button>
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
  </body>
</html>