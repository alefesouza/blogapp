<?php 
include_once 'wpn.php';
$channel = '';

if($_POST['check_uri'] == "true")
{	
	check_uri();
}

function check_uri()
{
	global $dbi;
	global $blogappid;
	global $ChannelUri;
	$sql = mysqli_query($dbi, "select device from notifications_users where device='$ChannelUri'") or die ("ERROR: 1".mysqli_error($dbi));
	$info = mysqli_fetch_array($sql);
	$channel = $info['device'];
	$uricheck = mysqli_num_rows($sql);;
	if($uricheck == 1)
	{
		$data = array("uri_exists"=>"true");
		echo json_encode($data);
	}
	else
	{
		$data = array("uri_exists"=>"false");
		echo json_encode($data);
		register_wns();
	}
}

function register_wns() 
{
		global $dbi;
		global $blogappid;
		global $ChannelUri;
		global $channel;
       
        // Set POST request variable
        $url = 'https://login.live.com/accesstoken.srf';
 
        $fields = array(
            'grant_type' => urlencode('client_credentials'),
            'client_id' => urlencode(SID),
            'client_secret' => urlencode(CLIENT_SECRET),
            'scope' => urlencode('notify.windows.com')
        );
		//url-ify the data for the POST
		foreach($fields as $key=>$value) { $fields_string .= $key.'='.$value.'&'; }
		rtrim($fields_string, '&');
        $headers = array(
            'Content-Type: application/x-www-form-urlencoded'
        );
        // Open connection
        $ch = curl_init();
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // disable SSL certificate support
        //curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields_string);
		
 
        // execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
		$obj = json_decode($result);
		$access_token = $obj->{'access_token'};
		$token_type = $obj->{'token_type'};
        // Close connection
        curl_close($ch);
        //echo $result;
	    if($channel == '')
		{
			$platform = $_GET["platform"];
			mysqli_query($dbi, "insert into notifications_users(device, platform) values('$ChannelUri', '$platform')") or die ("ERROR: 2".mysqli_error($dbi));
		}
		else
		{
			mysqli_query($dbi, "update notifications_users set device = '$ChannelUri' where device='$channel'") or die ("ERROR: 3".mysqli_error($dbi));
		}
}

function notify_wns_users($title, $description, $image, $option)
{
	global $dbi;
	global $blogappid;
	
	if($image == "") {
		$xml_data = '<?xml version="1.0" encoding="utf-8"?><toast activationType="foreground" duration="long"><visual><binding template="ToastGeneric"><text>'.$title.'</text><text>'.$description.'</text></binding></visual></toast>';
	} else {
		$xml_data = '<?xml version="1.0" encoding="utf-8"?><toast activationType="foreground" scenario="reminder" duration="long"><visual><binding template="ToastGeneric"><text>'.$title.'</text><text>'.$description.'</text><image placement="inline" src="'.$image.'" /></binding></visual></toast>';
	}
	
	$xml_data1 = '<tile> <visual lang="pt-BR" version="2"> <binding template="TileSquare150x150PeekImageAndText04" fallback="TileSquarePeekImageAndText04" branding="logo"> <image id="1" src="'.$image.'" alt="alt text"/> <text id="1">'.$title.'</text> </binding> <binding template="TileWide310x150PeekImage01" fallback="TileWidePeekImage01" branding="name'.$wp.'"> <image id="1" src="'.$image.'" alt="alt text"/> <text id="1">'.$title.'</text> <text id="2">'.$description.'</text> </binding> <binding template="TileSquare310x310ImageAndTextOverlay02" branding="name'.$wp.'"> <image id="1" src="'.$image.'" alt="alt text"/> <text id="1">'.$title.'</text> <text id="2">'.$description.'</text> </binding> </visual> </tile>';
	
	$sql = mysqli_query($dbi, "select * from notifications_users") or die ("ERROR: 4".mysqli_error($dbi));
	
	while($row = mysqli_fetch_array($sql))
	{
		$uri = $row['device'];
		
		if($row['platform'] == "w10") {
			$platform_data = str_replace('branding="name"', 'branding="nameAndLogo"', $xml_data1);
		} else {
			$platform_data = $xml_data1;
		}
		
		$obj = new WPN(SID,CLIENT_SECRET);
		
		if($option == "notificacaoetile" || $option == "notificacao") {
			$obj->post_tile($uri, $xml_data, $type = WPNTypesEnum::Toast, $tileTag = '');
		}
		
		if($option == "notificacaoetile" || $option == "tile") {
			$obj->post_tile($uri, $platform_data, $type = WPNTypesEnum::Tile, $tileTag = '');
		}
	}
}
?>