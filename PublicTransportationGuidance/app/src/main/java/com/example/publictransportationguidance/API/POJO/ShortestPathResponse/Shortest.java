package com.example.publictransportationguidance.API.POJO.ShortestPathResponse;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static int getNumberOfPathStops(List<ShortestPath> list){
        return list.size();
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

    public static HashMap<Integer,List<String>> pathMap(List<List<ShortestPath>> listOfList){
        HashMap<Integer,List<String>> paths=new HashMap<>();
        List<String> tempPath;

        int numberOfPaths=Shortest.getNumberOfPaths(listOfList);
        for(int pathNum=0;pathNum<numberOfPaths;pathNum++){
            tempPath=new ArrayList<String>();
            List<ShortestPath> path= listOfList.get(pathNum);
            int numberOfPathStops=Shortest.getNumberOfPathStops(path);

            for(int pathStop=0;pathStop<numberOfPathStops;pathStop++){
                String stop=path.get(pathStop).getName();   tempPath.add(stop);
            }

            paths.put(pathNum,tempPath);
        }

        return paths;
    }

    public static String pathToPrint(List<String> str){
        String path="";
        for (String stop : str){
           path+=(stop+" -> ");
        }
        return path.substring(0,path.length()-3);
    }

    public static ArrayList<String> getStringPathToPopulateRoom(HashMap pathMap){
        ArrayList<String> transportations=new ArrayList<String>();
        int hashMapSize=pathMap.size();
        for(int pathNum=0;pathNum<hashMapSize;pathNum++){
            List<String> pathStops = (List<String>) pathMap.get(pathNum);
            String path=Shortest.pathToPrint(pathStops);
            transportations.add(path);
        }
        return transportations;
    }

}
