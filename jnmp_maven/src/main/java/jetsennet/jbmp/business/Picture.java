/**********************************************************************
 * 日 期: 2012-09-19
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Picture.java
 * 历 史: 2012-09-19 Create
 *********************************************************************/
package jetsennet.jbmp.business;

import java.util.List;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.PictureDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.PictureEntity;
import jetsennet.util.StringUtil;

/**
 * 资源管理中格的图标
 */
public class Picture
{
    /**
     * 新建
     * @param objXml 参数
     * @throws Exception 异常
     * @return 结果
     */
    @Business
    public String addPicture(String objXml) throws Exception
    {
        PictureDal dalPicture = new PictureDal();
        return "" + dalPicture.insertXml(objXml);
    }

    /**
     * 编辑
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updatePicture(String objXml) throws Exception
    {
        PictureDal dalPicture = new PictureDal();
        dalPicture.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deletePicture(int keyId) throws Exception
    {
        PictureDal dalPicture = new PictureDal();
        dalPicture.delete(keyId);
    }
    /**
     * 根据Evertz机箱ID，查询出机箱的子节点并以xml返回
     * @param objId 要实例化的对象id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String instanceEvertz(int objId) throws Exception
    {
      
        PictureDal dal = ClassWrapper.wrapTrans(PictureDal.class);
        PictureEntity parent=dal.getObjDefalutPic(objId);
        List<PictureEntity> childList =dal.getObjChildDefalutPic(objId);
        StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><Topology><Canvas width='1207' height='556' align='left' />");
        //把机箱的默认图片作为背景
        sb.append("<BackGroundImage ImageSource='"+parent.getPicturePath()+"' "+
        "ImageWidth='1024' ImageHeight='768' ImageX='0' ImageY='0'  ScaleContent='false' IsZoom='true' />");
        if (childList != null)
        {
            int len=childList.size();
            PictureEntity tempPic;
            for (int i=0;i<len;i++)
            {
                tempPic=childList.get(i);
                int width=50;
                if(!StringUtil.isNullOrEmpty(tempPic.getField2())){
                    width=Integer.parseInt(tempPic.getField2());
                }
                sb.append("<Node Id='"+i+"' Name='' Type='image' Desc='' BindID='"+tempPic.getPictureId()+"' "+
                    "BindIP='' BindName='' BindType='object' BindObjType='' BindObjTypeId='' "+
                    "LabelPosition='bottom' FontFamily='Arial' FontColor='0x000000' "+
                    "FontSize='12' FontWeight='normal' FontStyle='normal' UnderLine='none' "+
                    "SubTopoId='' SubTopoName='' SubTopoGroupId='' JumpTopoId='' "+
                    "JumpTopoName='' X='"+(903-Integer.parseInt(tempPic.getPictureName())*50-width+50)+"' Y='220' Width='"+width+"' Height='316'>"+
                    "<IMAGE ImageSource='"+tempPic.getPicturePath()+"'    IsMaintainAspectRatio='true' />"+
                    "<VideoParam URL='undefined' WIDTH='undefined' HEIGHT='undefined' />"+
                "</Node>");
            }
        }
        sb.append("</Topology>");
        return sb.toString();
    }
    public static void main(String[] args)
    {
        Picture p=new Picture();
        try
        {
            System.out.println(p.instanceEvertz(427));;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
