package com.example.grocery.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterUserActivity extends AppCompatActivity implements LocationListener {
    private ImageButton backBtn,gpsBtn;
    private ImageView profileIv;
    private EditText nameEt,phoneEt,countryEt,stateEt,cityEt,addressEt
            ,emailEt,passwordEt,cpasswordEt;
    private Button registerBtn;
    private TextView registerSellerTv;
    private  String [] locationPermissions;
    private  String [] cameraPermissions;
    private  String [] storagePermissions;

    private static final int LOCATION_REQUEST_CODE=100;
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;

    private Uri image_uri;

    private LocationManager locationManager;
    private Double latitude,longitude;

    //part 2
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        backBtn=findViewById(R.id.backBtn);
        profileIv=findViewById(R.id.profileIv);
        nameEt=findViewById(R.id.nameEt);
        phoneEt=findViewById(R.id.phoneEt);
        countryEt=findViewById(R.id.countryEt);

        stateEt=findViewById(R.id.stateEt);
        cityEt=findViewById(R.id.cityEt);
        addressEt=findViewById(R.id.addressEt);
        emailEt=findViewById(R.id.emailEt);
        passwordEt=findViewById(R.id.passwordEt);
        cpasswordEt=findViewById(R.id.cpasswordEt);

        registerBtn=findViewById(R.id.registerBtn);
        registerSellerTv=findViewById(R.id.registerSellerTv);

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
        gpsBtn=findViewById(R.id.gpsBtn);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Detect current location
                if(CheckLocationPermissions()){
                    showlocation();
                }
                else{
                    //not allowed
                    requestLocationPermission();
                }
            }
        });
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pick Image
                if(checkCameraPermissions()) {
                    if(checkStoragePermissions()){
                       showImagePickDialog();
                    }
                    else {
                        requestStoragePermission();
                    }
                }
                else{
                    requestCameraPermission();}
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inptData();



            }
        });
        registerSellerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUserActivity.this,RegisterSellerActivity.class));
            }
        });
    }
    private String fullName,phoneNumber,country,state,city,address,email,password,confirmPassword;
    private void inptData() {
        fullName=nameEt.getText().toString().trim();
        //shopName=shopNameEt.getText().toString().trim();
        phoneNumber=phoneEt.getText().toString().trim();
        //deliveryFee=deliveryFeeEt.getText().toString().trim();
        country=countryEt.getText().toString().trim();
        state=stateEt.getText().toString().trim();
        city=cityEt.getText().toString().trim();
        address=addressEt.getText().toString().trim();
        email=emailEt.getText().toString().trim();
        password=passwordEt.getText().toString().trim();
        confirmPassword=cpasswordEt.getText().toString().trim();
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this,"write full name",Toast.LENGTH_SHORT).show();
            return ;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,"write phoneNumber",Toast.LENGTH_SHORT).show();
            return ;
        }

        if(latitude==0&&longitude==0){
            Toast.makeText(this,"click gps button at top",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"write valid email id",Toast.LENGTH_SHORT).show();
            return ;
        }
        if((password.length() <6)){
            Toast.makeText(this,"Password must be 6 characters long",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(this,"password doedn't match",Toast.LENGTH_SHORT).show();
            return ;
        }
        createAccount();


    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account..");
        progressDialog.show();
        //create account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Account createdd
                        savetoFireBaseData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savetoFireBaseData() {
        progressDialog.setMessage("Saving Account Info..");
        final String timestamp="" +System.currentTimeMillis();
        if(image_uri==null){
            //save info without image
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("uid",firebaseAuth.getUid());
            hashMap.put("email",email);
            hashMap.put("name",fullName);
            hashMap.put("phone",phoneNumber);
            hashMap.put("country",country);
            hashMap.put("state",state);
            hashMap.put("city",city);
            hashMap.put("address",address);
            hashMap.put("latitude",latitude);
            hashMap.put("longitude",longitude);
            hashMap.put("timestamp",timestamp);

            hashMap.put("accountType","User");

            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("profileImage","");

            //savetpdb
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterUserActivity.this,MainUserActivity.class));
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterUserActivity.this,MainUserActivity.class));
                            finish();

                        }
                    });


        }
        else{
            String filePathAndName="profile_images/"+""+firebaseAuth.getUid();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri=uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("uid",firebaseAuth.getUid());
                                hashMap.put("email",email);
                                hashMap.put("name",fullName);
                                //hashMap.put("shopName",shopName);
                                hashMap.put("phone",phoneNumber);
                               // hashMap.put("deliveryFee",deliveryFee);
                                hashMap.put("country",country);
                                hashMap.put("state",state);
                                hashMap.put("city",city);
                                hashMap.put("address",address);
                                hashMap.put("latitude",String.valueOf(latitude));
                                hashMap.put("longitude",String.valueOf(longitude));
                                hashMap.put("timestamp",timestamp);

                                hashMap.put("accountType","User");

                                hashMap.put("online","true");
                                hashMap.put("profileImage",""+downloadImageUri);

                                //savetpdb
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterUserActivity.this,MainUserActivity.class));
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterUserActivity.this,MainUserActivity.class));
                                                finish();

                                            }
                                        });



                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                        }
                    });

        }
    }

    private void showlocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location gps_loc;
        Location network_loc;

        try {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Double lat,lon;
            if(gps_loc!=null){
                Location finl=gps_loc;
                lat=finl.getLatitude();
                lon=finl.getLongitude();
            latitude=lat;
            longitude=lon;}
            else if(network_loc!=null){
                Location fina=network_loc;
                lat=fina.getLatitude();
                lon=fina.getLongitude();
                latitude=lat;
                longitude=lon;
            }
            else{
                lat=0.0;
                lon=0.0;
                latitude=lat;
                longitude=lon;
            }
            try {
                Geocoder geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
                List<Address> addresses=geocoder.getFromLocation(lat,lon,1);
                if(addresses!=null&&addresses.size()>0){
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country=addresses.get(0).getCountryName();
                    countryEt.setText(country);
                    stateEt.setText(state);
                    cityEt.setText(city);
                    addressEt.setText(address);
                }
            }
            catch (Exception r){
                Toast.makeText(this,""+r.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }

    private boolean checkStoragePermissions(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermissions(){
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) ==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);
        return result&&result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,CAMERA_REQUEST_CODE);
    }
    private void finaAddress() {
        //find address,country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this,Locale.getDefault());
        try {
            addresses=geocoder.getFromLocation(latitude,longitude,1);
            String address = addresses.get(0).getAddressLine(0);//complete address
            String city=addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            //set address
            countryEt.setText(country);
            stateEt.setText(state);
            cityEt.setText(city);
            addressEt.setText(address);
        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

        }
    }



    private boolean CheckLocationPermissions(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }
    @Override
    public void onLocationChanged(Location location) {
        //location detected
//        latitude=location.getLatitude();
//        longitude=location.getLongitude();

        showlocation();

    }
    @Override
    public void onStatusChanged(String s,int i,Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        //gps/location disabled
        Toast.makeText(this,"Please turn on Loaction..",Toast.LENGTH_SHORT).show();

    }
//    private void pickFromCamera(){
//        Intent intent=new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {

        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>=0){
                    boolean locationAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permisssion allowed
                       // detectLocation();
                        showlocation();

                    }
                    else{
                        Toast.makeText(this,"Location Permission is necessary..",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&storageAccepted){
                        CropImage.activity(image_uri)
                                .setAspectRatio(1,1)
                                .start(RegisterUserActivity.this);

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
                                .start(RegisterUserActivity.this);

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
    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
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

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&& resultCode==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image_uri=result.getUri();
            profileIv.setImageURI(image_uri);
        }
        if(((requestCode == IMAGE_PICK_GALLERY_CODE) && resultCode== RESULT_OK && (data != null))){
            image_uri=data.getData();
            Picasso.get().load(image_uri).fit().centerCrop().into(profileIv);

        }
        if((requestCode==IMAGE_PICK_CAMERA_CODE)&&resultCode==RESULT_OK&&data!=null){
            image_uri=data.getData();
            Picasso.get().load(image_uri).fit().centerCrop().into(profileIv);
        }

    }
//    private void pickFromCamera(){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//
//        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
//        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
//
//    }


}


//    private void pickFromCamera(){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE,"Temp Image Title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Image Description");
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//
//        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
//        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
//
//    }
//    private void detectLocation() {
//        Toast.makeText(this,"Please wait",Toast.LENGTH_LONG).show();
//        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
//    }