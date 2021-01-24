package com.example.grocery.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.R;
import com.example.grocery.adapters.AdapterShop;
import com.example.grocery.models.ModelShop;
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

public class MainUserActivity extends AppCompatActivity {
    private TextView nameTv,emailTv,phoneTv,tabShopsTv,tabOrdersTv;
    private ImageButton logoutBtn;
    private RelativeLayout shopR1,ordersR1;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ImageView profileIv;
    private RecyclerView shopsRv;

    private ArrayList<ModelShop> shopsList;
    private AdapterShop adapterShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        nameTv=findViewById(R.id.nameTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
        tabShopsTv=findViewById(R.id.tabShopsTv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        ordersR1=findViewById(R.id.ordersR1);
        shopR1=findViewById(R.id.shopR1);
        profileIv=findViewById(R.id.profileIv);
        shopsRv=findViewById(R.id.shopsRv);




        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();
        


        showShopsUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeoffline();
                firebaseAuth.signOut();
                checkUser();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersUI();
            }
        });
        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopsUI();
            }
        });

    }


    private void showShopsUI() {
        shopR1.setVisibility(View.VISIBLE);
        ordersR1.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect3);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        shopR1.setVisibility(View.GONE);
        ordersR1.setVisibility(View.VISIBLE);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect3);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));


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
                        Toast.makeText(MainUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user==null){
            startActivity(new Intent(MainUserActivity.this,LoginActivity.class));
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
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue().toString();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String city = ""+ds.child("city").getValue();

                            String accountType=""+ds.child("accountType").getValue();

                            nameTv.setText(name+"("+accountType+")");
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            //load only shops in users location
                            loadShops(city);


                        }

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void loadShops(final String city) {
        shopsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       shopsList.clear();

                       for(DataSnapshot ds:dataSnapshot.getChildren()){
                           ModelShop modelShop = ds.getValue(ModelShop.class);

                           String cityshop = ""+ds.child("city").getValue();

                           shopsList.add(modelShop);
                       }

                        adapterShop = new AdapterShop(MainUserActivity.this,shopsList);

                        shopsRv.setAdapter(adapterShop);
                        shopsRv.invalidate();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
