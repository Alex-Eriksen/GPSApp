package ave.tec.gpsapp.locationservice;

import android.location.Location;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ave.tec.gpsapp.MainActivity;

public class LocationService {
    private final String TAG = "SQL";

    private static LocationService _instance;
    public  static LocationService INSTANCE() {
        if (_instance == null){
            _instance = new LocationService();
        }
        return _instance;
    }

    private Connection mConnection;
    private String mUsername, mPassword, mIP, mPort, mDatabase;

    private Connection connect() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            mIP = "192.168.0.163";
            mDatabase = "AndroidLocationDB";
            mUsername = "location_user";
            mPassword = "Passw0rd";
            mPort = "1433";
            String connString =
                    "jdbc:sqlserver://" + mIP + ":" + mPort + ";"
                            + "databaseName=" + mDatabase + ";"
                            + "user=" + mUsername + "@" + mIP + ";"
                            + "password=" + mPassword + ";"
                            + "encrypt=true;"
                            + "trustServerCertificate=true;"
                            + "loginTimeout=30;";

            return DriverManager.getConnection(connString);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void uploadLocation(Location location) {
        try (Connection connection = connect();
        ) {
            if (connection == null) {
                Log.d(TAG, "uploadLocation: connection is null");
                return;
            }
            try (Statement statement = connection.createStatement();) {
                float bearing = 0, speed = 0, accuracy = 0;
                double altitude = 0.0;

                if (location.hasBearing()) bearing = location.getBearing();
                if (location.hasSpeed()) speed = location.getSpeed();
                if (location.hasAccuracy()) accuracy = location.getAccuracy();
                if (location.hasAltitude()) altitude = location.getAltitude();

                String insertSql = "INSERT INTO [dbo.Location] ([timestamp], [latitude], [longitude], [speed], [accuracy], [altitude], [bearing])" +
                                    "VALUES ("+location.getTime()+","+location.getLatitude()+","+location.getLongitude()+","+speed+","+accuracy+","+altitude+","+bearing+")";

                statement.executeQuery(insertSql);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
