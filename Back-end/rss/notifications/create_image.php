<?
function resizeImage($url) {
  set_time_limit(-1);
  $img = file_get_contents($url);

  $url_arr = explode('/', $url);
  $ct = count($url_arr);
  $name = $url_arr[$ct-1];
  $name_div = explode('.', $name);
  $ct_dot = count($name_div);
  $img_type = $name_div[$ct_dot -1];

  $im = imagecreatefromstring($img);
  $width = imagesx($im);
  $height = imagesy($im);
  $newwidth = '310';
  $newheight = '310';
  $thumb = imagecreatetruecolor($newwidth, $newheight);
  imagecopyresized($thumb, $im, 0, 0, 0, 0, $newwidth, $newheight, $width, $height);
  imagejpeg($thumb, 'images/'.$name);
}
?>