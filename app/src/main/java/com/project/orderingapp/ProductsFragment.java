package com.project.orderingapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.project.orderingapp.Async.UserVerification;
import com.project.orderingapp.Models.ApiResponseArray;
import com.project.orderingapp.Models.CartList;
import com.project.orderingapp.Models.ProductList;
import com.project.orderingapp.SharedPreferences.Cart;
import com.project.orderingapp.SharedPreferences.Products;
import com.project.orderingapp.helpers.Network;
import com.project.orderingapp.helpers.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Prompt.SIGN_IN;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    ProductList[] productListObject = new ProductList[]{};

    CartList[] cartListObject = new CartList[]{};

    RecyclerView productList_rv;

    Adapter adapter = new Adapter();

    List<ProductList> productList;

    List<CartList> currentCartList = new ArrayList<>();

    List<CartList> newCartList;

    MenuItem menuItem;

    TextView lblCartBadge;

    View view;

    FrameLayout frameLayout1;

    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;


    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);



        menuItem = menu.findItem(R.id.menuViewCart);

        View actionView = menuItem.getActionView();
        lblCartBadge = actionView.findViewById(R.id.lblCartBadge);

        readCart();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuViewCart) {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        if (Network.isNetworkAvailable(this.getContext())) {
            new getProducts().execute();
        }

        productListObject = new Gson().fromJson(Products.getProductsJsonObject(getContext()), ProductList[].class);

        productList = Arrays.asList(productListObject);

        view = inflater.inflate(R.layout.fragment_products, null);

        productList_rv = view.findViewById(R.id.products_rv);

        productList_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        productList_rv.setAdapter(adapter);

        productList_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    HomeActivity.hideFab();
                } else {
                    HomeActivity.showFab();
                }
            }
        });

        setHasOptionsMenu(true);

        return view;

    }

    public void readCart() {
        int totalCartCount = 0;

        cartListObject = new Gson().fromJson(Cart.getCartJsonObject(), CartList[].class);

        for (CartList cartList : cartListObject) {
            totalCartCount = totalCartCount + cartList.quantity;
        }

        if (totalCartCount > 0) {

            lblCartBadge.setText(String.valueOf(totalCartCount));

            lblCartBadge.setVisibility(View.VISIBLE);

        } else {
            lblCartBadge.setVisibility(View.GONE);
        }

        currentCartList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgProductImage;
        TextView lblProductName, lblUnitPrice;
        Button btnAddToCart;

        public ViewHolder(View itemView) {
            super(itemView);

            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            lblProductName = itemView.findViewById(R.id.lblProductName);
            lblUnitPrice = itemView.findViewById(R.id.lblUnitPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int position = getLayoutPosition();

            Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                new UserVerification(getContext(), firebaseUser, firebaseAuth).execute();
                // ...
            } else {
                //Toast.makeText(getContext(), response.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    void firebaseLogin() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                SIGN_IN);

    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.products_list_recyclerview, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            ProgressBar progressBar;

            holder.lblProductName.setText(productList.get(position).productName);
            holder.lblUnitPrice.setText(productList.get(position).currencyCode + " " + productList.get(position).unitPrice.toString());
            Picasso.get().load(productList.get(position).imgSrc).error(R.drawable.ic_menu_camera).into(holder.imgProductImage);

            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (firebaseUser != null) {
                        CartList pendingCartItem = new CartList();

                        pendingCartItem.productId = productList.get(position).id;
                        pendingCartItem.quantity = 1;
                        pendingCartItem.unitPrice = productList.get(position).unitPrice;


                        CartList[] currentCart = new Gson().fromJson(Cart.getCartJsonObject(), CartList[].class);

                        for (CartList cartList : currentCart) {

                            CartList item = new CartList();

                            item.productId = cartList.productId;
                            item.quantity = cartList.quantity;
                            item.unitPrice = cartList.unitPrice;

                            currentCartList.add(item);
                        }

                        Boolean exists = false;

                        if (currentCartList.size() > 0) {
                            for (int i = 0; i < currentCartList.size(); i++) {

                                if (currentCartList.get(i).productId == pendingCartItem.productId) {

                                    pendingCartItem.quantity = pendingCartItem.quantity + currentCartList.get(i).quantity;

                                    currentCartList.set(i, pendingCartItem);

                                    exists = true;

                                    break;

                                }
                            }
                            if (!exists) {
                                currentCartList.add(pendingCartItem);
                            }
                        } else {
                            currentCartList.add(pendingCartItem);
                        }

                        Cart cart = new Cart();

                        cart.cartJsonObject = new Gson().toJson(currentCartList);

                        Cart.setPreference(cart);

                        readCart();
                    } else {
                        firebaseLogin();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public void refreshList() {

            Log.d("ProductsAdapter", "refresh");
            Log.d("ProductsAdapter", "Product List size: " + productList.size());

            if (productList.size() < 1) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                view = inflater.inflate(R.layout.fragment_products, null);


            }

            adapter.notifyDataSetChanged();
        }

    }

    class getProducts extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        Boolean asyncSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ProductsFragment.this.getContext(), ProductsFragment.this.getContext().getResources().getString(R.string.app_name), "Loading...", true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Util util = new Util();

            try {

                String apiUrl = ProductsFragment.this.getString(R.string.apiUrl);

                String jsonResponse = util.callApi(apiUrl + "getProducts", null, 0);

                ApiResponseArray apiResponse = new Gson().fromJson(jsonResponse, ApiResponseArray.class);

                if (apiResponse.code == 200) {

                    String productListArray = apiResponse.content.toString();

                    Products products = new Products();

                    products.productsJsonObject = apiResponse.content.toString();

                    Products.setPreference(getContext(), products);

                    asyncSuccess = true;

                }

            } catch (Exception e) {

                Log.d("ERROR", e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            if (asyncSuccess) {

                productListObject = new Gson().fromJson(Products.getProductsJsonObject(getContext()), ProductList[].class);

                productList = Arrays.asList(productListObject);

                adapter.refreshList();

            }

        }
    }

}
