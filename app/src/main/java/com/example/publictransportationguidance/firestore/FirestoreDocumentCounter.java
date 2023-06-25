package com.example.publictransportationguidance.firestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreDocumentCounter {
    private static final String COLLECTION_NAME = "Nodes";

    public void countRedundantData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection(COLLECTION_NAME);

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        Map<String, Integer> dataCountMap = new HashMap<>();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String name = document.getString("Node Name");
                            String type = document.getString("Type");
                            String fLatitude = document.getString("FromLatitude");
                            String fLongitude = document.getString("FromLongitude");
                            String tLatitude = document.getString("ToLatitude");
                            String tLongitude = document.getString("ToLongitude");
                            if (name != null && fLatitude != null && fLongitude != null&&tLatitude != null &&
                                    tLongitude != null &&type!=null) {
                                String dataKey = name + "_" + fLatitude + "_" + fLongitude+
                                        "_" + tLatitude + "_" + tLongitude+ "_" + type;

                                // Increment the count for the specific data combination
                                Integer count = dataCountMap.get(dataKey);
                                if (count == null) {
                                    count = 0;
                                }
                                dataCountMap.put(dataKey, count + 1);
                            }
                        }

                        // Iterate over the data count map and check for redundancy
                        for (Map.Entry<String, Integer> entry : dataCountMap.entrySet()) {
                            int count = entry.getValue();

                            if (count >= 5) {
                                // Found at least 5 documents with the same data combination
                                String dataKey = entry.getKey();
                                String[] dataParts = dataKey.split("_");
                                String name = dataParts[0];
                                String type = dataParts[5];


                                //haidy: You can perform any action with the redundant data
                                System.out.println("Name: " + name);
                                System.out.println("type: " + type);
                                System.out.println("Count: " + count);
                            }
                        }
                    }
                } else {

                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }
}
