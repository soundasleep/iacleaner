/* Generated By:JJTree: Do not edit this line. ASTJsTraverseObjects.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTJsTraverseObjects extends SimpleNode {
  public ASTJsTraverseObjects(int id) {
    super(id);
  }

  public ASTJsTraverseObjects(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c5cc0cd971759e0fd6b8cb7437383115 (do not edit this line) */
