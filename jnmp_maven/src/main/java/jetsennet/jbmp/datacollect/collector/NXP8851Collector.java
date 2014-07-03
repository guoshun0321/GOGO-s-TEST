package jetsennet.jbmp.datacollect.collector;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;

import org.apache.log4j.Logger;

/**
 * 无锡NXP8851版本，状态采集
 * 
 * @author 郭祥
 */
public class NXP8851Collector extends AbsCollector
{

    /**
     * 获取到的数据
     */
    private byte[] datas = null;
    /**
     * 命令字，获取板卡信息
     */
    private static final byte CMD_GETCARDINFO = (byte) 0xA1;
    /**
     * 接收的超时时间，毫秒
     */
    private static final int SO_TIMEOUT = 1000;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(NXP8851Collector.class);

    @Override
    public void connect() throws CollectorException
    {
        DatagramSocket socket = null;
        try
        {
            datas = null;

            socket = new DatagramSocket();
            socket.setSoTimeout(SO_TIMEOUT);

            String ip = mo.getIpAddr();
            int port = mo.getIpPort();
            InetAddress remote = InetAddress.getByName(ip);
            byte[] cmd = this.cmdGetCardInfo(ip);

            logger.debug("准备发送命令：" + this.bytes2String(cmd));
            DatagramPacket outPacket = new DatagramPacket(cmd, cmd.length, remote, port);
            socket.send(outPacket);

            DatagramPacket inPacket = new DatagramPacket(new byte[256], 256);
            socket.receive(inPacket);

            datas = inPacket.getData();
            logger.debug("命令结果为：" + this.bytes2String(cmd));
        }
        catch (Exception ex)
        {
            throw new CollectorException(ex);
        }
        finally
        {
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (Exception ex)
                {
                    logger.error("关闭socket出错。", ex);
                }
                finally
                {
                    socket = null;
                }
            }
        }
    }

    @Override
    public void close()
    {

    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        if (objAttrLst == null)
        {
            throw new NullPointerException();
        }
        Map<ObjAttribEntity, Object> retval = new HashMap<ObjAttribEntity, Object>();
        if (datas == null)
        {
            for (ObjAttribEntity oa : objAttrLst)
            {
                retval.put(oa, null);
            }
        }
        else
        {
            int state = -1;
            try
            {
                state = this.getCardState(datas);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                state = -1;
            }
            if (state < 0)
            {
                for (ObjAttribEntity oa : objAttrLst)
                {
                    retval.put(oa, null);
                }
            }
            else
            {
                for (ObjAttribEntity oa : objAttrLst)
                {
                    CollData data = genCollData(oa, Integer.toString(state), CollData.DATATYPE_PERF);
                    retval.put(oa, data);
                }
            }
        }
        return retval;
    }

    private byte[] cmdGetCardInfo(String ip)
    {
        byte[] command = new byte[8];
        command[0] = (byte) 0xAA; //包头
        command[1] = CMD_GETCARDINFO; //命令字
        int dataLen = 1; //长度
        byte[] temp = this.int2Byte(dataLen);
        System.arraycopy(temp, 0, command, 2, 4);//数据包长度长度(int)
        command[6] = (byte) 0x00; //查询指令
        command[7] = (byte) 0xFF;//包尾
        return command;
    }

    /**
     * 解析板卡详细信息
     * 
     * @param data
     * @return
     */
    public int getCardState(byte[] data)
    {
        //解析返回的板卡信息数据
        //板卡序列号长度   板卡序列号   板卡软件版本长度    板卡软件版本  板卡状态    板卡别名长度  板卡别名    机箱号 槽号 时区   对齐方式    日期  时间  NTP服务器名称长度  NTP服务器名称    同步周期
        int pos = 0;
        logger.debug("包头：" + data[pos]);
        pos += 1;
        logger.debug("命令字：" + data[pos]);
        pos += 1;
        logger.debug("协议内容长度：" + this.byte2Int(data, pos));
        pos += 4;

        int sequenceNumLen = data[pos];
        pos += 1;
        logger.debug("板卡序列号长度：" + sequenceNumLen);
        pos += sequenceNumLen;

        int softVersionLen = data[pos];
        pos += 1;
        logger.debug("板卡软件版本长度：" + softVersionLen);
        pos += softVersionLen;

        int status = data[pos];//板卡状态 0：正常 1：异常
        logger.debug("板卡状态：" + status);

        return status;
    }

    private byte[] int2Byte(int i)
    {
        byte[] retval = new byte[4];
        retval[0] = (byte) (i & 255);
        retval[1] = (byte) (i >> 8 & 255);
        retval[2] = (byte) (i >> 16 & 255);
        retval[3] = (byte) (i >> 24 & 255);
        return retval;
    }

    private int byte2Int(byte[] bytes, int pos)
    {
        if (bytes == null || bytes.length < 4 || (bytes.length - 3) < pos)
        {
            throw new IllegalArgumentException();
        }
        int retval = 0;
        retval = retval | (bytes[pos++] & 255);
        retval = retval | (bytes[pos++] & 255) << 8;
        retval = retval | (bytes[pos++] & 255) << 16;
        retval = retval | (bytes[pos] & 255) << 24;
        return retval;
    }

    private String bytes2String(byte[] bytes)
    {
        if (bytes == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(Integer.toHexString(b & 255)).append(" ");
        }
        return sb.toString();
    }

    private CollData genCollData(ObjAttribEntity oa, String value, int dataType)
    {
        CollData data = new CollData();
        data.objID = oa.getObjId();
        data.objAttrID = oa.getObjAttrId();
        data.attrID = oa.getAttribId();
        data.dataType = dataType;
        data.value = value;
        data.srcIP = mo.getIpAddr();
        data.time = time;
        return data;
    }

    public static void main(String[] args)
    {
        NXP8851Collector col = new NXP8851Collector();
        byte[] bytes = { (byte) 0xFF, (byte) 0x11, (byte) 0x33 };
        String str = col.bytes2String(bytes);
        System.out.println(str);
    }

}
