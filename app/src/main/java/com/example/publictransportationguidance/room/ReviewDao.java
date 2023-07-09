package com.example.publictransportationguidance.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.publictransportationguidance.pojo.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Update
    void update(Review review);

//    @Delete
//    void delete(Review review);

    @Query("SELECT * FROM Reviews")
    List<Review> getAllReviews();

    @Query("DELETE FROM Reviews")
    void clearTable();

    @Query("DELETE FROM Reviews WHERE concatenatedReviewAsId = :reviewId")
    void deleteReview(String reviewId);

//    @Query("SELECT COUNT(*) FROM Reviews WHERE locationName = :location AND destinationName = :destination AND meanOfTransport = :transport AND reviewCase = :revCase")
//    int isReviewExists(String location, String destination, String transport, String revCase);

    @Query("SELECT COUNT(*) FROM Reviews WHERE concatenatedReviewAsId = :reviewId")
    int isReviewExists(String reviewId);
}