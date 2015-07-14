package jetsennet.orm.executor.resultset;

import java.util.ArrayList;
import java.util.List;

import jetsennet.util.XmlTransUtil;

public class SimpleXmlBuilder
{

    /**
     * 是否page
     */
    private boolean isPage;
    /**
     * 总页数
     */
    private int count;
    /**
     * 当前位置
     */
    private int cur;
    /**
     * record集合
     */
    private List<RecordEntry> records;

    public SimpleXmlBuilder(boolean isPage, int count, int cur)
    {
        this.isPage = isPage;
        this.count = count;
        this.cur = cur;
        records = new ArrayList<RecordEntry>();
    }

    public RecordEntry newRecord()
    {
        RecordEntry record = new RecordEntry();
        records.add(record);
        return record;
    }

    public String build()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<Records>");
        for (RecordEntry record : records)
        {
            sb.append("<Record>").append(record.sb).append("</Record>");
        }
        if (this.isPage)
        {
            sb.append("<Info>");
            sb.append("<Count>").append(this.count).append("</Count>");
            sb.append("<Cur>").append(this.cur).append("</Cur>");
            sb.append("</Info>");
        }
        sb.append("</Records>");
        return sb.toString();
    }

    public class RecordEntry
    {

        private StringBuilder sb;

        public RecordEntry()
        {
            sb = new StringBuilder();
        }

        public RecordEntry add(String key, String value)
        {
            sb.append('<').append(key).append('>');
            sb.append(XmlTransUtil.xmlSpecialChar(value));
            sb.append("</").append(key).append('>');
            return this;
        }
    }

}
