package com.coffee.app.ui.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.model.ProductSize;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.home.CoffeeProductCardGridAdapter;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductSizeAdapter extends RecyclerView.Adapter<ProductSizeAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProductSize> productSizes;
    private int selectedPosition = -1;

    LayoutInflater inflater;

    private  OnSizeItemSizeClickListener onSizeItemSizeClickListener;

    public interface OnSizeItemSizeClickListener {
        void onSizeItemClick(ProductSize productSize);
    }



    public ProductSizeAdapter(ArrayList<ProductSize> productSizes, OnSizeItemSizeClickListener onSizeItemSizeClickListener) {
        this.productSizes = productSizes != null ? productSizes : new ArrayList<>();
        this.onSizeItemSizeClickListener = onSizeItemSizeClickListener;

        // Automatically check the first item if productSizes is not empty
        if (productSizes.size() > 0) {
            selectedPosition = 0;
            onSizeItemSizeClickListener.onSizeItemClick(productSizes.get(selectedPosition));
        }

        setHasStableIds(true);
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_sizes_item, parent, false);
        return new MyViewHolder(view);
    }

    public int getItemCount() {
        return productSizes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (productSizes.isEmpty()) {
            return;
        }

        ProductSize productSize = productSizes.get(position);

        holder.textViewSizePrice.setText(Utils.formatVNCurrency(productSize.getSizePrice()));
        holder.radioButtonSizeName.setText(productSize.getSizeName());

        holder.radioButtonSizeName.setOnCheckedChangeListener(null);
        holder.radioButtonSizeName.setChecked(selectedPosition == position);

        holder.radioButtonSizeName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && selectedPosition != position) {


                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();


                // Notify changes for the previous and new selected positions
                if (previousPosition >= 0 && previousPosition < productSizes.size()) {
                    notifyItemChanged(previousPosition);
                }
                notifyItemChanged(selectedPosition);

                // Call the listener method if the selected position is valid
                if (selectedPosition >= 0 && selectedPosition < productSizes.size()) {
                    onSizeItemSizeClickListener.onSizeItemClick(productSizes.get(selectedPosition));
                }

            }
        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSizePrice;
        RadioButton radioButtonSizeName;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewSizePrice = itemView.findViewById(R.id.textViewSizePrice);
            radioButtonSizeName = (RadioButton) itemView.findViewById(R.id.radioBtnSizeName);
        }
    }

}

