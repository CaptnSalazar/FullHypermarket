package com.example.kiit.senterprisr.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kiit.senterprisr.Interface.ItemClickListner;
import com.example.kiit.senterprisr.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listner;
    public ProductViewHolder(View itemView)
    {
        super(itemView);
        imageView=(ImageView) itemView.findViewById(R.id.product_image);
        txtProductName=(TextView) itemView.findViewById(R.id.product_name);
        txtProductPrice=(TextView) itemView.findViewById(R.id.product_price);

    }
    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner=listner;
    }
    @Override
    public void onClick(View view)
    {
listner.onClick(view,getAdapterPosition(),false);
    }
}
