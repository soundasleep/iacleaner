/* Generated By:JJTree: Do not edit this line. ASTJsFunctionArgumentList.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTJsFunctionArgumentList extends SimpleNode {
  public ASTJsFunctionArgumentList(int id) {
    super(id);
  }

  public ASTJsFunctionArgumentList(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=b61bba37c252482fbed7802e924966ce (do not edit this line) */
