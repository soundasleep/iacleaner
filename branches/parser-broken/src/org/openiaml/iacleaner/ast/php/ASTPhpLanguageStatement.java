/* Generated By:JJTree: Do not edit this line. ASTPhpLanguageStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpLanguageStatement extends SimpleNode {
  public ASTPhpLanguageStatement(int id) {
    super(id);
  }

  public ASTPhpLanguageStatement(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1debede86076deca47b6df94250c6b44 (do not edit this line) */
