<?
header('Content-Type: text/html; charset=utf-8');

$feedurl = "http://www.windowsclub.com.br/feed";

$feed = file_get_contents($feedurl);
$rss = new SimpleXmlElement($feed);

$feedtitle = $rss->channel->item[0]->title;
$feeddescription = $rss->channel->item[0]->description;
$feedlink = $rss->channel->item[0]->link;

$article = file_get_contents($feedlink);

preg_match_all('~<img.*?src=["\']+(.*?)["\']+~', $article, $urls);
?>
<style>
  body {
    font-family: Arial;
  }
  
  input[type="text"], textarea {
    width: 100%;
  }
  
  textarea {
    height: 100px;
  }
</style>
<form action="" method="post">
  <p><label>Título da notificação<br><input type="text" name="title" value="<? echo $feedtitle; ?>"></label></p>
  <p><label>Descrição da notificação<br><textarea type="text" name="description" style=""><? echo $feeddescription; ?></textarea></label></p>
  <p><label>Imagem<br><input type="text" name="image" value="<? echo $urls[1][1]; ?>"></label></p>
  <p>
    <label><input type="radio" name="option" value="notificacaoetile" checked> Notificação e tile</label>
    <label><input type="radio" name="option" value="notificacao"> Notificação</label>
    <label><input type="radio" name="option" value="tile"> Tile</label>
  </p>
  <button type="submit" name="submit">Enviar</button>
</form>
<?
if(isset($_POST["submit"])) {
  include_once 'wns.php';
  
  $title = $_POST["title"];
  $description = $_POST["description"];
  $image = $_POST["image"];
  $option = $_POST["option"];
  
  notify_wns_users($title, $description, $image, $option);
} 
?>