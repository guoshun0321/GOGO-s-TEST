package jetsennet.jbmp.dataaccess;

import java.util.HashMap;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.TopoMapEntity;

/**
 * @author ?
 */
public class TopoMapDal extends DefaultDal<TopoMapEntity>
{
    /**
     * 表名
     */
    public final static String TABLE_NAME = "BMP_TOPOMAP";
    /**
     * 主键
     */
    public final static String PRIMARY_KEY = "MAP_ID";

    /**
     * 构造方法
     */
    public TopoMapDal()
    {
        super(TopoMapEntity.class);
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
    public int updateTopoMap(HashMap<String, String> model) throws Exception
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
