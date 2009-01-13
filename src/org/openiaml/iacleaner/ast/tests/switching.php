<?php 
echo rand(0,42);
echo "hi";
echo "hello, world!";
 ?>
 <html>
 	<title>hello, world!</title>
 </html>
<?php
echo "done";
if (true) {
?>
	<b>done</b>
<?php } else { ?>
	<b>not done</b>
<?php } ?>
<!-- now let's alternate between html, js and php -->
<script>
alert(<?php echo rand(0,42); ?>);
</script>