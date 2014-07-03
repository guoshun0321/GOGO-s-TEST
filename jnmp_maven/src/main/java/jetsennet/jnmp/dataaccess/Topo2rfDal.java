/**********************************************************************
 * 日 期: 2013-07-01
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Topo2rfDal.java
 * 历 史: 2013-07-01 Create
 *********************************************************************/
package jetsennet.jnmp.dataaccess;

import org.apache.log4j.Logger;
import jetsennet.jnmp.entity.Topo2rfEntity;
import jetsennet.jbmp.dataaccess.DefaultDal;

/**
 *  Dal
 */
public class Topo2rfDal extends DefaultDal<Topo2rfEntity>
{
    private static final Logger logger = Logger.getLogger(Topo2rfDal.class);
    
    public Topo2rfDal()
    {
        super(Topo2rfEntity.class);
    }
}
