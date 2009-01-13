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
  int ECHO_STATEMENT = 33;
  /** RegularExpression Id. */
  int FUNCTION_STATEMENT = 34;
  /** RegularExpression Id. */
  int RETURN_STATEMENT = 35;
  /** RegularExpression Id. */
  int GLOBAL_STATEMENT = 36;
  /** RegularExpression Id. */
  int NEW_STATEMENT = 37;
  /** RegularExpression Id. */
  int OR_STATEMENT = 38;
  /** RegularExpression Id. */
  int VAR_STATEMENT = 39;
  /** RegularExpression Id. */
  int CLASS_STATEMENT = 40;
  /** RegularExpression Id. */
  int INTERFACE_STATEMENT = 41;
  /** RegularExpression Id. */
  int IMPLEMENTS_STATEMENT = 42;
  /** RegularExpression Id. */
  int EXTENDS_STATEMENT = 43;
  /** RegularExpression Id. */
  int ACCESS_MODIFIER = 44;
  /** RegularExpression Id. */
  int PHP_TRY_STATEMENT = 45;
  /** RegularExpression Id. */
  int PHP_CATCH_STATEMENT = 46;
  /** RegularExpression Id. */
  int PHP_THROW_STATEMENT = 47;
  /** RegularExpression Id. */
  int PHP_FINALLY_STATEMENT = 48;
  /** RegularExpression Id. */
  int PHP_DIE_STATEMENT = 49;
  /** RegularExpression Id. */
  int PHP_EXIT_STATEMENT = 50;
  /** RegularExpression Id. */
  int PHP_IF_STATEMENT = 51;
  /** RegularExpression Id. */
  int PHP_ELSE_STATEMENT = 52;
  /** RegularExpression Id. */
  int STRING = 53;
  /** RegularExpression Id. */
  int STRING_SQ = 54;
  /** RegularExpression Id. */
  int DECIMAL = 55;
  /** RegularExpression Id. */
  int HEXADECIMAL = 56;
  /** RegularExpression Id. */
  int PHP_BUILTIN = 57;
  /** RegularExpression Id. */
  int PHP_FUNCTION_NAME = 58;
  /** RegularExpression Id. */
  int PHP_VARIABLE = 59;
  /** RegularExpression Id. */
  int PHP_OBJECT_OPERATOR = 60;
  /** RegularExpression Id. */
  int PHP_OPERATOR = 61;
  /** RegularExpression Id. */
  int PHP_PREFIX_OPERATOR = 62;
  /** RegularExpression Id. */
  int PHP_BLOCK_END = 63;
  /** RegularExpression Id. */
  int PHP_BLOCK_BEGIN = 64;
  /** RegularExpression Id. */
  int HTML_SCRIPT_TAG = 65;
  /** RegularExpression Id. */
  int HTML_SCRIPT_TAG_CLOSE = 66;
  /** RegularExpression Id. */
  int HTML_OPEN_TAG = 67;
  /** RegularExpression Id. */
  int HTML_CLOSE_TAG = 68;
  /** RegularExpression Id. */
  int HTML_CLOSE_TAG_XHTML = 69;
  /** RegularExpression Id. */
  int HTML_CLOSING_TAG = 70;
  /** RegularExpression Id. */
  int HTML_TAG_NAME = 71;
  /** RegularExpression Id. */
  int LETTER = 72;
  /** RegularExpression Id. */
  int HTML_ATTRIBUTE_NAME = 73;
  /** RegularExpression Id. */
  int HTML_STRING = 74;
  /** RegularExpression Id. */
  int HTML_ATTRIBUTE_EQUALS = 75;
  /** RegularExpression Id. */
  int HTML_TEXT_BLOCK = 76;
  /** RegularExpression Id. */
  int HTML_COMMENT_START = 77;
  /** RegularExpression Id. */
  int JS_FUNCTION_STATEMENT = 80;
  /** RegularExpression Id. */
  int JS_RETURN_STATEMENT = 81;
  /** RegularExpression Id. */
  int JS_NEW_STATEMENT = 82;
  /** RegularExpression Id. */
  int JS_VAR_STATEMENT = 83;
  /** RegularExpression Id. */
  int JS_IF_STATEMENT = 84;
  /** RegularExpression Id. */
  int JS_FOR_STATEMENT = 85;
  /** RegularExpression Id. */
  int JS_TRY_STATEMENT = 86;
  /** RegularExpression Id. */
  int JS_CATCH_STATEMENT = 87;
  /** RegularExpression Id. */
  int JS_THROW_STATEMENT = 88;
  /** RegularExpression Id. */
  int JS_FINALLY_STATEMENT = 89;
  /** RegularExpression Id. */
  int JS_BRACKET_OPEN = 90;
  /** RegularExpression Id. */
  int JS_BRACKET_CLOSE = 91;
  /** RegularExpression Id. */
  int JS_BRACE_OPEN = 92;
  /** RegularExpression Id. */
  int JS_BRACE_CLOSE = 93;
  /** RegularExpression Id. */
  int JS_ARRAY_OPEN = 94;
  /** RegularExpression Id. */
  int JS_ARRAY_CLOSE = 95;
  /** RegularExpression Id. */
  int JS_END = 96;
  /** RegularExpression Id. */
  int JS_COMMA = 97;
  /** RegularExpression Id. */
  int JS_ELSE_STATEMENT = 98;
  /** RegularExpression Id. */
  int JS_STRING = 99;
  /** RegularExpression Id. */
  int JS_STRING_SQ = 100;
  /** RegularExpression Id. */
  int JS_DECIMAL = 101;
  /** RegularExpression Id. */
  int JS_HEXADECIMAL = 102;
  /** RegularExpression Id. */
  int JS_BUILTIN = 103;
  /** RegularExpression Id. */
  int JS_OBJECT_OPERATOR = 104;
  /** RegularExpression Id. */
  int JS_PREFIX_OPERATOR = 105;
  /** RegularExpression Id. */
  int JS_NUMBER_OPERATOR = 106;
  /** RegularExpression Id. */
  int JS_OPERATOR = 107;
  /** RegularExpression Id. */
  int JS_ASSIGNMENT_OPERATOR = 108;
  /** RegularExpression Id. */
  int JS_OPERATOR_SINGLE = 109;
  /** RegularExpression Id. */
  int JS_EQUAL = 110;
  /** RegularExpression Id. */
  int JS_TERNARY_1 = 111;
  /** RegularExpression Id. */
  int JS_TERNARY_2 = 112;
  /** RegularExpression Id. */
  int JS_VARIABLE = 113;

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
  int PHP_WITHIN_COMMENT_SINGLE = 6;
  /** Lexical state. */
  int JS_WITHIN_COMMENT = 7;
  /** Lexical state. */
  int JS_WITHIN_COMMENT_SINGLE = 8;
  /** Lexical state. */
  int HTML_WITHIN_COMMENT = 9;

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
    "\"/*\"",
    "\"//\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"/*\"",
    "\"//\"",
    "\"*/\"",
    "<token of kind 26>",
    "<token of kind 27>",
    "<token of kind 28>",
    "\"*/\"",
    "<token of kind 30>",
    "<token of kind 31>",
    "<token of kind 32>",
    "\"echo\"",
    "\"function\"",
    "\"return\"",
    "\"global\"",
    "\"new\"",
    "\"or\"",
    "\"var\"",
    "\"class\"",
    "\"interface\"",
    "\"implements\"",
    "\"extends\"",
    "<ACCESS_MODIFIER>",
    "\"try\"",
    "\"catch\"",
    "\"throw\"",
    "\"finally\"",
    "\"die\"",
    "\"exit\"",
    "\"if\"",
    "\"else\"",
    "<STRING>",
    "<STRING_SQ>",
    "<DECIMAL>",
    "<HEXADECIMAL>",
    "<PHP_BUILTIN>",
    "<PHP_FUNCTION_NAME>",
    "<PHP_VARIABLE>",
    "\"::\"",
    "<PHP_OPERATOR>",
    "\"!\"",
    "\"?>\"",
    "\"<?php\"",
    "<HTML_SCRIPT_TAG>",
    "\"</script>\"",
    "\"<\"",
    "\">\"",
    "\"/>\"",
    "\"</\"",
    "<HTML_TAG_NAME>",
    "<LETTER>",
    "<HTML_ATTRIBUTE_NAME>",
    "<HTML_STRING>",
    "\"=\"",
    "<HTML_TEXT_BLOCK>",
    "\"<!--\"",
    "\"-->\"",
    "<token of kind 79>",
    "\"function\"",
    "\"return\"",
    "\"new\"",
    "\"var\"",
    "\"if\"",
    "\"for\"",
    "\"try\"",
    "\"catch\"",
    "\"throw\"",
    "\"finally\"",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\"else\"",
    "<JS_STRING>",
    "<JS_STRING_SQ>",
    "<JS_DECIMAL>",
    "<JS_HEXADECIMAL>",
    "<JS_BUILTIN>",
    "\".\"",
    "\"!\"",
    "<JS_NUMBER_OPERATOR>",
    "<JS_OPERATOR>",
    "<JS_ASSIGNMENT_OPERATOR>",
    "<JS_OPERATOR_SINGLE>",
    "\"=\"",
    "\"?\"",
    "\":\"",
    "<JS_VARIABLE>",
    "\";\"",
    "\"(\"",
    "\")\"",
    "\"=\"",
    "\"{\"",
    "\"}\"",
    "\"?\"",
    "\":\"",
    "\"[\"",
    "\"]\"",
    "\",\"",
  };

}