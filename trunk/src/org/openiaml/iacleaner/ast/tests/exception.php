<?php 
$a = 4;
try {
	throw new sf404Exception("kittens");
} catch (sf404Exception $e) {
	echo "exception: $e";
	throw $e;	// rethrow
} finally {
	echo "this will always run.";
}
 ?>