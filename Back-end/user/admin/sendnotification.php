<? if (isset($_COOKIE['login']) && $_COOKIE['login'] == "alefe") { } else { header("location:login.php"); } ?>
<div id="hide"><?
include("../connect_db.php");
$login_cookie = $_COOKIE['login'];
$room = mysql_query("SELECT * FROM login WHERE login='$login_cookie'") or die ("ERROR: ".mysql_error());
$info = mysql_fetch_array($room);

if(isset($_POST['enviar'])) {
$enviado = "Mensagem enviada!";

$APPLICATION_ID = "";
$REST_API_KEY = "";

$data = array(
	'where' => '{}',
	'data' => array(
		'action' => "net.aloogle.acasadocogumelo.UPDATE_STATUS",
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
  <title>Enviar notificação - Aloogle BlogApp</title>
	
    <link href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.2.1/css/material-wfont.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.2.1/css/ripples.min.css" rel="stylesheet">
    <script src="/libs/jquery/jquery-2.1.1.min.js"></script>
	
  <? include("../partes/head.php") ?>
  <? include("../partes/polymer.imports.php"); ?>
  <? include('../partes/css.php'); ?>

<style shim-shadowdom>
.opcoes {
	display: block;
	margin: 0 auto;
	background: #ffffff;
	border-radius: 2px;
	box-shadow: rgba(0, 0, 0, 0.0980392) 0px 2px 4px 0px, rgba(0, 0, 0, 0.0980392) 0px 0px 3px 0px;
}

paper-button.colored {
	background: #4285f4;
	color: #fff;
}

.form-control-wrapper .material-input:before, .form-control-wrapper input.form-control:focus~.material-input:after {
	background: #1976d2;
}

.imagem {
	float: right;
	display: none;
}
</style>
</head>

<body unresolved>
	
	 <core-drawer-panel id="drawerPanel">
		 <core-header-panel drawer>
	  <core-menu style="margin-top: 100px;" selected="0">
	  	  <core-item icon="av:volume-up" label="Enviar notificação"></core-item>
		  <? if($info['popular'] == "true") { ?>
	  	  <core-item icon="grade" label="Editar populares" onclick="window.open('populares.php', '_self');"></core-item>
		  <? } ?>
	  	  <core-item icon="exit-to-app" label="Sair" onclick="window.open('logout.php', '_self');"></core-item>
	  </core-menu>
		 </core-header-panel>

		 <core-header-panel main id="main">
    <core-toolbar id="defaultbar">

    <core-icon-button icon="menu" id="navicon"></core-icon-button>
	<div flex>Enviar notificação - <? echo $info['nome']; ?></div>
    </core-toolbar>
	
	<div class="container">
		<div class="opcoes">
			<br><br>
			<form class="form-horizontal" method="post" action="">
    <fieldset>
        <div class="form-group">
            <label for="barra" class="col-lg-2 control-label">Barra</label>
            <div class="col-lg-10">
                <input name="barra" type="text" class="form-control" id="barra" placeholder="Texto que aparece na barra de notificações quando ela chega">
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
                <input name="sumario" type="text" class="form-control" id="sumario" placeholder="Texto embaixo da notificação expandida">
            </div>
        </div>
        <div class="form-group">
            <label for="url" class="col-lg-2 control-label">URL</label>
            <div class="col-lg-10">
                <input name="url" type="text" class="form-control" id="url" placeholder="Link que abrirá ao tocar na notificação" value="http://">
            </div>
        </div>
        <div class="form-group">
            <label for="imagem" class="col-lg-2 control-label">Imagem</label>
            <div class="col-lg-10">
                <input name="imagem" type="text" class="form-control" id="imagem" placeholder="Imagem" value="http://">
            </div>
        </div>
        <div class="form-group">
            <label for="action" class="col-lg-2 control-label">Action</label>
            <div class="col-lg-10">
                <select name="action" id="action">
					<? 
						$result = mysqli_query($dbi, "SELECT * FROM login");
						while ($row = mysqli_fetch_array($result)) {
							echo "<option value=\"".$row['action']."\">".$row['action']."</option>\n";
						}
					?>
				</select>
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
			</div><br><br>
        </div>
	</div>
	</core-header-panel>

	  </core-drawer-panel>
</body>
	<script>
document.addEventListener('polymer-ready', function() {
  var navicon = document.getElementById('navicon');
  var drawerPanel = document.getElementById('drawerPanel');
  navicon.addEventListener('click', function() {
    drawerPanel.togglePanel();
  });
});
	</script>
	

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.2.1/js/material.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-material-design/0.2.1/js/ripples.js"></script>
    <script>
      $.material.init();
    </script>

</html>
