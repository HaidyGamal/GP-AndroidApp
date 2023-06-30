package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.publictransportationguidance.OnFriendshipCheckListener;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentContactUsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContactUsFragment extends Fragment implements OnFriendshipCheckListener {
    public ContactUsFragment() {}

    public static final String SHARE_LOCATION_COLLECTION_NAME= "Friendships";

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

    FragmentContactUsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact_us,container,false);
        firebaseInitializer();
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        /* M Osama: ensure that user has an account to prevent crashes */
        if(isUserAuthenticated()) {
            docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(mUser.getEmail()));
            ensureDocumentIsExit(mUser.getEmail());
        }

        /* M Osama: add a friend to currentUser */
        binding.addFriend.setOnClickListener(v -> {
            String friendEmail = binding.friendEmailEt.getText().toString();
            if(friendEmail.equals("")) emptyEditTextIsNotAllowedToast();
            else addFriend(friendEmail);
        });

        /* M Osama: delete a friend from currentUser */
        binding.deleteFriend.setOnClickListener(v -> {
            String friendEmail = binding.friendEmailEt.getText().toString();
            if(friendEmail.equals("")) emptyEditTextIsNotAllowedToast();
            else deleteFriend(friendEmail);
        });

        /* M Osama; track the user's friend */
        binding.startTracking.setOnClickListener(v -> {
            String friendEmailToTrack = binding.friendEmailToTrackEt.getText().toString();
            if(friendEmailToTrack.equals("")) emptyEditTextIsNotAllowedToast();
            else checkFriendship(mUser.getEmail(),friendEmailToTrack,this);
        });

    }


    private void firebaseInitializer(){
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    /* M Osama: updates user's current location Information to be listened to his friends */
    private void updateUserSharedLocation(String currentLat, String currentLong, String locationName) {
        if(isUserAuthenticated()) {
            Map<String, Object> data = new HashMap<>();
            data.put("lat", currentLat);
            data.put("long", currentLong);
            data.put("locationName", locationName);
            docRef.set(data)
                    .addOnSuccessListener(v -> Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(v -> Toast.makeText(getContext(), "De7k", Toast.LENGTH_SHORT).show());
        }
    }

    private void initializeAccount() {
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = new HashMap<>();

                // Check and add the 'friends' field if not present
                if (!documentSnapshot.contains("friends")) {
                    data.put("friends", new ArrayList<String>());
                }

                // Check and add the 'lat' field if not present
                if (!documentSnapshot.contains("lat")) {
                    data.put("lat", "");
                }

                // Check and add the 'long' field if not present
                if (!documentSnapshot.contains("long")) {
                    data.put("long", "");
                }

                // Check and add the 'locationName' field if not present
                if (!documentSnapshot.contains("locationName")) {
                    data.put("locationName", "");
                }

                if (!data.isEmpty()) {
                    docRef.update(data)
                            .addOnSuccessListener(v -> Toast.makeText(getContext(), "Account fields initialized", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(v -> Toast.makeText(getContext(), "Failed to initialize account fields", Toast.LENGTH_SHORT).show());
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to retrieve document", Toast.LENGTH_SHORT).show());
    }

    /* M Osama: can be deleted if we used user's collection instread of FriendShip collection */
    private void ensureDocumentIsExit(String documentId){
        DocumentReference docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists());
                else initializeAccount();
            } else Toast.makeText(getContext(), "Failed to retrieve document", Toast.LENGTH_SHORT).show();
        });
    }

    /* M Osama: used by the user's friends to track his location information */
    private void readFriendSharedLocation(String friendEmail) {
        if (isUserAuthenticated()) {
            db.collection(SHARE_LOCATION_COLLECTION_NAME)
                    .document(friendEmail)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Toast.makeText(getContext(), "Failed to retrieve document data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String lat = documentSnapshot.getString("lat");
                            String lng = documentSnapshot.getString("long");
                            String locationName = documentSnapshot.getString("locationName");
                            binding.latTv.setText(lat);
                            binding.longTv.setText(lng);
                            binding.locationName.setText(locationName);

                            // Handle the updated location data here
                        } else {
                            Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please Log In first", Toast.LENGTH_SHORT).show();
        }
    }

    /* M Osama: delete specific friend from the user's friends */
    private void deleteFriend(String friendEmail) {
        if(isUserAuthenticated()){
            docRef.update("friends", FieldValue.arrayRemove(friendEmail))
                    .addOnSuccessListener(v -> Toast.makeText(getContext(), "Friend deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(v -> Toast.makeText(getContext(), "Failed to delete friend", Toast.LENGTH_SHORT).show());
        }
        else Toast.makeText(getContext(), "Log In first", Toast.LENGTH_SHORT).show();
    }

    /* M Osama: add specific friend to the user's friends */
    private void addFriend(String friendEmail) {
        if(isUserAuthenticated()) {
            docRef.update("friends", FieldValue.arrayUnion(friendEmail))
                    .addOnSuccessListener(v -> Toast.makeText(getContext(), "Friend added", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(v -> Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show());
        }
        else Toast.makeText(getContext(), "Log In first", Toast.LENGTH_SHORT).show();
    }

    /* M Osama: check whether a friendship exists between two user's*/
    private void checkFriendship(String documentId, String email, OnFriendshipCheckListener listener) {
        db.collection(SHARE_LOCATION_COLLECTION_NAME)
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friendsList = (List<String>) documentSnapshot.get("friends");
                        if (friendsList != null && friendsList.contains(email)) {
                            db.collection(SHARE_LOCATION_COLLECTION_NAME)
                                    .document(email)
                                    .get()
                                    .addOnSuccessListener(documentTwoSnapshot -> {
                                        if (documentTwoSnapshot.exists()) {
                                            List<String> listFriends = (List<String>) documentTwoSnapshot.get("friends");
                                            if (listFriends != null && listFriends.contains(documentId)) {
                                                if (listener != null) {
                                                    listener.onFriendshipExists(email);
                                                }
                                            } else {
                                                if (listener != null) {
                                                    listener.onFriendshipDoesNotExist();
                                                }
                                            }
                                        } else {
                                            if (listener != null) {
                                                listener.onFriendshipDoesNotExist();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (listener != null) {
                                            listener.onFailedToRetrieveData();
                                        }
                                    });
                        } else {
                            if (listener != null) {
                                listener.onFriendshipDoesNotExist();
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.onFriendshipDoesNotExist();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailedToRetrieveData();
                    }
                });
    }

    /* M Osama: check whether the user has account or not */
    private boolean isUserAuthenticated() {
        return mUser != null; // Returns true if the user is authenticated, false otherwise
    }

    /* M Osama: enable location tracking incase of friendship existing */
    @Override
    public void onFriendshipExists(String email) {
        readFriendSharedLocation(email);
    }

    @Override
    public void onFriendshipDoesNotExist() {}

    @Override
    public void onFailedToRetrieveData() {}

    public void emptyEditTextIsNotAllowedToast(){
        Toast.makeText(getContext(), "لا يمكن ترك الصندوق فارغا", Toast.LENGTH_SHORT).show();
    }

}