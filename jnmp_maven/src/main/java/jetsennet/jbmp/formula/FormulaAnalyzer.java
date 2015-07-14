/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula;

import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

/**
 * @author GuoXiang
 */
public class FormulaAnalyzer extends Analyzer
{

    @Override
    protected void enter(Node node) throws ParseException
    {
        switch (node.getId())
        {
        case FormulaConstants.EXP_HEAD:
            this.enterExpHead((Token) node);
            break;
        case FormulaConstants.DIF_HEAD:
            this.enterDifHead((Token) node);
            break;
        case FormulaConstants.STR_HEAD:
            this.enterStrHead((Token) node);
            break;
        case FormulaConstants.NAME_HEAD:
            this.enterStrHead((Token) node);
            break;
        case FormulaConstants.OID_HEAD:
            this.enterOidHead((Token) node);
            break;
        case FormulaConstants.COLON:
            this.enterColon((Token) node);
            break;
        case FormulaConstants.LEFT_PAREN:
            this.enterLeftParen((Token) node);
            break;
        case FormulaConstants.RIGHT_PAREN:
            this.enterRightParen((Token) node);
            break;
        case FormulaConstants.SEMI_COLON:
            this.enterSemiColon((Token) node);
            break;
        case FormulaConstants.COMMA:
            this.enterComma((Token) node);
            break;
        case FormulaConstants.ADDOP:
            this.enterAddOp((Token) node);
            break;
        case FormulaConstants.MULOP:
            this.enterMulOp((Token) node);
            break;
        case FormulaConstants.STR:
            this.enterStr((Token) node);
            break;
        case FormulaConstants.OID:
            this.enterOid((Token) node);
            break;
        case FormulaConstants.NUM:
            this.enterNum((Token) node);
            break;
        case FormulaConstants.STR_STRING:
            this.enterStrString((Token) node);
            break;
        case FormulaConstants.S:
            this.enterS((Production) node);
            break;
        case FormulaConstants.R:
            this.enterR((Production) node);
            break;
        case FormulaConstants.N:
            this.enterN((Production) node);
            break;
        case FormulaConstants.EXP:
            this.enterExp((Production) node);
            break;
        case FormulaConstants.EXP1:
            this.enterExp((Production) node);
            break;
        case FormulaConstants.TERM:
            this.enterTerm((Production) node);
            break;
        case FormulaConstants.TERM1:
            this.enterTerm((Production) node);
            break;
        case FormulaConstants.FACTOR:
            this.enterFactor((Production) node);
            break;
        case FormulaConstants.FUNC_STRING:
            this.enterFuncString((Production) node);
            break;
        case FormulaConstants.FUNC_LIST:
            this.enterFuncList((Production) node);
            break;
        case FormulaConstants.FUNC_ELE:
            this.enterFuncEle((Production) node);
            break;
        case FormulaConstants.FUNC_ELE1:
            this.enterFuncEle1((Production) node);
            break;
        case FormulaConstants.SEXP:
            this.enterSexp((Production) node);
            break;
        case FormulaConstants.SEXP_TERM:
            this.enterSexpTerm((Production) node);
            break;
        case FormulaConstants.OID_STRING:
            this.enterOidString((Production) node);
            break;
        case FormulaConstants.IDENTIFIER_STRING:
            this.enterIdentifierString((Production) node);
            break;
        case FormulaConstants.FUNC_NAME:
            this.enterFuncName((Production) node);
            break;
        default:
            break;
        }
    }

    @Override
    protected Node exit(Node node) throws ParseException
    {
        switch (node.getId())
        {
        case FormulaConstants.EXP_HEAD:
            return exitExpHead((Token) node);
        case FormulaConstants.DIF_HEAD:
            return exitDifHead((Token) node);
        case FormulaConstants.STR_HEAD:
            return exitStrHead((Token) node);
        case FormulaConstants.NAME_HEAD:
            return exitStrHead((Token) node);
        case FormulaConstants.OID_HEAD:
            return exitOidHead((Token) node);
        case FormulaConstants.COLON:
            return exitColon((Token) node);
        case FormulaConstants.LEFT_PAREN:
            return exitLeftParen((Token) node);
        case FormulaConstants.RIGHT_PAREN:
            return exitRightParen((Token) node);
        case FormulaConstants.SEMI_COLON:
            return exitSemiColon((Token) node);
        case FormulaConstants.COMMA:
            return exitComma((Token) node);
        case FormulaConstants.ADDOP:
            return exitAddOp((Token) node);
        case FormulaConstants.MULOP:
            return exitMulOp((Token) node);
        case FormulaConstants.STR:
            return exitStr((Token) node);
        case FormulaConstants.OID:
            return exitOid((Token) node);
        case FormulaConstants.NUM:
            return exitNum((Token) node);
        case FormulaConstants.STR_STRING:
            return exitStrString((Token) node);
        case FormulaConstants.S:
            return exitS((Production) node);
        case FormulaConstants.R:
            return exitR((Production) node);
        case FormulaConstants.N:
            return exitN((Production) node);
        case FormulaConstants.EXP:
            return exitExp((Production) node);
        case FormulaConstants.EXP1:
            return exitExp((Production) node);
        case FormulaConstants.TERM:
            return exitTerm((Production) node);
        case FormulaConstants.TERM1:
            return exitTerm((Production) node);
        case FormulaConstants.FACTOR:
            return exitFactor((Production) node);
        case FormulaConstants.FUNC_STRING:
            return exitFuncString((Production) node);
        case FormulaConstants.FUNC_LIST:
            return exitFuncList((Production) node);
        case FormulaConstants.FUNC_ELE:
            return exitFuncEle((Production) node);
        case FormulaConstants.FUNC_ELE1:
            return exitFuncEle1((Production) node);
        case FormulaConstants.SEXP:
            return exitSexp((Production) node);
        case FormulaConstants.SEXP_TERM:
            return exitSexpTerm((Production) node);
        case FormulaConstants.OID_STRING:
            return exitOidString((Production) node);
        case FormulaConstants.IDENTIFIER_STRING:
            return exitIdentifierString((Production) node);
        case FormulaConstants.FUNC_NAME:
            return exitFuncName((Production) node);
        default:
            break;
        }
        return node;
    }

    @Override
    protected void child(Production node, Node child) throws ParseException
    {
        switch (node.getId())
        {
        case FormulaConstants.S:
            this.childS(node, child);
            break;
        case FormulaConstants.R:
            this.childR(node, child);
            break;
        case FormulaConstants.N:
            this.childN(node, child);
            break;
        case FormulaConstants.EXP:
            this.childExp(node, child);
            break;
        case FormulaConstants.EXP1:
            this.childExp1(node, child);
            break;
        case FormulaConstants.TERM:
            this.childTerm(node, child);
            break;
        case FormulaConstants.TERM1:
            this.childTerm1(node, child);
            break;
        case FormulaConstants.FACTOR:
            this.childFactor(node, child);
            break;
        case FormulaConstants.FUNC_STRING:
            this.childFuncString(node, child);
            break;
        case FormulaConstants.FUNC_LIST:
            this.childFuncList(node, child);
            break;
        case FormulaConstants.FUNC_ELE:
            this.childFuncEle(node, child);
            break;
        case FormulaConstants.FUNC_ELE1:
            this.childFuncEle1(node, child);
            break;
        case FormulaConstants.SEXP:
            this.childSexp(node, child);
            break;
        case FormulaConstants.SEXP_TERM:
            this.childSexpTerm(node, child);
            break;
        case FormulaConstants.OID_STRING:
            this.childOidString(node, child);
            break;
        case FormulaConstants.IDENTIFIER_STRING:
            this.childIdentifierString(node, child);
            break;
        case FormulaConstants.FUNC_NAME:
            this.childFuncName(node, child);
            break;
        default:
            break;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="EXP_HEAD">
    protected void enterExpHead(Token node) throws ParseException
    {
    }

    protected Node exitExpHead(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DIF_HEAD">
    protected void enterDifHead(Token node) throws ParseException
    {
    }

    protected Node exitDifHead(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STR_HEAD">
    protected void enterStrHead(Token node) throws ParseException
    {
    }

    protected Node exitStrHead(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NAME_HEAD">
    protected void enterNameHead(Token node) throws ParseException
    {
    }

    protected Node exitNameHead(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NAME_HEAD">
    protected void enterOidHead(Token node) throws ParseException
    {
    }

    protected Node exitOidHead(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="COLON">
    protected void enterColon(Token node) throws ParseException
    {
    }

    protected Node exitColon(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LEFT_PAREN">
    protected void enterLeftParen(Token node) throws ParseException
    {
    }

    protected Node exitLeftParen(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RIGHT_PAREN">
    protected void enterRightParen(Token node) throws ParseException
    {
    }

    protected Node exitRightParen(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SEMI_COLON">
    protected void enterSemiColon(Token node) throws ParseException
    {
    }

    protected Node exitSemiColon(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="COMMA">
    protected void enterComma(Token node) throws ParseException
    {
    }

    protected Node exitComma(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ADDOP">
    protected void enterAddOp(Token node) throws ParseException
    {
    }

    protected Node exitAddOp(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MULOP">
    protected void enterMulOp(Token node) throws ParseException
    {
    }

    protected Node exitMulOp(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STR">
    protected void enterStr(Token node) throws ParseException
    {
    }

    protected Node exitStr(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OID">
    protected void enterOid(Token node) throws ParseException
    {
    }

    protected Node exitOid(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NUM">
    protected void enterNum(Token node) throws ParseException
    {

    }

    protected Node exitNum(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STR_STRING">
    protected void enterStrString(Token node) throws ParseException
    {
    }

    protected Node exitStrString(Token node) throws ParseException
    {
        return node;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="S">
    protected void enterS(Production node) throws ParseException
    {
    }

    protected Node exitS(Production node) throws ParseException
    {
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
    }

    protected Node exitR(Production node) throws ParseException
    {
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
    }

    protected Node exitN(Production node) throws ParseException
    {
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
    }

    protected Node exitExp(Production node) throws ParseException
    {
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
    }

    protected Node exitExp1(Production node) throws ParseException
    {
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
    }

    protected Node exitTerm(Production node) throws ParseException
    {
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
    }

    protected Node exitTerm1(Production node) throws ParseException
    {
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
    }

    protected Node exitFactor(Production node) throws ParseException
    {
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
    }

    protected Node exitFuncString(Production node) throws ParseException
    {
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
    }

    protected Node exitFuncList(Production node) throws ParseException
    {
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
    }

    protected Node exitFuncEle(Production node) throws ParseException
    {
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
    }

    protected Node exitFuncEle1(Production node) throws ParseException
    {
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
    }

    protected Node exitSexp(Production node) throws ParseException
    {
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
    }

    protected Node exitSexpTerm(Production node) throws ParseException
    {
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
    }

    protected Node exitOidString(Production node) throws ParseException
    {
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
    }

    protected Node exitIdentifierString(Production node) throws ParseException
    {
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
    }

    protected Node exitFuncName(Production node) throws ParseException
    {
        return node;
    }

    protected void childFuncName(Production node, Node child) throws ParseException
    {
        node.addChild(child);
    }
    // </editor-fold>
}
