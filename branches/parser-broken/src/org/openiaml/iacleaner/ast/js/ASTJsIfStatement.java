/* Generated By:JJTree: Do not edit this line. ASTJsIfStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTJsIfStatement extends SimpleNode {
  public ASTJsIfStatement(int id) {
    super(id);
  }

  public ASTJsIfStatement(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=578d6dc013d831798a9fb5cd5ebb41e0 (do not edit this line) */
