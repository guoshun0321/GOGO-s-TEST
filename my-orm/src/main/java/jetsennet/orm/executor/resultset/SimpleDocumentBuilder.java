package jetsennet.orm.executor.resultset;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SimpleDocumentBuilder
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
     * 根节点名称
     */
    private String rootName;
    /**
     * item名称
     */
    private String itemName;
    /**
     * 根节点
     */
    private Element root;

    public SimpleDocumentBuilder(boolean isPage, int count, int cur, String rootName, String itemName)
    {
        this.isPage = isPage;
        this.count = count;
        this.cur = cur;
        this.rootName = rootName == null ? "Records" : rootName;
        this.itemName = itemName == null ? "Record" : itemName;
    }

    public RecordEntry addRecord()
    {
        Element item = root.addElement(itemName);
        RecordEntry retval = new RecordEntry(item);
        return retval;
    }

    public Document build()
    {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement(rootName);

        if (this.isPage)
        {
            Element info = root.addElement("Info");
            info.addElement("Count").setText(Integer.toString(this.count));
            info.addElement("Cur").setText(Integer.toString(this.cur));
        }
        return doc;
    }

    public class RecordEntry
    {

        private Element item;

        public RecordEntry(Element item)
        {
            this.item = item;
        }

        public void add(String key, String value)
        {
            item.addElement(key).setText(value);
        }
    }

}
