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
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * @author GuoXiang
 */
public class FormulaTokenizer extends Tokenizer
{
    /**
     * 构造函数
     * @param input 输入
     * @throws ParserCreationException 异常
     */
    public FormulaTokenizer(Reader input) throws ParserCreationException
    {
        super(input, false);
        this.createPatterns();
    }

    private void createPatterns() throws ParserCreationException
    {
        TokenPattern pattern;

        pattern = new TokenPattern(FormulaConstants.EXP_HEAD, "EXP_HEAD", TokenPattern.STRING_TYPE, "exp");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.DIF_HEAD, "DIF_HEAD", TokenPattern.STRING_TYPE, "dif");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.STR_HEAD, "STR_HEAD", TokenPattern.STRING_TYPE, "str");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.NAME_HEAD, "NAME_HEAD", TokenPattern.STRING_TYPE, "name");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.OID_HEAD, "OID_HEAD", TokenPattern.STRING_TYPE, "OID");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.COLON, "COLON", TokenPattern.STRING_TYPE, ":");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.LEFT_PAREN, "LEFT_PAREN", TokenPattern.STRING_TYPE, "(");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.RIGHT_PAREN, "RIGHT_PAREN", TokenPattern.STRING_TYPE, ")");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.SEMI_COLON, "SEMI_COLON", TokenPattern.STRING_TYPE, ";");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.COMMA, "COMMA", TokenPattern.STRING_TYPE, ",");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.ADDOP, "ADDOP", TokenPattern.REGEXP_TYPE, "(\\+|\\-)");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.MULOP, "MULOP", TokenPattern.REGEXP_TYPE, "(\\*|/)");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.STR, "STR", TokenPattern.REGEXP_TYPE, "[a-zA-Z][a-zA-Z0-9_]*");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.OID, "OID", TokenPattern.REGEXP_TYPE, "([0-9]+\\.)+([0-9]+)");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.NUM, "NUM", TokenPattern.REGEXP_TYPE, "[0-9]+");
        addPattern(pattern);

        pattern = new TokenPattern(FormulaConstants.STR_STRING, "STR_STRING", TokenPattern.REGEXP_TYPE, "\"[^\"]*\"");
        addPattern(pattern);
    }
}
