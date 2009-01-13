/* Generated By:JavaCC: Do not edit this line. InternetApplicationVisitor.java Version 4.1d1 */
package org.openiaml.iacleaner.ast;

public interface InternetApplicationVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTStart node, Object data);
  public Object visit(ASTStartJs node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTPhpRootBlock node, Object data);
  public Object visit(ASTPhpBlock node, Object data);
  public Object visit(ASTPhpInlineHtmlBlock node, Object data);
  public Object visit(ASTPhpStatement node, Object data);
  public Object visit(ASTPhpLanguageStatement node, Object data);
  public Object visit(ASTPhpOrSomething node, Object data);
  public Object visit(ASTPhpValue node, Object data);
  public Object visit(ASTPhpSimpleValue node, Object data);
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
  public Object visit(ASTHtmlScriptTag node, Object data);
  public Object visit(ASTHtmlTag node, Object data);
  public Object visit(ASTHtmlClosingTag node, Object data);
  public Object visit(ASTHtmlTagAttribute node, Object data);
  public Object visit(ASTHtmlTextBlock node, Object data);
  public Object visit(ASTHtmlComment node, Object data);
  public Object visit(ASTJsBlock node, Object data);
  public Object visit(ASTJsStatement node, Object data);
  public Object visit(ASTJsLanguageStatement node, Object data);
  public Object visit(ASTJsFunctionCall node, Object data);
  public Object visit(ASTJsReturnStatement node, Object data);
  public Object visit(ASTJsVariableAssignment node, Object data);
  public Object visit(ASTJsChainedOperator node, Object data);
  public Object visit(ASTJsValue node, Object data);
  public Object visit(ASTJsSimpleValue node, Object data);
  public Object visit(ASTJsArgumentList node, Object data);
  public Object visit(ASTJsVariableList node, Object data);
  public Object visit(ASTJsFunctionDefinition node, Object data);
  public Object visit(ASTJsFunctionArgumentList node, Object data);
  public Object visit(ASTJsIfStatement node, Object data);
}
/* JavaCC - OriginalChecksum=644d336219a64bcd2fd8c20ee5900904 (do not edit this line) */
