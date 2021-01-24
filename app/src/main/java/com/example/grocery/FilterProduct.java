package com.example.grocery;

import android.widget.Filter;

import com.example.grocery.adapters.AdapterProductSeller;
import com.example.grocery.models.ModelProducts;

import java.util.ArrayList;

public class FilterProduct extends Filter {
    private AdapterProductSeller adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProduct(AdapterProductSeller adapter,ArrayList<ModelProducts> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results=new FilterResults();
        //validate data for search query
        if(charSequence!=null&&charSequence.length()>0){
            //search filter not empty searching something,perform search

            //change to upper case to make case
            charSequence = charSequence.toString().toUpperCase();
            //store out filtered list
            ArrayList<ModelProducts> filteredModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                if(filterList.get(i).getProductTitle().toUpperCase().startsWith(charSequence.toString())||
                    filterList.get(i).getProductCategory().toUpperCase().startsWith(charSequence.toString())){
                    //add filtered data
                    filteredModels.add(filterList.get(i));

                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            //search filter empty,not searching retrun original/all/completer list
            results.count=filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence,FilterResults filterResults) {
        adapter.productList = (ArrayList<ModelProducts>)filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();


    }
}
