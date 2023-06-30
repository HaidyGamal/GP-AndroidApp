package com.example.publictransportationguidance;

public interface OnFriendshipCheckListener {
    void onFriendshipExists(String friendEmail);
    void onFriendshipDoesNotExist();
    void onFailedToRetrieveData();
}
