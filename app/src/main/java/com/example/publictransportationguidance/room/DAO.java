package com.example.publictransportationguidance.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.publictransportationguidance.pojo.pathsResponse.PathInfo;

@Dao
public interface DAO {

    @Insert
    void insertPath(PathInfo path);

    @Query("DELETE FROM Paths")
    void deleteAllPaths();

    @Query("SELECT COUNT(path) FROM Paths")
    int getNumberOfRowsOfPathsTable();

    @Query("SELECT * FROM Paths WHERE id =:pathNum")
    PathInfo getPathToPopulateWheel(int pathNum);

    @Query("UPDATE Paths SET time = :time WHERE id = :pathNum")
    void updatePathTime(int pathNum, int time);

    @Query("SELECT * FROM Paths ORDER BY cost")
    PathInfo getPathInfoOrderedByCost();
    @Query("SELECT * FROM Paths ORDER BY distance")
    PathInfo getPathInfoOrderedByDistance();
}


/************************************************************************************************/
/* M Osama: old queries when we were gettings stops from our API not Google ones */

//    @Insert
//    void insertStop(StopModel stop);

//    @Query("DELETE FROM Stops")
//    void deleteAllStops();

//    @Query("SELECT COUNT(name) FROM Stops")
//    int getNumberOfRowsInStopTable();

//    @Query("SELECT name FROM Stops")
//    List<String> getAllStops();

//    @Delete
//    void deleteStop(StopModel stop);

//    @Query("SELECT * FROM Stops ORDER BY name")
//    List<StopModel> getAllStopsInfo();
/************************************************************************************************/


