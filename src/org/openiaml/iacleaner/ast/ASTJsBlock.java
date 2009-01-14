package org.openiaml.iacleaner.ast;

public class ASTJsBlock extends SimpleNode {
  public ASTJsBlock(int id) {
    super(id);
  }

  public ASTJsBlock(InternetApplication p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public String script = null;
  
  public void setScript(String n) { this.script = n; }
  public String getScript() { return script; }
  
  /**
   * {@inheritDoc}
   * @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
   */
  public String toString() {
    return "JsBlock: " + script + " (parent=" + jjtGetParent() + ")";
  }
  
}
