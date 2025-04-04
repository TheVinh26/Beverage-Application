package com.coffee.app.ui.others;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.cart.CartActivity;
import com.coffee.app.ui.login.LoginActivity;
import com.coffee.app.ui.order.OrderFragment;
import com.coffee.app.ui.profile.ProfileFragment;
import com.coffee.app.ui.wishlist.WishlistActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class OthersFragment extends Fragment {
    TextView textViewLogin, textViewLogout, textViewProfile, textViewOrder, textViewCart, textViewWishlist, textViewCartBadge;
    View rootView;

    ImageButton btnCart;
    public OthersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_others, container, false);

        addControls();
        renderLoginOrLogoutView();
        getTotalCartItemsRequest();

        addEvents();

        return rootView;
    }

    private void addControls() {
        textViewLogin = (TextView) rootView.findViewById(R.id.textViewLogin);
        textViewLogout = (TextView) rootView.findViewById(R.id.textViewLogout);
        textViewProfile = (TextView) rootView.findViewById(R.id.textViewProfile);
        textViewOrder = (TextView) rootView.findViewById(R.id.textViewOrder);
        textViewCart = (TextView) rootView.findViewById(R.id.textViewCart);
        textViewWishlist = (TextView) rootView.findViewById(R.id.textViewWishlist);
        textViewCartBadge = (TextView) rootView.findViewById(R.id.textViewCartBadge);
        btnCart = (ImageButton) rootView.findViewById(R.id.btnCart);
    }

    private void addEvents() {
        handleLogout();
        navigateToLogin();
        navigateToWishlist();
        navigateToCart();
        navigateToProfile();
        navigateToOrder();
    }

    private void renderLoginOrLogoutView() {
        // Check if the user is logged in or not
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Show the logout button
            textViewLogout.setVisibility(View.VISIBLE);
            textViewLogin.setVisibility(View.GONE);
        } else {
            // Show the login button
            textViewLogin.setVisibility(View.VISIBLE);
            textViewLogout.setVisibility(View.GONE);
        }
    }

    private void getTotalCartItemsRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
                String userId = user.getUid();
        //String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/cart/total/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle the response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int totalItems = jsonObject.getInt("data");
                        textViewCartBadge.setText(String.valueOf(totalItems));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }

    private void handleLogout() {
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform the logout
                                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build();
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);

                                googleSignInClient.signOut()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign out from Firebase
                                                    FirebaseAuth.getInstance().signOut();
                                                    renderLoginOrLogoutView();
                                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(getActivity(), "Logout failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);

                AlertDialog dialog = builder.create();
                dialog.show();

                // Set the color of the buttons
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
    }


    private void navigateToLogin() {
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToProfile() {
        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load ProfileFragment at OthersFragment
                ProfileFragment profileFragment = new ProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
            }
        });
    }

    private void navigateToWishlist() {
        textViewWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profile activity
                 Intent intent = new Intent(getActivity(), WishlistActivity.class);
                 startActivity(intent);
            }
        });
    }

    private void navigateToCart() {
        textViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profile activity
                 Intent intent = new Intent(getActivity(), CartActivity.class);
                 startActivity(intent);
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), CartActivity.class);
                 startActivity(intent);
            }
        });
    }

    private void navigateToOrder() {
        textViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load OrderFragment at OthersFragment
                OrderFragment orderFragment = new OrderFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, orderFragment).commit();
            }
        });
    }
}