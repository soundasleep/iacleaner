/**
 * 
 */
package org.openiaml.iacleaner;

import org.openiaml.iacleaner.ast.html.ASTBlock;
import org.openiaml.iacleaner.ast.html.ASTHtmlBlock;
import org.openiaml.iacleaner.ast.html.ASTHtmlClosingTag;
import org.openiaml.iacleaner.ast.html.ASTHtmlComment;
import org.openiaml.iacleaner.ast.html.ASTHtmlPhpInlineBlock;
import org.openiaml.iacleaner.ast.html.ASTHtmlScriptTag;
import org.openiaml.iacleaner.ast.html.ASTHtmlTag;
import org.openiaml.iacleaner.ast.html.ASTHtmlTagAttribute;
import org.openiaml.iacleaner.ast.html.ASTHtmlTextBlock;
import org.openiaml.iacleaner.ast.html.ASTJsBlock;
import org.openiaml.iacleaner.ast.html.ASTPhpBlock;
import org.openiaml.iacleaner.ast.html.ASTStart;
import org.openiaml.iacleaner.ast.html.HtmlPageVisitor;
import org.openiaml.iacleaner.ast.html.SimpleNode;

/**
 * A simple visitor that traverses the node tree and outputs
 * a syntax formatted version.
 * 
 * @author jmwright
 *
 */
public class PrettifierVisitor implements HtmlPageVisitor {
	@Override
	public Object visit(SimpleNode node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTStart node, Object data) {
		node.childrenAccept(this, data);	// visit children
		return null;
	}

	@Override
	public Object visit(ASTHtmlTag node, Object data) {
		((StringBuffer) data).append("<").append(node.getName());
		node.childrenAccept(this, data);	// visit attributes
		((StringBuffer) data).append(">");
		return null;
	}

	@Override
	public Object visit(ASTHtmlClosingTag node, Object data) {
		((StringBuffer) data).append("</").append(node.getName());
		((StringBuffer) data).append(">");
		return null;
	}

	@Override
	public Object visit(ASTHtmlTagAttribute node, Object data) {
		((StringBuffer) data).append(" ").append(node.getName()).append("=").append(node.getValue());
		return null;
	}

	@Override
	public Object visit(ASTHtmlTextBlock node, Object data) {
		((StringBuffer) data).append(node.getText().trim());
		return null;
	}

	@Override
	public Object visit(ASTBlock node, Object data) {
		node.childrenAccept(this, data);	// visit children
		return null;
	}

	@Override
	public Object visit(ASTHtmlBlock node, Object data) {
		node.childrenAccept(this, data);	// visit children (nothing to see here)
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTHtmlScriptTag, java.lang.Object)
	 */
	@Override
	public Object visit(ASTHtmlScriptTag node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsBlock, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.html.HtmlPageVisitor#visit(org.openiaml.iacleaner.ast.html.ASTHtmlPhpInlineBlock, java.lang.Object)
	 */
	@Override
	public Object visit(ASTHtmlPhpInlineBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.html.HtmlPageVisitor#visit(org.openiaml.iacleaner.ast.html.ASTHtmlComment, java.lang.Object)
	 */
	@Override
	public Object visit(ASTHtmlComment node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.html.HtmlPageVisitor#visit(org.openiaml.iacleaner.ast.html.ASTPhpBlock, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}
	
}
