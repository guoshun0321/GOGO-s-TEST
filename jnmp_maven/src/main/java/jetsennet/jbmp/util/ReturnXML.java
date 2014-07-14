package jetsennet.jbmp.util;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ReturnXML
{

    private String rootName;

    private String recordName;

    private final String[] columnNames;

    private List<String[]> rows;

    private final int columnNum;

    public ReturnXML(String[] columnNames)
    {
        this("RecordSet", "Record", columnNames);
    }

    public ReturnXML(String rootName, String recordName, String[] columnNames)
    {
        if (rootName == null || recordName == null || columnNames == null || columnNames.length == 0)
        {
            throw new IllegalArgumentException();
        }
        this.rootName = rootName;
        this.recordName = recordName;
        this.columnNames = columnNames;
        this.columnNum = columnNames.length;
        rows = new ArrayList<String[]>();
    }

    public void addRow(String... values)
    {
        if (values != null)
        {
            String[] row = new String[columnNum];
            for (int i = 0; i < values.length && i < columnNum; i++)
            {
                row[i] = values[i] == null ? "" : values[i];
            }
            rows.add(row);
        }
    }

    public Document toXml()
    {
        Document doc = DocumentHelper.createDocument();
        Element rootEle = doc.addElement(rootName);
        int rowNum = rows.size();
        for (int i = 0; i < rowNum; i++)
        {
            String[] row = rows.get(i);
            Element recordEle = rootEle.addElement(recordName);
            for (int j = 0; j < columnNum; j++)
            {
                Element cellEle = recordEle.addElement(columnNames[j]);
                cellEle.setText(row[j]);
            }
        }
        return doc;
    }

    public static void main(String[] args)
    {
        ReturnXML rx = new ReturnXML(new String[] { "column1", "column2", "column3" });
        rx.addRow("11", "12", "13");
        rx.addRow("21", "22", "23");
        rx.addRow("31", "32", "33");
        System.out.println(rx.toXml().asXML());
    }
}
