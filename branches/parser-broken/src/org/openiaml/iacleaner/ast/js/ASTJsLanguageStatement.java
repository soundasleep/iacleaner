/* Generated By:JJTree: Do not edit this line. ASTJsLanguageStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTJsLanguageStatement extends SimpleNode {
  public ASTJsLanguageStatement(int id) {
    super(id);
  }

  public ASTJsLanguageStatement(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e134dfc68b6f079ca6c7298ae61440b1 (do not edit this line) */
