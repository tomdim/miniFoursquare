package ds_systems_project.mapreduceui;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.Intent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import java.util.HashMap;

public class Map extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    HashMap<String, POI> hashMap;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        hashMap = (HashMap<String, POI>) intent.getSerializableExtra("Results_HashMap");
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        for (String it : hashMap.keySet())
        {
            POI value = hashMap.get(it);
            LatLng pos = new LatLng(value.getLatitude(), value.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 1000, null);
            marker = mMap.addMarker((new MarkerOptions()
                    .position(pos)
                    .title(value.getName())));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                for (String it: hashMap.keySet())
                {
                    POI value = hashMap.get(it);
                    if(marker.getPosition().latitude == value.getLatitude() && marker.getPosition().longitude == value.getLongitude())
                    {
                        BackgroundTask1 runner = new BackgroundTask1();
                        runner.execute(value);
                    }

                    System.out.println(value.getPOI() + "  ||  " + value.getName() + "  ||  " + " Number of checkins:  " + value.getCounter());
                }

                return true;
            }
        });
    }

    public void DisplayAllPhotos(POI poi)
    {
        Intent intent = new Intent(this, DisplayPhotos.class);
        intent.putExtra("POI DATA", poi);
        System.out.println("INTENT:   "+ poi.getName() + " || " + poi.getCounter() + " || " + poi.getLongitude() + " || " + poi.getLatitude() + "\n");
        startActivity(intent);
    }

    private class BackgroundTask1 extends AsyncTask<POI, String, POI>
    {
        POI poi;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected POI doInBackground(POI... arguments)
        {
            poi = arguments[0];


            return poi;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(POI result)
        {
            super.onPostExecute(result);
            DisplayAllPhotos(result);
        }

    }
}