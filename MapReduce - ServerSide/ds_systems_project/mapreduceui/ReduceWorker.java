package ds_systems_project.mapreduceui;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;

public class ReduceWorker
{
	final String MASTER_IP = "192.168.2.11"; /*******************************/
	final int MASTER_PORT = 11169;
	final int REDUCER_PORT = 11111;


	protected HashMap<String, POI> mapper1 = null;
	protected HashMap<String, POI> mapper2 = null;
	protected HashMap<String, POI> mapper3 = null;

	protected HashMap<String, POI> tmp = null;
	protected HashMap<String, POI> final_results = null;
	int numOfRes = 0;

	public static void main(String[] args)
	{
		while(true)
		{
			ReduceWorker reducer = new ReduceWorker();
			
			reducer.waitForMappers();
			reducer.waitForMasterAck();
			reducer.reduce();
			reducer.sendResults();
		}
	}

	ReduceWorker()
	{
		this.mapper1 = new HashMap<>();
		this.mapper2 = new HashMap<>();
		this.mapper3 = new HashMap<>();
		this.final_results = new HashMap<>();
	}

	public void waitForMasterAck()
	{
		ServerSocket masterAckSocket = null;
		ObjectInputStream in;
		Socket connection = null;
		boolean ack = false;	
		String data;

		try
		{
			masterAckSocket = new ServerSocket(REDUCER_PORT, 20);

			while(ack == false)
			{
				System.out.println("Waiting for master to send the ack...");
				//Wait for connection
				connection = masterAckSocket.accept();

				//get Input streams
				in = new ObjectInputStream(connection.getInputStream());

				data = in.readUTF();
				System.out.println(data);

				StringTokenizer tokens = new StringTokenizer(data, "|");

				ack = Boolean.parseBoolean((String)tokens.nextElement());

				if( ack == true )
				{
					numOfRes = Integer.parseInt((String)tokens.nextElement());
				}

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
				masterAckSocket.close();
			} 
			catch (IOException ioException) 
			{
				ioException.printStackTrace();
			}
		}
	}

	public void waitForMappers()
	{
		ServerSocket reducerSocket = null;

		Socket connection1 = null;
		Socket connection2 = null;
		Socket connection3 = null;

		ObjectInputStream in;

		try
		{

			reducerSocket = new ServerSocket(REDUCER_PORT, 20);

			while(mapper1.isEmpty() || mapper2.isEmpty() || mapper3.isEmpty())
			{
				System.out.println("Waiting for mappers to send me their results...");
				//Wait for connection
				connection1 = reducerSocket.accept();
				connection2 = reducerSocket.accept();
				connection3 = reducerSocket.accept();

				//get Input streams
				in = new ObjectInputStream(connection1.getInputStream());
				try 
				{
					this.mapper1 = (HashMap<String, POI>) in.readObject();

					/*FOR DEBUGGING PURPOSES
					for (String poi: mapper1.keySet())
					{
			            String key =poi.toString();
			            String value = mapper1.get(poi).getName() + " " + mapper1.get(poi).getCounter() ;  
			            System.out.println(key + " " + value);  
					} 
					 */

					System.out.println("Mapper1 OK");
				}
				catch(ClassNotFoundException classnot) 
				{
					System.err.println("Data received in unknown format!");
				}
				in.close();
				connection1.close();

				in = new ObjectInputStream(connection2.getInputStream());
				try 
				{
					this.mapper2 = (HashMap<String, POI>) in.readObject();

					/*FOR DEBUGGING PURPOSES
					for (String poi: mapper2.keySet())
					{
			            String key =poi.toString();
			            String value = mapper2.get(poi).getName() + " " + mapper2.get(poi).getCounter() ;  
			            System.out.println(key + " " + value);  
					} 
					 */

					System.out.println("Mapper2 OK");
				}
				catch(ClassNotFoundException classnot) 
				{
					System.err.println("Data received in unknown format!");
				}
				in.close();
				connection2.close();

				in = new ObjectInputStream(connection3.getInputStream());
				try 
				{
					this.mapper3 = (HashMap<String, POI>) in.readObject();

					/*FOR DEBUGGING PURPOSES
					for (String poi: mapper3.keySet())
					{
			            String key =poi.toString();
			            String value = mapper3.get(poi).getName() + " " + mapper3.get(poi).getCounter() ;  
			            System.out.println(key + " " + value);  
					} 
					 */

					System.out.println("Mapper3 OK");
				}
				catch(ClassNotFoundException classnot) 
				{
					System.err.println("Data received in unknown format!");
				}
				in.close();
				connection3.close();

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
				reducerSocket.close();
			} 
			catch (IOException ioException) 
			{
				ioException.printStackTrace();
			}
		}
	}

	public void reduce()
	{
		tmp = new HashMap<>();

		tmp.putAll(mapper1);
		tmp.putAll(mapper2);
		tmp.putAll(mapper3);

		System.out.println("Reducing final results...");

		ReduceObject r = new ReduceObject(numOfRes);
		final_results = r.finalizeResults(tmp);

		System.out.println("Reduce Process DONE!");
	}

	public void sendResults()
	{
		Socket results = null;
		ObjectOutputStream out = null;

		try
		{
			results = new Socket(InetAddress.getByName(MASTER_IP), MASTER_PORT);
			out = new ObjectOutputStream(results.getOutputStream());

			System.out.println("Sending final results to Client...");
			out.writeObject(final_results);
			out.flush();
			System.out.println("Final results sent to Client!\n");
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
				results.close();
				out.close();
			} 
			catch (IOException ioException) 
			{
				ioException.printStackTrace();
			}
		}
	}
}