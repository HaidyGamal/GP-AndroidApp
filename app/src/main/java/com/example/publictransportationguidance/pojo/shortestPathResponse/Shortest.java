package com.example.publictransportationguidance.pojo.shortestPathResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shortest {

    public static int getNumberOfPaths(List<List<ShortestPath>> listOfList){ return listOfList.size(); }
    public static int getNumberOfPathStops(List<ShortestPath> list){
        return list.size();
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

    public static List<ShortestPath> getPath(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)) return listOfList.get(pathNumber);
        else                                        return getPath(listOfList,0);
    }
    public static List<String> getPathStops(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathStops=new ArrayList<>();
            List<ShortestPath> path = getPath(listOfList,pathNumber);
            for (int stopNum=0;stopNum<path.size();stopNum++){ pathStops.add(path.get(stopNum).getName()); }
            return pathStops;
        }
        else return getPathStops(listOfList,0);
    }
    public static List<String> getPathMeans(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathMeans=new ArrayList<>();
            String mean="";
            List<ShortestPath> path = getPath(listOfList,pathNumber);
            for (int stopNum=0;stopNum<path.size();stopNum++){
                mean=path.get(stopNum).getTransportationType();
                switch (mean){
                    case "microbus":  mean="ميكروباص"; pathMeans.add(mean);  break;
                    case "bus":       mean="أوتوبيس";  pathMeans.add(mean);  break;
                    default:                                                 break;
                }
            }
            return pathMeans;
        }
        else return getPathMeans(listOfList,0);
    }
    public static List<String> getPathMeansDetailed(List<List<ShortestPath>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathMeans=new ArrayList<>();
            String mean="";
            List<ShortestPath> path = getPath(listOfList,pathNumber);
            for (int stopNum=0;stopNum<path.size();stopNum++){
                mean=path.get(stopNum).getTransportationType();
                switch (mean){
                    case "microbus": mean="ميكروباص";  mean+=" ";                mean+=path.get(stopNum).getLineNumber();  pathMeans.add(mean);  break;
                    case "bus":      mean="أوتوبيس";   mean+=" ";  mean+="رقم ";  mean+=path.get(stopNum).getLineNumber();  pathMeans.add(mean);  break;
                    default:                                                                                                                     break;
                }
            }
            return pathMeans;
        }
        else return getPathMeans(listOfList,0);
    }

    public static String pathToPrint(List<String> str){
        String path="";
        for (String stop : str){path+=(stop+" -> ");}
        return path.substring(0,path.length()-3);
    }
    public static String detailedPathToPrint(List<String> str){
        String detailedPath="اركب من ";
        int x=1;
        for (String s: str){
            detailedPath+=(s+" ");
            if(x%2==0){detailedPath+=" ثم من "; x++;}
            else{x++;}
        }

        String path = detailedPath.substring(0,detailedPath.length()-1);
        return replaceLastOccurrence(path,"ثم من","إلي");
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
    public static ArrayList<String> getStringDetailedPathToPopulateRoom(List<List<ShortestPath>> listOfList,int pathNumber){
        ArrayList<String> transportations=new ArrayList<>();
        int numOfPaths=Shortest.concatStopsAndDetailedMeans(listOfList,pathNumber).size();
        for (int pathNum=0;pathNum<numOfPaths;pathNum++){
            transportations.add(detailedPathToPrint(Shortest.concatStopsAndDetailedMeans(listOfList,pathNum)));
        }
        return transportations;
    }

    public static ArrayList<String> getStringUnDetailedPathToPopulateRoom(List<List<ShortestPath>> listOfList,int pathNumber){
        ArrayList<String> transportations=new ArrayList<>();
        int numOfPaths=Shortest.concatStopsAndDetailedMeansWithoutRedundancy(listOfList,pathNumber).size();
        for (int pathNum=0;pathNum<numOfPaths;pathNum++){
            transportations.add(detailedPathToPrint(Shortest.concatStopsAndDetailedMeansWithoutRedundancy(listOfList,pathNum)));
        }
        return transportations;
    }

    public static List<String> concatStopsAndMeans(List<List<ShortestPath>> listOfList,int pathNumber){
        List<String> concatedStopsAndMeans=new ArrayList<>();
        List<ShortestPath> path = getPath(listOfList,pathNumber);
        List<String> pathStops=getPathStops(listOfList,pathNumber);
        List<String> pathMeans=getPathMeans(listOfList,pathNumber);
        int numOfStops = getNumberOfPathStops(path);
        for(int stopNum=0;stopNum<(numOfStops-1);stopNum++){
            concatedStopsAndMeans.add(pathStops.get(stopNum));
            concatedStopsAndMeans.add(pathMeans.get(stopNum));
        }
        String lastStop=pathStops.get(pathStops.size()-1);
        concatedStopsAndMeans.add(lastStop);
        return concatedStopsAndMeans;
    }


    public static List<String> concatStopsAndDetailedMeans(List<List<ShortestPath>> listOfList,int pathNumber){
        List<String> concatedStopsAndMeans=new ArrayList<>();
        List<ShortestPath> path = getPath(listOfList,pathNumber);
        List<String> pathStops=getPathStops(listOfList,pathNumber);
        List<String> pathDetailedMeans=getPathMeansDetailed(listOfList,pathNumber);
        int numOfStops = getNumberOfPathStops(path);
        for(int stopNum=0;stopNum<(numOfStops-1);stopNum++){
            concatedStopsAndMeans.add(pathStops.get(stopNum));
            concatedStopsAndMeans.add(pathDetailedMeans.get(stopNum));
        }
        String lastStop=pathStops.get(pathStops.size()-1);
        concatedStopsAndMeans.add(lastStop);
        return concatedStopsAndMeans;
    }
    public static List<String> concatStopsAndDetailedMeansWithoutRedundancy(List<List<ShortestPath>> listOfList,int pathNumber){
        List<String> concatedStopsAndMeans=new ArrayList<>();
        String prevMean="";
        List<ShortestPath> path = getPath(listOfList,pathNumber);
        List<String> pathStops=getPathStops(listOfList,pathNumber);
        List<String> pathDetailedMeans=getPathMeansDetailed(listOfList,pathNumber);
        int numOfStops = getNumberOfPathStops(path);
        for(int stopNum=0;stopNum<(numOfStops-1);stopNum++){
            if(pathDetailedMeans.get(stopNum).equals(prevMean));
            else{ concatedStopsAndMeans.add(pathStops.get(stopNum));  concatedStopsAndMeans.add(pathDetailedMeans.get(stopNum)); }
            prevMean=pathDetailedMeans.get(stopNum);
        }
        String lastStop=pathStops.get(pathStops.size()-1);
        concatedStopsAndMeans.add(lastStop);
        return concatedStopsAndMeans;
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

    public static String[] listToArray(List<String> arrayList){
        return arrayList.toArray(new String[0]);
    }

    public static String replaceLastOccurrence(String inputString, String oldSubstring, String newSubstring) {
        int lastIndex = inputString.lastIndexOf(oldSubstring);

        if (lastIndex == -1) {
            return inputString;  // oldSubstring not found in inputString
        }

        String result = inputString.substring(0, lastIndex) + newSubstring + inputString.substring(lastIndex + oldSubstring.length());

        return result;
    }


    public static List<String> stopsAndMeans(List<List<ShortestPath>> listOfList , int pathNumber){
        List<String> stops=getPathStops(listOfList,pathNumber);
        List<String> means=getPathMeansDetailed(listOfList,pathNumber);
        int size = stops.size();
        List<String> stopsAndMeans= new ArrayList<>();
        String prevMean="";

        for(int temp=0;temp<size-1;temp++){
            if(means.get(temp).equals(prevMean));
            else{ stopsAndMeans.add(stops.get(temp)); stopsAndMeans.add(means.get(temp));}
            prevMean=means.get(temp);
        }
        stopsAndMeans.add(stops.get(size-1));
        return stopsAndMeans;
    }


}

