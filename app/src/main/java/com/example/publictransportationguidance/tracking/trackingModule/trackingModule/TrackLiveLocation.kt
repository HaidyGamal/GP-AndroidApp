package com.example.publictransportationguidance.tracking.trackingModule.trackingModule

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageButton
import com.example.publictransportationguidance.tracking.trackingModule.Reviews.DislikeDialogFragment
import com.example.publictransportationguidance.tracking.trackingModule.Reviews.MyApp
import com.example.publictransportationguidance.R
import com.example.publictransportationguidance.helpers.GlobalVariables.*
import com.example.publictransportationguidance.helpers.PathsTokenizer.stopsAndMeans
import com.example.publictransportationguidance.pojo.Review
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths
import com.example.publictransportationguidance.room.AppRoom
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.PathUtils.Companion.showExpectedPath
import com.example.publictransportationguidance.tracking.trackingModule.util.ui.CameraUtils.Companion.showDefaultLocationOnMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TrackLiveLocation : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        @kotlin.jvm.JvmField
        var listOfActualPathNodes: ArrayList<LatLng> = ArrayList<LatLng>()
        lateinit var googleMap: GoogleMap
    }

    private lateinit var defaultLocation: LatLng                            /* M Osama: default location to move camera to */
    lateinit var pathNodes: ArrayList<LatLng>

    lateinit var paths: List<List<NearestPaths>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_location)
        val likeButton: ImageButton = findViewById(R.id.like)
        val dislikeButton: ImageButton = findViewById(R.id.dislike)
        likeButton.background.setColorFilter(Color.parseColor("#00000000"), PorterDuff.Mode.SRC_ATOP)

        pathNodes = (intent.getBundleExtra(INTENT_PATH)?.getSerializable(BUNDLE_PATH) as? ArrayList<LatLng>)!!

        var pathsDao = AppRoom.getInstance(application).pathsDao()
        var reviewsDao = AppRoom.getInstance(application).reviewDao()

        SharedPrefs.init(this)
        val sortingCriteria = SharedPrefs.readMap("CHOSEN_CRITERIA", SORTING_CRITERIA)
        val chosenPathNumber = SharedPrefs.readMap("CHOSEN_PATH_NUMBER",0)
        val defaultNumber = pathsDao.getSortedPathsASC(sortingCriteria)[chosenPathNumber].defaultPathNumber

        val myApp = application as MyApp
        paths = myApp.paths!!
        val stopsAndMeans = stopsAndMeans(paths,defaultNumber)

        var isLiked=0
        var color=0
        var exists:Int

        likeButton.setOnClickListener{

            if(isLiked==0){
                for (i in 0 until (stopsAndMeans.size - 2) step 2) {
                    exists = reviewsDao.isReviewExists(stopsAndMeans[i]+stopsAndMeans[i+2]+stopsAndMeans[i+1]+LIKES_FIELD)
                    Log.i("TAG","$exists Before insert")
                    if(exists>0){
                        Snackbar.make(likeButton, "يرجي الانتباه أنه مسموح للمستخدم بتقييم واحد لنفس المواصلة/الطريق", Snackbar.LENGTH_SHORT).show()
                    }
                    else if(exists==0) {
                        like(stopsAndMeans[i], stopsAndMeans[i + 2], stopsAndMeans[i + 1])
                        reviewsDao.insert(Review(stopsAndMeans[i], stopsAndMeans[i + 2], stopsAndMeans[i + 1], LIKES_FIELD))
                    }

                    isLiked=1
                    color = Color.parseColor("#B3FFFFFF")
                }
            }
            else if(isLiked==1){
                for (i in 0 until (stopsAndMeans.size - 2) step 2) {
                    exists = reviewsDao.isReviewExists(stopsAndMeans[i]+stopsAndMeans[i+2]+stopsAndMeans[i+1]+LIKES_FIELD)
                    Log.i("TAG","$exists Before delete")
                    if(exists==0);
                    else if(exists>0){
                        unLike(stopsAndMeans[i], stopsAndMeans[i + 2], stopsAndMeans[i + 1])
                        reviewsDao.deleteReview(stopsAndMeans[i]+stopsAndMeans[i + 2]+stopsAndMeans[i + 1]+LIKES_FIELD)
                    }

                    isLiked=0
                    color = Color.parseColor("#00000000")
                }
            }
            likeButton.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }

        dislikeButton.setOnClickListener{
            if(isLiked==1){
                Snackbar.make(likeButton, "يرجي الغاء الاعجاب أولا", Snackbar.LENGTH_SHORT).show()
            }
            else{
                val dislikeDialog = DislikeDialogFragment()
                dislikeDialog.show(supportFragmentManager, "dislike_dialog")
            }
        }

        /* M Osama: buildingMap for tracking user's location(Car) */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.trackingLocationMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    
    /* M Osama: called once; only when the map is loaded on the screen */
    override fun onMapReady(googleMap: GoogleMap) {
        Companion.googleMap = googleMap
        defaultLocation = LatLng(30.0444, 31.2357)
        showDefaultLocationOnMap(googleMap,defaultLocation)
        Handler().postDelayed({ showExpectedPath(googleMap, pathNodes, Color.BLACK) }, 3000)
    }

    private fun like(startNode: String, endNode: String, mean: String){
        val (documentId, data) = getDocumentIdAndData(startNode, endNode, mean)
        createDocumentIfNotExists(documentId,data){ incrementFieldInReviews(LIKES_FIELD,startNode,endNode,mean) }
    }

    private fun unLike(startNode: String, endNode: String, mean: String){
        val (documentId, data) = getDocumentIdAndData(startNode, endNode, mean)
        createDocumentIfNotExists(documentId,data){ decrementFieldInReviews(LIKES_FIELD,startNode,endNode,mean) }
    }


    private fun createDocumentId(startNode: String, endNode: String, mean: String):String = "$startNode|$endNode|$mean"
    private fun createReviewInitialValues(): Map<String,Int> = mapOf(BAD_PATH_DISLIKES_FIELD to 0, LIKES_FIELD to 0, UNFOUND_PATH_DISLIKES_FIELD to 0)
    private fun getDocumentIdAndData(startNode: String, endNode: String, mean: String): Pair<String, Map<String, Any>> {
        val documentId = createDocumentId(startNode, endNode, mean)
        val data = createReviewInitialValues()
        return Pair(documentId, data)
    }

    private fun incrementFieldInReviews(reviewField: String,startLatLng: String,endLatLng: String,mean: String) {
        val documentId = "$startLatLng|$endLatLng|$mean"

        val db = FirebaseFirestore.getInstance()

        val reviewRef: DocumentReference = db.collection("Reviews").document(documentId)

        reviewRef.update(reviewField, FieldValue.increment(1))
            .addOnSuccessListener { Log.i("TAG","Done") }
            .addOnFailureListener { e -> Log.i("TAG","De7k") }
    }

    private fun decrementFieldInReviews(reviewField: String,startLatLng: String,endLatLng: String,mean: String) {
        val documentId = "$startLatLng|$endLatLng|$mean"

        val db = FirebaseFirestore.getInstance()

        val reviewRef: DocumentReference = db.collection("Reviews").document(documentId)

        reviewRef.update(reviewField, FieldValue.increment(-1))
            .addOnSuccessListener { Log.i("TAG","Done") }
            .addOnFailureListener { e -> Log.i("TAG","De7k") }
    }

    private fun checkDocumentExistence(documentId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        val reviewRef: DocumentReference = db.collection("Reviews").document(documentId)

        reviewRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val documentExists = documentSnapshot.exists()
                callback.invoke(documentExists)
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Failed to check document existence", e)
                callback.invoke(false)
            }
    }

    private fun createDocumentIfNotExists(documentId: String, data: Map<String, Any>, callback: () -> Unit) {
        checkDocumentExistence(documentId) { exists ->
            if (!exists) {
                val db = FirebaseFirestore.getInstance()

                val reviewRef: DocumentReference = db.collection("Reviews").document(documentId)

                reviewRef.set(data)
                    .addOnSuccessListener {
                        Log.i("TAG", "Document created successfully")
                        callback.invoke()
                    }
                    .addOnFailureListener { e -> Log.e("TAG", "Failed to create document", e) }
            } else { callback.invoke() }
        }
    }


}

