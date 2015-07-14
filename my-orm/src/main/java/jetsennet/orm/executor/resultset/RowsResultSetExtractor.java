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
import java.util.ArrayList;
import java.util.List;

import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.mapping.IObjectReader;
import jetsennet.orm.tableinfo.mapping.ReaderUtils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-20       郭训常            创建<br/>
 */
public class RowsResultSetExtractor<T> extends AbsResultSetHandle<List<T>>
{

    private IObjectReader objectReader;
    private Class<T> cls;
    private TableInfo tableInfo;

    /**
     * @param objectReader
     * @param cls
     */
    public RowsResultSetExtractor(Class<T> cls, TableInfo tableInfo)
    {
        super();
        this.objectReader = ReaderUtils.getObjectReader();
        this.cls = cls;
        this.tableInfo = tableInfo;
    }

    public RowsResultSetExtractor(Class<T> cls)
    {
        this(cls, null);
    }

    public static <T> RowsResultSetExtractor<T> gen(Class<T> cls, TableInfo tableInfo)
    {
        return new RowsResultSetExtractor<T>(cls, tableInfo);
    }

    public static <T> RowsResultSetExtractor<T> gen(Class<T> cls)
    {
        return new RowsResultSetExtractor<T>(cls, null);
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException
    {
        List<T> results = new ArrayList<T>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int pos = 0;
        while (rs.next())
        {
            if (pos >= offset)
            {
                try
                {
                    T temp = this.objectReader.read(cls, tableInfo, rs, rsmd);
                    if (temp != null)
                    {
                        results.add(temp);
                    }
                }
                catch (Exception e)
                {
                    throw new SQLException(e);
                }
            }
            if (results.size() >= max)
            {
                break;
            }
            pos++;
        }
        return results;
    }

}
