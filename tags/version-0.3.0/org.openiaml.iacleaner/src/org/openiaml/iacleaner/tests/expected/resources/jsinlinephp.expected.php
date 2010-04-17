<html>
  <script>
    alert('<?php echo date(); ?>');
    alert("Hello, world!");
    if (true) {
      <?php echo "i++;"; ?>
      for (var i = 0; i < 10; i++) {
        <?php if (rand(0, 0xffff)) <= 300) {
            echo my_function("a");
          } ?>
      }
    }
  </script>
</html>