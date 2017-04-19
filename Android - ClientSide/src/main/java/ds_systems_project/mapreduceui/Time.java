package ds_systems_project.mapreduceui;

import java.io.Serializable;

public class Time implements Serializable
{
	private static final long serialVersionUID = 2L;
	private String time1, time2;
	
	public Time()
	{
		this.time1 = "0000-00-00 00:00:00";
		this.time2 = "0000-00-00 00:00:00";
	}
	
	public Time(String time1, String time2)
	{
		this.setTime1(time1);
		this.setTime2(time2);
	}
	
	public String getTime1()
	{
		return time1;
	}

	public void setTime1(String time1)
	{
		this.time1 = time1;
	}
	
	public String getTime2()
	{
		return time2;
	}

	public void setTime2(String time2)
	{
		this.time2 = time2;
	}
}
