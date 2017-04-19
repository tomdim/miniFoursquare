package ds_systems_project.mapreduceui;

public class Query {

	String query;
	Area area = null;
	Time time = null;

	public Query(Area area, Time time)
	{
		this.area = area;
		this.time = time;
	}

	public String createQuery(Area totalArea)
	{
		if (this.area.getLat2() == totalArea.getLat2())
		{
			query = "SELECT POI, POI_name, latitude, longitude, time, photos FROM checkins WHERE latitude >= " + 
					this.area.getLat1() + " AND latitude <= " + this.area.getLat2() +
					" AND longitude >= " + this.area.getLong1() + " AND longitude <= " + this.area.getLong2() +
					" AND time BETWEEN '" + this.time.getTime1() + "' AND '" + this.time.getTime2() + "';";
			System.out.println(query); //for debugging purposes
			return query;

		}

		query =  "SELECT POI, POI_name, latitude, longitude, time, photos FROM checkins WHERE latitude >= " + 
				this.area.getLat1() + " AND latitude < " + this.area.getLat2() +
				" AND longitude >= " + this.area.getLong1() + " AND longitude <= " + this.area.getLong2() +
				" AND time BETWEEN '" + this.time.getTime1() + "' AND '" + this.time.getTime2() + "';";
		System.out.println(query); //for debugging purposes
		return query;
	}

	public String getQuery() {
		return query;
	}
}
