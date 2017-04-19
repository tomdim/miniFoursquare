package ds_systems_project.mapreduceui;

import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MapWorker
{
	final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

	int MAPPER_PORT = 10666; //input from user via console
	/*
	MAPPER1_PORT : 10555;
	MAPPER2_PORT : 10666;
	MAPPER3_PORT : 10777;
	 */
	final String REDUCER_IP = "192.168.2.9";
	final int REDUCER_PORT = 11111;

	final String MASTER_IP = "192.168.2.11"; /***********************************/
	final int MASTER_PORT = 11169;

	Area area = new Area(0.0, 0.0, 0.0, 0.0, true);
	Time time = new Time();
	protected static ArrayList<Area> subAreas = null;
	Query query = null;
	protected static HashMap<String, POI> intermediateResults = null;

	public static void main(String[] args)
	{
		while(true)
		{
			MapWorker mapper = new MapWorker();
			subAreas = new ArrayList<Area>();
			intermediateResults = new HashMap<String, POI>();
			System.out.println("Running Mapper at port 10666...");
			//mapper.scanForPort();

			mapper.receiveDataFromClient();
			mapper.startMapper();
			mapper.sendToReducer();
		}
	}

	public MapWorker()
	{

	}

	protected void scanForPort()
	{
		Scanner scan = new Scanner(System.in);

		System.out.println("Enter the open port for the current mapper: ");
		MAPPER_PORT = scan.nextInt();

		scan.close();	
	}

	public void startMapper()
	{
		subAreas = area.divideArea(NUMBER_OF_CORES);
		System.out.println("Area devided to " + NUMBER_OF_CORES + " subareas.");

		for(int i = 0; i < NUMBER_OF_CORES; i++)
		{
			query = new Query(subAreas.get(i), time);
			connectToDB(query.createQuery(area), i);
			System.out.println("Query " + i + " Executed!");
		}

		map(subAreas);
	}

	public void map(ArrayList<Area> subAreas)
	{
		ArrayList<ArrayList<POI>> counted = new ArrayList<>();
		counted = (ArrayList<ArrayList<POI>>) subAreas.stream().parallel().map(p -> p.process()).collect(Collectors.toList());

		for(int i = 0; i < counted.size(); i++)
		{
			for(int j = 0; j < counted.get(i).size(); j++)
			{
				intermediateResults.put(counted.get(i).get(j).getPOI(), counted.get(i).get(j));
			}
		}

		System.out.println("Map Process DONE!");
	}

	public void connectToDB(String query_l, int subArea)
	{
		String dbURL = "jdbc:mysql://83.212.117.76:3306/ds_systems_2016?user=omada11&password=omada11db";
		String dbClass = "com.mysql.jdbc.Driver";

		try {
			Class.forName(dbClass);
			Connection con = DriverManager.getConnection(dbURL);
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(query_l);

			while (rs.next()) 
			{
				POI tmp = new POI(rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getString(5), rs.getString(6));
				subAreas.get(subArea).addPointOfInterest(tmp);
			}
			con.close();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}
	}

	public void receiveDataFromClient()
	{
		ServerSocket cToMapperSocket = null;
		Socket connection = null;
		ObjectInputStream in;
		String data = null;

		try
		{
			cToMapperSocket = new ServerSocket(MAPPER_PORT); 

			while (data == null)
			{
				//Wait for connection
				connection = cToMapperSocket.accept();

				//get Input streams
				in = new ObjectInputStream(connection.getInputStream());

				data = in.readUTF();

				StringTokenizer tokens = new StringTokenizer(data, "|");

				area.setLat1(Double.parseDouble((String)tokens.nextElement()));
				area.setLong1(Double.parseDouble((String)tokens.nextElement()));
				area.setLat2(Double.parseDouble((String)tokens.nextElement()));
				area.setLong2(Double.parseDouble((String)tokens.nextElement()));
				time.setTime1((String)tokens.nextElement());
				time.setTime2((String)tokens.nextElement());

				System.out.println(connection.getInetAddress().getHostAddress() + " > Area & Time Data Received from Client!");

				in.close();
				connection.close();
			}
		} 
		catch(IOException ioException) 
		{
			ioException.printStackTrace();
		}
		finally 
		{
			//Closing connection
			try
			{
				cToMapperSocket.close();
			} 
			catch (IOException ioException) 
			{
				ioException.printStackTrace();
			}
		}
	}

	public void notifyMaster()
	{
		Socket notification = null;
		ObjectOutputStream out = null;

		try
		{
			notification = new Socket(InetAddress.getByName(MASTER_IP), MASTER_PORT);
			out = new ObjectOutputStream(notification.getOutputStream());

			System.out.println("Sending ack to Master...");
			out.writeInt(1);
			out.flush();
			System.out.println("Ack sent to Master!\n*************************\n");
		} 
		catch(IOException ioException) 
		{
			ioException.printStackTrace();
		}
		finally 
		{
			//Closing connection
			try
			{
				out.close();
				notification.close();
			} 
			catch (IOException ioException) 
			{
				ioException.printStackTrace();
			}
		}
	}

	public void sendToReducer()
	{
		Socket mToReducerSocket = null;
		ObjectOutputStream out = null;

		try
		{
			mToReducerSocket = new Socket(InetAddress.getByName(REDUCER_IP), REDUCER_PORT);
			out = new ObjectOutputStream(mToReducerSocket.getOutputStream());

			/* FOR DEBUGGING PURPOSES
			for (String poi: intermediateResults.keySet())
			{
	            String key =poi.toString();
	            String value = intermediateResults.get(poi).getName() + " " + intermediateResults.get(poi).getCounter() ;  
	            System.out.println(key + " " + value);  
			}
			 */

			System.out.println("Sending intermediate results to Reducer...");
			out.writeObject(MapWorker.intermediateResults);
			out.flush();
			System.out.println("Intermediate results sent to Reducer!");
		} 
		catch(IOException ioException) 
		{
			ioException.printStackTrace();
		}
		finally 
		{
			//Closing connection
			try
			{
				out.close();
				mToReducerSocket.close();
			} 
			catch (IOException ioException) 
			{
				ioException.printStackTrace();
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			notifyMaster();
		}

	}
}