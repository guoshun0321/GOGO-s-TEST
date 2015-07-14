package jetsennet.jnmp.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import jetsennet.jbmp.business.AlarmStatistic;
import jetsennet.jbmp.dataaccess.TopoMapDal;
import jetsennet.jbmp.entity.AlarmStatisticEntity;
import jetsennet.jbmp.entity.TopoMapEntity;
import jetsennet.net.WSResult;
import jetsennet.net.WebServiceBase;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

@WebService(name = "NMPNavigateService", serviceName = "NMPNavigateService", targetNamespace = "http://JetsenNet/JNMP/")
public class NMPNavigateService extends WebServiceBase{

	private ConnectionInfo bmpConnectionInfo;
	private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JBMP");

	public NMPNavigateService()
	{
		bmpConnectionInfo = new ConnectionInfo(DbConfig.getProperty("bmp_driver"), DbConfig.getProperty("bmp_dburl"), DbConfig
				.getProperty("bmp_dbuser"), DbConfig.getProperty("bmp_dbpwd"));
	}

	public WSResult nmpGetBuildingInfo(String  userId)
    {
    	WSResult ret = new WSResult();

		ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
		Document ds1 = null;
		Document ds2 = null;
		Document ds3 = null;
		Document ds = DocumentHelper.createDocument();
		try 
		{
			ds1 = execBmp.fill("SELECT * FROM NMP_FLOOR");
			ds2 = execBmp.fill("SELECT A.*, B.FLOOR_ID FROM NMP_ROOM A INNER JOIN NMP_ROOM2FLOOR B ON A.ROOM_ID = B.ROOM_ID");
			ds3 = execBmp.fill("SELECT A.MAP_ID, A.MAP_NAME, B.RF_ID FROM BMP_TOPOMAP A INNER JOIN NMP_TOPO2RF B ON A.MAP_ID = B.MAP_ID AND B.RF_TYPE = 0 INNER JOIN NMP_RFTOPO2ROLE C ON A.MAP_ID = C.MAP_ID AND C.ROLE_ID = " + userId);
			
			Element nav = ds.addElement("nav");
			nav.addAttribute("userID", userId);
			Element building = nav.addElement("building");
			
			List<Element> list1 = ds1.selectNodes("//DataTable");
			Element floors = building.addElement("floors");
			floors.addAttribute("total", list1.size() + "");
			for(Element e1 : list1){
				Element floor = floors.addElement("floor");
				floor.addAttribute("id", e1.elementText("FLOOR_ID"));
				floor.addAttribute("name", e1.elementText("FLOOR_NAME"));
				floor.addAttribute("alias", e1.elementText("FLOOR_ALIAS"));
				floor.addAttribute("attach", e1.elementText("FLOOR_ATTACH"));
				floor.addAttribute("level", e1.elementText("FLOOR_NUM"));
				floor.addAttribute("remarks", "");
				
				List<Element> list2 = ds2.selectNodes("//DataTable[FLOOR_ID=" + e1.elementText("FLOOR_ID") + "]");
				Element rooms = floor.addElement("rooms");
				rooms.addAttribute("total", list2.size() + "");
				for(Element e2 : list2){
					Element room = rooms.addElement("room");
					room.addAttribute("id", e2.elementText("ROOM_ID"));
					room.addAttribute("name", e2.elementText("ROOM_NAME"));
					room.addAttribute("alias", e2.elementText("ROOM_ALIAS"));
					room.addAttribute("remarks", "");
					
					List<Element> list3 = ds3.selectNodes("//DataTable[RF_ID=" + e2.elementText("ROOM_ID") + "]");
					Element maps = room.addElement("maps");
					maps.addAttribute("total", list3.size() + "");
					for(Element e3 : list3){
						Element map = maps.addElement("map");
						map.addAttribute("id", e3.elementText("MAP_ID"));
						map.addAttribute("name", e3.elementText("MAP_NAME"));
						map.addAttribute("type", "");
						map.addAttribute("remarks", "");
					}
				}
			}
		} 
		catch (SQLException e) 
		{
			logger.error("", e);
		}
		ret.resultVal = ds.asXML();
    	
    	return ret;
    }
	
	public WSResult getMapAlarmInfo(String  mapIds){
		WSResult ret = new WSResult();
		
		Document ds = DocumentHelper.createDocument();
		Element nav = ds.addElement("nav");
		Element maps = nav.addElement("maps");
		
		TopoMapDal dal = new TopoMapDal();
		try {
			List<TopoMapEntity> list = dal.getLst(new SqlCondition("MAP_ID" , mapIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric, true));
			if(!list.isEmpty()){
				List<Integer> groupIdList = new ArrayList<Integer>();
				for(int i = 0; i < list.size(); i++){
					groupIdList.add(list.get(i).getGroupId());
				}
				List<AlarmStatisticEntity> listAlarm = AlarmStatistic.getInstance().getGrpStatisticInfos(groupIdList);
				for(int i = 0; i < list.size(); i++){
					Element map = maps.addElement("map");
					map.addAttribute("id", list.get(i).getMapId() + "");
					
					AlarmStatisticEntity e = listAlarm.get(i);
					String topAlarm = "0";
					if(e.getLevel40() > 0){
						topAlarm = "40";
					}else if(e.getLevel30() > 0){
						topAlarm = "30";
					}else if(e.getLevel20() > 0){
						topAlarm = "20";
					}else if(e.getLevel10() > 0){
						topAlarm = "10";
					}
					map.addAttribute("topAlarm", topAlarm);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		ret.resultVal = ds.asXML();
		
    	return ret;
	}
}
