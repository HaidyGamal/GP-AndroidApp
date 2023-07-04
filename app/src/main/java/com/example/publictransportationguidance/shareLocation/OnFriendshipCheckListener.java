package com.example.publictransportationguidance.shareLocation;

public interface OnFriendshipCheckListener {
    void onFriendshipExists(String friendEmail);
    void onFriendshipDoesNotExist();
    void onFailedToRetrieveData();
}
