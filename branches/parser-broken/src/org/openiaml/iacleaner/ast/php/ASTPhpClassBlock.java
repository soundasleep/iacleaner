/* Generated By:JJTree: Do not edit this line. ASTPhpClassBlock.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpClassBlock extends SimpleNode {
  public ASTPhpClassBlock(int id) {
    super(id);
  }

  public ASTPhpClassBlock(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=425e74f414cbf38653ede9480a144a96 (do not edit this line) */
