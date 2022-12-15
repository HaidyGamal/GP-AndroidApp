package com.example.publictransportationguidance.API.POJO.ShortestPathResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Shortest {

    public static int getNumberOfPaths(List<List<ShortestPath>> listOfList){ return listOfList.size(); }

    public static List<String> getPathStops(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathStops=new ArrayList<>();
            List<ShortestPath> path = getPath(listOfList,pathNumber);
            for (int i=0;i<path.size();i++){ pathStops.add(path.get(i).getName()); }
            return pathStops;
        }
        else return getPathStops(listOfList,0);
    }

    public static List<ShortestPath> getPath(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)) return listOfList.get(pathNumber);
        else                                        return getPath(listOfList,0);
    }

    public static int getPathCost(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<ShortestPath> path=listOfList.get(pathNumber);
            Integer pathSize = path.size();
            return path.get(pathSize-1).getTotalCost();
        }
        else return -1;
    }

    public static Double getPathDistance(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<ShortestPath> path=listOfList.get(pathNumber);
            Integer pathSize = path.size();
            return path.get(pathSize-1).getTotalDistance();
        }
        else return -1.0;
    }
}
