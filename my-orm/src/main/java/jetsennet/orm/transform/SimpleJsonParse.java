package jetsennet.orm.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 简单的json解析器
 * 
 * [{"id" : "0","name" : "名称","时间" : "19290102 11:22:33"},{"id" : "0","name" : "名称","时间" : "19290102 11:22:33"}]
 * 
 * @author 郭祥
 */
public class SimpleJsonParse
{

    /**
     * 将String转换为Map。该函数只支持一层的转换。
     * 
     * @param json
     * @return
     */
    public static final List<Map<String, Object>> parse(String json)
    {
        List<Map> array = JSON.parseArray(json, Map.class);
        int arraySize = array.size();
        List<Map<String, Object>> retval = new ArrayList<Map<String, Object>>(arraySize);
        for (int i = 0; i < arraySize; i++)
        {
            Map<String, Object> temp = array.get(i);
            retval.add(temp);
        }
        return retval;
    }

}
