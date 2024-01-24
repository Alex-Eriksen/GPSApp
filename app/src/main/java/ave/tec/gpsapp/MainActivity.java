package ave.tec.gpsapp;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;

    private TextView txt_latitude, txt_longitude, txt_speed, txt_accuracy, txt_altitude, txt_bearing;

    private final Timer timer = new Timer();

    private boolean mAutoUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_latitude = (TextView) findViewById(R.id.txt_latitude);
        txt_longitude = (TextView) findViewById(R.id.txt_longitude);
        txt_speed = (TextView) findViewById(R.id.txt_speed);
        txt_accuracy = (TextView) findViewById(R.id.txt_accuracy);
        txt_altitude = (TextView) findViewById(R.id.txt_altitude);
        txt_bearing = (TextView) findViewById(R.id.txt_bearing);

        ((Button) findViewById(R.id.btn_refresh)).setOnClickListener(this::update);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!mAutoUpdate)
                {
                    return;
                }
                getUpdates();
            }
        },0,1000);

        ((SwitchCompat) findViewById(R.id.sw_auto_update)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAutoUpdate = b;
            }
        });

        fusedLocationClient = getFusedLocationProviderClient(this);
        getUpdates();
    }

    private void update(View view) {
        getUpdates();
    }

    private void getUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions();
            return;
        }

        fusedLocationClient.getCurrentLocation(new CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build(), null);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location == null) {
                txt_latitude.setText("N/A");
                txt_longitude.setText("N/A");
                Toast.makeText(MainActivity.this, "Location is null.", Toast.LENGTH_SHORT).show();
            } else {
                setLocation(location);
            }
        });
    }

    private void setLocation(Location location){
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        if (location.hasSpeed()){
            setSpeed(location.getSpeed());
        }
        if(location.hasAccuracy()){
            setAccuracy(location.getAccuracy());
        }
        if(location.hasAltitude()){
            setAltitude(location.getAltitude());
        }
        if(location.hasBearing()){
            setBearing(location.getBearing());
        }
    }

    private void setLatitude(double latitude)
    {
        txt_latitude.setText(String.valueOf(latitude));
    }
    private void setLongitude(double longitude)
    {
        txt_longitude.setText(String.valueOf(longitude));
    }
    private void setSpeed(float speed)
    {
        txt_speed.setText(String.valueOf(speed));
    }
    private void setAccuracy(float accuracy)
    {
        txt_accuracy.setText(String.valueOf(accuracy));
    }
    private void setAltitude(double altitude)
    {
        txt_altitude.setText(String.valueOf(altitude));
    }
    private void setBearing(float bearing)
    {
        txt_bearing.setText(String.valueOf(bearing));
    }

    private void permissions()
    {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);

                            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);

                            if (fineLocationGranted != null && fineLocationGranted)
                            {
                                // Precise location access granted.
                                Toast.makeText(MainActivity.this, "Fine permissions granted.", Toast.LENGTH_SHORT).show();
                                getUpdates();
                            }
                            else if (coarseLocationGranted != null && coarseLocationGranted)
                            {
                                // Only approximate location access granted.
                                Toast.makeText(MainActivity.this, "Coarse permissions granted.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                // No location access granted.
                                Toast.makeText(MainActivity.this, "Lacking permissions.", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

    }
}