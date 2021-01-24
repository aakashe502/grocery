package com.example.grocery.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.Constants;
import com.example.grocery.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt,descriptionEt;
    private TextView categortTv,quantityEt,priceEt,discountedPriceEt,discountedNoteEt;
    private SwitchCompat discountSwitch;
    private Button addProductButton;

    //permissions constants
    private static final int LOCATION_REQUEST_CODE=100;
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;

    private  String [] locationPermissions;
    private  String [] cameraPermissions;
    private  String [] storagePermissions;

    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        backBtn=findViewById(R.id.backBtn);
        productIconIv=findViewById(R.id.productIconTv);

        titleEt=findViewById(R.id.titleEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        categortTv=findViewById(R.id.categoryTv);
        quantityEt=findViewById(R.id.quantityEt);
        priceEt=findViewById(R.id.priceEt);
        discountSwitch=findViewById(R.id.disconnectSwitch);
        discountedPriceEt=findViewById(R.id.discountedPriceEt);
        discountedNoteEt=findViewById(R.id.discountedNoteEt);
        addProductButton=findViewById(R.id.addProductBtn);

        discountedPriceEt.setVisibility(View.GONE);
        discountedNoteEt.setVisibility(View.GONE);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait.");
        progressDialog.setCanceledOnTouchOutside(false);
        locationPermissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,boolean b) {
                if(b){
                    discountedPriceEt.setVisibility(View.VISIBLE);
                    discountedNoteEt.setVisibility(View.VISIBLE);
                }
                else{
                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.GONE);
                }
            }
        });

        categortTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categortDialog();
            }
        });
        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCameraPermissions()) {
                    if(checkStoragePermissions()){
                        showImagePickDialog();
                    }else {
                        requestStoragePermission();
                    }
                }
                else{
                    requestCameraPermission();}
            }
        });


        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                inputData();
            }
        });

    }
    private  String productTitle,productDescription,productCategory,productQuantity,originalPrice,discountedPrice,discountNote;
    private  boolean discountAvailable=false;
    private void inputData() {
        productTitle=titleEt.getText().toString().trim();
        productDescription=descriptionEt.getText().toString().trim();
        productCategory=categortTv.getText().toString().trim();
        productQuantity=quantityEt.getText().toString().trim();
        originalPrice=priceEt.getText().toString().trim();
       discountAvailable = discountSwitch.isChecked();

       if(TextUtils.isEmpty(productTitle)) {
           Toast.makeText(this,"Title is Required..",Toast.LENGTH_SHORT).show();
           return;
       }
        if(TextUtils.isEmpty(productCategory)) {
            Toast.makeText(this,"productCategory is Required..",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this,"originalPrice is Required..",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(discountAvailable){
            discountedPrice=discountedPriceEt.getText().toString().trim();
            discountNote=discountedNoteEt.getText().toString().trim();
            if(TextUtils.isEmpty(discountedPrice)){
                Toast.makeText(this,"price is neccessary",Toast.LENGTH_SHORT).show();
                return ;
            }
            if(TextUtils.isEmpty(discountNote)){
                Toast.makeText(this,"discountNote is neccessary",Toast.LENGTH_SHORT).show();
                return ;
            }
        }
        else{
            discountedPrice="0";
            discountNote="";
        }
        addProduct();



    }

    private void addProduct() {
        progressDialog.setMessage("ADding Product");
        progressDialog.show();
        final String timestamp = ""+System.currentTimeMillis();

        if(image_uri==null){
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("productId",""+timestamp);
            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDescription",""+productDescription);
            hashMap.put("productCategory",""+productCategory);
            hashMap.put("productQuantity",""+productQuantity);
            hashMap.put("originalPrice",""+originalPrice);
            hashMap.put("discountPrice",""+discountedPrice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("ProductIcon","");
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("uid",""+firebaseAuth.getUid());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,"Product Addes",Toast.LENGTH_SHORT).show();
                            clearDAta();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            String filepath="profile_images"+""+timestamp;

            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filepath);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri= uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("productId",""+timestamp);
                                hashMap.put("productTitle",""+productTitle);
                                hashMap.put("productDescription",""+productDescription);
                                hashMap.put("productCategory",""+productCategory);
                                hashMap.put("productQuantity",""+productQuantity);
                                hashMap.put("originalPrice",""+originalPrice);
                                hashMap.put("discountPrice",""+discountedPrice);
                                hashMap.put("discountNote",""+discountNote);
                                hashMap.put("discountAvailable",""+discountAvailable);
                                hashMap.put("productIcon",""+downloadImageUri);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this,"Product Addes",Toast.LENGTH_SHORT).show();
                                                clearDAta();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void clearDAta() {
    titleEt.setText("");
    descriptionEt.setText("");
    categortTv.setText("");
    quantityEt.setText("");
    priceEt.setText("");
    discountedPriceEt.setText("");
    discountedNoteEt.setText("");
    productIconIv.setImageResource(R.drawable.ic_cart_gray);
    image_uri=null;
    }


    private void categortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i) {
                        //get pick up
                        String category=Constants.productCategories[i];
                        //set pick up
                       categortTv.setText(category);

                    }
                })
                .show();
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options={"Camera","Gallery"};
        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i) {
                        //handle clicks
                        if(i==0){
                            //camera clicked
                            if(checkCameraPermissions()){
                                //camera permission allowed
                                requestCameraPermission();

                            }
                            else{
                                ////not allowed
                                requestCameraPermission();

                            }
                        }
                        else{
                            //gallery cloicked
                            if(checkStoragePermissions()){
                                //storage allowed
                                pickFromGallery();

                            }
                            else{
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    private boolean checkCameraPermissions(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) ==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);
        return result&&result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,CAMERA_REQUEST_CODE);
    }
    private boolean checkStoragePermissions(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {

        switch (requestCode){
//            case LOCATION_REQUEST_CODE:{
//                if(grantResults.length>=0){
//                    boolean locationAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
//                    if(locationAccepted){
//                        //permisssion allowed
//                        // detectLocation();
////                        showlocation();
//
//                    }
//                    else{
//                        Toast.makeText(this,"Location Permission is necessary..",Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//            }
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&storageAccepted){
                        CropImage.activity(image_uri)
                                .setAspectRatio(1,1)
                                .start(AddProductActivity.this);

                    }
                    else{
                        Toast.makeText(this,"camera Permission is necessary..",Toast.LENGTH_SHORT).show();
                    }
//                    if(checkCameraPermissions()&&checkStoragePermissions()){
//                        pickFromGallery();
//                    }
//                    else{
//                        if(checkStoragePermissions()==false){
//                            requestStoragePermission();
//                        }
//                        if(checkCameraPermissions()==false){
//                            requestCameraPermission();
//                        }
//                        Toast.makeText(this,"storage or camera position is missing..",Toast.LENGTH_SHORT).show();
//                    }
                    break;

                }
                else{
                    Toast.makeText(this,"grant result not accesed",Toast.LENGTH_SHORT).show();}
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){ boolean storageAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //permisssion allowed
                        // pickFromGallery();
                        CropImage.activity(image_uri)
                                .setAspectRatio(1,1)
                                .start(AddProductActivity.this);

                    }
                    else{
                        Toast.makeText(this,"storage Permission is necessary..",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&& resultCode==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image_uri=result.getUri();
            productIconIv.setImageURI(image_uri);
        }
        if(((requestCode == IMAGE_PICK_GALLERY_CODE) && resultCode== RESULT_OK && (data != null))){
            image_uri=data.getData();
            Picasso.get().load(image_uri).fit().centerCrop().into(productIconIv);

        }
        if((requestCode==IMAGE_PICK_CAMERA_CODE)&&resultCode==RESULT_OK&&data!=null){
            image_uri=data.getData();
            Picasso.get().load(image_uri).fit().centerCrop().into(productIconIv);
        }

    }

}
