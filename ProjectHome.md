This project aims to provide a Java-based code syntax formatter for a diverse range of web languages, including:

  * HTML
  * Javascript
  * PHP
  * CSS
  * JSP

It allows you to format [complex, messy web application code](http://code.google.com/p/iacleaner/source/browse/trunk/org.openiaml.iacleaner/src/org/openiaml/iacleaner/tests/test.php) into a [more structured format](http://code.google.com/p/iacleaner/source/browse/trunk/org.openiaml.iacleaner/src/org/openiaml/iacleaner/tests/out.php).

To achieve this, it uses specialised reader/writer implementations and a custom parser. As a result, this formatter is quite fragile, so _use at your own risk!_ However, it does come with a [set of automated test cases](http://code.google.com/p/iacleaner/source/browse/trunk/org.openiaml.iacleaner/src/org/openiaml/iacleaner/tests/), to ensure that it generally outputs suitable code. It is also used in the [IAML project](http://openiaml.org) extensively as a code cleaner to its generated code.

**[Version 0.3.1](http://journals.jevon.org/users/jevon-phd/entry/19823)** of iacleaner has been released (April 2010).

## Quick Start ##

iacleaner can be installed through [an Eclipse update site](Installation.md), or you can [checkout the source from SVN](http://code.google.com/p/iacleaner/source/checkout) and build a JAR file yourself.

```
File source = new File("script.php");
IACleaner cleaner = new IAInlineCleaner();
String formatted = cleaner.cleanScript(source);

// rewrite the file
FileWriter fh = new FileWriter(source);
fh.write(formatted);
fh.close();
```

As of [issue 6](https://code.google.com/p/iacleaner/issues/detail?id=6), you can also [download a prebuilt JAR](http://code.google.com/p/iacleaner/downloads/list) and execute it using the command line:

```
$ java -jar iacleaner-0.3.1.jar --help
Usage: java -jar iacleaner.jar [args...]

 --input <file>     Use input file, otherwise stdin
 --output <file>    Use output file, otherwise stdout
 --extension <file> Use specified extension
 --wordwrap <file>  Attempt to wordwrap at the given column index
 --help             Display this help
```

## Future Work ##

If a web script cannot be handled properly by iacleaner, please [submit it](http://code.google.com/p/iacleaner/issues/entry) to the issue tracker so this can be resolved.

It would be nice to use an actual syntax parser (e.g. [JavaCC](https://javacc.dev.java.net)) to implement the formatting, but this is easier said than done. Feel free to [contribute](http://code.google.com/p/iacleaner/issues/list) ;)

Future work may include basic support for code compression.