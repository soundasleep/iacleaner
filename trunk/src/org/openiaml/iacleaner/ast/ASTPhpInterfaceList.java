/* Generated By:JJTree: Do not edit this line. ASTPhpInterfaceList.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTPhpInterfaceList extends SimpleNode {
  public ASTPhpInterfaceList(int id) {
    super(id);
  }

  public ASTPhpInterfaceList(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=5f869d401c2b6533287789d2747e33e3 (do not edit this line) */
