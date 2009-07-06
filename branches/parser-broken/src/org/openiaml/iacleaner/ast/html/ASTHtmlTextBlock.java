package org.openiaml.iacleaner.ast.html;

public class ASTHtmlTextBlock extends SimpleNode {
  public ASTHtmlTextBlock(int id) {
    super(id);
  }

  public ASTHtmlTextBlock(HtmlPage p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HtmlPageVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public String text = null;
  
  public void setText(String n) { this.text = n; }
  public String getText() { return text; }
  
  /**
   * {@inheritDoc}
   * @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
   */
  public String toString() {
    return "HtmlTextBlock \"" + text.trim() + "\" (parent=" + jjtGetParent() + ")";
  }
  
}
