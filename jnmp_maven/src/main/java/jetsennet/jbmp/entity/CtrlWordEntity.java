package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 受控词
 * @author GUO
 */
@Table(name = "BMP_CTRLWORD")
public class CtrlWordEntity
{

    /**
     * 受控词ID
     */
    @Id
    @Column(name = "CW_ID")
    private int cw_id;
    /**
     * 受控词类型
     */
    @Column(name = "CW_TYPE")
    private int cw_type;
    /**
     * 受控词名称
     */
    @Column(name = "CW_NAME")
    private String cw_name;
    @Column(name = "CW_CODE")
    private String cw_code;
    /**
     * 受控词描述
     */
    @Column(name = "CW_DESC")
    private String cw_DESC;
    /**
     * 受控词操作类型
     */
    @Column(name = "OPER_TYPE")
    private int oper_type;
    /**
     * 受控词类型，监控属性
     */
    public static final int CW_TYPE_MOATTR = 1;
    /**
     * 受控词类型，采集数据显示
     */
    public static final int CW_TYPE_DATE_DISPLAY = 2;
    /**
     * 受控词名称，监控属性，SNMP类型
     */
    public static final String CW_NAME_SNMP = "SNMP";
    /**
     * 受控词名称，监控属性，.NET属性类型
     */
    public static final String CW_NAME_DNET = ".NET";
    /**
     * 受控词名称，采集数据显示，饼图
     */
    public static final String CW_NAME_PIE = "PIE";
    /**
     * 受控词名称，采集数据显示，线图
     */
    public static final String CW_NAME_LINE = "LINE";
    /**
     * 受控词操作类型，可编辑可删除
     */
    public static final int OPER_TYPE_EDIT_DEL = 10;
    /**
     * 受控词操作类型，可编辑不可删除
     */
    public static final int OPER_TYPE_EDIT = 11;
    /**
     * 受控词操作类型，不可编辑可删除
     */
    public static final int OPER_TYPE_DEL = 12;
    /**
     * 受控词操作类型，不可编辑不可删除
     */
    public static final int OPER_TYPE_NONE = 13;

    /**
     * 构造函数
     */
    public CtrlWordEntity()
    {
    }

    /**
     * @param cw_id 参数
     * @param cw_type 参数
     * @param cw_name 参数
     * @param cw_DESC 参数
     */
    public CtrlWordEntity(int cw_id, int cw_type, String cw_name, String cw_DESC)
    {
        super();
        this.cw_id = cw_id;
        this.cw_type = cw_type;
        this.cw_name = cw_name;
        this.cw_DESC = cw_DESC;
    }

    /**
     * @return the cw_id
     */
    public int getCw_id()
    {
        return cw_id;
    }

    /**
     * @param cw_id the cw_id to set
     */
    public void setCw_id(int cw_id)
    {
        this.cw_id = cw_id;
    }

    /**
     * @return the cw_type
     */
    public int getCw_type()
    {
        return cw_type;
    }

    /**
     * @param cw_type the cw_type to set
     */
    public void setCw_type(int cw_type)
    {
        this.cw_type = cw_type;
    }

    /**
     * @return the cw_name
     */
    public String getCw_name()
    {
        return cw_name;
    }

    /**
     * @param cw_name the cw_name to set
     */
    public void setCw_name(String cw_name)
    {
        this.cw_name = cw_name;
    }

    /**
     * @return the cw_DESC
     */
    public String getCw_DESC()
    {
        return cw_DESC;
    }

    /**
     * @param cw_DESC the cw_DESC to set
     */
    public void setCw_DESC(String cw_DESC)
    {
        this.cw_DESC = cw_DESC;
    }

    public String getCw_code()
    {
        return cw_code;
    }

    public void setCw_code(String cw_code)
    {
        this.cw_code = cw_code;
    }

    /**
     * @return the oper_type
     */
    public int getOper_type()
    {
        return oper_type;
    }

    /**
     * @param oper_type the oper_type to set
     */
    public void setOper_type(int oper_type)
    {
        this.oper_type = oper_type;
    }

}
