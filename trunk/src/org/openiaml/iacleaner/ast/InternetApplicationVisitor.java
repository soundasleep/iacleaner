/* Generated By:JavaCC: Do not edit this line. InternetApplicationVisitor.java Version 4.1d1 */
package org.openiaml.iacleaner.ast;

public interface InternetApplicationVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTStart node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTPhpRootBlock node, Object data);
  public Object visit(ASTPhpBlock node, Object data);
  public Object visit(ASTPhpInlineHtmlBlock node, Object data);
  public Object visit(ASTPhpStatement node, Object data);
  public Object visit(ASTPhpLanguageStatement node, Object data);
  public Object visit(ASTPhpVariableAssignment node, Object data);
  public Object visit(ASTPhpFunctionCall node, Object data);
  public Object visit(ASTPhpOrSomething node, Object data);
  public Object visit(ASTPhpTryCatchBlock node, Object data);
  public Object visit(ASTPhpValue node, Object data);
  public Object visit(ASTPhpValuePossiblyWrapped node, Object data);
  public Object visit(ASTPhpSimpleValue node, Object data);
  public Object visit(ASTPhpSelectArray node, Object data);
  public Object visit(ASTPhpArgumentList node, Object data);
  public Object visit(ASTPhpVariableList node, Object data);
  public Object visit(ASTPhpFunctionDefinition node, Object data);
  public Object visit(ASTPhpFunctionArgumentList node, Object data);
  public Object visit(ASTPhpIfStatement node, Object data);
  public Object visit(ASTPhpClassDefinition node, Object data);
  public Object visit(ASTPhpInterfaceDefinition node, Object data);
  public Object visit(ASTPhpInterfaceList node, Object data);
  public Object visit(ASTPhpClassBlock node, Object data);
  public Object visit(ASTPhpInterfaceBlock node, Object data);
  public Object visit(ASTHtmlBlock node, Object data);
  public Object visit(ASTHtmlTag node, Object data);
  public Object visit(ASTHtmlClosingTag node, Object data);
  public Object visit(ASTHtmlTagAttribute node, Object data);
  public Object visit(ASTHtmlTextBlock node, Object data);
  public Object visit(ASTHtmlComment node, Object data);
}
/* JavaCC - OriginalChecksum=4d5d4d5efdbd3cb61d0d5779aee95e45 (do not edit this line) */
