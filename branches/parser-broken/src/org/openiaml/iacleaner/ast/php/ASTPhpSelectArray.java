/* Generated By:JJTree: Do not edit this line. ASTPhpSelectArray.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpSelectArray extends SimpleNode {
  public ASTPhpSelectArray(int id) {
    super(id);
  }

  public ASTPhpSelectArray(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c3f18c4852ed2e7568d95e923ba27b8e (do not edit this line) */
