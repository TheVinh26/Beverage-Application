package com.coffee.app.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.MainActivity;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.detail.ProductDetailBottomSheetFragment;
import com.coffee.app.ui.menu.ProductAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TeaProductCardGridAdapter extends RecyclerView.Adapter<TeaProductCardGridAdapter.MyViewHolder> {
    Context context;
    ArrayList<Product> productList;

    LayoutInflater inflater;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private TeaProductCardGridAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(TeaProductCardGridAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }



    public TeaProductCardGridAdapter(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_grid_item, parent, false);
        return new MyViewHolder(view);
    }

    public int getItemCount() {
        return productList.size();
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productList.get(position);

        Picasso.get().load(product.getImage()).into(holder.imageViewProduct);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(Utils.formatVNCurrency(product.getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(product);
                }
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewName;
        TextView textViewPrice;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageViewProduct = itemView.findViewById(R.id.productGridImage);
            textViewName = itemView.findViewById(R.id.productGridName);
            textViewPrice = itemView.findViewById(R.id.productGridPrice);
        }
    }

}

