package ds_systems_project.mapreduceui;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class UI extends AppCompatActivity
{
    private EditText la1;
    private EditText lo1;
    private EditText la2;
    private EditText lo2;
    private EditText t1;
    private EditText t2;
    private EditText res;
    private EditText la1_text;
    private EditText lo1_text;
    private EditText la2_text;
    private EditText lo2_text;
    private EditText t1_text;
    private EditText t2_text;
    private EditText res_text;
    private Button sub_button;

    private Context context;
    private ProgressDialog pd;

    public UI() {}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        context = this;

        AlertDialog.Builder welcome = new AlertDialog.Builder(this);
        welcome.setMessage("Press Continue to insert the necessary data, and let the games begin!!").setPositiveButton("Continue", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).setTitle("Welcome").create();
        welcome.show();

        la1 = (EditText) findViewById(R.id.lat1);
        lo1 = (EditText) findViewById(R.id.long1);
        la2 = (EditText) findViewById(R.id.lat2);
        lo2 = (EditText) findViewById(R.id.long2);
        t1 = (EditText) findViewById(R.id.time1);
        t2 = (EditText) findViewById(R.id.time2);
        res = (EditText) findViewById(R.id.results);
        la1_text = (EditText) findViewById(R.id.lat1_text);
        lo1_text = (EditText) findViewById(R.id.long1_text);
        la2_text = (EditText) findViewById(R.id.lat2_text);
        lo2_text = (EditText) findViewById(R.id.long2_text);
        t1_text = (EditText) findViewById(R.id.time1_text);
        t2_text = (EditText) findViewById(R.id.time2_text);
        res_text = (EditText) findViewById(R.id.results_text);

        sub_button = (Button) findViewById(R.id.sub_button);

        sub_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                BackgroundTask runner = new BackgroundTask();
                String lat1 = la1_text.getText().toString();
                String long1 = lo1_text.getText().toString();
                String lat2 = la2_text.getText().toString();
                String long2 = lo2_text.getText().toString();
                String time1 = t1_text.getText().toString();
                String time2 = t2_text.getText().toString();
                String results = res_text.getText().toString();

                System.out.println(lat1 + long1 + lat2 + long2 + time1 + time2);

                runner.execute(lat1, long1, lat2, long2, time1, time2, results);
            }
        });
    }

    public void startMap(HashMap<String, POI> map)
    {
        Intent intent = new Intent(this, Map.class);
        intent.putExtra("Results_HashMap", map);
        startActivity(intent);
    }

    private class BackgroundTask extends AsyncTask <String, String, HashMap<String, POI>>
    {

        final int NUMBER_OF_MAPPERS = 3;
        final String MAPPER1_IP = "192.168.2.9";
        final String MAPPER2_IP = "192.168.2.22";
        final String MAPPER3_IP = "192.168.2.17";
        final int MAPPER1_PORT = 10555;
        final int MAPPER2_PORT = 10666;
        final int MAPPER3_PORT = 10777;

        final String REDUCER_IP = "192.168.2.8";
        final int REDUCER_PORT = 11111;

        final int CLIENT_PORT = 11169;
        Area area = null;
        Time time = null;
        protected ArrayList<Area> subAreas = new ArrayList<Area>();
        protected int NUMBER_OF_RESULTS = 0;

        String time1, time2;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected HashMap doInBackground(String... arguments)
        {
            Double lat1 = Double.parseDouble(arguments[0]);
            Double long1 = Double.parseDouble(arguments[1]);
            Double lat2 = Double.parseDouble(arguments[2]);
            Double long2 = Double.parseDouble(arguments[3]);
            String time1 = arguments[4];
            String time2 = arguments[5];
            NUMBER_OF_RESULTS = Integer.parseInt(arguments[6]);
            double tmp;

            if(long1 > long2)
            {
                tmp = long2;
                long2 = long1;
                long1 = tmp;
            }

            if(lat1 > lat2)
            {
                tmp = lat2;
                lat2 = lat1;
                lat1 = tmp;
            }

            area = new Area(0.0, 0.0, 0.0, 0.0, true);
            time = new Time();

            // Extract arguments
            area.setLat1(lat1);
            area.setLong1(long1);
            area.setLat2(lat2);
            area.setLong2(long2);
            time.setTime1(time1);
            time.setTime2(time2);

            subAreas = area.divideArea(NUMBER_OF_MAPPERS);

            try
            {
                distributeToMappers();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            waitForMappers();

            try
            {
                ackToReducer();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            HashMap<String, POI> map = collectDataFromReducer();

            System.out.println("doInBackground() -- OK");
            showResultsToConsole(map);
            return map;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(HashMap<String, POI> result)
        {
            super.onPostExecute(result);
            System.out.println("onPostExecute() -- OK");
            if (pd!=null) {
                pd.dismiss();
                sub_button.setEnabled(true);
            }
            showResultsToConsole(result);
            showResultsOnMap(result);
        }

        private void distributeToMappers() throws UnknownHostException, IOException
        {
            Socket requestSocket1 = null;
            Socket requestSocket2 = null;
            Socket requestSocket3 = null;
            ObjectOutputStream out1 = null;
            ObjectOutputStream out2 = null;
            ObjectOutputStream out3 = null;

            try
            {
                requestSocket1 = new Socket(InetAddress.getByName(MAPPER1_IP), MAPPER1_PORT);
                requestSocket2 = new Socket(InetAddress.getByName(MAPPER2_IP), MAPPER2_PORT);
                requestSocket3 = new Socket(InetAddress.getByName(MAPPER3_IP), MAPPER3_PORT);
                out1 = new ObjectOutputStream(requestSocket1.getOutputStream());
                out2 = new ObjectOutputStream(requestSocket2.getOutputStream());
                out3 = new ObjectOutputStream(requestSocket3.getOutputStream());

                String msg1 = subAreas.get(0).getLat1() + "|"  + subAreas.get(0).getLong1() + "|" + subAreas.get(0).getLat2() + "|" + subAreas.get(0).getLong2() + "|" + time.getTime1() + "|" + time.getTime2();

                String msg2 = subAreas.get(1).getLat1() + "|"  + subAreas.get(1).getLong1() + "|" + subAreas.get(1).getLat2() + "|" + subAreas.get(1).getLong2() + "|" + time.getTime1() + "|" + time.getTime2();

                String msg3 = subAreas.get(2).getLat1() + "|"  + subAreas.get(2).getLong1() + "|" + subAreas.get(2).getLat2() + "|" + subAreas.get(2).getLong2() + "|" + time.getTime1() + "|" + time.getTime2();

                System.out.println("Mess 1 sent!: " + msg1);
                out1.writeUTF(msg1);
                out1.flush();

                System.out.println("Mess 2 sent!: " + msg2);
                out2.writeUTF(msg2);
                out2.flush();

                System.out.println("Mess 3 sent!: " + msg3);
                out3.writeUTF(msg3);
                out3.flush();
            }
            catch(UnknownHostException unknownHost)
            {
                System.err.println("You are trying to connect to an unknown host!");
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            } finally {
                try
                {
                    out1.close();
                    requestSocket1.close();
                    out2.close();
                    requestSocket2.close();
                    out3.close();
                    requestSocket3.close();
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }

        }

        public void waitForMappers()
        {
            ServerSocket socket = null;
            Socket connection = null;
            ObjectInputStream in;
            int state = 0;
            try
            {
                socket = new ServerSocket(CLIENT_PORT);

                while (state < 3)
                {
                    //Wait for connection
                    connection = socket.accept();

                    //get Input streams
                    in = new ObjectInputStream(connection.getInputStream());

                    state += in.readInt();

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
                    socket.close();
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }
        }

        public void ackToReducer() throws UnknownHostException, IOException
        {
            Socket ackSocket = null;
            ObjectOutputStream out = null;
            String ack = "";

            try
            {
                ackSocket = new Socket(InetAddress.getByName(REDUCER_IP), REDUCER_PORT);
                out = new ObjectOutputStream(ackSocket.getOutputStream());

                ack = "true" + "|" + NUMBER_OF_RESULTS;

                out.writeUTF(ack);
                out.flush();
                System.out.println("ACK: " + ack);
            }
            catch(UnknownHostException unknownHost)
            {
                System.err.println("You are trying to connect to an unknown host!");
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            } finally {
                try
                {
                    out.close();
                    ackSocket.close();
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }
        }

        public HashMap<String, POI> collectDataFromReducer()
        {
            ServerSocket clientSocket = null;
            Socket connection = null;
            ObjectInputStream in;
            HashMap<String, POI> final_results = null;

            try
            {
                clientSocket = new ServerSocket(CLIENT_PORT);

                while (true)
                {
                    //Wait for connection
                    connection = clientSocket.accept();

                    //get Input streams
                    in = new ObjectInputStream(connection.getInputStream());

                    try
                    {
                        final_results = (HashMap<String, POI>) in.readObject();
                        System.out.println("collectDataFromReducer() -- OK");
                        showResultsToConsole(final_results);

                        return final_results;
                        //showResults(final_results);
                    }
                    catch(ClassNotFoundException classnot)
                    {
                        System.err.println("Data received in unknown format!");
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
                    clientSocket.close();
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }

            return final_results;
        }

        public void showResultsOnMap(HashMap<String, POI> final_results)
        {
            startMap(final_results);
        }

        public void showResultsToConsole(HashMap<String, POI> final_results)
        {
            int counter = 0;
            System.out.println("Top " + NUMBER_OF_RESULTS + " Points of Interest in the area you asked for");
            System.out.println("-----------------------------------------------------------");
            for (String it: final_results.keySet())
            {
                counter++;
                POI value = final_results.get(it);
                System.out.println("POI Number " + counter + ":  " + value.getPOI() + "  ||  " + value.getName() + "  ||  " + " Number of checkins:  " + value.getCounter());
            }
        }
    }

}
