package com.example.publictransportationguidance.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.publictransportationguidance.pojo.Review;
import com.example.publictransportationguidance.pojo.pathsResponse.PathInfo;

@Database(entities = {PathInfo.class, Review.class}, version = 17)
public abstract class AppRoom extends RoomDatabase {
    private static AppRoom instance;
    public abstract PathsDao pathsDao();
    public abstract ReviewDao reviewDao();
    private static String DATABASE_NAME="cachedData";     /* M Osama: Found in  com.example.ProjectName/data/data/database/databaseName  */

    public static synchronized AppRoom getInstance(Context context) {
        if (instance == null) {
            instance = androidx.room.Room.databaseBuilder(context.getApplicationContext(), AppRoom.class, DATABASE_NAME)
                    .allowMainThreadQueries().fallbackToDestructiveMigration().addCallback(roomCallback).build(); /* DB to populate your database; put it in assets/database*/
        }
        return instance;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        PopulateDbAsyncTask(AppRoom instance) {
            PathsDao pathsDao = instance.pathsDao();
            ReviewDao reviewDao = instance.reviewDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {  return null; }
    }

}
