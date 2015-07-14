/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.util;

public interface ISerializer {


    /**反序列化
     * @param serializedXml 序列化字串
     * @param rootName 根元素名称
     */
    void deserialize(String serializedXml, String rootName);

    /**序列化
     * @param rootName 根元素名称
     * @return
     */
    String serialize(String rootName);
}