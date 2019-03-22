package com.project.orderingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.orderingapp.Models.CartList;
import com.project.orderingapp.Models.CartListDetailed;
import com.project.orderingapp.Models.ProductList;
import com.project.orderingapp.SharedPreferences.Cart;
import com.project.orderingapp.SharedPreferences.Products;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    CartList[] cartListObject = new CartList[]{};

    ProductList[] productListObject = new ProductList[]{};

    RecyclerView cart_rv;

    Adapter adapter = new Adapter();

    List<CartList> cartList = new ArrayList<>();

    List<ProductList> productList;

    List<CartListDetailed> cartListDetailed = new ArrayList<>();

    TextView lblTotal;

    Double totalCartAmount = 0.0;

    Button btnCheckout;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView;

        rootView = inflater.inflate(R.layout.fragment_cart, null);

        productListObject = new Gson().fromJson(Products.getProductsJsonObject(getContext()), ProductList[].class);

        productList = Arrays.asList(productListObject);

        cart_rv = rootView.findViewById(R.id.cart_rv);

        lblTotal = rootView.findViewById(R.id.lblTotal);

        btnCheckout = rootView.findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cartList.size() > 0) {
                    Toast.makeText(getContext(), "Cart is now for checkout!!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getContext(), PaymentMethodActivity.class);

                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "Your cart is empty!!", Toast.LENGTH_LONG).show();
                }

            }
        });

        generateCart();

        cart_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        cart_rv.setItemViewCacheSize(20);

        cart_rv.setDrawingCacheEnabled(true);

        cart_rv.setAdapter(adapter);

        return rootView;
    }

    public void reloadCart() {


    }

    private void generateCart() {

        totalCartAmount = 0.0;

        cartListDetailed.clear();

        cartList.clear();

        cartListObject = new Gson().fromJson(Cart.getCartJsonObject(), CartList[].class);

        for (CartList cartItems : cartListObject) {

            CartList item = new CartList();

            CartListDetailed item1 = new CartListDetailed();
            item.productId = cartItems.productId;
            item.unitPrice = cartItems.unitPrice;
            item.quantity = cartItems.quantity;

            totalCartAmount += (item.unitPrice * item.quantity);


            item1.productId = cartItems.productId;
            item1.quantity = cartItems.quantity;

            for (ProductList productItems : productList) {
                if (productItems.id == cartItems.productId) {
                    item1.productImage = productItems.imgSrc;
                    item1.productName = productItems.productName;
                    item1.unitPrice = productItems.unitPrice;

                    cartListDetailed.add(item1);

                }
            }

            cartList.add(item);
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");

        lblTotal.setText(df.format(totalCartAmount));
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.cart_list_recyclerview, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.lblProductName.setText(cartListDetailed.get(position).productName);
            holder.lblUnitPrice.setText("PHP " + cartListDetailed.get(position).unitPrice.toString());
            holder.lblQuantity.setText(String.valueOf(cartListDetailed.get(position).quantity));

            Picasso.get().load(cartListDetailed.get(position).productImage).error(R.drawable.ic_menu_camera).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgProductImage);

            holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CartList item = new CartList();

                    item.quantity = cartListDetailed.get(position).quantity - 1;
                    item.unitPrice = cartListDetailed.get(position).unitPrice;
                    item.productId = cartListDetailed.get(position).productId;

                    if (item.quantity > 0) {

                        cartList.set(position, item);

                    } else {
                        cartList.remove(position);
                    }

                    Cart cart = new Cart();

                    cart.cartJsonObject = new Gson().toJson(cartList);

                    Cart.setPreference(cart);

                    generateCart();

                    adapter.notifyDataSetChanged();

                }
            });

            holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CartList item = new CartList();

                    item.quantity = cartListDetailed.get(position).quantity + 1;
                    item.unitPrice = cartListDetailed.get(position).unitPrice;
                    item.productId = cartListDetailed.get(position).productId;

                    if (item.quantity > 0) {

                        cartList.set(position, item);

                    } else {
                        cartList.remove(position);
                    }

                    Cart cart = new Cart();

                    cart.cartJsonObject = new Gson().toJson(cartList);

                    Cart.setPreference(cart);

                    generateCart();

                    adapter.notifyDataSetChanged();

                }
            });


        }

        @Override
        public int getItemCount() {
            return cartListDetailed.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductImage;
        TextView lblProductName, lblUnitPrice, lblQuantity;
        Button btnIncrease, btnDecrease;

        public ViewHolder(View itemView) {
            super(itemView);

            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            lblProductName = itemView.findViewById(R.id.lblProductName);
            lblUnitPrice = itemView.findViewById(R.id.lblUnitPrice);
            lblQuantity = itemView.findViewById(R.id.lblQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);

        }

    }

}
