package ds_systems_project.mapreduceui;

import java.util.*;
import java.io.Serializable;

public class POI implements Serializable
{
	private static final long serialVersionUID = -2723353051271966874L;

	private String id;
	private String name;
	private double latitude, longitude;
	private String time;
	private String photo;
	private List<String> photos_list;
	private int counter;

	POI()
	{
		this.id = null;
		this.name = null;
		this.setLatitude(-1d);
		this.setLongitude(-1d);
		this.setTime(null);
		this.photo = null;
		this.photos_list = null;
		this.counter = 0;
	}

	POI(String id, String name, double latitude, double longitude, String time, String photo)
	{
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
		this.photo = photo;
		this.photos_list = null;
		this.counter = 0;
	}

	public void createPhotoList()
	{
		photos_list = new ArrayList<String>();
		photos_list.add(this.getIndividualPhoto());
	}

	public String getPOI()
	{
		return this.id;
	}

	public void setPOI(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(double longitude) 
	{
		this.longitude = longitude;
	}

	public double getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(double latitude) 
	{
		this.latitude = latitude;
	}

	public String getTime() 
	{
		return time;
	}

	public void setTime(String time) 
	{
		this.time = time;
	}

	public int getCounter()
	{
		return this.counter;
	}

	public void setCounter(int counter)
	{
		this.counter = counter;
	}

	public void incrementCounter()
	{
		counter++;
	}

	public List<String> getPhotoList() {
		return photos_list;
	}

	public  void setPhotoList(List<String> photos_list) {
		this.photos_list = photos_list;
	}

	public void addPhoto(String photo) {
		photos_list.add(photo);
	}

	public String getIndividualPhoto() {
		return photo;
	}

	public void setIndividualPhoto(String photo) {
		this.photo = photo;
	}

	@Override
	public String toString()
	{
		return("ID: " + getPOI() + " Name: " + getName() + " Counter: " + getCounter());
	}
}
