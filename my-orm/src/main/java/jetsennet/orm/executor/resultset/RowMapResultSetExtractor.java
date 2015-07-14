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
import java.util.Map;

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
public class RowMapResultSetExtractor extends AbsResultSetHandle<List<Map<String, Object>>>
{
    private IObjectReader objectReader;

    /**
     * @param objectReader
     */
    public RowMapResultSetExtractor()
    {
        super();
        this.objectReader = ReaderUtils.getObjectReader();
    }

    /**
     * @param objectReader
     * @param max
     */
    public RowMapResultSetExtractor(int max)
    {
        this();
        this.max = max;
    }

    public static RowMapResultSetExtractor gen()
    {
        return new RowMapResultSetExtractor();
    }

    @Override
    public List<Map<String, Object>> handle(ResultSet rs) throws SQLException
    {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next())
        {
            try
            {
                results.add(this.objectReader.readToMap(rs, rsmd));
            }
            catch (Exception e)
            {
                throw new SQLException(e);
            }
            if (results.size() >= max)
            {
                break;
            }
        }
        return results;
    }

}
