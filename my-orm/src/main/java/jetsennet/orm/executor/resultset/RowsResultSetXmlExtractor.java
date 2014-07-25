/**
 * Copyright 2010-2016 the original author or authors.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jetsennet.orm.tableinfo.mapping.IXmlReader;
import jetsennet.orm.tableinfo.mapping.ReaderUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-1-31       郭训长            创建<br/>
 */
public class RowsResultSetXmlExtractor extends AbsResultSetHandle<Document>
{

    private String rootName = null;
    private String itemName = null;

    private IXmlReader xmlReader = null;

    public RowsResultSetXmlExtractor(String rootName, String itemName)
    {
        super();
        this.xmlReader = ReaderUtils.getXmlReader();
        this.rootName = rootName;
        this.itemName = itemName;
    }

    public static RowsResultSetXmlExtractor gen(String rootName, String itemName)
    {
        return new RowsResultSetXmlExtractor(rootName, itemName);
    }

    @Override
    public Document handle(ResultSet rs) throws SQLException
    {
        Document results = DocumentHelper.createDocument();
        Element rootElement = results.addElement(rootName);
        ResultSetMetaData rsmd = rs.getMetaData();
        int pos = 0;
        int len = 0;
        while (rs.next())
        {
            if (pos >= offset)
            {
                try
                {
                    rootElement.add(this.xmlReader.read(itemName, rs, rsmd));
                    len++;
                }
                catch (Exception e)
                {
                    throw new SQLException(e);
                }
            }
            if (len >= max)
            {
                break;
            }
            pos++;
        }
        return results;
    }

    /**
     * @param rootName the rootName to set
     */
    public void setRootName(String rootName)
    {
        this.rootName = rootName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    /**
     * @return the rootName
     */
    public String getRootName()
    {
        return rootName;
    }

    /**
     * @return the itemName
     */
    public String getItemName()
    {
        return itemName;
    }
}
