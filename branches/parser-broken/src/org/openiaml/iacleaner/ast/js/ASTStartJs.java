/* Generated By:JJTree: Do not edit this line. ASTStartJs.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTStartJs extends SimpleNode {
  public ASTStartJs(int id) {
    super(id);
  }

  public ASTStartJs(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=92307643cd3fc472c3f6402abe94c41f (do not edit this line) */