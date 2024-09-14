package com.example.final_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context context;
    private List<FoodProduct> itemList;
    private CartItemActionListener cartItemActionListener;

    public ItemAdapter(Context context, List<FoodProduct> itemList) {
        this.context = context;
        this.itemList = itemList;
        ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodProduct item = itemList.get(position);
        if(item.getQuantity() > 0)
            holder.textViewName.setText(item.getName() +" " + item.getQuantity());
        else
            holder.textViewName.setText(item.getName() );

        String price = String.valueOf(item.getPrice());
        holder.textViewPrice.setText(price);
        holder.textViewDescription.setText(item.getDescription());

        // Load image into ImageView using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.imageView);

        // Set up OnClickListener to open ItemDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item_image", item.getImageUrl());
            intent.putExtra("item_title", item.getName());
            intent.putExtra("price", price);
            intent.putExtra("item_description", item.getDescription());
            context.startActivity(intent);
        });

        // Set up OnClickListener to remove item from cart
//        holder.removeButton.setOnClickListener(v -> {
//            cartItemActionListener.onRemoveItem(item);
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateItemList(List<FoodProduct> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }

    public interface CartItemActionListener {
        void onRemoveItem(FoodProduct item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewPrice, textViewDescription;
        public ImageView imageView;
//        public Button removeButton; // Add this line

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.item_title);
            textViewPrice = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.item_image);
            textViewDescription = itemView.findViewById(R.id.item_description);
        }
    }
}
