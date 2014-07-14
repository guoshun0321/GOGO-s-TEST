package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.DiskArrayOwnerEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class DiskArrayOwner
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addDiskArrayOwner(String objXml) throws Exception
    {
        DefaultDal<DiskArrayOwnerEntity> dal = new DefaultDal<DiskArrayOwnerEntity>(DiskArrayOwnerEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 修改
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateDiskArrayOwner(String objXml) throws Exception
    {
        DefaultDal<DiskArrayOwnerEntity> dal = new DefaultDal<DiskArrayOwnerEntity>(DiskArrayOwnerEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteDiskArrayOwner(int keyId) throws Exception
    {
        DefaultDal<DiskArrayOwnerEntity> dal = new DefaultDal<DiskArrayOwnerEntity>(DiskArrayOwnerEntity.class);
        dal.delete(keyId);
    }

    /**
     * 更新
     * @param ownerNo 拥有者
     * @param size 大小
     * @throws Exception 异常
     */
    @Business
    public void updateDiskArrayOwner(String ownerNo, int size) throws Exception
    {
        DefaultDal<DiskArrayOwnerEntity> dal = new DefaultDal<DiskArrayOwnerEntity>(DiskArrayOwnerEntity.class);

        List<SqlField> fields = new ArrayList<SqlField>();
        fields.add(new SqlField("DISKARRAYOWNER_USE", size, SqlParamType.Numeric));

        SqlCondition conds = new SqlCondition("DISKARRAYOWNER_NO", ownerNo, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);

        dal.update(fields, conds);
    }
}
