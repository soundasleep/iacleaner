package org.openiaml.iacleaner.ast;

public class ASTHtmlTag extends SimpleNode {
  public ASTHtmlTag(int id) {
    super(id);
  }

  public ASTHtmlTag(InternetApplication p, int id) {
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
    return "HtmlTag: " + name + " (parent=" + jjtGetParent() + ")";
  }
  
}