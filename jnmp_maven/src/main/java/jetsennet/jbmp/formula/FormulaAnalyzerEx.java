/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula;

import java.util.Stack;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

/**
 * @author GuoXiang
 */
public class FormulaAnalyzerEx extends FormulaAnalyzer
{

    private Stack<FormulaElement> stack;
    private FormulaElement root;

    /**
     * 构造函数
     */
    public FormulaAnalyzerEx()
    {
        stack = new Stack<FormulaElement>();
        root = null;
    }

    // <editor-fold defaultstate="collapsed" desc="入栈、出栈">
    private void push(Node node)
    {
        FormulaElement parent = stack.peek();
        FormulaElement fe = new FormulaElement(node);
        parent.addChildren(fe);
        stack.push(fe);
    }

    private FormulaElement pull()
    {
        return stack.pop();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="终结符">
    // <editor-fold defaultstate="collapsed" desc="EXP_HEAD">
    protected void enterExpHead(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitExpHead(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DIF_HEAD">
    protected void enterDifHead(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitDifHead(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STR_HEAD">
    protected void enterStrHead(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitStrHead(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NAME_HEAD">
    protected void enterNameHead(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitNameHead(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NAME_HEAD">
    protected void enterOidHead(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitOidHead(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="COLON">
    protected void enterColon(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitColon(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LEFT_PAREN">
    protected void enterLeftParen(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitLeftParen(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RIGHT_PAREN">
    protected void enterRightParen(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitRightParen(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SEMI_COLON">
    protected void enterSemiColon(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitSemiColon(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="COMMA">
    protected void enterComma(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitComma(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ADDOP">
    protected void enterAddOp(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitAddOp(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MULOP">
    protected void enterMulOp(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitMulOp(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STR">
    protected void enterStr(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitStr(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OID">
    protected void enterOid(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitOid(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NUM">
    protected void enterNum(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitNum(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STR_STRING">
    protected void enterStrString(Token node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitStrString(Token node) throws ParseException
    {
        this.pull();
        return node;
    }

    // </editor-fold>
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="非终结符">
    // <editor-fold defaultstate="collapsed" desc="S">
    protected void enterS(Production node) throws ParseException
    {
        root = new FormulaElement(node);
        stack.push(root);
    }

    protected Node exitS(Production node) throws ParseException
    {
        stack.pop();
        return node;
    }

    protected void childS(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="R">
    protected void enterR(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitR(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childR(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="N">
    protected void enterN(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitN(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childN(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="EXP">
    protected void enterExp(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitExp(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childExp(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="EXP1">
    protected void enterExp1(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitExp1(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childExp1(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TERM">
    protected void enterTerm(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitTerm(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childTerm(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TERM1">
    protected void enterTerm1(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitTerm1(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childTerm1(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FACTOR">
    protected void enterFactor(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitFactor(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childFactor(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FUNC_STRING">
    protected void enterFuncString(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitFuncString(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childFuncString(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FUNC_LIST">
    protected void enterFuncList(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitFuncList(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childFuncList(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FUNC_ELE">
    protected void enterFuncEle(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitFuncEle(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childFuncEle(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FUNC_ELE1">
    protected void enterFuncEle1(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitFuncEle1(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childFuncEle1(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SEXP">
    protected void enterSexp(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitSexp(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childSexp(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SEXP_TERM">
    protected void enterSexpTerm(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitSexpTerm(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childSexpTerm(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OID_STRING">
    protected void enterOidString(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitOidString(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childOidString(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="IDENTIFIER_STRING">
    protected void enterIdentifierString(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitIdentifierString(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childIdentifierString(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FUNC_NAME">
    protected void enterFuncName(Production node) throws ParseException
    {
        this.push(node);
    }

    protected Node exitFuncName(Production node) throws ParseException
    {
        this.pull();
        return node;
    }

    protected void childFuncName(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }

    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the stack
     */
    public Stack<FormulaElement> getStack()
    {
        return stack;
    }

    /**
     * @param stack the stack to set
     */
    public void setStack(Stack<FormulaElement> stack)
    {
        this.stack = stack;
    }

    /**
     * @return the root
     */
    public FormulaElement getRoot()
    {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(FormulaElement root)
    {
        this.root = root;
    }
    // </editor-fold>
}
