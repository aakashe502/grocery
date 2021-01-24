package com.example.grocery.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.Constants;
import com.example.grocery.R;
import com.example.grocery.adapters.AdapterProductUser;
import com.example.grocery.models.ModelProducts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopDetailsActivity extends AppCompatActivity {
    private ImageView shopIv;
    private TextView shopNameTv,phoneTv,emailTv,openCloseTv,deliveryFeeTv
            ,addressTv,filteredProductBtn;
    private ImageButton callBtn,mapBtn,cartBtn,backBtn,filterProductBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;
    private String shopUid;
    private FirebaseAuth firebaseAuth;
    private String myLatitude,myLongitude;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductUser adapterProductUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopUid=getIntent().getStringExtra("shopUid");
        shopIv=findViewById(R.id.shopIv);
        shopNameTv=findViewById(R.id.shopNameTv);
        phoneTv=findViewById(R.id.phoneTv);
        emailTv=findViewById(R.id.emailTv);
        openCloseTv=findViewById(R.id.openCloseTv);
        deliveryFeeTv=findViewById(R.id.deliveryFeeTv);
        addressTv=findViewById(R.id.addressTv);
        callBtn=findViewById(R.id.callBtn);
        mapBtn=findViewById(R.id.mapBtn);
        cartBtn=findViewById(R.id.cartBtn);
        backBtn=findViewById(R.id.backBtn);
        searchProductEt=findViewById(R.id.searchProductEt);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        filteredProductBtn=findViewById(R.id.filteredProductBtn);

        productsRv=findViewById(R.id.productsRv);
        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,int i,int i1,int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence,int i,int i1,int i2) {
                try {
                    adapterProductUser.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });







        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });
    mapBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openMAp();
        }
    });
    filterProductBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder =new AlertDialog.Builder(ShopDetailsActivity.this);
            builder.setTitle("Choose Category")
                    .setItems(Constants.productCategories1,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,int i) {
                            String selected = Constants.productCategories1[i];
                            filteredProductBtn.setText(selected);
                            if(selected.equals("All")){
                                //load all
                                loadShopDetails();
                            }
                            else{
                                //load filtered
                                adapterProductUser.getFilter().filter(selected);
                            }

                        }
                    })
                    .show();
        }
    });

    }

    private void openMAp() {
        String address = "https://maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);

    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this,""+shopPhone,Toast.LENGTH_SHORT).show();
    }


    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue().toString();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String city = ""+ds.child("city").getValue();
                            String accountType=""+ds.child("accountType").getValue();

                             myLatitude = ""+ds.child("latitude").getValue();
                             myLongitude = ""+ds.child("latitude").getValue();


                        }

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void loadShopDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = ""+dataSnapshot.child("name").getValue();
                shopName = ""+dataSnapshot.child("shopName").getValue();
                shopEmail = ""+dataSnapshot.child("email").getValue();
                shopPhone = ""+dataSnapshot.child("phone").getValue();
                shopLatitude = ""+dataSnapshot.child("latitude").getValue();
                shopLongitude = ""+dataSnapshot.child("longitude").getValue();
                shopAddress = ""+dataSnapshot.child("address").getValue();

                String deliverFee = ""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                String shopopen = ""+dataSnapshot.child("shopOpen").getValue();

                //set Data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Delivery Fee: RS "+deliverFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                if(openCloseTv.equals("true")){
                    openCloseTv.setText("Open");
                }
                else{
                    openCloseTv.setText("Closed");
                }

                try {
                    Picasso.get().load(profileImage).into(shopIv);
                }
                catch (Exception e){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void loadShopProducts(){
        productsList  =new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                            productsList.add(modelProducts);
                        }
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this,productsList);

                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
