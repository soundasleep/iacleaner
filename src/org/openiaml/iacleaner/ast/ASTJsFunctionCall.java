package org.openiaml.iacleaner.ast;

public class ASTJsFunctionCall extends SimpleNode {
  public ASTJsFunctionCall(int id) {
    super(id);
  }

  public ASTJsFunctionCall(InternetApplication p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(InternetApplicationVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public String name = null;
  
  public void setName(String n) { this.name = n; }
  public String getName() { return name; }
  
  /**
   * {@inheritDoc}
   * @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
   */
  public String toString() {
    return "JsFunctionCall: " + name + " (parent=" + jjtGetParent() + ")";
  }
}

