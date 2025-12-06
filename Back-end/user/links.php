<?
include("connect_db.php");
include("checklogin.php");

$id = $info['userid'];

if(isset($_POST['enviar'])) {
$to = intval($_POST['to']);
for($i=1; $i <= $to; $i++) {
	$name = $_POST['link'.$i.'name'];
	$url = $_POST['link'.$i.'url'];
	$icone = $_POST['link'.$i.'icone'];
	if($name != "" && $url != "") {
	$tojson [] = array('name' => $name, 'url' => $url, 'icon' => $icone);
	}
}
if(!isset($tojson)) { $tojson = array(); }
$json = array('name' => $_POST['sectionname'], 'count' => count($tojson), 'links' => $tojson);
$json = json_encode($json);
$json = str_replace('\\', '\\\\', $json);
mysqli_query($dbi, "UPDATE lateral_links SET json='$json' WHERE userid='$id'") or die ("ERROR: ".mysql_error());

$roomvalues = mysqli_query($dbi, "SELECT * FROM lateral_links WHERE userid='$id'") or die ("ERROR: ".mysql_error());
$infovalues = mysqli_fetch_array($roomvalues);
$enviado = "Links atualizados";
} else {
$roomvalues = mysqli_query($dbi, "SELECT * FROM lateral_links WHERE userid='$id'") or die ("ERROR: ".mysql_error());
$infovalues = mysqli_fetch_array($roomvalues);
}
?>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar links - Aloogle BlogApp</title>
	  
    <link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.indigo-light_blue.min.css">
    <link rel="stylesheet" href="styles.css">
<link href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.3.0/css/material.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.3.0/css/ripples.min.css" rel="stylesheet">
<script src="/libs/jquery/jquery-2.1.3.min.js"></script>
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
<button class="mdl-button mdl-js-button mdl-button--fab help" style="background-color: #d23f31;" id="help" onclick="add()">
  <i class="mdl-color-text--white material-icons">add</i>
</button>
    <div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
      <header class="demo-header mdl-layout__header mdl-color--primary mdl-color--grey-100 mdl-color-text--white">
        <div class="mdl-layout__header-row">
          <span class="mdl-layout-title">Editar links - <? echo $info['nome']; ?></span>
        </div>
      </header>
      <div class="demo-drawer mdl-layout__drawer mdl-color--white mdl-color-text--black">
        <nav class="demo-navigation mdl-navigation mdl-color--white" style="margin-top: 70px;">
			<a class="mdl-navigation__link" href="sendnotification.php"><i class="mdl-color-text--black material-icons">volume_up</i>Enviar notificação</a>
		  <? if($info['popular'] == "true") { ?>
          <a class="mdl-navigation__link" href="populares.php"><i class="mdl-color-text--black material-icons">grade</i>Editar populares</a>
			<? } ?>
		  <? if($info['tipo'] == "youtuber" || $info['tipo'] = "wordpress") { ?>
          <a class="mdl-navigation__link selected" href="links.php"><i class="mdl-color-text--black material-icons">link</i><b>Editar links</b></a>
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
    <fieldset id="formulario">
		<? if($infovalues['json'] == "") { 
			$count = 1;
		} else {
		$json = json_decode($infovalues['json']);
		$count = $json->count;
		if($count == 0) { $count++; } } ?>
		<input type="hidden" value="<? echo $count; ?>" id="to" name="to" />
        <div class="form-group" id="group">
			<div class="col-lg-2 control-label"><b>Nome da seção</b></div>
            <div class="col-lg-10">
                <input name="sectionname" type="text" class="form-control" id="sectionname" value="<? if(isset($json->name)) { echo $json->name; } else { echo "Links"; } ?>" placeholder="Nome">
            </div>
		</div>
		<?
	for($i = 0; $i < $count; $i++) { ?>
        <div class="form-group" id="group<? echo $i + 1; ?>">
            <label for="link<? echo $i + 1; ?>name" class="col-lg-2 control-label">Link <? echo $i + 1; ?></label>
            <div class="col-lg-2">
                <input name="link<? echo $i + 1; ?>name" type="text" class="form-control" id="link<? echo $i + 1; ?>name" value="<? echo $json->links[$i]->name; ?>" placeholder="Nome">
            </div>
            <div class="col-lg-4">
                <input name="link<? echo $i + 1; ?>url" type="text" class="form-control" id="link<? echo $i + 1; ?>url" value="<? if(isset($json->links[$i]->url)) { echo $json->links[$i]->url; } else { echo "http://"; } ?>" placeholder="Link">
            </div>
            <div class="col-lg-3">
                <input name="link<? echo $i + 1; ?>icone" type="text" class="form-control" id="link<? echo $i + 1; ?>icone" value="<? if(isset($json->links[$i]->icon)) { echo $json->links[$i]->icon; }?>" placeholder="Ícone (opcional)">
            </div>
			
            <div class="col-lg-1">
			<button class="mdl-button mdl-js-button mdl-js-ripple-effect mdl-button--icon" onclick="remov(<? echo $i + 1; ?>);"><i class="material-icons">clear</i></button>
            </div>
			
        </div>
	<? }?>
    </fieldset>
            <div class="col-lg-10 col-lg-offset-2">
                <button type="submit" name="enviar" class="btn btn-primary">Atualizar</button>
				<? echo $enviado; ?>
            </div>
</form>
				 
	</div>
	  </div>
      </main>
    </div>
	<script src="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.min.js"></script>
  </body>
<script>
	<? if($infovalues['json'] == "") { $var = "1"; } else { $var = $count; } ?>
	var n = <? echo $var; ?>;
	function add() {
		setLabels();
		n++;
		var element = '<div class="form-group" id="group' + n + '"><label for="link' + n + 'name" class="col-lg-2 control-label">Link ' + n + '</label><div class="col-lg-2"><input name="link' + n + 'name" type="text" class="form-control" id="post' + n + '" placeholder="Nome"></div><div class="col-lg-4"><input name="link' + n + 'url" type="text" class="form-control" id="link' + n + 'url" value="http://" placeholder="Link"></div><div class="col-lg-3"><input name="link' + n + 'icone" type="text" class="form-control" id="link' + n + 'icone" value="" placeholder="Ícone (opcional)"></div><div class="col-lg-1"><button class="mdl-button mdl-js-button mdl-js-ripple-effect mdl-button--icon" onclick="remov(' + n + ');"><i class="material-icons">clear</i></button></div></div>';
		$('#formulario').append(element);
		$('#to').val(n);
	}
	
	function remov(x) {
		$('#group' + x).remove();
		setLabels();
		n--;
		$('#to').val(n);
	}
	
	function setLabels() {
		var labels = document.getElementsByTagName('label');
		for(var i = 0; i < labels.length; i++) {
			labels[i].innerHTML = "Link " + (i + 1);
		}
	}
</script>
</html>