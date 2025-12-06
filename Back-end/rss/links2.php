<?
include("connect_db.php");

$blogapp = $_GET["blogapp"];
$blogappsql = mysqli_query($dbi, "SELECT * FROM login WHERE blogapp='$blogapp';") or die ("ERROR: ".mysql_error());
$infoblogapp = mysqli_fetch_array($blogappsql);
$id = $infoblogapp["id"];
$defaultend = "AND blogappid=$id ORDER BY number;";

if(version_compare($_GET["version"], '1.9.3.0', '>=')) {
  $query = "SELECT * FROM lateral WHERE version='1.9.3.0' $defaultend;";
} else if($_GET["build"] == "wp") {
  $query = "SELECT * FROM lateral WHERE build='wp' $defaultend;";
} else {
  $query = "SELECT * FROM lateral WHERE version='1.9.1.0' $defaultend;";
}

$sqlquery = mysqli_query($dbi, $query);

echo "{ \"buttons\": [ ";
$count = 0;
  while($row = mysqli_fetch_array($sqlquery)) { ?>
{ "title": <? echo json_encode($row["title"]); ?>,

<? if(strpos($row["icon"], "ms-appx:///") !== false || strpos($row["icon"], "http://") !== false) { $icon = "Holder"; $image = $row["icon"]; } else { $icon = $row["icon"]; $image = "ms-appx:///Assets/Images/Triforce.png"; } ?>
"icon": <? echo json_encode($icon); ?>, "link": <? echo json_encode($row["value"]); ?>, "image": <? echo json_encode($image); ?> }<? if($count < mysqli_num_rows($sqlquery) - 1) { echo ", "; } $count++;
}

echo " ] }";
?>