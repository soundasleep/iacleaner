/* Generated By:JJTree: Do not edit this line. ASTPhpFunctionDefinition.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.php;

public class ASTPhpFunctionDefinition extends SimpleNode {
  public ASTPhpFunctionDefinition(int id) {
    super(id);
  }

  public ASTPhpFunctionDefinition(PhpPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PhpPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8463b5aa5ab928e93f8f88724d8f3cb8 (do not edit this line) */