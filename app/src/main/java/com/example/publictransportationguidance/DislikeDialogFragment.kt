package com.example.publictransportationguidance

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.publictransportationguidance.helpers.GlobalVariables
import com.example.publictransportationguidance.helpers.GlobalVariables.BAD_PATH_DISLIKES_FIELD
import com.example.publictransportationguidance.helpers.GlobalVariables.UNFOUND_PATH_DISLIKES_FIELD
import com.example.publictransportationguidance.helpers.PathsTokenizer
import com.example.publictransportationguidance.room.RoomDB
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DislikeDialogFragment : DialogFragment() {

    private lateinit var pathToDislikeSpinner: Spinner
    private lateinit var reasonOfDislike: Spinner

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_dislike, null)

        pathToDislikeSpinner = dialogView.findViewById(R.id.path_to_dislike)
        reasonOfDislike = dialogView.findViewById(R.id.reasonOfDislike)

        SharedPrefs.init(requireContext())
        val sortingCriteria = SharedPrefs.readMap("CHOSEN_CRITERIA", GlobalVariables.SORTING_CRITERIA)
        val chosenPathNumber = SharedPrefs.readMap("CHOSEN_PATH_NUMBER", 0)

        val myApp = requireActivity().application as MyApp
        val paths = myApp.paths!!

        val dao = RoomDB.getInstance(requireContext()).Dao()
        val defaultNumber = dao.getSortedPathsASC(sortingCriteria)[chosenPathNumber].defaultPathNumber
        val stopsAndMeans = PathsTokenizer.stopsAndMeans(paths, defaultNumber)

        var subPaths= ArrayList<String>()
        var path =""
        for (i in 0 until (stopsAndMeans.size - 2) step 2) {
            path = "${stopsAndMeans[i]}->${stopsAndMeans[i+1]}->${stopsAndMeans[i+2]}"
            subPaths.add(path)
        }

        var numberOfPathToReview:Int
        var base:Int
        builder.setView(dialogView)
            .setTitle("ما الذي لم يعجبك")
            .setPositiveButton("تم") { _: DialogInterface?, _: Int ->
                numberOfPathToReview=pathToDislikeSpinner.selectedItemPosition
                base=2*numberOfPathToReview
                if(reasonOfDislike.selectedItemPosition==0) onDislikeBecauseBadPath(stopsAndMeans[base], stopsAndMeans[base+2], stopsAndMeans[base+1])
                else                                        onDislikeBecauseUnFoundPath(stopsAndMeans[base], stopsAndMeans[base+2], stopsAndMeans[base+1])

                Toast.makeText(context,"${pathToDislikeSpinner.selectedItemPosition}",Toast.LENGTH_SHORT).show()
                Toast.makeText(context,"${reasonOfDislike.selectedItemPosition}",Toast.LENGTH_SHORT).show()
                Log.i("TAG","${pathToDislikeSpinner.selectedItemPosition} +++ ${reasonOfDislike.selectedItemPosition}")
            }
            .setNegativeButton("الغاء") { _: DialogInterface?, _: Int ->
                Toast.makeText(context, "تم العودة بنجاح", Toast.LENGTH_SHORT).show()
            }


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subPaths)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pathToDislikeSpinner.adapter = adapter

        return builder.create()
    }

    private fun onDislikeBecauseBadPath(startNode: String, endNode: String, mean: String){
        val (documentId, data) = getDocumentIdAndData(startNode, endNode, mean)
        createDocumentIfNotExists(documentId,data){ incrementFieldInReviews(BAD_PATH_DISLIKES_FIELD,startNode,endNode,mean) }
    }

    private fun onDislikeBecauseUnFoundPath(startNode: String, endNode: String, mean: String){
        val (documentId, data) = getDocumentIdAndData(startNode, endNode, mean)
        createDocumentIfNotExists(documentId,data){ incrementFieldInReviews(UNFOUND_PATH_DISLIKES_FIELD,startNode,endNode,mean) }
    }

    private fun createDocumentId(startNode: String, endNode: String, mean: String):String = "$startNode|$endNode|$mean"
    private fun createReviewInitialValues(): Map<String,Int> = mapOf(BAD_PATH_DISLIKES_FIELD to 0, GlobalVariables.LIKES_FIELD to 0, UNFOUND_PATH_DISLIKES_FIELD to 0)
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

    private fun decerementFieldInReviews(reviewField: String,startLatLng: String,endLatLng: String,mean: String) {
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
