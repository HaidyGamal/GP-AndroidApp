package com.example.publictransportationguidance.UI;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.Shortest;
import com.example.publictransportationguidance.API.POJO.StopsResponse.AllStops;
import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.Room.RoomDB;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.publictransportationguidance.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        navigationView.setBackgroundColor(getResources().getColor(R.color.light_green));

        DAO dao=RoomDB.getInstance(getApplicationContext()).Dao();

        /* M Osama: Sending Request to return with all available stops */
        RetrofitClient.getInstance().getApi().getAllStops().enqueue(new Callback<AllStops>() {
            @Override
            public void onResponse(Call<AllStops> call, Response<AllStops> response) {
                AllStops allStops=response.body();
                List<StopModel> stops=allStops.getAllNodes();
                dao.deleteAllStops();                             /* M Osama: cache latest Stops in the App's first open */
                for (StopModel st : stops)  dao.insertStop(st);
            }

            @Override
            public void onFailure(Call<AllStops> call, Throwable t) {
                Toast.makeText(MainActivity.this, "هناك مشكلة في النت لديكم", Toast.LENGTH_SHORT).show();
            }
        });


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
                    Snackbar.make(view, "أنتم الآن في نظام التعامل مع الضريرين", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    binding.appBarMain.fab.setImageResource(0);
                    /*M Osama: to add code/sound assistant in blindMode */
                }
                else{
                    Snackbar.make(view, "أنتم الآن في نظام التعامل مع المبصرين", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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