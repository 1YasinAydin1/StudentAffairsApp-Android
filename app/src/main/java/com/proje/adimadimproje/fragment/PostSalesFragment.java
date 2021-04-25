package com.proje.adimadimproje.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Adapter.PostSalesSearchAdapter;
import com.proje.adimadimproje.Adapter.SpinnerAdapter;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.Model.SpinnerModel;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class PostSalesFragment extends Fragment {

    RecyclerView SearchPostSalesRecyclerView;
    PostSalesSearchAdapter salesSearchAdapter;
    List<PostSales> sales;
    List<SpinnerModel> spinnerModels;
    SpinnerAdapter spinnerAdapter;

    EditText PostSalesPriceMaxEditText,PostSalesPriceMinEditText;
    Button PostSalesCategoryClose;
    RelativeLayout PostSalesFilterButton,PostSalesFilterDelete;
    RelativeLayout PostSalesCategory;
    int maxPrice = Integer.MAX_VALUE,minPrice = 0;
    String CategoryName = "Kategori",Tag1="",Tag2="",Tag3="",OrderBy="time";
    Spinner spinner,PostSpinnerVol1,PostSpinnerVol2,PostSpinnerVol3,spinnerOrderBy;
    Boolean isTrue = false,isOrderBy = false;
    Animation blink_anim,blink_anim1;
    public PostSalesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_sales, container, false);

        spinnerModels = new ArrayList<>();
        spinnerOrderBy = view.findViewById(R.id.PostSpinnerOrderBy);
        spinner = view.findViewById(R.id.PostSalesCategorySpinner);
        // region SpinnerCategory // Custom Spinner Kullanılmıştır
        spinnerModels.add(new SpinnerModel(R.drawable.ic_category,"Kategori"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_house,"Emlak"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_vehicle,"Araç"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_furniture,"Ev Eşyası ve Spot"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_electronic,"Elektronik"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_movie_music_book,"Film,Kitap ve Müzik"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_tools,"Ders araç, gereç ve ilgili"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_fashion,"Giyim ve Aksesuar"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_job,"İş İlanı"));
        spinnerAdapter = new SpinnerAdapter(getContext(),spinnerModels);
        spinner.setAdapter(spinnerAdapter);
        // endregion

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // Sıralama İşlemleri
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        OrderBy = "time";
                        isOrderBy = false;
                        getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
                        break;
                    case 1:
                        OrderBy = "PostSPrice";
                        isOrderBy = false;
                        getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
                        break;
                    case 2:
                        OrderBy = "PostSPrice";
                        isOrderBy = true;
                        getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
                        break;
                    case 3:
                        OrderBy = "time";
                        isOrderBy = true;
                        getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        PostSalesFilterDelete = view.findViewById(R.id.PostSalesFilterDelete);
        PostSalesFilterDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxPrice=Integer.MAX_VALUE;minPrice=0;CategoryName="Kategori";Tag1="";Tag2="";Tag3="";OrderBy="time";
                getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
            }
        });
        PostSalesFilterButton = view.findViewById(R.id.PostSalesFilterButton);
        PostSalesPriceMinEditText = view.findViewById(R.id.PostSalesPriceMinEditText);
        PostSalesPriceMaxEditText = view.findViewById(R.id.PostSalesPriceMaxEditText);
        PostSalesCategoryClose = view.findViewById(R.id.PostSalesCategoryClose);
        PostSalesCategory = view.findViewById(R.id.PostSalesCategory);
        PostSpinnerVol1 = view.findViewById(R.id.PostSpinnerVol1);
        PostSpinnerVol2 = view.findViewById(R.id.PostSpinnerVol2);
        PostSpinnerVol3 = view.findViewById(R.id.PostSpinnerVol3);
        blink_anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        blink_anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.blink1);


        PostSalesFilterButton.setOnClickListener(new View.OnClickListener() { // Filtreleme Görünürlük işlemleri
            @Override
            public void onClick(View v) {
                if (!isTrue){
                    isTrue = true;
                    PostSalesCategory.startAnimation(blink_anim);
                    PostSalesCategory.setVisibility(View.VISIBLE);
                    SearchPostSalesRecyclerView.setVisibility(View.GONE);
                }
                else{
                    isTrue = false;
                    PostSalesCategory.startAnimation(blink_anim1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PostSalesCategory.setVisibility(View.GONE);
                            SearchPostSalesRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }, 550);
                }
            }
        });
        SpinnerText();
        PostSalesCategoryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTrue = false;
                PostSalesCategory.startAnimation(blink_anim1);
                String priceMin = String.valueOf(PostSalesPriceMinEditText.getText());
                String priceMax = String.valueOf(PostSalesPriceMaxEditText.getText());
                minPrice = (priceMin.equals("")) ? 0 : Integer.parseInt(priceMin);
                maxPrice = (priceMax.equals("")) ? Integer.MAX_VALUE : Integer.parseInt(priceMax);
                getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PostSalesCategory.setVisibility(View.GONE);
                        SearchPostSalesRecyclerView.setVisibility(View.VISIBLE);
                    }
                }, 600);

            }
        });
        SearchPostSalesRecyclerView = view.findViewById(R.id.SearchPostSalesRecyclerView);
        SearchPostSalesRecyclerView.setHasFixedSize(true);
        SearchPostSalesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        sales = new ArrayList<>();
        salesSearchAdapter = new PostSalesSearchAdapter(getContext(),sales);
        SearchPostSalesRecyclerView.setAdapter(salesSearchAdapter);
        getPostSales(maxPrice,minPrice,CategoryName,Tag1,Tag2,Tag3,OrderBy);
        return view;
    }
    private void SpinnerText() { // Spinner listeleri kategoriye göre dolduruluyor
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerModel spinnerModel = (SpinnerModel) parent.getItemAtPosition(position);
                CategoryName = spinnerModel.getTextView();
                PostSpinnerVol1.setVisibility(View.GONE);
                PostSpinnerVol2.setVisibility(View.GONE);
                PostSpinnerVol3.setVisibility(View.GONE);
                if (position!=0){
                    PostSpinnerVol1.setVisibility(View.VISIBLE);
                    PostSpinnerVol2.setVisibility(View.VISIBLE);
                    PostSpinnerVol3.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> adapter1;
                    ArrayAdapter<CharSequence> adapter2;
                    ArrayAdapter<CharSequence> adapter3;
                    switch (position){
                        case 1:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.EmlakKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.EmlakDurum,R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.EmlakMetreKare, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 2:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.AracKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.AracDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.AracModel, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 3:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.EvEşyasıKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.EsyaDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.EsyaTur, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 4:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.ElektronikKimden,R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.ElektronikDurum,R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.ElektronikTur,R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 5:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.EglenceKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.EglenceDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.EglenceTur,R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 6:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.EgitimKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.EgitimDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.EgitimTur, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 7:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.GiyimKusamKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.GiyimKusamDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.GiyimKusamTur,R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 8:
                            adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.IsIlanKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.IsIlanDurum,R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.IsIlanKimdenTur, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                    }
                    PostSpinnerVol1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position!=0)
                                Tag1 = PostSpinnerVol1.getItemAtPosition(position).toString();
                            else
                                Tag1 = "";
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    PostSpinnerVol2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position!=0)
                                Tag2 = PostSpinnerVol2.getItemAtPosition(position).toString();
                            else
                                Tag2 = "";
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    PostSpinnerVol3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position!=0)
                                Tag3 = PostSpinnerVol3.getItemAtPosition(position).toString();
                            else
                                Tag3 = "";
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getPostSales(int maxPrice,int minPrice, String CategoryName,String Tag1,String Tag2, String Tag3, String OrderBy){
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostSales").orderByChild(OrderBy);
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // Filteleme işlemlerini sıfırlama
                if(CategoryName.equals("Kategori")&&maxPrice==Integer.MAX_VALUE&&minPrice==0&&Tag1.equals("")&&Tag2.equals("")&&Tag3.equals("")){
                    PostSalesFilterDelete.setVisibility(View.GONE);
                    spinner.setSelection(0);
                    PostSpinnerVol1.setSelection(0);
                    PostSpinnerVol2.setSelection(0);
                    PostSpinnerVol3.setSelection(0);
                    PostSalesPriceMinEditText.setText("");
                    PostSalesPriceMaxEditText.setText("");
                }
                else
                    PostSalesFilterDelete.setVisibility(View.VISIBLE);

                sales.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();
                    int Price = Integer.parseInt(data.get("PostSPrice").toString());
                    String PSCategoryName = data.get("PostSCategory").toString();
                    String PSTag1 = data.get("PostSTag1").toString();
                    String PSTag2 = data.get("PostSTag2").toString();
                    String PSTag3 = data.get("PostSTag3").toString();
                    Boolean postStatus = (Boolean) data.get("PostSStatus");

                    // Filtreleme işlemleri
                    if (!postStatus&& (maxPrice==Integer.MAX_VALUE ? maxPrice==Integer.MAX_VALUE : Price<maxPrice)&&
                            (minPrice==0 ? minPrice==0 : Price>minPrice)&&
                            (CategoryName=="Kategori" ? CategoryName=="Kategori" : PSCategoryName.equals(CategoryName))&&
                            (Tag1=="" ? Tag1=="" : PSTag1.equals(Tag1))&&
                            (Tag2=="" ? Tag2=="" : PSTag2.equals(Tag2))&&
                            (Tag3=="" ? Tag3=="" : PSCategoryName.equals("Emlak") ?
                                    Integer.parseInt(PSTag3)<Integer.parseInt(Tag3) :PSTag3.equals(Tag3))){

                    int imageSize =Integer.parseInt((String)data.get("imageSize"));
                    String[] image = new String[imageSize];
                    for (int i = 0 ; i < imageSize ; i++){
                        String imageName = "image"+(i+1);
                        String imageS = (String) data.get(imageName);
                        image[i] = imageS;
                    }
                    PostSales post = new PostSales(data.get("PostSID")+"",data.get("userID")+"",
                            data.get("PostSStatus")+"",data.get("PostSCategory")+"",  data.get("PostSTitle")+"",
                            data.get("PostSComment")+"",""+data.get("PostSPrice"),
                            data.get("PostSTag1")+"",data.get("PostSTag2")+"",data.get("PostSTag3")+"",
                            data.get("PostSCCName1")+"",data.get("PostSCCName2")+"",data.get("PostSCCName3")+"",
                            data.get("PostSTime")+"",data.get("PostSDate")+"",data.get("PostSTime")+"",
                            data.get("imageSize")+"",image);
                    sales.add(post);
                        }
                }
                if (isOrderBy == true)
                    Collections.reverse(sales);
                 salesSearchAdapter.notifyDataSetChanged();
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}