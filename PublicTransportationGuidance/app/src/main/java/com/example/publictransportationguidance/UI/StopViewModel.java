package com.example.publictransportationguidance.UI;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.Room.RoomDB;

public class StopViewModel {

    MutableLiveData<String> stopNameMutableLiveData=new MutableLiveData<>();

    public void getStopName(Context context, int stopNumber){
        String stopName=getStopFromRoom(context,stopNumber).getName();
        stopNameMutableLiveData.setValue(stopName);
    }

    // Controller [C]
    private StopModel getStopFromRoom(Context context, int stopNumber){
        return RoomDB.getInstance(context).Dao().getAllStops().get(stopNumber);
    }

}
