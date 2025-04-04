package com.coffee.app.ui.wishlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.cart.CartActivity;
import com.coffee.app.ui.detail.ProductDetailBottomSheetFragment;
import com.coffee.app.ui.login.LoginActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Product> products = new ArrayList<>();
    TextView backBtn;
    WishlistAdapter wishlistAdapter;
    ShimmerFrameLayout skeletonLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Check authentication firebase if not login, redirect to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }


        addControls();
        skeletonLayout.startShimmer();
        getWishListRequest();
        addEvents();
    }

    private void addControls() {
        recyclerView = (RecyclerView) findViewById(R.id.wishlistRecyclerView);
        skeletonLayout = (ShimmerFrameLayout) findViewById(R.id.skeletonLayout);

        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration( new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        backBtn =(TextView) findViewById(R.id.backBtn);
    }

    private void addEvents()  {
        backPrevious();
    }

    private void backPrevious() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void getWishListRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        //String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/wishlist/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            products.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productObj = dataArray.getJSONObject(i);
                                int productId = productObj.getInt("product_id");
                                String name = productObj.getString("product_name");
                                String img = productObj.getString("product_image");
                                double price = Double.parseDouble(productObj.getString("product_price"));
                                String status = productObj.getString("product_status");
                                String description = productObj.getString("product_description");

                                Product product = new Product();
                                product.setId(productId);
                                product.setName(name);
                                product.setImage(img);
                                product.setPrice(price);
                                product.setStatus(status);
                                product.setDescription(description);


                                products.add(product);

                            }
                            renderWishlist();
                            showProductDetail();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            skeletonLayout.stopShimmer();
                            skeletonLayout.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        skeletonLayout.stopShimmer();
                        skeletonLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        // Handle error response more gracefully (e.g., show user-friendly message)
                    }
                });


        queue.add(jsonObjectRequest);

    }

    private void renderWishlist() {
        wishlistAdapter = new WishlistAdapter(getApplicationContext(), products);
        recyclerView.setAdapter(wishlistAdapter);

        skeletonLayout.stopShimmer();
        skeletonLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void showProductDetail()
    {
        wishlistAdapter.setOnItemClickListener(new WishlistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                ProductDetailBottomSheetFragment productDetailBottomSheetFragment = new ProductDetailBottomSheetFragment(product);
                productDetailBottomSheetFragment.show(getSupportFragmentManager(), productDetailBottomSheetFragment.getTag());
            }
        });
    }


}