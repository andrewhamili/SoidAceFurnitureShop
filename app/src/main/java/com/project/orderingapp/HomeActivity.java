package com.project.orderingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.orderingapp.Async.GetPaymentMethods;
import com.project.orderingapp.Async.UserVerification;
import com.project.orderingapp.SharedPreferences.Cart;
import com.project.orderingapp.helpers.Network;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context context;

    static FloatingActionButton fab;

    ImageView imgUserImage;
    TextView lblDisplayName, lblEmail;
    Menu menu;
    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;

    public static Context getContextOfApplication() {
        return context;
    }

    public static void showFab() {
        fab.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            updateUI(firebaseUser);

        } else {

        }


    }
/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(this, "Menu " + id, Toast.LENGTH_LONG).show();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuViewCart) {

            Intent intent = new Intent(this, CartActivity.class);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            ProductsFragment productsFragment = new ProductsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_home, productsFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_logout) {

            AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Cart.clearPreference();
                            finish();
                            startActivity(getIntent());
                        }
                    });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void hideFab() {
        fab.hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        imgUserImage = findViewById(R.id.imgUserImage);


        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProductsFragment productsFragment = new ProductsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_home, productsFragment);
                fragmentTransaction.commit();

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        lblDisplayName = view.findViewById(R.id.lblDisplayName);
        lblEmail = view.findViewById(R.id.lblEmail);

        imgUserImage = view.findViewById(R.id.imgUserImage);

        ProductsFragment productsFragment = new ProductsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_home, productsFragment);
        fragmentTransaction.commit();

        toolbar.setTitle("Products");
    }

    void updateUI(FirebaseUser firebaseUser) {

        lblDisplayName.setText(firebaseUser.getDisplayName());
        lblEmail.setText(firebaseUser.getEmail());

        menu.findItem(R.id.nav_logout).setVisible(true);

        Picasso.get().load(firebaseUser.getPhotoUrl()).into(imgUserImage);

        if (Network.isNetworkAvailable(HomeActivity.this)) {

            new UserVerification(HomeActivity.this, firebaseUser, firebaseAuth).execute();

            new GetPaymentMethods(HomeActivity.this).execute();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

}
