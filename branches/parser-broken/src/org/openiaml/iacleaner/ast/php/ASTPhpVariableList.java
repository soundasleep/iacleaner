/* Generated By:JJTree: Do not edit this line. ASTPhpVariableList.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpVariableList extends SimpleNode {
  public ASTPhpVariableList(int id) {
    super(id);
  }

  public ASTPhpVariableList(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4175b1afacf7f9d8f449579e9e9526d6 (do not edit this line) */
