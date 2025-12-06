<?
include('connect_db.php');
$blogappid = $_GET['blogappid'];
$postid = $_GET['postid'];

$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE id=$blogappid;") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$blogid = $infoblogapp["userid"];

$json = file_get_contents('https://public-api.wordpress.com/rest/v1.1/sites/'.$blogid.'/posts/'.$postid.'/replies?order=ASC');
$site = json_decode($json);

$comentarios = $site->comments;
?>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.6/material.indigo-pink.min.css" />
<link href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en" rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<style>
body {
	margin: 0px;
	padding: 0px;
	background: #e5e5e5;
}
	
img {
	display: block;
  border-radius: 75px;
  width: 55px;
  height: 55px;
  margin: 0px 10px;
}

.date {
  font-style: italic;
  color: #424f58;
}

.author {
  font-weight: bold;
}
</style>
</head>
<body>

<div class="demo-grid-ruler mdl-grid">
<? for($i=0; $i < count($comentarios); $i++) { ?>
<div class="mdl-card mdl-shadow--2dp mdl-cell mdl-cell--6-col mdl-cell--4-col-tablet mdl-cell--4-col-phone" id="<? echo $comentarios[$i]->ID; ?>">
  <div class="mdl-card__supporting-text">
		<img alt="" src="<? echo $comentarios[$i]->author->avatar_URL; ?>" style="float: left;">
    <div style="float: left;">
      <span class="author"><? echo $comentarios[$i]->author->name; ?></span>
      <br>
			<span class="date">
      <?
			$data = str_replace("T", " ", substr($comentarios[$i]->date, 0, -9));
			$data = DateTime::createFromFormat('Y-m-d H:i', $data);
			echo $data->format("d/m/Y H:i"); ?></span></div><br><br><br><br>
		<? echo $comentarios[$i]->content; ?>
  </div>
</div>
<? }
	
if(count($comentarios) == 0) { ?>
<div class="mdl-card mdl-shadow--2dp mdl-cell mdl-cell--12-col mdl-cell--8-col-tablet mdl-cell--4-col-phone" style="min-height: 30px;">
  <div class="mdl-card__supporting-text">
		Ainda não há comentários neste post.
  </div>
</div>
<? } ?>
</div>

</body>
<script src="https://storage.googleapis.com/code.getmdl.io/1.0.6/material.min.js"></script>
</html>