/* Generated By:JJTree: Do not edit this line. ASTPhpInterfaceBlock.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTPhpInterfaceBlock extends SimpleNode {
  public ASTPhpInterfaceBlock(int id) {
    super(id);
  }

  public ASTPhpInterfaceBlock(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=da98f07aae5605e8ebfccafb8c287f89 (do not edit this line) */