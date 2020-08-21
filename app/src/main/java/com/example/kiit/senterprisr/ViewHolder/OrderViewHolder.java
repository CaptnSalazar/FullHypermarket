package com.example.kiit.senterprisr.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiit.senterprisr.Interface.ItemClickListner;
import com.example.kiit.senterprisr.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductname,txtProductPrice,txtProductQuantity,date,id,state;

    private ItemClickListner itemClickListner;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductname=itemView.findViewById(R.id.order_product_name);
        txtProductPrice=itemView.findViewById(R.id.order_product_price);
        txtProductQuantity=itemView.findViewById(R.id.order_product_quantity);
        date=itemView.findViewById(R.id.order_date);
        id=itemView.findViewById(R.id.order_customer_name);
        state=itemView.findViewById(R.id.order_state);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);
    }
    public  void setItemClickListner(ItemClickListner itemClickListner)
    {
        this.itemClickListner=itemClickListner;
    }
}
