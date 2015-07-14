/************************************************************************
日 期：2011-12-27
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.formula;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * @author GuoXiang
 */
public class FormulaParse extends RecursiveDescentParser
{
    /**
     * 构造函数
     * @param in 参数
     * @throws ParserCreationException 异常
     */
    public FormulaParse(Reader in) throws ParserCreationException
    {
        super(in);
        createPatterns();
    }

    /**
     * 构造函数
     * @param in 参数
     * @param analyzer 参数
     * @throws ParserCreationException 异常
     */
    public FormulaParse(Reader in, FormulaAnalyzer analyzer) throws ParserCreationException
    {

        super(in, analyzer);
        createPatterns();
    }

    @Override
    protected Tokenizer newTokenizer(Reader in) throws ParserCreationException
    {
        return new FormulaTokenizer(in);
    }

    /**
     * @throws ParserCreationException 异常
     */
    private void createPatterns() throws ParserCreationException
    {
        ProductionPattern pattern;
        ProductionPatternAlternative alt;

        pattern = new ProductionPattern(FormulaConstants.S, "S");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.R, 1, 1);
        alt.addProduction(FormulaConstants.N, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.R, "R");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.EXP_HEAD, 1, 1);
        alt.addToken(FormulaConstants.COLON, 1, 1);
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(FormulaConstants.EXP, 1, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.DIF_HEAD, 1, 1);
        alt.addToken(FormulaConstants.COLON, 1, 1);
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(FormulaConstants.EXP, 1, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.STR_HEAD, 1, 1);
        alt.addToken(FormulaConstants.COLON, 1, 1);
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(FormulaConstants.SEXP, 1, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.N, "N");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.SEMI_COLON, 1, 1);
        alt.addToken(FormulaConstants.NAME_HEAD, 1, 1);
        alt.addToken(FormulaConstants.COLON, 1, 1);
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(FormulaConstants.SEXP, 1, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.EXP, "EXP");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.TERM, 1, 1);
        alt.addProduction(FormulaConstants.EXP1, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.EXP1, "EXP1");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.ADDOP, 1, 1);
        alt.addProduction(FormulaConstants.TERM, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.TERM, "TERM");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.FACTOR, 1, 1);
        alt.addProduction(FormulaConstants.TERM1, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.TERM1, "TERM");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.MULOP, 1, 1);
        alt.addProduction(FormulaConstants.FACTOR, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.FACTOR, "FACTOR");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.NUM, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(FormulaConstants.EXP, 1, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.IDENTIFIER_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.FUNC_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.OID_STRING, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.FUNC_STRING, "FUNC_STRING");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.FUNC_NAME, 1, 1);
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(FormulaConstants.FUNC_LIST, 0, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.FUNC_LIST, "FUNC_LIST");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.FUNC_ELE, 1, 1);
        alt.addProduction(FormulaConstants.FUNC_ELE1, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.FUNC_ELE, "FUNC_ELE");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.FUNC_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.IDENTIFIER_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.NUM, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.STR_STRING, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.FUNC_ELE1, "FUNC_ELE1");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.COMMA, 1, 1);
        alt.addProduction(FormulaConstants.FUNC_ELE, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.OID_STRING, "OID_STRING");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.OID_HEAD, 1, 1);
        alt.addToken(FormulaConstants.COLON, 1, 1);
        alt.addToken(FormulaConstants.LEFT_PAREN, 1, 1);
        alt.addToken(FormulaConstants.OID, 1, 1);
        alt.addToken(FormulaConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.SEXP, "SEXP");
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.SEXP_TERM, 1, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.SEXP_TERM, "SEXP_TERM");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.STR_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.IDENTIFIER_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(FormulaConstants.OID_STRING, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.IDENTIFIER_STRING, "IDENTIFIER_STRING");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.STR, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(FormulaConstants.FUNC_NAME, "FUNC_NAME");
        alt = new ProductionPatternAlternative();
        alt.addToken(FormulaConstants.STR, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
