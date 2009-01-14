<?php 
$e = new Exception();
$e2 = new Exception("oh dear");

class MyClass {
	var $value;
	
	/*
	public function __construct($message = "") {
		$this->value = $message;
	}
	
	public function getValue() {
		return $this->value;
	}
	*/
}

interface BetterInterface {
	public function getClearValue();
}

class YourClass extends MyClass implements BetterInterface {
	private function compileClearValue() {
		return $this->getValue() . " [ok]";
	}
	
	public function getClearValue() {
		return $this->compileClearValue();
	}
}

?>