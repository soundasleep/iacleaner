/* Generated By:JJTree: Do not edit this line. ASTPhpFunctionCall.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTPhpFunctionCall extends SimpleNode {
  public ASTPhpFunctionCall(int id) {
    super(id);
  }

  public ASTPhpFunctionCall(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d0010e17b91600c6113962c5315e2386 (do not edit this line) */
