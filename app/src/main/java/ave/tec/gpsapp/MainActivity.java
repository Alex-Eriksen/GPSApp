package ave.tec.gpsapp;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;

    private TextView txt_latitude;
    private TextView txt_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_latitude = (TextView) findViewById(R.id.txt_latitude);
        txt_longitude = (TextView) findViewById(R.id.txt_longitude);

        fusedLocationClient = getFusedLocationProviderClient(this);
        getUpdates();
    }

    private void getUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            permissions();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location == null)
            {
                txt_latitude.setText("N/A");
                txt_longitude.setText("N/A");
                Toast.makeText(MainActivity.this, "Location is null.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                setLatitude(location.getLatitude());
                setLongitude(location.getLongitude());
            }
        });
    }

    private void setLatitude(double latitude)
    {
        txt_latitude.setText(String.valueOf(latitude));
    }
    private void setLongitude(double longitude)
    {
        txt_longitude.setText(String.valueOf(longitude));
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