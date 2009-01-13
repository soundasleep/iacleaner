
/**
 * Define an exception class.
 */
function IamlJavascriptException(message) {
  this.message = message;
	
  this.getMessage = function() { return message; }
  this.toString = function() { return "IamlJavascriptException: " + message; } 
}

var e = new IamlJavascriptException("hello");

try {
	throw e;
	throw new IamlJavascriptException;
} catch (e if e instanceof IamlJavascriptException) {
	alert("caught expected: " + e);
	throw e;
} catch (e) {
	alert("default catch");
} finally {
	alert("finally");
}