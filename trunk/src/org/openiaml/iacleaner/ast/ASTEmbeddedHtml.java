/* Generated By:JJTree: Do not edit this line. ASTEmbeddedHtml.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTEmbeddedHtml extends SimpleNode {
  public ASTEmbeddedHtml(int id) {
    super(id);
  }

  public ASTEmbeddedHtml(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1a67daa2f10e733c12a03400a63880e9 (do not edit this line) */
