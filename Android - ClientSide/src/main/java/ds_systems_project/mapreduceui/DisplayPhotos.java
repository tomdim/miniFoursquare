package ds_systems_project.mapreduceui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class DisplayPhotos extends AppCompatActivity
{

    private TextView lat;
    private TextView lon;
    private TextView num_check;
    private TextView photos;
    private TextView poi_n_text;
    private TextView lat_text;
    private TextView lon_text;
    private TextView num_check_text;
    private TextView photos_text;
    private POI poi;

    public DisplayPhotos() {}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.links_layout);

        lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);
        num_check = (TextView) findViewById(R.id.num_check);
        photos = (TextView) findViewById(R.id.photos);

        poi_n_text = (TextView) findViewById(R.id.poi_n_text);
        lat_text = (TextView) findViewById(R.id.lat_text);
        lon_text = (TextView) findViewById(R.id.lon_text);
        num_check_text = (TextView) findViewById(R.id.num_check_text);
        photos_text = (TextView) findViewById(R.id.photos_text);

        Intent intent = getIntent();
        poi = (POI) intent.getSerializableExtra("POI DATA");

        poi_n_text.setText(poi.getName());
        lat_text.setText("" + poi.getLatitude());
        lon_text.setText("" + poi.getLongitude());
        num_check_text.setText("" + poi.getCounter());

        String photos_str = "";

        for(int i = 0; i < poi.getPhotoList().size(); i++)
        {
            photos_str += i+1 + ". " + poi.getPhotoList().get(i) + "\n\n";
        }

        photos_text.setMovementMethod(new ScrollingMovementMethod());
        photos_text.setText(photos_str);
        photos_text.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
