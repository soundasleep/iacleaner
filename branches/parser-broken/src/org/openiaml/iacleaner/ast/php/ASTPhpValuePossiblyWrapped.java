/* Generated By:JJTree: Do not edit this line. ASTPhpValuePossiblyWrapped.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpValuePossiblyWrapped extends SimpleNode {
  public ASTPhpValuePossiblyWrapped(int id) {
    super(id);
  }

  public ASTPhpValuePossiblyWrapped(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c978a5258fa6079353a9cc71d56a9155 (do not edit this line) */
