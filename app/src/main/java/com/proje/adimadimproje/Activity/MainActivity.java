package com.proje.adimadimproje.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.proje.adimadimproje.R;
import com.proje.adimadimproje.fragment.ChangeFragment;
import com.proje.adimadimproje.fragment.HomeFragment;
import com.proje.adimadimproje.fragment.LocationFragment;
import com.proje.adimadimproje.fragment.NotificationFragment;
import com.proje.adimadimproje.fragment.PostSwitchFragment;
import com.proje.adimadimproje.fragment.ProfileFragment;
import com.proje.adimadimproje.fragment.PostSalesFragment;

public class MainActivity extends AppCompatActivity {

    ChangeFragment changeFragment;
   BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeFragment = new ChangeFragment(this);
        SharedPreferences.Editor editor1 = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor1.putBoolean("forPostDetailOnClick",false);
        editor1.apply();

        Bundle intent = getIntent().getExtras();
        // Burada uygulamamızda genel anlamda fragman yapısı kullandığımız için geçişler daha yumuşak olsun diye
        // Intent yaparak ilgili fragmana yönlendiriyoruz. Böyle yapmasaydık ilgili fragmanlar arasında geçişte
        // göze hoş gelmeyen bir geçiş olacaktı
        if (intent!=null){
            if (intent.getString("location").equals("location")){
                changeFragment.change(new LocationFragment());
            }else if (intent.getString("PostSales").equals("PostSales")){
                String PostSales = intent.getString("PostSales");
                SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("PostSales",PostSales);
                editor.apply();
                changeFragment.change(new PostSalesFragment());
            }else if (!intent.getString("profileID").equals("profileID")){
                String PostUserID = intent.getString("profileID");
                SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("profileID",PostUserID);
                editor.apply();
                changeFragment.change(new ProfileFragment());
            }
        }else
            changeFragment.change(new HomeFragment());

        bottomNavigationView = findViewById(R.id.selamselam);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        changeFragment.change(new HomeFragment());
                        break;
                    case R.id.nav_search: changeFragment.change(new PostSalesFragment());
                        break;
                    case R.id.nav_add: changeFragment.change(new PostSwitchFragment());
                        break;
                    case R.id.nav_notification: changeFragment.change(new NotificationFragment());
                        break;
                    case R.id.nav_profile:
                        SharedPreferences.Editor se = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                        se.putString("profileID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        se.apply();
                        changeFragment.change(new ProfileFragment());
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Boolean isTrue = false;
        String  fragmentName = getSupportFragmentManager().getFragments().get(0).getClass().getSimpleName();
        String[] fragments = {"Location","Like","Message"};
        for (String string : fragments){
            if (fragmentName.equals(string+"Fragment")){
                this.finish();
                isTrue = true;break; }}
        if (!isTrue)
            finish();
    }

}
