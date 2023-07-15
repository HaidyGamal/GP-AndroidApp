package com.example.publictransportationguidance.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.publictransportationguidance.pojo.pathsResponse.PathInfo;

import java.util.List;

@Dao
public interface PathsDao {

    @Insert
    void insertPath(PathInfo path);

    @Query("DELETE FROM Paths")
    void deleteAllPaths();

    @Query("SELECT COUNT(path) FROM Paths")
    int getNumberOfRowsOfPathsTable();

    @Query("SELECT * FROM Paths ORDER BY " + "CASE " + "WHEN :sortingCriteria = 'time' THEN time " + "WHEN :sortingCriteria = 'distance' THEN distance " + "WHEN :sortingCriteria = 'cost' THEN cost " + "END ASC")
    List<PathInfo> getSortedPathsASC(String sortingCriteria);

    @Query("DELETE FROM Paths")
    void clearAllPaths();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPaths(List<PathInfo> paths);

}


