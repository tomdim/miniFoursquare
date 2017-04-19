package ds_systems_project.mapreduceui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReduceObject 
{
	int NUMBER_OF_RESULTS = 0;
	ArrayList<POI> poi_list = null;

	public ReduceObject(ArrayList<POI> poi_list)
	{
		this.poi_list = poi_list; 
	}

	public ReduceObject(int numOfRes) 
	{
		this.NUMBER_OF_RESULTS = numOfRes;
	}

	public HashMap<String, POI> finalizeResults(HashMap<String, POI> tmp)
	{
		//remove Duplicate POIs and photos
		HashMap<String, POI> tmp_l = new HashMap<>();

		ArrayList<POI> results = removeDuplicatePOIs(tmp);
		System.out.println("Removed duplicate POIs from final results!");
		ArrayList<POI> results2 = checkPhotos(results);
		System.out.println("Removed duplicate photos from final results!");

		/*FOR DEBUGGING PURPOSES
		for(int i = 0; i < results2.size(); i++)
		{
			System.out.println("Printing POI MATE");
			System.out.println(results2.get(i).getPOI() + " || " + results2.get(i).getCounter());
		}
		 */

		for(int i = 0; i < results2.size(); i++)
		{
			tmp_l.put(results2.get(i).getPOI(), results2.get(i));
			//System.out.println(tmp_l.get(results2.get(i).getPOI())); - FOR DEBUGGING PURPOSES
		}

		//sort & keep the top N results
		LinkedList<Map.Entry<String, POI>> linkedList = new LinkedList<>(tmp_l.entrySet());
		List<Map.Entry<String, POI>> list = linkedList;

		Collections.sort(list, (o1, o2) -> ((Comparable<Integer>) ((POI) ((Map.Entry<String, POI>) (o2)).getValue()).getCounter()).compareTo(((Map.Entry<String, POI>) (o1)).getValue().getCounter()));

		int counter = 0; //used to keep the top N results

		HashMap <String, POI>sortedHashMap = new LinkedHashMap<>();
		for (Iterator<Map.Entry<String, POI>> it = list.iterator(); it.hasNext();)
		{
			Map.Entry<String, POI> entry = (Map.Entry<String, POI>) it.next();

			if(counter < NUMBER_OF_RESULTS)
			{
				System.out.println(entry.getKey() + " || " + entry.getValue().getName() + " || " + entry.getValue().getCounter()); //- FOR DEBUGGING PURPOSES
				sortedHashMap.put(entry.getKey(), entry.getValue());
			}
			else
			{
				break;
			}
			counter++;
		}

		System.out.println("Final " + NUMBER_OF_RESULTS + " results sorted!");
		return sortedHashMap;

	}//end of finalizeResults

	public ArrayList<POI> removeDuplicatePOIs(HashMap<String, POI> tmp)
	{
		ArrayList<POI> poi_list = new ArrayList<POI>();
		for (POI poi : tmp.values()) 
		{
			poi_list.add(poi);
		}

		Stream<POI> distinct = null;

		distinct = poi_list.parallelStream().distinct();

		poi_list = (ArrayList<POI>) distinct.collect(Collectors.toList());

		return poi_list;
	}

	public ArrayList<POI> checkPhotos(ArrayList<POI> poi_list)
	{
		Stream<String> distinct = null;

		for(int i=0; i < poi_list.size(); i++)
		{
			distinct = poi_list.get(i).getPhotoList().stream().distinct().filter(p -> p != "Not exists");
			poi_list.get(i).setPhotoList( distinct.collect(Collectors.toList()) );
		}

		return poi_list;
	}

	public ArrayList<POI> getPoi_list() {
		return poi_list;
	}

	public void setPoi_list(ArrayList<POI> poi_list) {
		this.poi_list = poi_list;
	}

}
