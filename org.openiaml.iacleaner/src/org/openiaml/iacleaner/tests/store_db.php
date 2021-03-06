<?php
/**
 * For storing a single value into the database (attribute id).
 */
 
require("create_database.php");	// init db if necessary

function log_message($msg) {
	$fp = fopen("php.log", "a");
	fwrite($fp, date("Y-m-d H:i:s") . " store_db.php: $msg\n");
	fclose($fp);
}

function local_die($msg) {
	log_message("ERROR: $msg"); 
	die($msg);
}

// get the http:// url to this application
function get_baseurl() {
	return "http://" . $_SERVER["HTTP_HOST"] . dirname($_SERVER["REQUEST_URI"]);
}

/**
 * Recursive functions in PHP. See default.js for Javascript implementations of these.
 * TODO add test cases to make sure these are both identical.
 */
function store_event($page_id, $event_name, $arg0) {
	global $trace;
	// have we already called this store_event? if so, bail
	if (in_array($page_id, $trace)) {
		log_message("breaking out of possibly infinite store_event loop: $page_id");
		return;
	}
	
	/*
	 * a simple solution (but not scalable TODO): create a new HTTP request to store the event.
	 * a better solution is obiously to store the new event in this same page.
	 */	
	$url = get_baseurl() . "/store_event.php?page_id=".urlencode($page_id)."&event_name=".urlencode($event_name)."&arg0=".urlencode($arg0)."&trace=".urlencode(implode(",", $trace));
	log_message("store_db.php calling $url");
	$r = file_get_contents($url);
	if ($r != "ok")
		local_die("store_event failed: " + $r);
		
	return true;
}

function store_db($attribute_id, $arg0) {
	global $trace;
	// have we already called this store_event? if so, bail
	if (in_array($attribute_id, $trace)) {
		log_message("breaking out of possibly infinite store_db loop: $attribute_id");
		return;
	}
	
	/*
	 * a simple solution (but not scalable TODO): create a new HTTP request to store the event.
	 * a better solution is obiously to store the new event in this same page.
	 */	
	$url = get_baseurl() . "/store_db.php?attribute_id=".urlencode($attribute_id)."&arg0=".urlencode($arg0)."&trace=".urlencode(implode(",", $trace));
	log_message("store_db.php calling $url");
	$r = file_get_contents($url);
	if ($r != "ok")
		local_die("store_db failed: " + $r); 
		
	return true;
}

log_message("store_db.php? " . print_r($_GET, true));
$attribute_id = $_GET["attribute_id"] or local_die("no attribute id");
$arg0 = $_GET["arg0"] or local_die("no arg0");

// anti-infinite-loop trace tracking
$trace = isset($_GET["trace"]) ? explode(",", $_GET["trace"]) : array();
$trace[] = $attribute_id;

// find the table name
$db_name = false;
$table_name = false;
$row_name = false;

	


if (!$db_name)
	local_die("no db found");
if (!$table_name)
	local_die("no table found (db=$db_name)");
if (!$row_name)
	local_die("no row found (table=$table_name)");

// connect to the database source
$db = new PDO($db_name) or local_die("could not open db '$db_name'");

// does anything exist?
$results = $db->query("SELECT * FROM $table_name") or local_die("could not look for existing values in '$table_name': " .print_r($db->errorInfo(), true));
if ($results->fetch()) {
	// yes: update all existing
	$s = $db->prepare("UPDATE $table_name SET $row_name = ?") or local_die("could not prepare update query: " . print_r($db->errorInfo(), true));
} else {
	// no: insert new
	$s = $db->prepare("INSERT INTO $table_name ($row_name) VALUES (?)") or local_die("could not prepare insert query: " . print_r($db->errorInfo(), true));
}

// update all existing
$s->execute(array($arg0)) or local_die("could not execute query: " . print_r($db->errorInfo(), true));

// done
$s = null;
log_message("store_db.php: adding attribute_id=$attribute_id, arg0=$arg0 into db=$db_name, table=$table_name, row=$row_name");

log_message("done");
echo "ok";

