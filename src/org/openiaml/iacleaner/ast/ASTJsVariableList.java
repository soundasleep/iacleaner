/* Generated By:JJTree: Do not edit this line. ASTJsVariableList.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTJsVariableList extends SimpleNode {
  public ASTJsVariableList(int id) {
    super(id);
  }

  public ASTJsVariableList(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=710e6fd1d27bad0f13e699dbb9963ff0 (do not edit this line) */