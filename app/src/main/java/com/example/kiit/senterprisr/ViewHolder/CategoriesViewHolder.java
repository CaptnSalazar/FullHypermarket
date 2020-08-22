package com.example.kiit.senterprisr.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kiit.senterprisr.Interface.ItemClickListner;
import com.example.kiit.senterprisr.R;

public class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtCat;
    public ItemClickListner listner;
    public CategoriesViewHolder(View itemView)
    {
        super(itemView);
        txtCat=(TextView) itemView.findViewById(R.id.cattext);

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
