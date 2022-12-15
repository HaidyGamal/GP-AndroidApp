package com.example.publictransportationguidance.Room;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;

import java.util.List;

@androidx.room.Dao
public interface DAO {

    @Insert
    void insert(StopModel stop);

    @Update
    void update(StopModel stop);

    @Delete
    void delete(StopModel stop);

    @Query("DELETE FROM Stops")
    void deleteAllStops();

    @Query("SELECT COUNT(name) FROM Stops")
    int getNumberOfRows();

    @Query("SELECT * FROM Stops ORDER BY name")
    List<StopModel> getAllStops();

}

//    @Query("SELECT columnOneName FROM tableName WHERE id LIKE :rand_id")
//    Animal getColumn(int rand_id);

    /* Add a query as you want by writting a normal function
    with @Query("") notation containing the SQL query
    */