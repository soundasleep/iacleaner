package org.openiaml.iacleaner.ast.js;

public class ASTPhpBlock extends SimpleNode {
  public ASTPhpBlock(int id) {
    super(id);
  }

  public ASTPhpBlock(Javascript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavascriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  private String script = null;
  
  public void setScript(String n) { this.script = n; }
  public String getScript() { return script; }
  
  /**
   * {@inheritDoc}
   * @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
   */
  public String toString() {
    return "PhpBlock (parent=" + jjtGetParent() + " script=" + script + ")";
  }
}