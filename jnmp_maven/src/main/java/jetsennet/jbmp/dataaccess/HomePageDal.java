/************************************************************************
日 期：2012-3-1
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import jetsennet.jbmp.entity.HomePageEntity;

/**
 * @author yl
 */
public class HomePageDal extends DefaultDal<HomePageEntity>
{
    /**
     * 构造函数
     */
    public HomePageDal()
    {
        super(HomePageEntity.class);
    }
}
