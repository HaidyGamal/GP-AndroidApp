package com.example.publictransportationguidance.ui;

import static com.example.publictransportationguidance.helpers.Functions.LISTEN_TO_CHOSEN_MODE;
import static com.example.publictransportationguidance.helpers.Functions.execute;
import static com.example.publictransportationguidance.helpers.Functions.stringIsFound;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ARABIC;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ASKING_FOR_MODE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BLIND_MODE_ACCEPTANCE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BLIND_MODE_REJECTANCE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ON_BLIND_MODE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.RE_ASKING_FOR_MODE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SHARE_LOCATION_COLLECTION_NAME;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SpeechRecognitionCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public NavController navController;

    public TextToSpeechHelper textToSpeechHelper;
    public SpeechToTextHelper speechToTextHelper;

    private HashMap<Integer, String> speechRecognitionResults = new HashMap<>();
    private int currentRequestCode;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

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

        firebaseInitializer();

        /* M Osama: ensure that user has an account to prevent crashes */
        if(isUserAuthenticated()) {
            docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(mUser.getEmail()));
            ensureDocumentIsExist(mUser.getEmail());
        }

        /* M Osama: activate BlindMode vocally */
        initializeTTSandSTT();
        execute(() -> textToSpeechHelper.speak(ASKING_FOR_MODE, () -> listenToChosenMode(this)));

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
    void listenToChosenMode(Activity activity){
        currentRequestCode = LISTEN_TO_CHOSEN_MODE;
        speechToTextHelper.startSpeechRecognition(activity,currentRequestCode);
    }

    /* M Osama: ran after startSpeechRecognition */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == currentRequestCode && resultCode == RESULT_OK && data != null) {
            ArrayList<String> speechConvertedToText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = SpeechToTextHelper.convertHaaToTaaMarbuta(speechConvertedToText.get(0));
            speechRecognitionResults.put(requestCode,result);
            processSpeechRecognitionResult(requestCode,result);
        }
    }

    @Override
    public void onSpeechRecognitionResult(String result, EditText targetEditText) {
        processSpeechRecognitionResult(currentRequestCode, result);
    }

    @Override
    public void onSpeechRecognitionResult(String result) {
        processSpeechRecognitionResult(currentRequestCode, result);
    }

    private void processSpeechRecognitionResult(int requestCode, String result) {
        switch (requestCode) {
            case LISTEN_TO_CHOSEN_MODE:
                handleChosenMode(result);
                break;
        }
    }

    private void handleChosenMode(String result){
        if(stringIsFound(result,BLIND_MODE_ACCEPTANCE))         blindModeOnInitializer();
        else if(stringIsFound(result,BLIND_MODE_REJECTANCE))    blindModeOffInitializer();
        else  execute(() -> textToSpeechHelper.speak(RE_ASKING_FOR_MODE, () -> listenToChosenMode(this)));
    }

    public  void blindModeOnInitializer(){
        SharedPrefs.write("ON_BLIND_MODE", 1);
        binding.appBarMain.fab.setImageResource(0);
    }

    public void blindModeOffInitializer(){
        SharedPrefs.write("ON_BLIND_MODE", 0);
        binding.appBarMain.fab.setImageResource(R.drawable.ic_blind_mode);
    }

    /* M Osama: can be deleted if we used user's collection instread of FriendShip collection */
    private void ensureDocumentIsExist(String documentId){
        docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, no further action needed
                } else {
                    initializeAccount();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve document", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initializeAccount() {
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {}
            else {
                // Create a new document with the user's email as the document ID
                Map<String, Object> data = new HashMap<>();

                // Add initial fields to the document
                data.put("friends", new ArrayList<String>());
                data.put("lat", "");
                data.put("long", "");
                data.put("locationName", "");

                docRef.set(data)
                        .addOnSuccessListener(v -> Toast.makeText(this, "Account document created", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(v -> Toast.makeText(this, "Failed to create account document", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve document", Toast.LENGTH_SHORT).show());
    }


    /* M Osama: check whether the user has account or not */
    private boolean isUserAuthenticated() {
        return mUser != null; // Returns true if the user is authenticated, false otherwise
    }

    private void firebaseInitializer(){
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }


}