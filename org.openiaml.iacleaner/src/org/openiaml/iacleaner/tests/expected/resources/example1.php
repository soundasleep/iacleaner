<?php
/**
 * Clear the existing session.
 */

// this is a really long comment that might normally be cut short by wordwrap. but we definitely don't want this to happen.

session_start();

$log_unique_id = sprintf("%04x", rand(0,0xffff));
function log_message($msg, $also_debug = true) {
    global $log_unique_id;
    $msg = "[$log_unique_id] $msg";     // append a unique ID to help us track requests

    $fp = fopen("php.log", "a");
    fwrite($fp, date("Y-m-d H:i:s") . " header.php: $msg\n");
    fclose($fp);

    // also echo to debug
    if ($also_debug) {
        echo "\$('response').innerHTML = \"" . htmlentities($msg) . "\";\n";
    }
}

function local_die($message) {
    log_message("DEATH: $message");
    die($message);
}

// make sure that the db exists for stored_events
$db = new PDO('sqlite:stored_events.db') or local_die("could not open db");
$s = $db->prepare("SELECT * FROM stored_events");
if (!$s) {
    // create the table
    $q = $db->query("CREATE TABLE stored_events (
            id INT AUTO_INCREMENT PRIMARY KEY,
            page_id VARCHAR(64) NOT NULL,
            event_name VARCHAR(64) NOT NULL,
            arg0 BLOB
        );") or local_die("could not create table: " . print_r($db->errorInfo(), true));
    log_message("table for stored_events created", false);
}

function require_session($var, $default = "") {
    return isset($_SESSION[$var]) ? $_SESSION[$var] : $default;
}

function require_get($var, $default = "") {
    return isset($_GET[$var]) ? $_GET[$var] : $default;
}

class IamlRuntimeException extends Exception {
    public function __construct($message = "") {
        parent::__construct($message);
    }
}


log_message("clear_session.php? " . print_r($_GET, true));

foreach ($_SESSION as $k => $v) {
    $_SESSION[$k] = "";
}

echo "ok";
log_message("clear_session.php completed");

