package com.proje.adimadimproje.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.MapsActivity;
import com.proje.adimadimproje.Model.Place;
import com.proje.adimadimproje.R;
import com.proje.adimadimproje.fragment.LocationFragment;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {


    public Context mcontext;
    public List<Place> Placelist;


    public LocationAdapter(Context mcontext, List<Place> Placelist) {
        this.mcontext = mcontext;
        this.Placelist = Placelist;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_location,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder ViewHolder, int position) {

        Place place=Placelist.get(position);
        ViewHolder.nameLocation.setText(place.getPlaceName());
        ViewHolder.CardViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("place",Placelist.get(position));
                mcontext.startActivity(intent);
            }
        });
        ViewHolder.CardViewLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("Güncelle");
                LinearLayout linearLayout = new LinearLayout(mcontext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10,10,10,10);
                final EditText editText = new EditText(mcontext);
                editText.setHint("Yeni Yer İsmi ...");
                linearLayout.addView(editText);
                builder.setView(linearLayout);
                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length()!=0){
                                try {
                                    SQLiteDatabase database= mcontext.openOrCreateDatabase("Places", MODE_PRIVATE, null);
                                    String toCompile = "UPDATE places SET placeName = ? WHERE placeName = ?";
                                    SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);
                                    sqLiteStatement.bindString(1, editText.getText().toString());
                                    sqLiteStatement.bindString(2, place.getPlaceName());
                                    sqLiteStatement.execute();
                                    ViewHolder.nameLocation.setText(editText.getText().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                });
                builder.setNegativeButton("Geri", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                return true;
            }
        });
     
    }


    @Override
    public int getItemCount() {
        return Placelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameLocation;
        CardView CardViewLocation;


        public ViewHolder(View view){
            super(view);
            nameLocation=  view.findViewById(R.id.nameLocation);
            CardViewLocation= view.findViewById(R.id.CardViewLocation);

        }
    }
}
