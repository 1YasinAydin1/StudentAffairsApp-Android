package com.proje.adimadimproje.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.proje.adimadimproje.Adapter.LocationAdapter;
import com.proje.adimadimproje.MapsActivity;
import com.proje.adimadimproje.Model.Place;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LocationFragment extends Fragment {


    Button locationButton;
    RecyclerView LocationRecyclerView;
    SQLiteDatabase database;
    ArrayList<Place> Placelist;
    LocationAdapter locationAdapter;

    // Burada kullanıcının sevdiği, beğendiği her türlü konumun adresini ve konumunu kaydetme, görüntüleme, adını değiştirme ve silme işlemleri yer alır

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        locationButton = view.findViewById(R.id.GoToMaps);
        LocationRecyclerView = view.findViewById(R.id.LocationRecyclerView);
        LocationRecyclerView.setHasFixedSize(true);
        LocationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Placelist = new ArrayList<>();

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("info","new");
                startActivity(intent);
            }
        });
        try { // Daha önceden kaydedilen bir konum varsa veritabanından çekiliyor
            database = getActivity().openOrCreateDatabase("Places",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM places",null);

            int IDIx = cursor.getColumnIndex("ID");
            int nameIx = cursor.getColumnIndex("placeName");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx = cursor.getColumnIndex("longitude");
            while (cursor.moveToNext()){
                String getplaceID = cursor.getString(IDIx);
                String getplaceName = cursor.getString(nameIx);
                Double getlatitudeIx = Double.parseDouble(cursor.getString(latitudeIx));
                Double getlongitudeIx = Double.parseDouble(cursor.getString(longitudeIx));
                Place place = new Place(getplaceName,getlatitudeIx,getlongitudeIx);
                Placelist.add(place);
            }
            locationAdapter = new LocationAdapter(getContext(),Placelist);
            LocationRecyclerView.setAdapter(locationAdapter);
            locationAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        // Sağa sola kaydırarak silebilir
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Placelist.remove(viewHolder.getAdapterPosition());
                database = getActivity().openOrCreateDatabase("Places", MODE_PRIVATE, null);
                database.execSQL("DROP TABLE places");
                for (Place place : Placelist){
                    try {
                        database.execSQL("CREATE TABLE IF NOT EXISTS places (ID INTEGER PRIMARY KEY,placeName VARCHAR,latitude VARCHAR,longitude VARCHAR)");
                        String toCompile = "INSERT INTO places (placeName,latitude,longitude) VALUES (?,?,?)";
                        SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);
                        sqLiteStatement.bindString(1, place.getPlaceName());
                        sqLiteStatement.bindString(2, place.getLatitude().toString());
                        sqLiteStatement.bindString(3, place.getLongitude().toString());
                        sqLiteStatement.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                locationAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(LocationRecyclerView);
        return view;
    }
}