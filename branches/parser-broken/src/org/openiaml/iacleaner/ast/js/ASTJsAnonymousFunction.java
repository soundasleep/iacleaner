/* Generated By:JJTree: Do not edit this line. ASTJsAnonymousFunction.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTJsAnonymousFunction extends SimpleNode {
  public ASTJsAnonymousFunction(int id) {
    super(id);
  }

  public ASTJsAnonymousFunction(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d73c08feecee6d5b0b93a810f122ae57 (do not edit this line) */
