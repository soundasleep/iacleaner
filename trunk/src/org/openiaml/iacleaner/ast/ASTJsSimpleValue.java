/* Generated By:JJTree: Do not edit this line. ASTJsSimpleValue.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.openiaml.iacleaner.ast;

public class ASTJsSimpleValue extends SimpleNode {
  public ASTJsSimpleValue(int id) {
    super(id);
  }

  public ASTJsSimpleValue(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=267ab830c911e28136097db8be2ae05d (do not edit this line) */
