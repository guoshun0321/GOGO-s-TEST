/************************************************************************
日 期：2011-11-2
作 者: YL
版 本：
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.HashMap;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.TopoTemplateEntity;

/**
 * @author ？
 */
public class TopoTemplateDal extends DefaultDal<TopoTemplateEntity>
{
    /**
     * 表名
     */
    public final static String TABLE_NAME = "BMP_TOPOTEMPLATE";
    /**
     * 主键
     */
    public final static String PRIMARY_KEY = "TEMP_ID";

    /**
     * 构造方法
     */
    public TopoTemplateDal()
    {
        super(TopoTemplateEntity.class);
    }

    /**
     * 新增
     * @param model 参数
     * @param newKey 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public int add(HashMap<String, String> model, boolean newKey) throws Exception
    {
        return insert(model, newKey);
    }

    /**
     * 修改
     * @param model 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public int updateTopoTemplate(HashMap<String, String> model) throws Exception
    {
        return update(model);
    }

    /**
     * 删除
     * @param keyId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public int deleteById(int keyId) throws Exception
    {
        return delete(keyId);
    }

}
