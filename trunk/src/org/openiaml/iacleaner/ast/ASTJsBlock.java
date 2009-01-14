package org.openiaml.iacleaner.ast;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.openiaml.iacleaner.ast.js.Javascript;
import org.openiaml.iacleaner.ast.ParseException;
import org.openiaml.iacleaner.ast.js.SimpleNode;

public class ASTJsBlock extends org.openiaml.iacleaner.ast.SimpleNode {
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

  private String script = null;
  
  public void setScript(String n) { this.script = n; }
  public String getScript() { return script; }
  
  /**
   * {@inheritDoc}
   * @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
   */
  public String toString() {
    return "JsBlock (parent=" + jjtGetParent() + ")";
  }
  
  public void dump(String prefix) {
	    System.out.println(toString(prefix));
	    if (children != null) {
	      for (int i = 0; i < children.length; ++i) {
	  SimpleNode n = (SimpleNode)children[i];
	  if (n != null) {
	    n.dump(prefix + " ");
	  }
	      }
	    }
	    
	    // now, dump the parsed script
	    if (node != null) {
		    System.out.println(prefix + "[parsed script]");		    
		    ((SimpleNode) node).dump(prefix + " ");
	    }
	  }
  
  private org.openiaml.iacleaner.ast.js.SimpleNode node = null;

  /**
   * Parse the script and populate the node if possible.
 * @throws InnerParseException if an exception occured when parsing
   */
  public void parseScript() throws InnerParseException {
	  try {
		node = Javascript.loadString(getScript(), "UTF8");
	} catch (FileNotFoundException e) {
		throw new InnerParseException(e);
	} catch (UnsupportedEncodingException e) {
		throw new InnerParseException(e);
	} catch (org.openiaml.iacleaner.ast.js.ParseException e) {
		throw new InnerParseException(e);
	}
  }
  
  /**
   * Get the calculated node. Will be null if {@link #parseScript()}
   * has not yet been called.
 * @return 
   */
  public SimpleNode getNode() {
	  return node;
  }
  
  public class InnerParseException extends ParseException {
	  
	private static final long serialVersionUID = 1L;
	
	private Throwable cause;

	public InnerParseException(Throwable e) {
		super(e.getMessage());
		cause = e;
	}
	
	public Throwable getCause() {
		return cause;
	}
	  
  }
  
}
