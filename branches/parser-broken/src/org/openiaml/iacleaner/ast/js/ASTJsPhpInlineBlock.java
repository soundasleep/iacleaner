/* Generated By:JJTree: Do not edit this line. ASTJsPhpInlineBlock.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast.js;

public class ASTJsPhpInlineBlock extends SimpleNode {
  public ASTJsPhpInlineBlock(int id) {
    super(id);
  }

  public ASTJsPhpInlineBlock(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d7b1ce10096335027361ab883b002cf0 (do not edit this line) */