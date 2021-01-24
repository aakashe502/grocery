package com.example.grocery.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocery.Activities.EditProductActivity;
import com.example.grocery.FilterProduct;
import com.example.grocery.R;
import com.example.grocery.models.ModelProducts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter< AdapterProductSeller.HolderProductSeller> implements Filterable {
    private Context context;
    public ArrayList<ModelProducts> productList,filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context,ArrayList<ModelProducts> productList,ArrayList<ModelProducts> filterList) {
        this.context = context;
        this.productList = productList;
        this.filterList = filterList;
    }


    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder,int position) {
        final ModelProducts modelProducts = productList.get(position);
        String id=modelProducts.getProductId();
        String uid=modelProducts.getUid();
        String discountAvailable = modelProducts.getDiscountAvailable();
        String discountNote=modelProducts.getDiscountNote();
        String discountPrice = modelProducts.getDiscountPrice();
        String productCategory = modelProducts.getProductCategory();
        String productDescription = modelProducts.getProductDescription();
        String icon= modelProducts.getProductIcon();
        String quantity = modelProducts.getProductQuantity();
        String title= modelProducts.getProductTitle();
        String timestamp = modelProducts.getTimestamp();
        String originalPrice = modelProducts.getOriginalPrice();

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("Rs "+discountPrice);
        holder.originalPriceTv.setText("Rs "+originalPrice);
        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_cart_primary).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_cart_primary);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HANDLE CLICKS SHOW ITEM DETAIL IN BOTTOM SHEET
                detailsBottomSheet(modelProducts);//here modelproduct


            }
        });



    }

    private void detailsBottomSheet(ModelProducts modelProducts) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view
        View view=LayoutInflater.from(context).inflate(R.layout.ds_product_detail_seller,null);
        bottomSheetDialog.setContentView(view);


        //initialize view of bottomsheet
        ImageButton backBtn=view.findViewById(R.id.backBtn);
        ImageButton deleteBtn=view.findViewById(R.id.deleteBtn);
        ImageButton editBtn=view.findViewById(R.id.editBtn);
        ImageView productIconIv=view.findViewById(R.id.productIconIv);
        TextView discountedNoteTv=view.findViewById(R.id.discountedNoteTv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView categoryTv=view.findViewById(R.id.categoryTv);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView discountedPriceTv=view.findViewById(R.id.discountedPriceTv);
        TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);
//get data

        final String id=modelProducts.getProductId();
        String uid=modelProducts.getUid();
        String discountAvailable = modelProducts.getDiscountAvailable();
        String discountNote=modelProducts.getDiscountNote();
        String discountPrice = modelProducts.getDiscountPrice();
        String productCategory = modelProducts.getProductCategory();
        String productDescription = modelProducts.getProductDescription();
        String icon= modelProducts.getProductIcon();
        String quantity = modelProducts.getProductQuantity();
        final String title= modelProducts.getProductTitle();
        final String timestamp = modelProducts.getTimestamp();
        String originalPrice = modelProducts.getOriginalPrice();

        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountedNoteTv.setText(discountNote);
        discountedPriceTv.setText("RS "+discountPrice);
        originalPriceTv.setText(originalPrice);

        if(discountAvailable.equals("true")){
            discountedPriceTv.setVisibility(View.VISIBLE);
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            discountedPriceTv.setVisibility(View.GONE);
            discountedNoteTv.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_cart_primary).into(productIconIv);
        }
        catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_add_shopping_cart_primary);
        }

        //show dialog
        bottomSheetDialog.show();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit product activity and pass id to it
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(context,EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product"+title+" ?")
                        .setPositiveButton("DELETE",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i) {
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i) {

                            }
                        })
                        .show();


            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss bottomsheet
                bottomSheetDialog.dismiss();
            }
        });




    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Product deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting product
                        Toast.makeText(context,"Failed deleting product..",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterProduct(this,filterList);
        }
        return filter;
    }

    class HolderProductSeller extends  RecyclerView.ViewHolder{
       private ImageView productIconIv;
       private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,originalPriceTv;
       public HolderProductSeller(@NonNull View itemView) {
           super(itemView);
           productIconIv=itemView.findViewById(R.id.productIconIv);
           discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
           titleTv=itemView.findViewById(R.id.titleTv);
           quantityTv=itemView.findViewById(R.id.quantityTv);
           discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
           originalPriceTv=itemView.findViewById(R.id.originalPriceTv);
       }
   }
}
