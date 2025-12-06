<?php
header('Content-Type: text/html; charset=utf-8');

$dbi = mysqli_connect("localhost","","","aloog304_blogapp");
$dbi -> set_charset("utf8");

if (mysqli_connect_errno()) {
  echo "Não foi possível conectar a base de dados: " . mysqli_connect_error();
}

function file_force_contents($filename, $data, $flags = 0){
    if(!is_dir(dirname($filename)))
        mkdir(dirname($filename).'/', 0777, TRUE);
    return file_put_contents($filename, $data,$flags);
}
?>
