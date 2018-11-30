package ca.harshgupta.seg2105_project;

import android.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ClientHome extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout_client);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_client);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerClient,
                new ClientHomeFragment1()).commit();
        actionBar.setTitle("Home");

        navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //menuItem.setChecked(true);
                int id = menuItem.getItemId();
                FragmentManager fragmentManager = getFragmentManager();

                switch(id){
                    case R.id.nav_fragment_client_home:
                        fragmentManager.beginTransaction().replace(R.id.containerClient,
                                new ClientHomeFragment1()).commit();
                        actionBar.setTitle("Home");
                        Toast.makeText(ClientHome.this,"Loading First Fragment",Toast.LENGTH_LONG).show();
                        break;

                    case R.id.nav_fragment_client_home_2:
                        fragmentManager.beginTransaction().replace(R.id.containerClient,
                                new ClientHomeFragment2()).commit();
                        actionBar.setTitle("Find Services");
                        Toast.makeText(ClientHome.this,"Loading Second Fragment",Toast.LENGTH_LONG).show();
                         break;

                    case R.id.nav_fragment_client_home_3:
                        fragmentManager.beginTransaction().replace(R.id.containerClient,
                                new ClientHomeFragment3()).commit();
                        actionBar.setTitle("Fragment 3");
                        Toast.makeText(ClientHome.this,"Loading Third Fragment",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_fragment_client_signout:
                        Toast.makeText(ClientHome.this,"Signing Out",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intentToSignOut = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intentToSignOut,0);
                        break;
                }

                // For example, swap UI fragments here
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}