package ds_systems_project.mapreduceui;

import java.io.Serializable;
import java.util.*;

import java.io.Serializable;
import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;

public class Area implements Serializable
{
	private static final long serialVersionUID = 1L;
	private double long1, lat1;
	private double long2, lat2;
	private boolean contains_sub_areas;
	private ArrayList<POI> pointsOfInterest = new ArrayList<POI>();

	public Area()
	{
		this.long1 = 0;
		this.lat1 = 0;
		this.long2 = 0;
		this.lat2 = 0;
		this.contains_sub_areas = false;
	}

	public Area(double long1, double lat1, double long2, double lat2, boolean contains_sub_areas)
	{
		this.setLong1(long1);
		this.setLat1(lat1);
		this.setLong2(long2);
		this.setLat2(lat2);
		this.setContainsSubAreas(contains_sub_areas);
	}

	public ArrayList<Area> divideArea(int DIVISIONS)
	{
		ArrayList<Area> subAreas_l = new ArrayList<Area>();
		for(int i = 0; i < DIVISIONS; i++)
		{
			subAreas_l.add(new Area());
		}

		double l = this.getLat1();

		for(int i = 1; i < DIVISIONS; i++)
		{
			subAreas_l.get(i-1).setLat1(l);

			l = this.getLat1() + i * ((this.getLat2() - this.getLat1())/DIVISIONS);

			subAreas_l.get(i-1).setLat2(l);
			subAreas_l.get(i-1).setLong1(this.getLong1());
			subAreas_l.get(i-1).setLong2(this.getLong2());
		}
		subAreas_l.get(DIVISIONS-1).setLat1(l);
		subAreas_l.get(DIVISIONS-1).setLat2(this.getLat2());
		subAreas_l.get(DIVISIONS-1).setLong1(this.getLong1());
		subAreas_l.get(DIVISIONS-1).setLong2(this.getLong2());

		return subAreas_l;
	}

//	public ArrayList<POI> process()
//	{
//		ArrayList<POI> distinctPOIvalues = new ArrayList<POI>();
//		POI poi;
//		for(int i = 0; i < pointsOfInterest.size(); i++)
//		{
//			poi = countNmerge(pointsOfInterest.get(i));
//
//			if(!distinctPOIvalues.contains(poi))
//				distinctPOIvalues.add(poi);
//		}
//
//		return distinctPOIvalues;
//	}

//	public POI countNmerge(POI poi)
//	{
//		ArrayList<POI> poi_l = new ArrayList<POI>();
//		Stream<POI> x = this.getPointsOfInterest().parallelStream().filter(p -> p.getPOI().contains(poi.getPOI()));
//
//		poi_l = (ArrayList<POI>) x.collect(Collectors.toList());
//
//		poi.createPhotoList();
//
//		for(int i = 0; i < poi_l.size(); i++)
//		{
//			poi.addPhoto(poi_l.get(i).getIndividualPhoto());
//		}
//
//		poi.setCounter(poi_l.size());
//
//		return poi;
//	}

	public double getLong1()
	{
		return long1;
	}

	public void setLong1(double long1)
	{
		this.long1 = long1;
	}

	public double getLat1()
	{
		return lat1;
	}

	public void setLat1(double lat1)
	{
		this.lat1 = lat1;
	}

	public double getLong2()
	{
		return long2;
	}

	public void setLong2(double long2)
	{
		this.long2 = long2;
	}

	public double getLat2()
	{
		return lat2;
	}

	public void setLat2(double lat2)
	{
		this.lat2 = lat2;
	}

	public boolean getContainsSubAreas()
	{
		return this.contains_sub_areas;
	}

	public void setContainsSubAreas(boolean contains_sub_areas)
	{
		this.contains_sub_areas = contains_sub_areas;
	}

	public void addPointOfInterest(POI poi)
	{
		this.pointsOfInterest.add(poi);
		;
	}

	public ArrayList<POI> getPointsOfInterest()
	{
		return this.pointsOfInterest;
	}
}


/*
public class Area implements Serializable
{
	private static final long serialVersionUID = 1L;
	private double long1, lat1; 
	private double long2, lat2;

	public Area()
	{
		this.long1 = 0;
		this.lat1 = 0;
		this.long2 = 0;
		this.lat2 = 0;
	}

	public Area(double long1, double lat1, double long2, double lat2)
	{
		this.setLong1(long1);
		this.setLat1(lat1);
		this.setLong2(long2);
		this.setLat2(lat2);
	}

	public ArrayList<Area> divideArea(int DIVISIONS)
	{
		ArrayList<Area> subAreas_l = new ArrayList<Area>();
		for(int i = 0; i < DIVISIONS; i++)
		{
			subAreas_l.add(new Area());
		}

		double l = this.getLat1();

		for(int i = 1; i < DIVISIONS; i++)
		{
			subAreas_l.get(i-1).setLat1(l);

			l = this.getLat1() + i * ((this.getLat2() - this.getLat1())/DIVISIONS);

			subAreas_l.get(i-1).setLat2(l);
			subAreas_l.get(i-1).setLong1(this.getLong1());
			subAreas_l.get(i-1).setLong2(this.getLong2());
		}	
		subAreas_l.get(DIVISIONS-1).setLat1(l);
		subAreas_l.get(DIVISIONS-1).setLat2(this.getLat2());
		subAreas_l.get(DIVISIONS-1).setLong1(this.getLong1());
		subAreas_l.get(DIVISIONS-1).setLong2(this.getLong2());

		return subAreas_l;
	}

	public double getLong1()
	{
		return long1;
	}

	public void setLong1(double long1)
	{
		this.long1 = long1;
	}

	public double getLat1()
	{
		return lat1;
	}

	public void setLat1(double lat1)
	{
		this.lat1 = lat1;
	}

	public double getLong2()
	{
		return long2;
	}

	public void setLong2(double long2)
	{
		this.long2 = long2;
	}

	public double getLat2()
	{
		return lat2;
	}

	public void setLat2(double lat2)
	{
		this.lat2 = lat2;
	}
}*/
