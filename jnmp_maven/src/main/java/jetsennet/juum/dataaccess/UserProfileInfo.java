package jetsennet.juum.dataaccess;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import jetsennet.sqlclient.ModelBase;
import jetsennet.util.FormatUtil;
import jetsennet.util.ISerializer;
import jetsennet.util.StringUtil;

/**
 * 用户状态
 * @author Administrator
 */
public class UserProfileInfo extends ModelBase implements ISerializer
{

    /**
     * @param userId 用户id
     * @param loginId 参数
     * @param userName 用户名称
     * @param userToken ？
     * @param homePath ？
     * @param pathSize ？
     */
    public UserProfileInfo(String userId, String loginId, String userName, String userToken, String homePath, int pathSize)
    {
        this.UserId = userId;
        this.LoginId = loginId;
        this.UserName = userName;
        this.UserToken = userToken;
        this.HomePath = homePath;
        this.PathSize = pathSize;
        this.LoginTime = new Date();
        this.UpdateTime = new Date();
    }

    private String UserId;

    public String getUserId()
    {
        return UserId;
    }

    public void setUserId(String val)
    {
        this.UserId = val;
    }

    private String LoginId;

    public String getLoginId()
    {
        return LoginId;
    }

    public void setLoginId(String val)
    {
        this.LoginId = val;
    }

    private String UserName;

    public String getUserName()
    {
        return UserName;
    }

    public void setUserName(String val)
    {
        this.UserName = val;
    }

    private String UserToken;

    public String getUserToken()
    {
        return UserToken;
    }

    public void setUserToken(String val)
    {
        this.UserToken = val;
    }

    private String HomePath;

    public String getHomePath()
    {
        return HomePath;
    }

    public void setHomePath(String val)
    {
        this.HomePath = val;
    }

    private String UserParam;

    public String getUserParam()
    {
        return UserParam;
    }

    public void setUserParam(String val)
    {
        this.UserParam = val;
    }

    private String UserGroups;

    public String getUserGroups()
    {
        return UserGroups;
    }

    public void setUserGroups(String val)
    {
        this.UserGroups = val;
    }

    private String UserRoles;

    public String getUserRoles()
    {
        return UserRoles;
    }

    public void setUserRoles(String val)
    {
        this.UserRoles = val;
    }

    private int PathSize;

    public int getPathSize()
    {
        return PathSize;
    }

    public void setPathSize(int val)
    {
        this.PathSize = val;
    }

    private int userType;

    public int getUserType()
    {
        return userType;
    }

    public void setUserType(int val)
    {
        this.userType = val;
    }

    private int RightLevel;

    public int getRightLevel()
    {
        return RightLevel;
    }

    public void setRightLevel(int val)
    {
        this.RightLevel = val;
    }

    private Date LoginTime;

    public Date getLoginTime()
    {
        return LoginTime;
    }

    public void setLoginTime(Date val)
    {
        this.LoginTime = val;
    }

    private Date UpdateTime;

    public Date getUpdateTime()
    {
        return UpdateTime;
    }

    public void setUpdateTime(Date val)
    {
        this.UpdateTime = val;
    }

    private HashMap<String, Boolean> hTabServiceAuth;

    /**
     * @return 结果
     */
    public HashMap<String, Boolean> gethTabServiceAuth()
    {
        return hTabServiceAuth;
    }

    /**
     * @param val 参数
     */
    public void sethTabServiceAuth(HashMap<String, Boolean> val)
    {
        this.hTabServiceAuth = val;
    }

    /**
     * 构造方法
     */
    public UserProfileInfo()
    {

    }

    @Override
    public void setValue(String fieldName, Object fieldValue)
    {
        if (StringUtil.isNullOrEmpty(fieldName) || fieldValue == null)
        {
            return;
        }
        fieldName = fieldName.toUpperCase();
        if (isFirstArea(fieldName)) // The First Letter In(ABCDEFGHIJKLMN)
        {

            if ("LoginId".equalsIgnoreCase(fieldName))
            {
                this.setLoginId(String.valueOf(fieldValue));
                return;
            }
            if ("HomePath".equalsIgnoreCase(fieldName))
            {
                this.setHomePath(String.valueOf(fieldValue));
                return;
            }
            if ("LoginTime".equalsIgnoreCase(fieldName))
            {
                this.setLoginTime(FormatUtil.parseDate(fieldValue));
                return;
            }
        }
        else
        {

            if ("UserId".equalsIgnoreCase(fieldName))
            {
                this.setUserId(String.valueOf(fieldValue));
                return;
            }
            if ("UserName".equalsIgnoreCase(fieldName))
            {
                this.setUserName(String.valueOf(fieldValue));
                return;
            }
            if ("UserToken".equalsIgnoreCase(fieldName))
            {
                this.setUserToken(String.valueOf(fieldValue));
                return;
            }
            if ("UserParam".equalsIgnoreCase(fieldName))
            {
                this.setUserParam(String.valueOf(fieldValue));
                return;
            }
            if ("PathSize".equalsIgnoreCase(fieldName))
            {
                this.setPathSize(Integer.parseInt(String.valueOf(fieldValue)));
                return;
            }
            if ("UserType".equalsIgnoreCase(fieldName))
            {
                this.setUserType(Integer.parseInt(String.valueOf(fieldValue)));
                return;
            }
            if ("UpdateTime".equalsIgnoreCase(fieldName))
            {
                this.setUpdateTime(FormatUtil.parseDate(fieldValue));
                return;
            }
        }
    }

    @Override
    public Object getValue(String fieldName)
    {
        if (StringUtil.isNullOrEmpty(fieldName))
        {
            return null;
        }
        if (isFirstArea(fieldName)) // The First Letter In(ABCDEFGHIJKLMN)
        {

            if ("LoginId".equalsIgnoreCase(fieldName))
            {
                return this.getLoginId();
            }
            if ("HomePath".equalsIgnoreCase(fieldName))
            {
                return this.getHomePath();
            }
            if ("LoginTime".equalsIgnoreCase(fieldName))
            {
                return this.getLoginTime();
            }
        }
        else
        {

            if ("UserId".equalsIgnoreCase(fieldName))
            {
                return this.getUserId();
            }
            if ("UserName".equalsIgnoreCase(fieldName))
            {
                return this.getUserName();
            }
            if ("UserToken".equalsIgnoreCase(fieldName))
            {
                return this.getUserToken();
            }
            if ("UserParam".equalsIgnoreCase(fieldName))
            {
                return this.getUserParam();
            }
            if ("PathSize".equalsIgnoreCase(fieldName))
            {
                return this.getPathSize();
            }
            if ("UpdateTime".equalsIgnoreCase(fieldName))
            {
                return this.getUpdateTime();
            }
        }
        return null;
    }

    @Override
    public String serialize(String xmlRoot)
    {
        StringBuilder sbSerial = new StringBuilder();
        sbSerial.append("<" + xmlRoot + ">");

        sbSerial.append(String.format("<UserId>%s</UserId>", FormatUtil.escapeXml(this.getUserId())));
        sbSerial.append(String.format("<LoginId>%s</LoginId>", FormatUtil.escapeXml(this.getLoginId())));
        sbSerial.append(String.format("<UserName>%s</UserName>", FormatUtil.escapeXml(this.getUserName())));
        sbSerial.append(String.format("<UserToken>%s</UserToken>", FormatUtil.escapeXml(this.getUserToken())));
        sbSerial.append(String.format("<HomePath>%s</HomePath>", FormatUtil.escapeXml(this.getHomePath())));
        sbSerial.append(String.format("<PathSize>%s</PathSize>", this.getPathSize()));
        sbSerial.append(String.format("<RightLevel>%s</RightLevel>", this.getRightLevel()));
        sbSerial.append(String.format("<UserGroups>%s</UserGroups>", this.getUserGroups()));
        sbSerial.append(String.format("<UserRoles>%s</UserRoles>", this.getUserRoles()));
        sbSerial.append(String.format("<UserType>%s</UserType>", this.getUserType()));
        sbSerial.append(String.format("<LoginTime>%s</LoginTime>", this.getLoginTime()));
        sbSerial.append(String.format("<UpdateTime>%s</UpdateTime>", this.getUpdateTime()));
        sbSerial.append(String.format("<UserParam>%s</UserParam>", FormatUtil.escapeXml(this.getUserParam())));
        sbSerial.append("</" + xmlRoot + ">");
        return sbSerial.toString();
    }

    @Override
    public void deserialize(String serializedXml, String xmlRoot)
    {
        if (StringUtil.isNullOrEmpty(serializedXml))
        {
            return;
        }
        List<Node> nodes = null;
        try
        {
            Document doc = DocumentHelper.parseText(serializedXml);
            nodes = doc.getRootElement().elements();
        }
        catch (Exception ex)
        {
        }
        if (nodes != null)
        {
            for (int i = 0; i < nodes.size(); i++)
            {
                this.setValue(nodes.get(i).getName(), nodes.get(i).getText());
            }
        }
    }
}
