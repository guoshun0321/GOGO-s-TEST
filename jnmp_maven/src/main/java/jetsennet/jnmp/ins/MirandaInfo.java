/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jnmp.ins;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.mib.node.EditNode;

/**
 * @author Guo
 */
public class MirandaInfo
{

    // <editor-fold defaultstate="collapsed" desc="基本信息">
    private int devIndex;
    private int slotIndex;
    private int trapIndex;
    private String type;
    private String name;
    private int status;
    private String statusText;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="扩展信息">
    private String serverName;
    private String hostName;
    private String devType;
    private int slot = -1;
    private int boardType = -1;
    private String usedOid = null;
    // </editor-fold>
    private static final Logger logger = Logger.getLogger(MirandaInfo.class);

    /**
     * 构造方法
     */
    public MirandaInfo()
    {
    }

    // <editor-fold defaultstate="collapsed" desc="解析">
    /**
     * @param cells 参数
     * @return 结果
     */
    public static MirandaInfo parse(ArrayList<EditNode> cells)
    {
        MirandaInfo info = null;
        try
        {
            if (validate(cells))
            {
                info = new MirandaInfo();
                info.devIndex = Integer.valueOf(cells.get(0).getEditValue());
                info.slotIndex = Integer.valueOf(cells.get(1).getEditValue());
                info.trapIndex = Integer.valueOf(cells.get(2).getEditValue());
                if (info.trapIndex >= 100747 && info.trapIndex <= 100766)
                {
                    info.slotIndex = info.trapIndex - 100747 + 1;
                }
                info.type = cells.get(3).getEditValue();
                parseType(info, info.type);
                info.name = cells.get(4).getEditValue();
                info.status = Integer.valueOf(cells.get(5).getEditValue());
                info.usedOid = cells.get(5).getOid();
                info.statusText = cells.get(6).getEditValue();
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            info = null;
        }
        return info;
    }

    private static boolean validate(ArrayList<EditNode> cells)
    {
        if (cells == null || cells.size() != 7 || cells.get(0) == null || "".equals(cells.get(0).getEditValue().trim()))
        {
            return false;
        }
        return true;
    }

    private static void parseType(MirandaInfo info, String type)
    {
        if (type == null)
        {
            return;
        }
        if (type.startsWith("health") || info.slotIndex == 0)
        {
            String[] strs = type.split("/");
            info.serverName = strs[2];
            info.devType = strs[3];
            info.hostName = strs[4];
        }
        else
        {
            String[] strs = type.split("_");
            info.serverName = strs[0];
            info.hostName = strs[1];
            info.devType = strs[2];
            info.slot = Integer.valueOf(strs[4]);
            info.boardType = Integer.valueOf(strs[5]);
        }
    }

    // </editor-fold>

    /**
     * @return the devIndex
     */
    public int getDevIndex()
    {
        return devIndex;
    }

    /**
     * @param devIndex the devIndex to set
     */
    public void setDevIndex(int devIndex)
    {
        this.devIndex = devIndex;
    }

    /**
     * @return the slotIndex
     */
    public int getSlotIndex()
    {
        return slotIndex;
    }

    /**
     * @param slotIndex the slotIndex to set
     */
    public void setSlotIndex(int slotIndex)
    {
        this.slotIndex = slotIndex;
    }

    /**
     * @return the trapIndex
     */
    public int getTrapIndex()
    {
        return trapIndex;
    }

    /**
     * @param trapIndex the trapIndex to set
     */
    public void setTrapIndex(int trapIndex)
    {
        this.trapIndex = trapIndex;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * @return the statusText
     */
    public String getStatusText()
    {
        return statusText;
    }

    /**
     * @param statusText the statusText to set
     */
    public void setStatusText(String statusText)
    {
        this.statusText = statusText;
    }

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    /**
     * @return the serverName
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
     * @return the devType
     */
    public String getDevType()
    {
        return devType;
    }

    /**
     * @param devType the devType to set
     */
    public void setDevType(String devType)
    {
        this.devType = devType;
    }

    /**
     * @return the slot
     */
    public int getSlot()
    {
        return slot;
    }

    /**
     * @param slot the slot to set
     */
    public void setSlot(int slot)
    {
        this.slot = slot;
    }

    /**
     * @return the boardType
     */
    public int getBoardType()
    {
        return boardType;
    }

    /**
     * @param boardType the boardType to set
     */
    public void setBoardType(int boardType)
    {
        this.boardType = boardType;
    }

    /**
     * @return the usedOid
     */
    public String getUsedOid()
    {
        return usedOid;
    }

    /**
     * @param usedOid the usedOid to set
     */
    public void setUsedOid(String usedOid)
    {
        this.usedOid = usedOid;
    }
}
