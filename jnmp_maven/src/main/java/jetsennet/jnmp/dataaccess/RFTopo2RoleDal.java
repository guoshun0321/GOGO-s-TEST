/**********************************************************************
 * 日 期: 2013-07-01
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Topo2rfDal.java
 * 历 史: 2013-07-01 Create
 *********************************************************************/
package jetsennet.jnmp.dataaccess;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jnmp.entity.RFTopo2RoleEntity;

import org.apache.log4j.Logger;

/**
 *  Dal
 */
public class RFTopo2RoleDal extends DefaultDal<RFTopo2RoleEntity>
{
    private static final Logger logger = Logger.getLogger(RFTopo2RoleDal.class);
    
    public RFTopo2RoleDal()
    {
        super(RFTopo2RoleEntity.class);
    }
}
