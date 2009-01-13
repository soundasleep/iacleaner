
/**
 * Define an exception class.
 */
function IamlJavascriptException(message) {
  this.message = message;
	
  this.getMessage = function() { return message; }
  this.toString = function() { return "IamlJavascriptException: " + message; } 
}

var e = new IamlJavascriptException("hello");
