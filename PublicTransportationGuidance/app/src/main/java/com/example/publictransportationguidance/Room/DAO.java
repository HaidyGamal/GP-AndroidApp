package com.example.publictransportationguidance.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.publictransportationguidance.API.POJO.PathInfo;
import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insertStop(StopModel stop);
    @Insert
    void insertPath(PathInfo path);

    @Query("DELETE FROM Stops")
    void deleteAllStops();
    @Query("DELETE FROM Paths")
    void deleteAllPaths();

    @Query("SELECT COUNT(name) FROM Stops")
    int getNumberOfRowsInStopTable();
    @Query("SELECT COUNT(path) FROM Paths")
    int getNumberOfRowsOfPathsTable();

    @Query("SELECT name FROM Stops")
    List<String> getAllStops();

    @Query("SELECT * FROM Paths ORDER BY cost")
    PathInfo getPathInfoOrderedByCost();
    @Query("SELECT * FROM Paths ORDER BY distance")
    PathInfo getPathInfoOrderedByDistance();

    @Delete
    void deleteStop(StopModel stop);

    @Query("SELECT * FROM Stops ORDER BY name")
    List<StopModel> getAllStopsInfo();

    @Query("SELECT * FROM Paths WHERE id =:pathNum")
    PathInfo getPathToPopulateWheel(int pathNum);



}
