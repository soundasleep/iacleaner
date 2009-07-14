<html>
  <?php echo("a");
    echo("a\"");
    echo("a \\");
    echo("a \\\" ); echo (");
    echo('a');
    echo('a\'');
    echo('a \\');
    echo('a \\\' ); echo (');
    echo(" a very long string which shouldn't hit wordwrap, even though it's definitely long enough ");
    echo(' a very long string which shouldn't hit wordwrap, even though it\'s definitely long enough ');
    ?>
</html>