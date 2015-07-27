# iacleaner

This project aims to provide a Java-based code syntax formatter for a diverse range of web languages, including:

* HTML
* Javascript
* PHP
* CSS
* JSP

It allows you to format [http://code.google.com/p/iacleaner/source/browse/trunk/org.openiaml.iacleaner/src/org/openiaml/iacleaner/tests/test.php complex, messy web application code] into a [http://code.google.com/p/iacleaner/source/browse/trunk/org.openiaml.iacleaner/src/org/openiaml/iacleaner/tests/out.php more structured format].

To achieve this, it uses specialised reader/writer implementations and a custom parser. As a result, this formatter is quite fragile, so _use at your own risk!_ However, it does come with a [http://code.google.com/p/iacleaner/source/browse/trunk/org.openiaml.iacleaner/src/org/openiaml/iacleaner/tests/ set of automated test cases], to ensure that it generally outputs suitable code. It is also used in the [http://openiaml.org IAML project] extensively as a code cleaner to its generated code.

*[Version 0.3.1](http://journals.jevon.org/users/jevon-phd/entry/19823)* of iacleaner has been released (April 2010).

==Quick Start==

iacleaner can be installed through [Installation an Eclipse update site], or you can [http://code.google.com/p/iacleaner/source/checkout checkout the source from SVN] and build a JAR file yourself.

```
File source = new File("script.php");
IACleaner cleaner = new IAInlineCleaner();
String formatted = cleaner.cleanScript(source);

// rewrite the file
FileWriter fh = new FileWriter(source);
fh.write(formatted);
fh.close();
```

As of issue 6, you can also [http://code.google.com/p/iacleaner/downloads/list download a prebuilt JAR] and execute it using the command line:

```
$ java -jar iacleaner-0.3.1.jar --help
Usage: java -jar iacleaner.jar [args...]

 --input <file>     Use input file, otherwise stdin
 --output <file>    Use output file, otherwise stdout
 --extension <file> Use specified extension
 --wordwrap <file>  Attempt to wordwrap at the given column index
 --help             Display this help
```

==Future Work==

If a web script cannot be handled properly by iacleaner, please [http://code.google.com/p/iacleaner/issues/entry submit it] to the issue tracker so this can be resolved.

It would be nice to use an actual syntax parser (e.g. [https://javacc.dev.java.net JavaCC]) to implement the formatting, but this is easier said than done. Feel free to [http://code.google.com/p/iacleaner/issues/list contribute] ;)

Future work may include basic support for code compression.

_Automatically exported from http://code.google.com/p/iacleaner_

