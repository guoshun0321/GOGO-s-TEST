package jetsennet.jbmp.dataaccess;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.CheckTemplateEntity;

public class CheckTemplateDal extends DefaultDal<CheckTemplateEntity>
{
    private static final Logger logger = Logger.getLogger(CheckTemplateDal.class);

    public CheckTemplateDal()
    {
        super(CheckTemplateEntity.class);
    }
}
