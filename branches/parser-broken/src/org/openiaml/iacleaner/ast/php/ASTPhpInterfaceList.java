/* Generated By:JJTree: Do not edit this line. ASTPhpInterfaceList.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpInterfaceList extends SimpleNode {
  public ASTPhpInterfaceList(int id) {
    super(id);
  }

  public ASTPhpInterfaceList(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f7bf8f499c4693d819890d9e8bd452c1 (do not edit this line) */