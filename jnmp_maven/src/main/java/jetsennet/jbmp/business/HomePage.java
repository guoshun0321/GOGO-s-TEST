/************************************************************************
日 期：2012-3-1
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.HashMap;

import jetsennet.jbmp.dataaccess.HomePageDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.HomePageEntity;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author yl
 */
public class HomePage
{
    /**
     * 根据用户ID获取主页的XML信息
     * @param userId 用户id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String getHomePageByUserId(int userId) throws Exception
    {
        String result = "";

        HomePageDal dal = new HomePageDal();
        HomePageEntity entity = dal.get(userId);

        if (entity != null)
        {
            String path = entity.getHomePath();

            if (!StringUtil.isNullOrEmpty(path))
            {
                String pageInfo = new TopoXmlFile().readTopoMapFile(path); // 读取XML文件
                result = pageInfo;
            }
        }

        return result;
    }

    /**
     * 更新
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String updateHomePage(String objXml) throws Exception
    {
        String result = "";

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        String userId = model.get("USER_ID");
        String pageInfo = model.get("PAGE_INFO");

        HomePageDal dal = new HomePageDal();
        HomePageEntity entity = dal.get(Integer.valueOf(userId));

        if (!StringUtil.isNullOrEmpty(pageInfo))
        {
            String path = new TopoXmlFile().writeTopoMapFile("BMP_HOMEPAGE", userId, pageInfo); // 写XML信息到文件

            if (!"-1".equals(path)) // 如果成功
            {
                if (entity != null)
                {
                    // 更新
                    entity.setHomePath(path); // 更改PAGE_PATH的值，存相对路径
                    result = String.valueOf(dal.update(entity));
                }
                else
                {
                    // 新增
                    HomePageEntity newEntity = new HomePageEntity();
                    newEntity.setUserId(Integer.valueOf(userId));
                    newEntity.setHomePath(path);

                    result = String.valueOf(dal.insert(newEntity));
                }
            }
            else
            {
                result = path;
            }
        }

        return result;
    }
}
