package com.example.publictransportationguidance.ui;

import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ON_BLIND_MODE;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.publictransportationguidance.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //haidy: to use the passed URL that the user clicked on
        Uri uri = getIntent().getData();
        if (uri != null) {
            String url = uri.toString();
            Toast.makeText(getApplicationContext(),"url >> "+url, Toast.LENGTH_SHORT).show();}

        /* M Osama: Initialize SharedPrefs */
        SharedPrefs.init(this);
        ON_BLIND_MODE =SharedPrefs.readMap("ON_BLIND_MODE",0);
        IS_LOGGED_IN =SharedPrefs.readMap("IS_LOGGED_IN",0);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setBackgroundColor(getResources().getColor(R.color.light_green));

        /* M Osama: default -> BlindMode is Off */
        if(ON_BLIND_MODE ==0)  binding.appBarMain.fab.setImageResource(R.drawable.ic_blind_mode);
        else                   binding.appBarMain.fab.setImageResource(0);


        /* M Osama: Passing each menu ID as a set of Ids because each menu should be considered as top level destinations. */
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_add_new_route,R.id.nav_contact_us,R.id.nav_about).setOpenableLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        /* M Osama: Return to Home Fragment once the Fab is clicked */
        binding.appBarMain.fab.setOnClickListener((View view)-> {
            navController.navigate(R.id.nav_home);
            ON_BLIND_MODE =SharedPrefs.readMap("ON_BLIND_MODE",0);
            if(ON_BLIND_MODE ==0){
                Snackbar.make(view, "أنتم الآن في نظام التعامل مع الضريرين", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                binding.appBarMain.fab.setImageResource(0);
                /*M Osama: to add code/sound assistant in blindMode */
            }
            else{
                Snackbar.make(view, "أنتم الآن في نظام التعامل مع المبصرين", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                binding.appBarMain.fab.setImageResource(R.drawable.ic_blind_mode);
            }
            ON_BLIND_MODE ^= 1;
            SharedPrefs.write("ON_BLIND_MODE", ON_BLIND_MODE); /*M Osama: toggle mode between Normal & Blind */
        });

    }

    /*M Osama: Inflate the menu(right at the right of action bar); this adds items to the action bar if it is present. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* M Osama: Function To Support Navigation through fragments */
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


}