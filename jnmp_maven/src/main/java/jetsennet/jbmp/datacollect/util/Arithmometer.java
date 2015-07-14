package jetsennet.jbmp.datacollect.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import jetsennet.jbmp.exception.IllegalExpressionException;

/**
 * 计算器类
 * @author Guo
 */
public class Arithmometer
{

    private Stack<Double> number;
    private Stack<String> notation;
    private Map<String, Integer> priority;
    private ArrayList<String> results;
    private Map<String, String> identifier;
    private int position;
    private static final Logger logger = Logger.getLogger(Arithmometer.class);

    /**
     * 构造函数
     */
    public Arithmometer()
    {
        identifier = new HashMap<String, String>();
        priority = new HashMap<String, Integer>();
        initPriority();
    }

    /**
     * 设置优先级
     */
    public void initPriority()
    {
        priority.put("+", 1);
        priority.put("-", 1);
        priority.put("*", 2);
        priority.put("/", 2);
        priority.put("(", 3);
        priority.put(")", 3);
    }

    /**
     * 计算
     * @param s 参数
     * @return 结果
     * @throws IllegalExpressionException 字符串不合法
     */
    public double calculate(String s) throws IllegalExpressionException
    {
        // logger.debug("原表达式为：" + s);
        // 对表达式中的负数做处理，负数-a被表达成(0-1)的形式
        s = minus(s);
        // logger.debug("符号处理过后的表达式为：" + s);
        number = new Stack<Double>();
        notation = new Stack<String>();
        results = this.parseString(s);
        position = -1;
        int size = results.size();

        while (true)
        {
            position++;
            if (position >= size)
            {
                while (!notation.isEmpty())
                {
                    number.push(count());
                }
                return number.pop();
            }
            String c = results.get(position);

            if (c == null)
            {
                return number.pop();
            }
            else
            {
                if (isNotation(c))
                {
                    if (")".equals(c))
                    {
                        while (!"(".equals(notation.peek()))
                        {
                            number.push(count());
                        }
                        notation.pop();
                        continue;
                    }
                    else
                    {
                        if ("(".equals(c))
                        {
                            notation.push(c);
                            continue;
                        }
                        else
                        {
                            if (notation.empty() || "(".equals(notation.peek()))
                            {
                                notation.push(c);
                            }
                            else
                            {
                                if (!(priority.get(c) > priority.get(notation.peek())))
                                {
                                    number.push(count());
                                }
                                notation.push(c);
                                continue;
                            }
                        }
                    }
                }
                else
                {
                    number.push(Double.parseDouble(c));
                }
            }
        }
    }

    /**
     * 判断是否为"+ - * / ) ("
     * @param c 输入字符
     * @return 结果
     */
    public boolean isNotation(String c)
    {
        String regex = "^([\\+\\-\\*/\\(\\)]{1,1})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(c);
        return matcher.find();
    }

    /**
     * 在String指定地方添加String
     * @param s 原始字符串
     * @param index 添加字符串的位置（0到s.length()+1）
     * @param addition 添加的字符串
     * @return 结果
     */
    public String addStringToIndex(String s, int index, String addition)
    {
        if (index == 0)
        {
            s = addition + s;
        }
        else if (index == (s.length() + 1))
        {
            s = s + addition;
        }
        else if (index > 0 && index <= s.length())
        {
            String begin = s.substring(0, index);
            String end = s.substring(index, s.length());
            s = begin + addition + end;
        }
        else
        {
            s = null;
        }
        return s;
    }

    /**
     * 对表达式中的负数做处理，负数-a被表达成(0-1)的形式
     * @param c 原始字符串
     * @return 结果
     */
    public String minus(String c)
    {
        String regex = "(^(\\-{1,1}|\\+{1,1})\\d+)";
        int begin = -1;
        int end = -1;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(c);
        if (matcher.find())
        {
            begin = matcher.start();
            end = matcher.end();
            c = this.addStringToIndex(c, end, ")");
            c = this.addStringToIndex(c, begin, "(0");
        }

        while (true)
        {
            regex = "([\\+\\-\\*/\\(]{1,1}(\\-|\\+){1,1}\\d+)";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(c);
            if (matcher.find())
            {
                begin = matcher.start() + 1;
                end = matcher.end();
                c = this.addStringToIndex(c, end, ")");
                c = this.addStringToIndex(c, begin, "(0");
            }
            else
            {
                break;
            }
        }
        return c;
    }

    /**
     * 栈中的数据弹出计算
     * @return 结果
     */
    public Double count()
    {
        Double a = null;
        if (!number.isEmpty())
        {
            a = number.pop();
        }
        Double b = null;
        if (!number.isEmpty())
        {
            b = number.pop();
        }
        String n = null;
        if (!notation.isEmpty())
        {
            n = notation.pop();
        }
        if (a != null && b != null && n != null)
        {
            if ("+".equals(n))
            {
                return a + b;
            }
            else if ("-".equals(n))
            {
                return b - a;
            }
            else if ("*".equals(n))
            {
                return a * b;
            }
            else if ("/".equals(n))
            {
                return b / a;
            }
        }
        else if (a != null && b == null && n == null)
        {
            return a;
        }
        else
        {
            throw new RuntimeException("非法表达式");
        }
        return null;
    }

    /**
     * 对字符串做词法分析
     * @param in 输入字符串
     * @return 结果
     * @throws IllegalExpressionException 字符串不合法
     */
    public ArrayList<String> parseString(String in) throws IllegalExpressionException
    {

        String doubleRegex = "^(\\d+\\.?\\d*).*$";
        String functionRegex = "^(\\w+).*$";
        String notationRegex = "^([\\+\\-\\*/\\(\\)]{1,1}).*$";
        results = new ArrayList<String>();

        Pattern pattern1 = Pattern.compile(doubleRegex);
        Pattern pattern2 = Pattern.compile(functionRegex);
        Pattern pattern3 = Pattern.compile(notationRegex);

        while (in.length() > 0)
        {
            Matcher matcher1 = pattern1.matcher(in);
            Matcher matcher2 = pattern2.matcher(in);
            Matcher matcher3 = pattern3.matcher(in);

            if (matcher1.find())
            {
                String result = matcher1.group(1);
                results.add(result);
                in = in.substring(result.length());
            }
            else if (matcher2.find())
            {
                String result = matcher2.group(1);
                results.add(result);
                in = in.substring(result.length());
                throw new IllegalExpressionException(in);
            }
            else if (matcher3.find())
            {
                String result = matcher3.group(1);
                results.add(result);
                in = in.substring(result.length());
            }
            else
            {
                logger.error("非法字符串");
                throw new RuntimeException("非法字符串");
            }
        }
        return results;
    }

    /**
     * 获取字符串中第一个在括号中，且没有子括号的子字符串
     * @param s 原始字符串
     * @return 结果
     */
    public String getSubCalculation(String s)
    {
        String result = null;
        String regex = "\\([\\w\\+\\-\\*/\\.]+\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        if (matcher.find())
        {
            result = matcher.group();
            logger.debug("result = " + result);
        }
        return result;
    }

    /**
     * 计算从getSubCalculation中取出的子字符串
     * @param s 从getSubCalculation中取出的子字符串
     * @return 计算结果
     * @throws IllegalExpressionException 字符串不合法
     */
    public String subCalculation(String s) throws IllegalExpressionException
    {
        s = s.substring(1, s.length() - 1);
        // logger.debug("计算表达式为 ：" + s);
        return String.valueOf(this.calculate(s));
    }

    /**
     * 解析字符串中的字符是标识符还是函数
     * @param s 原始字符串
     */
    public void getIdentifier(String s)
    {
        s = s.replaceAll(" ", "");
        String regex = "(\\b[a-zA-Z_]{1,1}\\w*\\b)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find())
        {
            if (s.charAt(matcher.end()) == '(')
            {
                identifier.put(matcher.group(), "function");
            }
            else
            {
                identifier.put(matcher.group(), null);
            }
        }
        System.out.println(identifier);
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        Arithmometer am = new Arithmometer();
        // System.out.println(am.calculate("((32-22)/(30-25)+(19-9)/(7-2))*100"));
        // System.out.println(am.calculate("(3-1)+(1-3)"));
        // System.out.println(am.calculate("100+5*((3-1))"));
        // am.minus("-111--111+(-21-31)--41111");
        try
        {
            System.out.println(am.calculate("+111-+111+(-21-31)--41111"));
        }
        catch (IllegalExpressionException e)
        {
            e.printStackTrace();
        }
        // String s = "asd1+asda2+asd(asd3)";
        // am.getIdentifier(s);
        /*
         * String s = "((32-22)/(30-25)+(19-9)/(7-2))*100"; String temp = am.getSubCalculation(s); while (temp != null) { String temp1 =
         * am.subCalculation(temp); s = s.replace(temp, temp1); System.out.println(s); temp = am.getSubCalculation(s); }
         */
    }
}
