package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.helpers.GlobalVariables.SHARE_LOCATION_COLLECTION_NAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.publictransportationguidance.databinding.FragmentShareLocationBinding;
import com.example.publictransportationguidance.shareLocation.RequestSharingLocationDialog;
import com.example.publictransportationguidance.shareLocation.OnFriendshipCheckListener;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.shareLocation.ShareLocationDialogListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShareLocationFragment extends Fragment implements OnFriendshipCheckListener, ShareLocationDialogListener {
    public ShareLocationFragment() {}

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

    FragmentShareLocationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_share_location,container,false);
        firebaseInitializer();
        db = FirebaseFirestore.getInstance();

        if (getParentFragment() != null && !getParentFragment().isAdded()) {
            getParentFragment().getChildFragmentManager().beginTransaction().add(new ShareLocationFragment(), "ShareLocationFragment").commit();
        }


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        /* M Osama: ensure that user has an account to prevent crashes */
        if(isUserAuthenticated()) {
            docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(mUser.getEmail()));
            ensureDocumentIsExist(mUser.getEmail());
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
            String trackedEmail = binding.friendEmailToTrackEt.getText().toString();
            if(trackedEmail.equals("")) emptyEditTextIsNotAllowedToast();
            else checkFriendship(mUser.getEmail(),trackedEmail,this);
        });

//        if(mUser!=null) {
//            listenToWhichFriendWantToTrack(mUser.getEmail());
//            listenToTrackingResponse();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mUser!=null) {
            listenToWhichFriendWantToTrack(mUser.getEmail());
            listenToTrackingResponse();
        }
    }

    private void firebaseInitializer(){
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    private void initializeAccount() {
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = new HashMap<>();

                if (!documentSnapshot.contains("friends"))      data.put("friends", new ArrayList<Map<String,Object>>());           // Check and add the 'friends' field if not present
                if (!documentSnapshot.contains("lat"))          data.put("lat", "");                                                // Check and add the 'lat' field if not present
                if (!documentSnapshot.contains("long"))         data.put("long", "");                                               // Check and add the 'long' field if not present
                if (!documentSnapshot.contains("locationName")) data.put("locationName", "");                                       // Check and add the 'locationName' field if not present

                if (!data.isEmpty()) {
                    docRef.update(data)
                            .addOnSuccessListener(v -> Log.i("TAG","From(ShareLocationFragment)"+"Account fields initialized"))
                            .addOnFailureListener(v -> Log.i("TAG","From(ShareLocationFragment)"+"Failed to initialize account fields"));
                }
            }
        }).addOnFailureListener(e -> Log.i("TAG","From(ShareLocationFragment)"+"Failed to retrieve document"));
    }

    /* M Osama: can be deleted if we used user's collection instread of FriendShip collection */
    private void ensureDocumentIsExist(String documentId){
        docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists());
                else initializeAccount();
            } else Log.i("TAG","From(SharedLocationFragment)"+"Failed to retrieve document");
        });
    }

    /* M Osama: used by the user's friends to track his location information */
    private void readFriendSharedLocation(String friendEmail) {
        if (isUserAuthenticated()) {
            db.collection(SHARE_LOCATION_COLLECTION_NAME)
                    .document(friendEmail)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Log.i("TAG","From(SharedLocationFragment)"+"Failed to retrieve document data");
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
                        } else Log.i("TAG","From(SharedLocationFragment)"+"Document Doesn't Exist");
                    });
        } else fireToast(getString(R.string.PleaseLogInFirst));

    }

    /* M Osama: check whether the user has account or not */
    private boolean isUserAuthenticated() {
        return mUser != null; // Returns true if the user is authenticated, false otherwise
    }

    /* M Osama: add specific friend to the user's friends */
    private void addFriend(String friendEmail) {
        if (isUserAuthenticated()) {
            docRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        List<Map<String, Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");

                        boolean friendExists = false;
                        for (Map<String, Object> friend : friends) {
                            String email = (String) friend.keySet().iterator().next();
                            if (email != null && email.equals(friendEmail)) {
                                friendExists = true;
                                break;
                            }
                        }

                        if (!friendExists) {
                            Map<String, Object> newFriend = new HashMap<>();
                            newFriend.put(friendEmail, "No");
                            docRef.update("friends", FieldValue.arrayUnion(newFriend))
                                    .addOnSuccessListener(v -> fireToast("تم اضافة الصديق بنجاح"))
                                    .addOnFailureListener(v -> fireToast("حدث فشل في اضافة الصديق"));
                        } else {
                            fireToast("الصديق موجود بالفعل");
                        }
                    })
                    .addOnFailureListener(v -> fireToast("حدث فشل في اضافة الصديق"));
        } else fireToast(getString(R.string.PleaseLogInFirst));

    }

    /* M Osama: delete a friend using his email*/
    private void deleteFriend(String friendEmail) {
        if (isUserAuthenticated()) {
            docRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        List<Map<String, Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        List<Map<String, Object>> updatedFriends = new ArrayList<>();

                        boolean friendDeleted = false;
                        for (Map<String, Object> friend : friends) {
                            String email = (String) friend.keySet().iterator().next();
                            if (email != null && email.equals(friendEmail)) {
                                friendDeleted = true;
                            } else {
                                updatedFriends.add(friend);
                            }
                        }

                        if (friendDeleted) {
                            docRef.update("friends", updatedFriends)
                                    .addOnSuccessListener(v -> fireToast("تم حذف الصديق بنجاح"))
                                    .addOnFailureListener(v -> fireToast("فشلنا في حذف الصديق، نرجو إعادة المحاولة"));
                        } else {
                            fireToast("هذا ليس متوفر في قائمة الأصدقاء");
                        }
                    })
                    .addOnFailureListener(v -> fireToast("فشلنا في حذف الصديق، نرجو إعادة المحاولة"));
        } else fireToast(getString(R.string.PleaseLogInFirst));

    }

    private void editFriend(String friendEmail,String newValue) {
        if (isUserAuthenticated()) {
            DocumentReference editDocRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(friendEmail));
            editDocRef.get().addOnSuccessListener(documentSnapshot -> {
                List<Map<String, Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                boolean friendFound = false;
                for (Map<String, Object> friend : friends) {
                    String email = friend.keySet().iterator().next();
                    if (email != null && email.equals(mUser.getEmail())) {
                        friendFound = true;
                        friend.put(mUser.getEmail(), newValue);
                        break;
                    }
                }

                if (friendFound) {
                    editDocRef.update("friends", friends)
                            .addOnSuccessListener(v -> Log.i("TAG","From(ShareLocationFragment) Friend updated successfully"))
                            .addOnFailureListener(v -> Log.i("TAG","From(ShareLocationFragment) Failed to update friend"));
                } else Log.i("TAG","From(ShareLocationFragment) Friend not found");
            })
                    .addOnFailureListener(v -> fireToast("Failed to update friend"));
        } else fireToast(getString(R.string.PleaseLogInFirst));
    }

    /* M Osama: check the existence of friendShip between two persons */
    private void checkFriendship(String userEmail, String friendEmail, OnFriendshipCheckListener listener) {
        db.collection(SHARE_LOCATION_COLLECTION_NAME)
                .document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> friendsList = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        if (friendsList != null) {
                            boolean friendshipExists = false;
                            for(int i=0;i<friendsList.size();i++){
                                String currentFriendEmail = friendsList.get(i).keySet().toArray()[0]+"";
                                if(friendEmail.equals(currentFriendEmail)){
                                    friendshipExists=true;
                                    break;
                                }
                            }

                            if (friendshipExists) {
                                db.collection(SHARE_LOCATION_COLLECTION_NAME)
                                        .document(friendEmail)
                                        .get()
                                        .addOnSuccessListener(documentTwoSnapshot -> {
                                            if (documentTwoSnapshot.exists()) {
                                                List<Map<String, Object>> listFriends = (List<Map<String, Object>>) documentTwoSnapshot.get("friends");
                                                if (listFriends != null) {
                                                    boolean reverseFriendshipExists = false;
                                                    for(int i=0;i<listFriends.size();i++){
                                                        String tempEmail = listFriends.get(i).keySet().toArray()[0]+"";
                                                        if(userEmail.equals(tempEmail)){
                                                            reverseFriendshipExists=true;
                                                            break;
                                                        }
                                                    }

                                                    if (reverseFriendshipExists) if (listener != null) {
                                                        listener.onFriendshipExists(friendEmail);
                                                        fireToast("أنتم أصدقاء");
                                                    }
                                                    else {
                                                        if (listener != null) {
                                                            listener.onFriendshipDoesNotExist();
                                                            fireToast("أنتم لستم أصدقاء");
                                                        }
                                                    }
                                                } else if (listener != null) listener.onFriendshipDoesNotExist();
                                            } else if (listener != null) listener.onFriendshipDoesNotExist();
                                        })
                                        .addOnFailureListener(e -> {
                                            if (listener != null) {
                                                listener.onFailedToRetrieveData();
                                                fireToast("جودة الإنترنت سيئة");
                                            }
                                        });
                            } else {
                                if (listener != null) {
                                    listener.onFriendshipDoesNotExist();
                                    fireToast("أنتم لستم أصدقاء");
                                }
                            }
                        } else if (listener != null) listener.onFriendshipDoesNotExist();
                    } else if (listener != null) listener.onFriendshipDoesNotExist();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailedToRetrieveData();
                });
    }


    /* M Osama: enable location tracking incase of friendship existing */
    @Override
    public void onFriendshipExists(String trackedEmail) {
        editFriend(trackedEmail,"Yes");
    }

    private void listenToTrackingResponse() {
        if (isUserAuthenticated()) {
            Map<String, String> friendStates = new HashMap<>();  // To store previous states of friends

            DocumentReference documentReference = db.collection(SHARE_LOCATION_COLLECTION_NAME)
                    .document(Objects.requireNonNull(mUser.getEmail()));

            documentReference.addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Log.i("TAG", "Failed to retrieve document data");
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    List<Map<String, Object>> friends = (List<Map<String, Object>>) snapshot.get("friends");
                    if (friends != null) {
                        for (Map<String, Object> friend : friends) {
                            String email = "";
                            String value = "";

                            for (Map.Entry<String, Object> entry : friend.entrySet()) {
                                email = entry.getKey();
                                value = entry.getValue().toString();
                                break;
                            }

                            if (email != null && value != null) {
                                String previousValue = friendStates.get(email);
                                if (previousValue != null && previousValue.equals("No") && value.equals("YesYes")) {
                                    Log.i("TAG", "Friend " + email + " changed from No to Yes");
                                    readFriendSharedLocation(email);
                                }

                                friendStates.put(email, value);
                            }
                        }
                    }
                }
            });
        } else fireToast(getString(R.string.PleaseLogInFirst));

    }


    private void listenToWhichFriendWantToTrack(String specificEmail) {
        if (isUserAuthenticated()) {
            Map<String, String> friendStates = new HashMap<>();  // To store previous states of friends

            docRef.addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    fireToast("فشلنا في متابعة تحديثات مكان صديقك");
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    List<Map<String, Object>> friends = (List<Map<String, Object>>) snapshot.get("friends");
                    if (friends != null) {
                        for (Map<String, Object> friend : friends) {
                            String email = "";
                            String value = "";
                            for (Map.Entry<String, Object> entry : friend.entrySet()) {
                                email = entry.getKey();
                                value = entry.getValue().toString();
                                break;  // Assume only one key-value pair per friend map
                            }

                            if (email != null && value != null) {
                                String previousValue = friendStates.get(email);
                                if (previousValue != null && previousValue.equals("No") && value.equals("Yes")) {
                                    if(mUser.getEmail().equals(specificEmail)) {
                                        showRequestSharingLocationDialog(email);
                                    }
//                                    fireToast("صديقك صاحب هذا الحساب " + email + " يريد متابعة تحديثات موقعك");
                                }
                                friendStates.put(email, value);  // Update the previous state for the email
                            }
                        }
                    }
                }
            });
        } else fireToast(getString(R.string.PleaseLogInFirst));

    }



    @Override
    public void onFriendshipDoesNotExist() {}

    @Override
    public void onFailedToRetrieveData() {}

    public void emptyEditTextIsNotAllowedToast(){
        Toast.makeText(getContext(), "لا يمكن ترك الصندوق فارغا", Toast.LENGTH_SHORT).show();
    }

    private void fireToast(String toastContent){
        Toast.makeText(getParentFragmentManager().getPrimaryNavigationFragment().getContext(), toastContent, Toast.LENGTH_SHORT).show();
    }

    private void showDialog(RequestSharingLocationDialog dialog) {
        dialog.setShareLocationDialogListener(this);
        dialog.show(getActivity().getSupportFragmentManager(), RequestSharingLocationDialog.TAG);
    }


    private void showRequestSharingLocationDialog(String email) {
        if (isAdded() && getActivity() != null) {
            RequestSharingLocationDialog dialog = new RequestSharingLocationDialog(email);
            showDialog(dialog);
        }
    }



    @Override
    public void onOptionSelected(int option, String email) {
        if(option==1) editOther(email,"YesYes");
        else          editMe(email,"No");

    }

    private void editOther(String friendEmail, String newValue) {
        if (isUserAuthenticated()) {

            DocumentReference editDocRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(friendEmail));
            editDocRef.get().addOnSuccessListener(documentSnapshot -> {
                        List<Map<String, Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        boolean friendFound = false;
                        for (Map<String, Object> friend : friends) {
                            String email = friend.keySet().iterator().next();
                            if (email != null && email.equals(mUser.getEmail())) {
                                friendFound = true;
                                friend.put(mUser.getEmail(), newValue);
                                break;
                            }
                        }

                        if (friendFound) {
                            editDocRef.update("friends", friends)
                                    .addOnSuccessListener(v -> fireToast("تم تحديث قائمة الأصدقاء بنجاح"))
                                    .addOnFailureListener(v -> fireToast("فشلنا في تحديث قائمة الأصدقاء"));
                        } else fireToast("لم نجد هذا الصديق");
                    })
                    .addOnFailureListener(v -> fireToast("فشلنا في تحديث قائمة الأصدقاء"));
        } else fireToast(getString(R.string.PleaseLogInFirst));
    }

    private void editMe(String friendEmail,String newValue) {
        if (isUserAuthenticated()) {

            DocumentReference editDocRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(mUser.getEmail()));
            editDocRef.get().addOnSuccessListener(documentSnapshot -> {
                        List<Map<String, Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        boolean friendFound = false;
                        for (Map<String, Object> friend : friends) {
                            String email = friend.keySet().iterator().next();
                            if (email != null && email.equals(friendEmail)) {
                                friendFound = true;
                                friend.put(friendEmail, newValue);
                                break;
                            }
                        }

                        if (friendFound) {
                            editDocRef.update("friends", friends)
                                    .addOnSuccessListener(v -> fireToast("تم تحديث قائمة الأصدقاء بنجاح"))
                                    .addOnFailureListener(v -> fireToast("فشلنا في تحديث قائمة الأصدقاء"));
                        } else fireToast("لم نجد هذا الصديق");
                    })
                    .addOnFailureListener(v -> fireToast("فشلنا في تحديث قائمة الأصدقاء"));
        } else fireToast(getString(R.string.PleaseLogInFirst));
    }

}
