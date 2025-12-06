<? 
include("connect_db.php");
include("checklogin.php");
?>
<div id="hide"><?
if(isset($_POST['enviar'])) {
$enviado = "Mensagem enviada!";

$APPLICATION_ID = $info['appkey'];
$REST_API_KEY = $info['restkey'];

$data = array(
	'where' => '{}',
	'data' => array(
		'action' => $info['package'].'.UPDATE_STATUS',
		'id' => '5',
		'tipo' => '1',
		'barra' => $_POST['barra'],
		'titulo' => $_POST['titulo'],
		'texto' => $_POST['texto'],
		'titulogrande' => $_POST['titulogrande'],
		'textogrande' => $_POST['textogrande'],
		'sumario' => $_POST['sumario'],
		'url' => $_POST['url'],
		'imagem' => 'http://',
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
?>
</div>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Enviar notificação - Aloogle BlogApp</title>
	  
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
          <span class="mdl-layout-title">Enviar notificação - <? echo $info['nome']; ?></span>
        </div>
      </header>
      <div class="demo-drawer mdl-layout__drawer mdl-color--white mdl-color-text--black">
        <nav class="demo-navigation mdl-navigation mdl-color--white" style="margin-top: 70px;">
			<a class="mdl-navigation__link selected" href=""><i class="mdl-color-text--black material-icons">volume_up</i><b>Enviar notificação</b></a>
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
          <a class="mdl-navigation__link" href="editarjs.php"><i class="mdl-color-text--black material-icons">functions</i>Editar JS</a>
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
            <label for="barra" class="col-lg-2 control-label">Barra</label>
            <div class="col-lg-10">
                <input name="barra" type="text" class="form-control" id="barra" placeholder="Texto que aparece na barra de notificações quando ela chega" value=" - <? echo $info['nome']; ?>">
            </div>
        </div>
        <div class="form-group">
            <label for="titulo" class="col-lg-2 control-label">Título</label>
            <div class="col-lg-10">
                <input name="titulo" type="text" class="form-control" id="titulo" placeholder="Título da notificação normal">
            </div>
        </div>
        <div class="form-group">
            <label for="texto" class="col-lg-2 control-label">Descrição</label>
            <div class="col-lg-10">
                <input name="texto" type="text" class="form-control" id="texto" placeholder="Descrição da notificação normal">
            </div>
        </div>
        <div class="form-group">
            <label for="titulogrande" class="col-lg-2 control-label">Título grande</label>
            <div class="col-lg-10">
                <input name="titulogrande" type="text" class="form-control" id="titulogrande" placeholder="Título da notificação expandida">
            </div>
        </div>
        <div class="form-group">
            <label for="textogrande" class="col-lg-2 control-label">Descrição grande</label>
            <div class="col-lg-10">
                <textarea class="form-control" rows="3" name="textogrande" id="textogrande"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label for="sumario" class="col-lg-2 control-label">Sumário</label>
            <div class="col-lg-10">
                <input name="sumario" type="text" class="form-control" id="sumario" placeholder="Texto embaixo da notificação expandida" value="<? echo $info['nome']; ?>">
            </div>
        </div>
        <div class="form-group">
            <label for="url" class="col-lg-2 control-label">URL</label>
            <div class="col-lg-10">
                <input name="url" type="text" class="form-control" id="url" placeholder="Link que abrirá ao tocar na notificação" value="http://">
            </div>
        </div>
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
  </body>
</html>