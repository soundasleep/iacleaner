/* Generated By:JJTree: Do not edit this line. ASTPhpInterfaceBlock.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpInterfaceBlock extends SimpleNode {
  public ASTPhpInterfaceBlock(int id) {
    super(id);
  }

  public ASTPhpInterfaceBlock(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=26073be2da33c9931050a3afd3457842 (do not edit this line) */
