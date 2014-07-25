/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.util;

public class TimeSpan {
	
	private long millisecond;
	
	public TimeSpan(long millisecond){
		this.millisecond = millisecond;
	}
	
	//相距的天数
	public double getDays(){
		return millisecond/1000.0/60/60/24;
	}
	
	//相距的小时
	public double getHours(){
		return millisecond/1000.0/60/60;
	}
	
	//相距的分钟
	public double getMinutes(){
		return millisecond/1000.0/60;
	}
	
	//相跑的毫秒数
	public long getMillisecond(){
		return this.millisecond;
	}
}