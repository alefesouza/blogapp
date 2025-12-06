<?php
	include("connect_db.php");
if (isset($token)) { header("location:sendnotification.php"); } else {}
	$room = mysqli_query($dbi, "SELECT * FROM login WHERE login='$login_cookie'");
	$info = mysqli_fetch_array($room);
	$login = $_POST['login'];
	$entrar = $_POST['entrar'];
	$senha = $_POST['senha'];
	if (isset($entrar)) {
		$verifica = mysqli_query($dbi, "SELECT * FROM login WHERE login='".strtolower($login)."' AND senha='$senha'");
		if (mysqli_num_rows($verifica) <= 0) {
			$error = "Seu login ou senha esta incorreto";
		} else {
			$login = mysqli_fetch_array($verifica);
			$_SESSION['token'] = $login['token'];
			header("location:index.php");
		}
	}
?>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - Aloogle BlogApp</title>
	  
    <link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.min.css">
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
	
	input {
		margin-top: 16px;
	}
</style>
  </head>
  <body>
    <div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
      <header class="demo-header mdl-layout__header mdl-color--primary mdl-color--grey-100 mdl-color-text--white">
        <div class="mdl-layout__header-row">
          <span class="mdl-layout-title">Login</span>
        </div>
      </header>
      <div class="demo-drawer mdl-layout__drawer mdl-color--white mdl-color-text--black">
        <nav class="demo-navigation mdl-navigation mdl-color--white" style="margin-top: 70px;">
          <a class="mdl-navigation__link selected" href=""><i class="mdl-color-text--black material-icons">person</i>Login</a>
        </nav>
      </div>
      <main class="mdl-layout__content">
		  <div class="mdl-card mdl-shadow--2dp demo-card-square" align="center">
  <div class="mdl-card__supporting-text">
			<div style="width: 182px;">
<form class="form-horizontal" method="post" action="">
    <fieldset>
        <div class="form-group">
                <input name="login" type="text" class="form-control" id="login" placeholder="Login">
                <input name="senha" type="password" class="form-control" id="senha" placeholder="Senha">
                <button type="submit" name="entrar" class="btn btn-primary">Entrar</button>
        </div>
    </fieldset>
</form>
<div><? echo $error; ?></div>
			</div><br><br>
</div>
	  </div>
      </main>
    </div>
	<script src="https://storage.googleapis.com/code.getmdl.io/1.0.0/material.min.js"></script>
  </body>
</html>