package com.example.grocery.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.adapters.AdapterProductSeller;
import com.example.grocery.Constants;
import com.example.grocery.models.ModelProducts;
import com.example.grocery.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {
    private TextView nameTv,tabOrdersTv,filterProductTv;
    private ImageButton logoutBtn,filterProductBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ImageButton addproduct;
    private TextView shopNameTv,EmailTv,tabProductsTv;
    private RelativeLayout productsR1,ordersR1;

    private EditText searchProductEt;

    private ImageView profileIv;
    private RecyclerView productsRv;
    private ArrayList<ModelProducts> productsList;
    private AdapterProductSeller adapterProductSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        nameTv=findViewById(R.id.nameTv);
        logoutBtn=findViewById(R.id.logoutBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        addproduct=findViewById(R.id.addProductBtn);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);

        ordersR1=findViewById(R.id.ordersR1);
        productsR1=findViewById(R.id.productsR1);


        searchProductEt=findViewById(R.id.searchProductEt);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        filterProductTv=findViewById(R.id.filterProductTv);
        productsRv=findViewById(R.id.productsRv);
        productsRv.setHasFixedSize(true);
        productsRv.setItemViewCacheSize(10);
        productsRv.setNestedScrollingEnabled(false);


        shopNameTv=findViewById(R.id.shopNameTv);
        EmailTv=findViewById(R.id.emailTv);
        profileIv=findViewById(R.id.profileIv);
        tabProductsTv=findViewById(R.id.tabProductsTv);

        addproduct=findViewById(R.id.addProductBtn);
        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,int i,int i1,int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence,int i,int i1,int i2) {
                try {
                    adapterProductSeller.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainSellerActivity.this,AddProductActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        loadProducts();


        showProductsUI();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeoffline();

            }
        });
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load products

                showProductsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load orders
                showOrders();

            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i) {
                                String selected = Constants.productCategories1[i];
                                filterProductTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadProducts();
                                }
                                else{
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }

                            }
                        })
                        .show();
            }
        });

    }

    private void loadFilteredProducts(final String selected) {
        productsList=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        productsList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            String productCategory = ""+ds.child("productCategory").getValue();
                            //if selected category matches then add in list
                            if(selected.equals(productCategory)){
                                ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                                productsList.add(modelProducts);
                            }


                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this,productsList,productsList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadProducts() {
        productsList=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        productsList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                            productsList.add(modelProducts);

                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this,productsList,productsList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                        productsRv.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void showProductsUI() {
        productsR1.setVisibility(View.VISIBLE);
        ordersR1.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect3);


        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }
    private void showOrders() {
        productsR1.setVisibility(View.GONE);
        ordersR1.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect4);


        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect3);
    }



    private void makeMeoffline() {
        progressDialog.setMessage("Logging out User..");
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("online","false");

        //updating value to firebase
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updated successfully
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainSellerActivity.this,LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String shopname=""+ds.child("shopName").getValue();
                            String email=""+ds.child("email").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
//                            String accountType=""+ds.child("accountType").getValue();

                            nameTv.setText(name);
                            shopNameTv.setText(shopname);
                            EmailTv.setText(email);

                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_store_gray);
                            }




                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
}
