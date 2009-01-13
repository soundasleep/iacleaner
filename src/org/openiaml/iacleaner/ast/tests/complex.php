<?php
		/* generated page "viewkey" */

		// include header code
		

session_start();

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

		require("create_database.php");		// ensure DB exists
		
		// run any event triggers for this page
	// EventTrigger org.openiaml.model.model.impl.EventTriggerImpl@66afc4 (isGenerated: true, id: model.11e47202c05.31, name: access)
		// RunInstanceWires sorted by priority
		
		do_model_11e47202c05_19(
	
	
		
			/* a property from our session */
			require_session("model_11e37b8d89c_17")
		
	

);
	
		
		// expand any operations (both in this page and in any surrounding scopes)
	// expanding operations for org.openiaml.model.model.visual.impl.PageImpl@15c0d33 (isGenerated: false, id: visual.11e37b8d948.d, name: viewkey, overridden: false) (url: null)
	
	/** composite operation "update" */
	$running_model_11e47202c05_28 = false;
	function do_model_11e47202c05_28(	$model_11e47202c05_2b) {
		
		// operation: update
		global $running_model_11e47202c05_28;
		if ($running_model_11e47202c05_28 == false) {
			$running_model_11e47202c05_28 = true;		// prevent loops
			
			// has this operation got a fail handler?
			
			
			// execute the operation
	// flow starts here
	// chained operation setPropertyToValue		
		
		// outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@1839d2e (isGenerated: true, id: model.11e47202c05.2a, name: fieldValue, defaultValue: null)
		
			queue_set_application_property("visual_11e37df5bce_1c", 
	$model_11e47202c05_2b
);
		

		
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f70a5d (isGenerated: true, id: operations.11e47202c63.e)		
		
		
	
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f70a5d (isGenerated: true, id: operations.11e47202c63.e)	
	
	// continue chained operations
	

	

			// continue chained operations
	

			
			
			
			$running_model_11e47202c05_28 = false;
		}
	}

	/** composite operation "check key" */
	$running_model_11e47202c05_19 = false;
	function do_model_11e47202c05_19(	$model_11e47202c05_1a) {
		
		// operation: check key
		global $running_model_11e47202c05_19;
		if ($running_model_11e47202c05_19 == false) {
			$running_model_11e47202c05_19 = true;		// prevent loops
			
			// has this operation got a fail handler?
			
				// fail edge org.openiaml.model.model.wires.impl.NavigateWireImpl@bce3d7 (isGenerated: true, id: wires.11e47202c63.a) (name: fail, overridden: false)
				try {
			
			
			// execute the operation
	// flow starts here
	// decision operation equal?		
	if (
		
	$model_11e47202c05_1a == 		"42"

	
	) {
		// passed (exactly one pass)
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f33149 (isGenerated: true, id: operations.11e47202c63.9)
	} else {
		// failed (exactly one fail)
	
		throw new IamlRuntimeException("Error: Key check failed. You may need to login.");
	

	}

	// continue chained operations
	

	

			// continue chained operations
	
	
		// ignoring fail wire
	


			
			
				} catch (IamlRuntimeException $e) {
					log_message("Caught exception $e");
	// we need to navigate to the fail page
	$url = "visual_11e47202c63_4.php?fail=" . urlencode($e->getMessage());
	log_message("Redirecting to '$url' (fail)");
	header("Location: $url");
	die;

				}
			
			
			$running_model_11e47202c05_19 = false;
		}
	}

	/** composite operation "do logout" */
	$running_model_11e47202c05_20 = false;
	function do_model_11e47202c05_20() {
		
		// operation: do logout
		global $running_model_11e47202c05_20;
		if ($running_model_11e47202c05_20 == false) {
			$running_model_11e47202c05_20 = true;		// prevent loops
			
			// has this operation got a fail handler?
			
			
			// execute the operation
	// flow starts here
	// chained operation setPropertyToValue		
		
		// outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@13641d6 (isGenerated: false, id: model.11e37b8d89c.17, name: my login key, defaultValue: null)
		
			$_SESSION["model_11e37b8d89c_17"] = 
	"null" /* static value "model_11e47202c05_21" */ 
;
		

		
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@cedf4e (isGenerated: true, id: operations.11e47202c63.c)		
		
		
	
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@cedf4e (isGenerated: true, id: operations.11e47202c63.c)	
	
	// continue chained operations
	

	

			// continue chained operations
	
	
		// navigate to another page
		$url = "visual_11e19f1b1ea_2.php";
		log_message("Redirecting to '$url'");
		header("Location: $url");
		die;
	


			
			
			
			$running_model_11e47202c05_20 = false;
		}
	}
	
		?>
		<html>
		<head>
			<title>viewkey</title>
			<link rel="stylesheet" type="text/css" href="default.css" />
			<script language="Javascript" src="js/prototype.js"></script>
			<script language="Javascript">
				

/* approach #2: use event queues stored on the server, if the function is
   not available on the current page */
function store_event(page_id, event_name, arg0) {
	var url = 'store_event.php?page_id=' + escape(page_id) + '&event_name=' + escape(event_name) + "&arg0=" + escape(arg0);
	debug("creating ajax request to url: " + url);
	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) + 1;		// increment ajax counter
	new Ajax.Request(url,
  {
    method:'get',
    onSuccess: function(transport){
      	var response = transport.responseText || "no response text";
      	debug("success: " + response);
      	document.getElementById('response').innerHTML = response;
      	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) - 1;		// decrement ajax counter
    },
    onFailure: function(transport){ 
      	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) - 1;		// decrement ajax counter
    	debug("something went wrong: " + transport.responseText);
      	$('ajax_monitor').innerHTML = 'failed: ' + response.responseText;		// for speeding up testing
    	alert('Something went wrong...');	// alert is last so we can continue execution
     }
  });
  debug("store_event called");
}

/* save directly to database (only one attribute) */
function store_db(attribute_id, arg0) {
	var url = 'store_db.php?attribute_id=' + escape(attribute_id) + '&arg0=' + escape(arg0);
	debug("creating ajax request to url: " + url);
	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) + 1;		// increment ajax counter
	new Ajax.Request(url,
  {
    method:'get',
    onSuccess: function(transport){
      	var response = transport.responseText || "no response text";
      	debug("success: " + response);
      	document.getElementById('response').innerHTML = response;
      	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) - 1;		// decrement ajax counter
    },
    onFailure: function(transport){ 
      	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) - 1;		// decrement ajax counter
    	debug("something went wrong: " + transport.responseText);
      	$('ajax_monitor').innerHTML = 'failed: ' + response.responseText;		// for speeding up testing
    	alert('Something went wrong...');	// alert is last so we can continue execution
     }
  });
  debug("store_db called");	
}

/* save a session variable (only one attribute) */
function set_session(id, arg0, function_queue) {
	var url = 'set_session.php?id=' + escape(id) + '&arg0=' + escape(arg0);
	debug("creating ajax request to url: " + url);
	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) + 1;		// increment ajax counter
	new Ajax.Request(url,
  {
    method:'get',
    onSuccess: function(transport){
      	var response = transport.responseText || "no response text";
      	debug("success: " + response);
      	document.getElementById('response').innerHTML = response;
      	
      	// execute the function queue
      	if (function_queue) {
      		debug('executing function queue');
      		function_queue();
      	}

      	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) - 1;		// decrement ajax counter
    },
    onFailure: function(transport){ 
      	$('ajax_monitor').innerHTML = (1 * $('ajax_monitor').innerHTML) - 1;		// decrement ajax counter
    	debug("something went wrong: " + transport.responseText);
      	$('ajax_monitor').innerHTML = 'failed: ' + response.responseText;		// for speeding up testing
    	alert('Something went wrong...');	// alert is last so we can continue execution
     }
  });
  debug("set_session called");	
}

var debug_message_saved = "";
function debug(msg) {
	var debug_string = "<li>" + msg + "\n";
	if (document.getElementById('debug')) {
		document.getElementById('debug').innerHTML += debug_message_saved + debug_string;
		debug_message_saved = "";
	} else {
		debug_message_saved += debug_string;
	}
}

/**
 * Populate all of the fields on the page.
 */
function populateFields() {
	// get all fields on the page
	debug("populating fields...");
	populateAll(document.getElementsByTagName("input"));
	populateAll(document.getElementsByTagName("textarea"));
}

/**
 * Populate all of the fields in the list, based on .id and .value
 */
function populateAll(fields) {
	var i;
	for (i = 0; i < fields.length; i++) {
		var cookieName = "field_" + fields[i].id;
		if (readCookie(cookieName) != null) {
			debug("field " + fields[i].id + " (cookie name " + cookieName + ") set to value " + readCookie(cookieName));
			fields[i].value = readCookie(cookieName);
		}
	}
}

/**
 * Save the value of a particular field
 */
function setField(field) {
	debug("saving cookie for " + field.id);
	createCookie("field_" + field.id, field.value, 30);
}

/**
 * Is every element in the array equal?
 */ 
function is_array_equal(a) {
	if (a.length <= 1)
		return true;
		
	for (var i = 1; i < a.length; i++) {
		if (a[0] != a[i])
			return false;
	}
	
	return true;
}

/**
 * Define an exception class.
 */
function IamlJavascriptException(message) {
	this.message = message;
	
	this.getMessage = function() { return message; }
	this.toString = function() { return "IamlJavascriptException: " + message; } 
}

// ---------

/**
 * Copied from http://www.quirksmode.org/js/cookies.html
 */
function createCookie(name,value,days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function eraseCookie(name) {
	createCookie(name,"",-1);
}

/* page onload functions */
populateFields();

			</script>
			<script language="Javascript">
				var try_catch_depth = 0;
			
	// expanding operations for org.openiaml.model.model.visual.impl.PageImpl@15c0d33 (isGenerated: false, id: visual.11e37b8d948.d, name: viewkey, overridden: false) (url: null)
	
	/** composite operation "update" */
	var running_model_11e47202c05_28 = false;
	function do_model_11e47202c05_28(	model_11e47202c05_2b) {
		
		// operation: update
		if (running_model_11e47202c05_28 == false) {
			running_model_11e47202c05_28 = true;		// prevent loops
			
			// has this operation got a fail handler?
			
			
			// execute the operation
			// if we are at the root try/catch, catch the exception explicitly
			if (try_catch_depth == 0  ) {
				try {
					// get chained functions
					var function_queue = function() {
						// continue with any chained operations
	
					}
					var function_queue_queued = false;
				
	// flow starts here
	// chained operation setPropertyToValue		
		
		// outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@1839d2e (isGenerated: true, id: model.11e47202c05.2a, name: fieldValue, defaultValue: null)
		
				
	document.getElementById('visual_11e37df5bce_1c').value
 = 
	model_11e47202c05_2b
;

		

		
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f70a5d (isGenerated: true, id: operations.11e47202c63.e)		
		
		
		
	
	// expanding inline event trigger edit
	
		
	

	
	
					
					// should we still run the chained functions?
					if (!function_queue_queued)
						function_queue();
				} catch (e if e instanceof IamlJavascriptException) {
					// unexpected exception
					alert("Unexpected exception: " + e);
				}
			} else {
				// get chained functions
				var function_queue = function() {
					// continue with any chained operations
	
				}
				var function_queue_queued = false;
			
	// flow starts here
	// chained operation setPropertyToValue		
		
		// outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@1839d2e (isGenerated: true, id: model.11e47202c05.2a, name: fieldValue, defaultValue: null)
		
				
	document.getElementById('visual_11e37df5bce_1c').value
 = 
	model_11e47202c05_2b
;

		

		
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f70a5d (isGenerated: true, id: operations.11e47202c63.e)		
		
		
		
	
	// expanding inline event trigger edit
	
		
	

	
	
				
				// should we still run the chained functions?
				if (!function_queue_queued)
					function_queue();
			}					

			

			running_model_11e47202c05_28 = false;
		}
	}

	/** composite operation "check key" */
	var running_model_11e47202c05_19 = false;
	function do_model_11e47202c05_19(	model_11e47202c05_1a) {
		
		// operation: check key
		if (running_model_11e47202c05_19 == false) {
			running_model_11e47202c05_19 = true;		// prevent loops
			
			// has this operation got a fail handler?
			
				// fail edge org.openiaml.model.model.wires.impl.NavigateWireImpl@bce3d7 (isGenerated: true, id: wires.11e47202c63.a) (name: fail, overridden: false)
				try {
					try_catch_depth++;
			
			
			// execute the operation
			// if we are at the root try/catch, catch the exception explicitly
			if (try_catch_depth == 0 && false ) {
				try {
					// get chained functions
					var function_queue = function() {
						// continue with any chained operations
	
	
		// ignoring fail wire
	

					}
					var function_queue_queued = false;
				
	// flow starts here
	// chained operation equal?		
	if (
		
		is_array_equal(new Array(		model_11e47202c05_1a, 		"42"
))
	
	) {
		// passed (exactly one pass)
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f33149 (isGenerated: true, id: operations.11e47202c63.9)
	} else {
		// failed (exactly one fail)
	
		throw new IamlJavascriptException("Error: Key check failed. You may need to login.");
	

	}
	
					
					// should we still run the chained functions?
					if (!function_queue_queued)
						function_queue();
				} catch (e if e instanceof IamlJavascriptException) {
					// unexpected exception
					alert("Unexpected exception: " + e);
				}
			} else {
				// get chained functions
				var function_queue = function() {
					// continue with any chained operations
	
	
		// ignoring fail wire
	

				}
				var function_queue_queued = false;
			
	// flow starts here
	// chained operation equal?		
	if (
		
		is_array_equal(new Array(		model_11e47202c05_1a, 		"42"
))
	
	) {
		// passed (exactly one pass)
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f33149 (isGenerated: true, id: operations.11e47202c63.9)
	} else {
		// failed (exactly one fail)
	
		throw new IamlJavascriptException("Error: Key check failed. You may need to login.");
	

	}
	
				
				// should we still run the chained functions?
				if (!function_queue_queued)
					function_queue();
			}					

			
					try_catch_depth--;
				} catch (e if e instanceof IamlJavascriptException) {
					debug("Caught exception " + e);
	// we need to navigate to the fail page
	var url = "visual_11e47202c63_4.php?fail=" + e; /* TODO urlencode me */
	debug("Redirecting to '" + url + "' (fail)");
	window.location = url;

				}
			

			running_model_11e47202c05_19 = false;
		}
	}

	/** composite operation "do logout" */
	var running_model_11e47202c05_20 = false;
	function do_model_11e47202c05_20() {
		
		// operation: do logout
		if (running_model_11e47202c05_20 == false) {
			running_model_11e47202c05_20 = true;		// prevent loops
			
			// has this operation got a fail handler?
			
			
			// execute the operation
			// if we are at the root try/catch, catch the exception explicitly
			if (try_catch_depth == 0  ) {
				try {
					// get chained functions
					var function_queue = function() {
						// continue with any chained operations
	
	
		// navigate to another page
		var url = "visual_11e19f1b1ea_2.php";
		debug("Redirecting to '" + url + "'");
		window.location = url;
	

					}
					var function_queue_queued = false;
				
	// flow starts here
	// chained operation setPropertyToValue		
		
		// outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@13641d6 (isGenerated: false, id: model.11e37b8d89c.17, name: my login key, defaultValue: null)
		
			set_session("model_11e37b8d89c_17", 
	"null" /* static value "model_11e47202c05_21" */ 
,
				function_queue);		// pass it a function queue to execute afterwards
			function_queue_queued = true;		// we are waiting for it to return to execute the function queue
		

		
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@cedf4e (isGenerated: true, id: operations.11e47202c63.c)		
		
		
		
	
	
	
					
					// should we still run the chained functions?
					if (!function_queue_queued)
						function_queue();
				} catch (e if e instanceof IamlJavascriptException) {
					// unexpected exception
					alert("Unexpected exception: " + e);
				}
			} else {
				// get chained functions
				var function_queue = function() {
					// continue with any chained operations
	
	
		// navigate to another page
		var url = "visual_11e19f1b1ea_2.php";
		debug("Redirecting to '" + url + "'");
		window.location = url;
	

				}
				var function_queue_queued = false;
			
	// flow starts here
	// chained operation setPropertyToValue		
		
		// outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@13641d6 (isGenerated: false, id: model.11e37b8d89c.17, name: my login key, defaultValue: null)
		
			set_session("model_11e37b8d89c_17", 
	"null" /* static value "model_11e47202c05_21" */ 
,
				function_queue);		// pass it a function queue to execute afterwards
			function_queue_queued = true;		// we are waiting for it to return to execute the function queue
		

		
	// do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@cedf4e (isGenerated: true, id: operations.11e47202c63.c)		
		
		
		
	
	
	
				
				// should we still run the chained functions?
				if (!function_queue_queued)
					function_queue();
			}					

			

			running_model_11e47202c05_20 = false;
		}
	}
				
				// this needs to be loaded *after* the page has loaded
				function loadStoredEvents() {
					$('response').innerHTML = "before loadStoredEvents...";
					<?php 
					

/**
 * Expand all possible events for the current page.
 */
// first, make sure the table exists
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
}

log_message("finding events for page id visual.11e37b8d948.d");
$s = $db->prepare("SELECT * FROM stored_events WHERE page_id=?") or local_die("could not get stored events: " . print_r($db->errorInfo(), true));
$s->execute(array("visual.11e37b8d948.d")) or local_die("could not execute select query: " . print_r($db->errorInfo(), true));
foreach ($s->fetchAll() as $row) {
	log_message("found event $row[id]: event_name=$row[event_name] arg0=$row[arg0]");
	echo $row["event_name"] . "(\"" . $row["arg0"] . "\"); // from db " . $row["id"] . "\n";
}

// delete all the events for this page now
log_message("deleting all events for page visual.11e37b8d948.d");
$s = $db->prepare("DELETE FROM stored_events WHERE page_id=?") or local_die("could not delete stored events: " . print_r($db->errorInfo(), true));
$s->execute(array("visual.11e37b8d948.d")) or local_die("could not execute delete query: " . print_r($db->errorInfo(), true));

					?>
				}
			</script>
		</head>
		
		<body onLoad="loadStoredEvents()">
		<div style="font-size:small; text-align: right;"><a href="clear_session.php">clear session</a> - <a href="sitemap.html">sitemap</a></div>
		
		<h1>viewkey</h1>
		
		<div id="runtime_errors"><!-- any runtime errors go into here --></div>
		
		<?php
		/* expand any fail error messages */
		if (require_get("fail")) {
?>
	<div class="error">
		<p>An exception occured: <i><?php echo htmlspecialchars(require_get("fail")); ?></i></p>
	</div>
<?php 
		}
		?>
		
	<div>
	<label id="label_visual_11e37df5bce_1c">
		current login key
		
		<input type="text" id="visual_11e37df5bce_1c" name="visual_11e37df5bce_1c" 
	
		onChange=" return false;"
	
>
	</label>
	</div>
		
		<div id="debug">			
		<div>
		
		<div id="response">empty</div>
		
		<!-- for test cases to improve speed: an outstanding ajax calls counter -->
		<div id="ajax_monitor"></div>

		</body>
		</html>
	