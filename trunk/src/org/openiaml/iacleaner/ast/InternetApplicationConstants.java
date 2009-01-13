/* Generated By:JJTree&JavaCC: Do not edit this line. InternetApplicationConstants.java */
package org.openiaml.iacleaner.ast;


/** 
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface InternetApplicationConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int ECHO_STATEMENT = 21;
  /** RegularExpression Id. */
  int FUNCTION_STATEMENT = 22;
  /** RegularExpression Id. */
  int RETURN_STATEMENT = 23;
  /** RegularExpression Id. */
  int GLOBAL_STATEMENT = 24;
  /** RegularExpression Id. */
  int NEW_STATEMENT = 25;
  /** RegularExpression Id. */
  int VAR_STATEMENT = 26;
  /** RegularExpression Id. */
  int CLASS_STATEMENT = 27;
  /** RegularExpression Id. */
  int INTERFACE_STATEMENT = 28;
  /** RegularExpression Id. */
  int IMPLEMENTS_STATEMENT = 29;
  /** RegularExpression Id. */
  int EXTENDS_STATEMENT = 30;
  /** RegularExpression Id. */
  int ACCESS_MODIFIER = 31;
  /** RegularExpression Id. */
  int PHP_IF_STATEMENT = 32;
  /** RegularExpression Id. */
  int PHP_ELSE_STATEMENT = 33;
  /** RegularExpression Id. */
  int STRING = 34;
  /** RegularExpression Id. */
  int STRING_SQ = 35;
  /** RegularExpression Id. */
  int DECIMAL = 36;
  /** RegularExpression Id. */
  int HEXADECIMAL = 37;
  /** RegularExpression Id. */
  int PHP_BUILTIN = 38;
  /** RegularExpression Id. */
  int PHP_FUNCTION_NAME = 39;
  /** RegularExpression Id. */
  int PHP_SINGLE_COMMENT = 40;
  /** RegularExpression Id. */
  int PHP_MULTI_COMMENT = 41;
  /** RegularExpression Id. */
  int PHP_VARIABLE = 44;
  /** RegularExpression Id. */
  int PHP_OPERATOR = 45;
  /** RegularExpression Id. */
  int PHP_BLOCK_END = 46;
  /** RegularExpression Id. */
  int PHP_BLOCK_BEGIN = 47;
  /** RegularExpression Id. */
  int HTML_SCRIPT_TAG = 48;
  /** RegularExpression Id. */
  int HTML_SCRIPT_TAG_CLOSE = 49;
  /** RegularExpression Id. */
  int HTML_OPEN_TAG = 50;
  /** RegularExpression Id. */
  int HTML_CLOSE_TAG = 51;
  /** RegularExpression Id. */
  int HTML_CLOSING_TAG = 52;
  /** RegularExpression Id. */
  int HTML_TAG_NAME = 53;
  /** RegularExpression Id. */
  int LETTER = 54;
  /** RegularExpression Id. */
  int HTML_ATTRIBUTE_NAME = 55;
  /** RegularExpression Id. */
  int HTML_STRING = 56;
  /** RegularExpression Id. */
  int HTML_ATTRIBUTE_EQUALS = 57;
  /** RegularExpression Id. */
  int HTML_TEXT_BLOCK = 58;
  /** RegularExpression Id. */
  int HTML_COMMENT_START = 59;
  /** RegularExpression Id. */
  int JS_FUNCTION_STATEMENT = 62;
  /** RegularExpression Id. */
  int JS_RETURN_STATEMENT = 63;
  /** RegularExpression Id. */
  int JS_NEW_STATEMENT = 64;
  /** RegularExpression Id. */
  int JS_VAR_STATEMENT = 65;
  /** RegularExpression Id. */
  int JS_IF_STATEMENT = 66;
  /** RegularExpression Id. */
  int JS_BRACKET_OPEN = 67;
  /** RegularExpression Id. */
  int JS_BRACKET_CLOSE = 68;
  /** RegularExpression Id. */
  int JS_BRACE_OPEN = 69;
  /** RegularExpression Id. */
  int JS_BRACE_CLOSE = 70;
  /** RegularExpression Id. */
  int JS_END = 71;
  /** RegularExpression Id. */
  int JS_EQUAL = 72;
  /** RegularExpression Id. */
  int JS_COMMA = 73;
  /** RegularExpression Id. */
  int JS_ELSE_STATEMENT = 74;
  /** RegularExpression Id. */
  int JS_STRING = 75;
  /** RegularExpression Id. */
  int JS_DECIMAL = 76;
  /** RegularExpression Id. */
  int JS_HEXADECIMAL = 77;
  /** RegularExpression Id. */
  int JS_BUILTIN = 78;
  /** RegularExpression Id. */
  int JS_FUNCTION_NAME = 79;
  /** RegularExpression Id. */
  int JS_SINGLE_COMMENT = 80;
  /** RegularExpression Id. */
  int JS_MULTI_COMMENT = 81;
  /** RegularExpression Id. */
  int JS_VARIABLE = 84;
  /** RegularExpression Id. */
  int JS_OPERATOR = 85;
  /** RegularExpression Id. */
  int JS_OBJECT_OPERATOR = 86;

  /** Lexical state. */
  int HTML_STATE = 0;
  /** Lexical state. */
  int HTML_TAG_STATE = 1;
  /** Lexical state. */
  int HTML_OPENING_TAG = 2;
  /** Lexical state. */
  int JS_STATE = 3;
  /** Lexical state. */
  int DEFAULT = 4;
  /** Lexical state. */
  int PHP_WITHIN_COMMENT = 5;
  /** Lexical state. */
  int HTML_WITHIN_COMMENT = 6;
  /** Lexical state. */
  int JS_WITHIN_COMMENT = 7;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"echo\"",
    "\"function\"",
    "\"return\"",
    "\"global\"",
    "\"new\"",
    "\"var\"",
    "\"class\"",
    "\"interface\"",
    "\"implements\"",
    "\"extends\"",
    "<ACCESS_MODIFIER>",
    "\"if\"",
    "\"else\"",
    "<STRING>",
    "<STRING_SQ>",
    "<DECIMAL>",
    "<HEXADECIMAL>",
    "<PHP_BUILTIN>",
    "<PHP_FUNCTION_NAME>",
    "<PHP_SINGLE_COMMENT>",
    "\"/*\"",
    "\"*/\"",
    "<token of kind 43>",
    "<PHP_VARIABLE>",
    "<PHP_OPERATOR>",
    "\"?>\"",
    "\"<?php\"",
    "\"<script>\"",
    "\"</script>\"",
    "\"<\"",
    "\">\"",
    "\"</\"",
    "<HTML_TAG_NAME>",
    "<LETTER>",
    "<HTML_ATTRIBUTE_NAME>",
    "<HTML_STRING>",
    "\"=\"",
    "<HTML_TEXT_BLOCK>",
    "\"<!--\"",
    "\"-->\"",
    "<token of kind 61>",
    "\"function\"",
    "\"return\"",
    "\"new\"",
    "\"var\"",
    "\"if\"",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\";\"",
    "\"=\"",
    "\",\"",
    "\"else\"",
    "<JS_STRING>",
    "<JS_DECIMAL>",
    "<JS_HEXADECIMAL>",
    "<JS_BUILTIN>",
    "<JS_FUNCTION_NAME>",
    "<JS_SINGLE_COMMENT>",
    "\"/*\"",
    "\"*/\"",
    "<token of kind 83>",
    "<JS_VARIABLE>",
    "<JS_OPERATOR>",
    "\".\"",
    "\";\"",
    "\"(\"",
    "\")\"",
    "\"=\"",
    "\",\"",
    "\"{\"",
    "\"}\"",
  };

}
