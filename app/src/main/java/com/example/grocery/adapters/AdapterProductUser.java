package com.example.grocery.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocery.FilterProductUser;
import com.example.grocery.R;
import com.example.grocery.models.ModelProducts;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductUser  extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProducts> productsList,filterList;
    private FilterProductUser filter;


    public AdapterProductUser(Context context,ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }


    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);

        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder,int position) {
        ModelProducts modelProducts = productsList.get(position);
        String discountAvailable = modelProducts.getDiscountAvailable();
        String discountNote = modelProducts.getDiscountNote();
        String discountPrice = modelProducts.getDiscountPrice();
        String productCategory = modelProducts.getProductCategory();
        String originalPrice = modelProducts.getOriginalPrice();
        String productDescription = modelProducts.getProductDescription();
        String productTitle = modelProducts.getProductTitle();
        String productQuantity = modelProducts.getProductQuantity();
        String productId = modelProducts.getProductId();
        String timestamp = modelProducts.getTimestamp();
        String productIcon = modelProducts.getProductIcon();

        holder.titleTv.setText(productTitle);
        holder.discountedNoteTv.setText(discountNote);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("RS"+originalPrice);
        holder.discountedPriceTv.setText("RS"+discountPrice);

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
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_cart_primary).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_cart_primary);
        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add product to cart
                showQuantityDialog();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show product details

            }
        });



    }

    private void showQuantityDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view=LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter ==null){
            filter = new FilterProductUser(this,filterList);
        }
        return filter;
    }


    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,descriptionTv,addToCartTv,
                discountedPriceTv,originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv=itemView.findViewById(R.id.productIconIv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            descriptionTv=itemView.findViewById(R.id.descriptionTv);
            addToCartTv=itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);
        }
    }
}
