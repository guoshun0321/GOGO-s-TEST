package jetsennet.orm.configuration;

/**
 * orm配置构造器
 * 
 * @author 郭祥
 */
public interface IConfigurationBuilder
{

    /**
     * 生成配置文件
     * 
     * @param source 数据来源
     * @return
     */
    public Configuration genConfiguration();

}
