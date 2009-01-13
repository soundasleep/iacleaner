/**
 * 
 */
package org.openiaml.iacleaner;

import org.openiaml.iacleaner.ast.ASTBlock;
import org.openiaml.iacleaner.ast.ASTHtmlBlock;
import org.openiaml.iacleaner.ast.ASTHtmlClosingTag;
import org.openiaml.iacleaner.ast.ASTHtmlComment;
import org.openiaml.iacleaner.ast.ASTHtmlScriptTag;
import org.openiaml.iacleaner.ast.ASTHtmlTag;
import org.openiaml.iacleaner.ast.ASTHtmlTagAttribute;
import org.openiaml.iacleaner.ast.ASTHtmlTextBlock;
import org.openiaml.iacleaner.ast.ASTJsArgumentList;
import org.openiaml.iacleaner.ast.ASTJsBlock;
import org.openiaml.iacleaner.ast.ASTJsChainedOperator;
import org.openiaml.iacleaner.ast.ASTJsFunctionArgumentList;
import org.openiaml.iacleaner.ast.ASTJsFunctionCall;
import org.openiaml.iacleaner.ast.ASTJsFunctionDefinition;
import org.openiaml.iacleaner.ast.ASTJsIfStatement;
import org.openiaml.iacleaner.ast.ASTJsLanguageStatement;
import org.openiaml.iacleaner.ast.ASTJsReturnStatement;
import org.openiaml.iacleaner.ast.ASTJsSimpleValue;
import org.openiaml.iacleaner.ast.ASTJsStatement;
import org.openiaml.iacleaner.ast.ASTJsValue;
import org.openiaml.iacleaner.ast.ASTJsVariableAssignment;
import org.openiaml.iacleaner.ast.ASTJsVariableList;
import org.openiaml.iacleaner.ast.ASTPhpArgumentList;
import org.openiaml.iacleaner.ast.ASTPhpBlock;
import org.openiaml.iacleaner.ast.ASTPhpClassBlock;
import org.openiaml.iacleaner.ast.ASTPhpClassDefinition;
import org.openiaml.iacleaner.ast.ASTPhpFunctionArgumentList;
import org.openiaml.iacleaner.ast.ASTPhpFunctionCall;
import org.openiaml.iacleaner.ast.ASTPhpFunctionDefinition;
import org.openiaml.iacleaner.ast.ASTPhpIfStatement;
import org.openiaml.iacleaner.ast.ASTPhpInlineHtmlBlock;
import org.openiaml.iacleaner.ast.ASTPhpInterfaceBlock;
import org.openiaml.iacleaner.ast.ASTPhpInterfaceDefinition;
import org.openiaml.iacleaner.ast.ASTPhpInterfaceList;
import org.openiaml.iacleaner.ast.ASTPhpLanguageStatement;
import org.openiaml.iacleaner.ast.ASTPhpOrSomething;
import org.openiaml.iacleaner.ast.ASTPhpRootBlock;
import org.openiaml.iacleaner.ast.ASTPhpSelectArray;
import org.openiaml.iacleaner.ast.ASTPhpSimpleValue;
import org.openiaml.iacleaner.ast.ASTPhpStatement;
import org.openiaml.iacleaner.ast.ASTPhpTryCatchBlock;
import org.openiaml.iacleaner.ast.ASTPhpValue;
import org.openiaml.iacleaner.ast.ASTPhpValuePossiblyWrapped;
import org.openiaml.iacleaner.ast.ASTPhpVariableAssignment;
import org.openiaml.iacleaner.ast.ASTPhpVariableList;
import org.openiaml.iacleaner.ast.ASTStart;
import org.openiaml.iacleaner.ast.ASTStartJs;
import org.openiaml.iacleaner.ast.InternetApplicationVisitor;
import org.openiaml.iacleaner.ast.SimpleNode;

/**
 * A simple visitor that traverses the node tree and outputs
 * a syntax formatted version.
 * 
 * @author jmwright
 *
 */
public class PrettifierVisitor implements InternetApplicationVisitor {
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
	public Object visit(ASTPhpRootBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpLanguageStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpValue node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpSimpleValue node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpArgumentList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpVariableList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpFunctionDefinition node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpFunctionArgumentList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpIfStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpClassDefinition node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpInterfaceDefinition node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpInterfaceList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpClassBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTPhpInterfaceBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTHtmlBlock node, Object data) {
		node.childrenAccept(this, data);	// visit children (nothing to see here)
		return null;
	}

	@Override
	public Object visit(ASTPhpInlineHtmlBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTHtmlComment node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTHtmlScriptTag node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTJsBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	@Override
	public Object visit(ASTStartJs node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsStatement, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsLanguageStatement, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsLanguageStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsValue, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsValue node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsSimpleValue, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsSimpleValue node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsArgumentList, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsArgumentList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsVariableList, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsVariableList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsFunctionDefinition, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsFunctionDefinition node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsFunctionArgumentList, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsFunctionArgumentList node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsIfStatement, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsIfStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsChainedOperator, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsChainedOperator node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTPhpOrSomething, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpOrSomething node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsFunctionCall, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsFunctionCall node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsReturnStatement, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsReturnStatement node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTJsVariableAssignment, java.lang.Object)
	 */
	@Override
	public Object visit(ASTJsVariableAssignment node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTPhpSelectArray, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpSelectArray node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTPhpFunctionCall, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpFunctionCall node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTPhpTryCatchBlock, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpTryCatchBlock node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTPhpVariableAssignment, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpVariableAssignment node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.ast.InternetApplicationVisitor#visit(org.openiaml.iacleaner.ast.ASTPhpValuePossiblyWrapped, java.lang.Object)
	 */
	@Override
	public Object visit(ASTPhpValuePossiblyWrapped node, Object data) {
		((StringBuffer) data).append("unknown node: " + node);
		return null;
	}
	
}
