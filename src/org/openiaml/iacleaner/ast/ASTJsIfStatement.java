/* Generated By:JJTree: Do not edit this line. ASTJsIfStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTJsIfStatement extends SimpleNode {
  public ASTJsIfStatement(int id) {
    super(id);
  }

  public ASTJsIfStatement(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6af7153b039406c1e755d4d5bb1e4eaf (do not edit this line) */
