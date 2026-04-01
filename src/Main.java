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
    int numZones = list.size();
    
    double maxPossibleUtility = 0;
    double maxCapacity = 0;
    double maxRange = 0;
    
    // Derive constraints from CSV data
    for (Zone zone : list) {
        maxPossibleUtility += zone.utility;
        maxCapacity += zone.supply;
        maxRange += (zone.distance * 2);
    }
    
    System.out.println("--- DYNAMIC PROGRAMMING CONSTRAINTS ---");
    System.out.println("Max Capacity (Total Supply): " + maxCapacity);
    System.out.println("Max Range (Total Distance): " + maxRange);
    System.out.println("Max Possible Utility: " + maxPossibleUtility);
    System.out.println("--- STARTING DYNAMIC PROGRAMMING ---");
    
    // Sort zones by earliest start time for proper temporal ordering
    list.sort((z1, z2) -> z1.winStart.compareTo(z2.winStart));
    
    LocalDateTime globalStartTime = list.get(0).winStart;
    
    // Discretize distance and time
    int DISTANCE_CHUNK = 500;
    int TIME_CHUNK_HOURS = 6; // Time chunks of 6 hours
    
    int maxDistanceChunks = (int) Math.ceil(maxRange / DISTANCE_CHUNK) + 1;
    int maxCapacityInt = (int) Math.ceil(maxCapacity);
    
    // Calculate total time window span
    LocalDateTime latestEnd = list.get(0).winEnd;
    for (Zone zone : list) {
        if (zone.winEnd.isAfter(latestEnd)) {
            latestEnd = zone.winEnd;
        }
    }
    long totalHours = java.time.temporal.ChronoUnit.HOURS.between(globalStartTime, latestEnd);
    int maxTimeChunks = (int) Math.ceil((double) totalHours / TIME_CHUNK_HOURS) + 1;
    
    System.out.println("Discretizing: maxCapacity=" + maxCapacityInt + 
                      ", maxDistanceChunks=" + maxDistanceChunks +
                      ", maxTimeChunks=" + maxTimeChunks);
    
    // DP[supply][distance_chunk][time_chunk] = maximum utility
    double[][][] dp = new double[maxCapacityInt + 1][maxDistanceChunks][maxTimeChunks];
    
    // Initialize with -1 (unreachable)
    for (int s = 0; s <= maxCapacityInt; s++) {
        for (int d = 0; d < maxDistanceChunks; d++) {
            for (int t = 0; t < maxTimeChunks; t++) {
                dp[s][d][t] = -1;
            }
        }
    }
    
    dp[0][0][0] = 0; // Base case: no supplies, no distance, no time passed
    
    // Process each zone
    for (int i = 0; i < numZones; i++) {
        Zone zone = list.get(i);
        
        int zoneSupply = zone.supply;
        int zoneDistance = (int) Math.ceil(zone.distance * 2);
        int zoneDistanceChunks = (int) Math.ceil((double) zoneDistance / DISTANCE_CHUNK);
        double zoneUtility = zone.utility;
        
        LocalDateTime winStart = zone.winStart;
        LocalDateTime winEnd = zone.winEnd;
        
        // Traverse backwards to avoid using same zone twice
        for (int s = maxCapacityInt; s >= zoneSupply; s--) {
            for (int d = maxDistanceChunks - 1; d >= zoneDistanceChunks; d--) {
                for (int t = maxTimeChunks - 1; t >= 0; t--) {
                    
                    if (dp[s - zoneSupply][d - zoneDistanceChunks][t] >= 0) {
                        // Calculate current time from time chunk
                        LocalDateTime currentTime = globalStartTime.plusHours((long) t * TIME_CHUNK_HOURS);
                        
                        // Travel time to this zone (distance in km, 1 km = 1 minute)
                        long travelMinutes = (long) Math.ceil(zone.distance);
                        LocalDateTime arrivalTime = currentTime.plusMinutes(travelMinutes);
                        LocalDateTime departureTime = arrivalTime.plusMinutes(travelMinutes);
                        
                        // Check if arrival is within access window
                        if (!arrivalTime.isBefore(winStart) && !arrivalTime.isAfter(winEnd)) {
                            
                            // Calculate time chunk for departure
                            long hoursElapsed = java.time.temporal.ChronoUnit.HOURS.between(globalStartTime, departureTime);
                            int departureTimeChunk = (int) Math.ceil((double) hoursElapsed / TIME_CHUNK_HOURS);
                            
                            if (departureTimeChunk < maxTimeChunks) {
                                dp[s][d][departureTimeChunk] = Math.max(
                                    dp[s][d][departureTimeChunk],
                                    dp[s - zoneSupply][d - zoneDistanceChunks][t] + zoneUtility
                                );
                            }
                        }
                    }
                }
            }
        }
        
        if ((i + 1) % 20 == 0) {
            System.out.println("Processed zone " + (i+1) + "/" + numZones);
        }
    }
    
    // Find maximum utility across all valid states
    double maxUtility = 0;
    for (int s = 0; s <= maxCapacityInt; s++) {
        for (int d = 0; d < maxDistanceChunks; d++) {
            for (int t = 0; t < maxTimeChunks; t++) {
                if (dp[s][d][t] >= 0) {
                    maxUtility = Math.max(maxUtility, dp[s][d][t]);
                }
            }
        }
    }
    
    double utilityScore = (maxUtility / maxPossibleUtility) * 100.0;
    System.out.println("--- RESULTS ---");
    System.out.printf("Utility Score: %.2f / 100\n", utilityScore);
    System.out.printf("Maximum Utility Achieved: %.2f\n", maxUtility);
    System.out.printf("Maximum Possible Utility: %.2f\n", maxPossibleUtility);
    
    return maxUtility;
}
}