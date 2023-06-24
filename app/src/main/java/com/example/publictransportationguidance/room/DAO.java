package com.example.publictransportationguidance.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.publictransportationguidance.pojo.pathsResponse.PathInfo;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insertPath(PathInfo path);

    @Query("DELETE FROM Paths")
    void deleteAllPaths();

    @Query("SELECT COUNT(path) FROM Paths")
    int getNumberOfRowsOfPathsTable();

    @Query("UPDATE Paths SET time = :time WHERE defaultPathNumber = :pathNum")
    void updatePathTime(int pathNum, int time);


    @Query("SELECT * FROM Paths ORDER BY " + "CASE " + "WHEN :sortingCriteria = 'time' THEN time " + "WHEN :sortingCriteria = 'distance' THEN distance " + "WHEN :sortingCriteria = 'cost' THEN cost " + "END ASC")
    List<PathInfo> getSortedPathsASC(String sortingCriteria);

    @Query("DELETE FROM Paths")
    void clearAllPaths();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPaths(List<PathInfo> paths);

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

//@Query("SELECT * FROM Paths ORDER BY cost")
//PathInfo getPathInfoOrderedByCost();
//    @Query("SELECT * FROM Paths ORDER BY distance")
//    PathInfo getPathInfoOrderedByDistance();

//@Query("SELECT * FROM Paths WHERE defaultPathNumber =:pathNum")
//PathInfo getPathToPopulateWheel(int pathNum);

//    @Query("SELECT * FROM Paths ORDER BY " + "CASE " + "WHEN :sortingCriteria = 'time' THEN time " + "WHEN :sortingCriteria = 'distance' THEN distance " + "WHEN :sortingCriteria = 'cost' THEN cost " + "END DESC")
//    List<PathInfo> getSortedPathsDESC(String sortingCriteria);
/************************************************************************************************/


