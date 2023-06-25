package com.example.publictransportationguidance.ui;

import static com.example.publictransportationguidance.blindMode.speechToText.SpeechToTextHelper.RECOGNIZER_RESULT;
import static com.example.publictransportationguidance.helpers.Functions.execute;
import static com.example.publictransportationguidance.helpers.Functions.stringIsFound;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ARABIC;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ASKING_FOR_MODE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BLIND_MODE_ACCEPTANCE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ON_BLIND_MODE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.blindMode.speechToText.SpeechRecognitionCallback;
import com.example.publictransportationguidance.blindMode.speechToText.SpeechToTextHelper;
import com.example.publictransportationguidance.blindMode.textToSpeech.TextToSpeechHelper;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SpeechRecognitionCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public NavController navController;

    public TextToSpeechHelper textToSpeechHelper;
    public SpeechToTextHelper speechToTextHelper;

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
        SharedPrefs.write("ON_BLIND_MODE",0);                                   /* M Osama: every time the app is opened it's onNormalMode; delete it to track the last Mode*/
        ON_BLIND_MODE =SharedPrefs.readMap("ON_BLIND_MODE",0);
        IS_LOGGED_IN =SharedPrefs.readMap("IS_LOGGED_IN",0);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setBackgroundColor(getResources().getColor(R.color.light_green));

//        /* M Osama: default -> BlindMode is Off */
//        if(ON_BLIND_MODE ==0)  binding.appBarMain.fab.setImageResource(R.drawable.ic_blind_mode);
//        else                   binding.appBarMain.fab.setImageResource(0);

        /* M Osama: Passing each menu ID as a set of Ids because each menu should be considered as top level destinations. */
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_add_new_route,R.id.nav_contact_us,R.id.nav_about).setOpenableLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /* M Osama: activate BlindMode manually */
        binding.appBarMain.fab.setOnClickListener((View view)-> {
            navController.navigate(R.id.nav_home);
            ON_BLIND_MODE =SharedPrefs.readMap("ON_BLIND_MODE",0);
            if(ON_BLIND_MODE ==0){
                Snackbar.make(view, "أنتم الآن في نظام التعامل مع الضريرين", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                blindModeOnInitializer();
            }
            else{
                Snackbar.make(view, "أنتم الآن في نظام التعامل مع المبصرين", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                blindModeOffInitializer();
            }
        });

        /* M Osama: activate BlindMode vocally */
        initializeTTSandSTT();
        execute(() -> textToSpeechHelper.speak(ASKING_FOR_MODE, () -> startListening(this)));

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

    void initializeTTSandSTT(){
        textToSpeechHelper = TextToSpeechHelper.getInstance(this,ARABIC);
        speechToTextHelper = SpeechToTextHelper.getInstance(ARABIC);
    }

    /* M Osama what we need to execute after the Speech is ended */
    void startListening(Activity activity){
        speechToTextHelper.startSpeechRecognition(activity);
    }

    /* M Osama: ran after startSpeechRecognition */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> speechConvertedToText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            onSpeechRecognitionResult(SpeechToTextHelper.convertHaaToTaaMarbuta(speechConvertedToText.get(0)));
        }
    }

    @Override
    public void onSpeechRecognitionResult(String result, EditText targetEditText) {

    }

    @Override
    public void onSpeechRecognitionResult(String result) {
        if(stringIsFound(result,BLIND_MODE_ACCEPTANCE)) blindModeOnInitializer();
        else                                            blindModeOffInitializer();
    }

    public  void blindModeOnInitializer(){
        SharedPrefs.write("ON_BLIND_MODE", 1);
        binding.appBarMain.fab.setImageResource(0);
    }

    public void blindModeOffInitializer(){
        SharedPrefs.write("ON_BLIND_MODE", 0);
        binding.appBarMain.fab.setImageResource(R.drawable.ic_blind_mode);
    }

}