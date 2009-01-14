<?php 

$log_unique_id = sprintf("%04x", rand(0,0xffff));
function log_message($msg, $also_debug = true) {
	global $log_unique_id;
	$msg = "[$log_unique_id] $msg";		// append a unique ID to help us track requests
	
	$fp = fopen("php.log", "a");
	fwrite($fp, date("Y-m-d H:i:s") . " header.php: $msg\n");
	fclose($fp);
	
	// also echo to debug
	if ($also_debug) {
		echo "\$('response').innerHTML = \"" . htmlentities($msg) . "\";\n";
	}
}

?>