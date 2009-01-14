package org.openiaml.iacleaner.ast.html;

public class ASTHtmlTagAttribute extends SimpleNode {
  public ASTHtmlTagAttribute(int id) {
    super(id);
  }

  public ASTHtmlTagAttribute(HtmlPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HtmlPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public String name = null;
  
  public void setName(String n) { this.name = n; }
  public String getName() { return name; }
  
  public String value = null;
  
  public void setValue(String n) { this.value = n; }
  public String getValue() { return value; }
  
  /**
   * {@inheritDoc}
   * @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
   */
  public String toString() {
    return "HtmlTagAttribute: " + name + "=" + value;
  }
  
}

