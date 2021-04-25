package com.proje.adimadimproje;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Model.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean onetime = false;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (info.matches("new")){ // Kullanıcın şu an ki konumu alınır
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (!onetime){
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Last Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
                        onetime=true;}
                }
            };

            // İzin işlemleri
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);}
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null)
                {
                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Last Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
                }
            }
        }
        else{ // Daha önce kaydedilen konum
            mMap.clear();
            Place place = (Place) intent.getSerializableExtra("place");
            LatLng userLocation = new LatLng(place.getLatitude(),place.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLocation).title(place.getPlaceName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length>0 && grantResults !=null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Intent intent = getIntent();
            String info = intent.getStringExtra("info");
            if (info.matches("new")){

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null)
                {
                    mMap.clear();
                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Last Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
                }
            }else{
                mMap.clear();
                Place place = (Place) intent.getSerializableExtra("place");
                LatLng userLocation = new LatLng(place.getLatitude(),place.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title(place.getPlaceName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Harita da her hangi bir yere uzun basarsa oranın adresi alınır ve kaydetme işlemi kullanıcıya sorulur
    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = " ";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList !=null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null)
                    address+=" "+addressList.get(0).getThoroughfare();
                if (addressList.get(0).getSubThoroughfare() != null)
                    address+=" "+addressList.get(0).getSubThoroughfare();
                if (addressList.get(0).getCountryName() !=null)
                    address+=" "+addressList.get(0).getCountryName();
            }else{address = "Address not found";}
        } catch (IOException e) {
            e.printStackTrace();
        }


        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        final Place place = new Place(address,latLng.latitude,latLng.longitude);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Kaydetmek istiyor musun?");
        alertDialog.setMessage(place.getPlaceName());
        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    database = MapsActivity.this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS places (ID INTEGER PRIMARY KEY,placeName VARCHAR,latitude VARCHAR,longitude VARCHAR)");
                    String toCompile = "INSERT INTO places (placeName,latitude,longitude) VALUES (?,?,?)";
                    SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);
                    String asf = sqLiteStatement.toString();
                    sqLiteStatement.bindString(1, place.getPlaceName());
                    sqLiteStatement.bindString(2, place.getLatitude().toString());
                    sqLiteStatement.bindString(3, place.getLongitude().toString());
                    sqLiteStatement.execute();

                    Toast.makeText(MapsActivity.this, "Kaydedildi", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "Kaydedilmedi", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();


    }
}