package com.example.publictransportationguidance.helpers;

import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathsTokenizer {

    public static int getNumberOfPaths(List<List<NearestPaths>> listOfList){ return listOfList.size(); }
    public static int getNumberOfPathStops(List<NearestPaths> list){
        return list.size();
    }

    public static int getPathCost(List<List<NearestPaths>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<NearestPaths> path=listOfList.get(pathNumber);
            Integer pathSize = path.size();
            return path.get(pathSize-1).getTotalCost();
        }
        else return -1;
    }
    public static Double getPathDistance(List<List<NearestPaths>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<NearestPaths> path=listOfList.get(pathNumber);
            Integer pathSize = path.size();
            return path.get(pathSize-1).getTotalDistance();
        }
        else return -1.0;
    }

    public static List<NearestPaths> getPath(List<List<NearestPaths>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)) return listOfList.get(pathNumber);
        else                                        return getPath(listOfList,0);
    }
    public static List<String> getPathStops(List<List<NearestPaths>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathStops=new ArrayList<>();
            List<NearestPaths> path = getPath(listOfList,pathNumber);
            for (int stopNum=0;stopNum<path.size();stopNum++){ pathStops.add(path.get(stopNum).getName()); }
            return pathStops;
        }
        else return getPathStops(listOfList,0);
    }
    public static List<String> getPathMeans(List<List<NearestPaths>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathMeans=new ArrayList<>();
            String mean="";
            List<NearestPaths> path = getPath(listOfList,pathNumber);
            for (int stopNum=0;stopNum<path.size();stopNum++){
                mean=path.get(stopNum).getTransportationType();
                switch (mean){
                    case "microbus":  mean="ميكروباص"; pathMeans.add(mean);  break;
                    case "bus":       mean="أوتوبيس";  pathMeans.add(mean);  break;
                    case "metro":     mean="مترو";     pathMeans.add(mean);  break;
                    default:                                                 break;
                }
            }
            return pathMeans;
        }
        else return getPathMeans(listOfList,0);
    }
    public static List<String> getPathMeansDetailed(List<List<NearestPaths>> listOfList, int pathNumber){
        if(pathNumber<getNumberOfPaths(listOfList)){
            List<String> pathMeans=new ArrayList<>();
            String mean="";
            List<NearestPaths> path = getPath(listOfList,pathNumber);
            for (int stopNum=0;stopNum<path.size();stopNum++){
                mean=path.get(stopNum).getTransportationType();
                switch (mean){
                    case "microbus": mean="ميكروباص";  mean+=" ";                 mean+=path.get(stopNum).getLineNumber();  pathMeans.add(mean);  break;
                    case "bus":      mean="أوتوبيس";   mean+=" ";  mean+="رقم ";  mean+=path.get(stopNum).getLineNumber();  pathMeans.add(mean);  break;
                    case "metro":    mean="مترو";      mean+=" ";                 mean+=path.get(stopNum).getLineNumber();  pathMeans.add(mean);  break;
                    default:                                                                                                                      break;
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
            String path= PathsTokenizer.pathToPrint(pathStops);
            transportations.add(path);
        }
        return transportations;
    }

    public static HashMap<Integer,List<String>> enumeratePaths(List<List<NearestPaths>> listOfList){
        HashMap<Integer,List<String>> paths=new HashMap<>();
        List<String> tempPath;

        int numberOfPaths= PathsTokenizer.getNumberOfPaths(listOfList);
        for(int pathNum=0;pathNum<numberOfPaths;pathNum++){
            tempPath=new ArrayList<String>();
            List<NearestPaths> path= listOfList.get(pathNum);
            int numberOfPathStops= PathsTokenizer.getNumberOfPathStops(path);

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

        if (lastIndex == -1) return inputString;  // oldSubstring not found in inputString

        String result = inputString.substring(0, lastIndex) + newSubstring + inputString.substring(lastIndex + oldSubstring.length());

        return result;
    }

    public static List<String> stopsAndMeans(List<List<NearestPaths>> listOfList , int pathNumber){
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
