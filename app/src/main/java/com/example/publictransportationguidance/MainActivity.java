package com.example.publictransportationguidance;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
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

    private final static int NORMAL_MODE=0;
    private final static int BLIND_MODE=1;
    public static int currentMode=NORMAL_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        /* M Osama: Passing each menu ID as a set of Ids because each menu should be considered as top level destinations. */
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_add_new_route,R.id.nav_contact_us,R.id.nav_about)
                .setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /* M Osama: Return to Home Fragment once the Fab is clicked */
        binding.appBarMain.fab.setOnClickListener((View view)-> {
                navController.navigate(R.id.nav_home);
                if(currentMode==NORMAL_MODE){
                    Snackbar.make(view, "Now, you are on blind mode", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    binding.appBarMain.fab.setImageResource(0);
                    /*M Osama: to add code/sound assistant in blindMode */
                }
                else{
                    Snackbar.make(view, "Now, you are on normal mode", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    binding.appBarMain.fab.setImageResource(R.drawable.ic_blind_mode);
                }
                currentMode ^= 1; /*M Osama: toggle mode between Normal & Blind */
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

}