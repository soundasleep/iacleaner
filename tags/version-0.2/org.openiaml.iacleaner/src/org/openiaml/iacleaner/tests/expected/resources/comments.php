

     <html>

  <?php

  // a single line comment

    function /* this is a function! */ my_function(
            $arg0, /* comment within a parameter */
            $arg1 = null
            ) /* comment after a function */
        {

// comment before a statement
        return "42";    // comment after a statement


        return "43";

            // comment on a new line

}

echo my_function(         /* empty arg */    );

/* /* */
/* // */
// /*

/* a comment
that spans multiple lines */

            /* a comment
                     which is indented */

/*            */

    /**
     * java doc comment
     */

?><body>



    <h1> Hello,   world!</h1>/* comment in html - unparsed */
     </body>
</html>