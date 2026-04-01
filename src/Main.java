import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;



public class Main {
    public static void main(String[] args) {
        ArrayList<Zone> list = listLoadData();
        long startTime = System.currentTimeMillis();
        DynamicProgramming(list);
        long endTime = System.currentTimeMillis(); 
        System.out.println("DP Execution Time: " + (endTime - startTime) + " ms");
        
    }
    
    public static PriorityQueue<Zone> loadData() {
        try {
            PriorityQueue<Zone> pq = new PriorityQueue<>();
            Scanner input = new Scanner(new File("problem1.csv"));
            input.next();
            while (input.hasNext()){
                String[] line = input.next().split(",");
                Zone temp = new Zone(line[0],line[1],line[2],line[3],line[4],line[5],line[6]);
                pq.add(temp);
            }
            input.close();
            return pq;            
        } catch (FileNotFoundException e) {
           System.err.println("file not found");
            return null;
        }        
    }    

    public static ArrayList<Zone> listLoadData(){
        try {
            ArrayList<Zone> list = new ArrayList<>();
            Scanner input = new Scanner(new File("problem1.csv"));
            input.next();
            while (input.hasNext()){
                String[] line = input.next().split(",");
                Zone temp = new Zone(line[0],line[1],line[2],line[3],line[4],line[5],line[6]);
                list.add(temp);
            }
            input.close();
            return list;            
        } catch (FileNotFoundException e) {
           System.err.println("file not found");
            return null;
        }
    }

    public static double DynamicProgramming(ArrayList<Zone> list){
        /*
         * First get the dimensions of our benefit 3D array,
         * They are going to be: total number of zones,maximum distance per zone, and maximum supply per zone       
         */
        int numZones = list.size();
        int maxDistance = 0;
        int maxSupply = 0;
        double maxPossibleUtility = 0;
        LocalDateTime earliestTime =list.get(0).winStart;
        for (Zone zone : list) {
            if (zone.distance>maxDistance)
                maxDistance = (int)Math.ceil(zone.distance);
            if (zone.supply>maxSupply)
                maxSupply=(int)Math.ceil(zone.supply);
            if (zone.winStart.isBefore(earliestTime))
                earliestTime=zone.winStart;
            maxPossibleUtility+= zone.utility;
        }
        LocalDateTime currentTime = earliestTime;
        System.out.println("--- STARTING DYNAMIC PROGRAMMING ---");
        double[][][] benefit = new double[numZones][maxDistance][maxSupply];
        for (int zone = 0 ; zone <numZones; zone++) {
            for (int distance = 0; distance < maxDistance; distance++) {
                for (int supply = 0; supply < maxSupply; supply++) {
                    int currentSupply = (int) Math.ceil(list.get(zone).supply);
                    int currentDistance = (int) Math.ceil(list.get(zone).distance);
                    double currentUtility = list.get(zone).utility;
                    LocalDateTime winStart = list.get(zone).winStart;
                    LocalDateTime winEnd = list.get(zone).winEnd;
                    int timeToVisit = (int) Math.ceil(currentDistance/60);
                    if (zone==0){// fill first column
                        if (currentDistance <= distance & currentSupply <= supply){
                            benefit[zone][distance][supply] = currentUtility;
                        }
                    }
                    else if (currentSupply<=supply & currentDistance<=distance ){
                        //check if access window allows.
                        if (currentTime.plusHours(timeToVisit).isAfter(winStart) & currentTime.plusHours(timeToVisit).isBefore(winEnd)) {
                            // check if current zone is worth visiting
                            if (currentUtility + benefit[zone-1][distance-currentDistance][supply-currentSupply] > benefit[zone-1][distance][supply]) {
                                benefit[zone][distance][supply] = currentUtility + benefit[zone-1][distance-currentDistance][supply-currentSupply];
                                currentTime = currentTime.plusHours(timeToVisit);
                            } else
                                benefit[zone][distance][supply] = benefit[zone-1][distance][supply];
                        } else
                            benefit[zone][distance][supply] = benefit[zone-1][distance][supply];
                        
                    } else // zone cannot be visted
                    benefit[zone][distance][supply] = benefit[zone-1][distance][supply]; 
            }
            }
        }
       double utilityScore = (benefit[numZones-1][maxDistance-1][maxSupply-1] / maxPossibleUtility) * 100.0;
        System.out.println("\n--- RESULTS ---");
        System.out.printf("Utility Score: %.2f / 100\n", utilityScore);
        return benefit[numZones-1][maxDistance-1][maxSupply-1];
        
    }
}
