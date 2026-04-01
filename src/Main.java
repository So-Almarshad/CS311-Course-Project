import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.*;




public class Main {
    public static void main(String[] args) {
        PriorityQueue<Zone> heap = loadData();
        long startTime = System.currentTimeMillis();          
        runGreedy(heap);            
        long endTime = System.currentTimeMillis();     
        System.out.println("Execution Time: " + (endTime - startTime) + " ms\n");

        ArrayList<Zone> list = listLoadData();
        startTime = System.currentTimeMillis();
        DynamicProgramming(list);
        endTime = System.currentTimeMillis(); 
        System.out.println("DP Execution Time: " + (endTime - startTime) + " ms");
   
    }
    
    public static PriorityQueue<Zone> loadData() {
        try {
            PriorityQueue<Zone> pq = new PriorityQueue<>();
            Scanner input = new Scanner(new File("problem1.csv"));
            input.nextLine();
            while (input.hasNextLine()){
                String[] line = input.nextLine().split(",");
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

    public static LocalDateTime getEarliestStartTime(PriorityQueue<Zone> heap) {
        LocalDateTime earliest = null;
        
        for (Zone z : heap) {
            if (earliest == null || z.winStart.isBefore(earliest)) {
                earliest = z.winStart;
            }
        }
        
        return earliest;
    }

    public static void runGreedy(PriorityQueue<Zone> heap) {
        
        

        int totalSupplies = 0;
        double totalDistance = 0.0;
        double maxPossibleUtility = 0.0;
        
        for (Zone z : heap) {
            totalSupplies += z.supply;
            totalDistance += (z.distance * 2); // Round-trip distance
            maxPossibleUtility += z.utility;
        }
       
        double totalUtility = 0.0;
        //int zonesVisited = 0;
        
        LocalDateTime currentTime = getEarliestStartTime(heap);
        ArrayList<Zone> waitingRoom = new ArrayList<>();
        
        //int totalSuppliesDelivered = 0;
        //double totalDistanceDriven = 0.0;
        
        System.out.println("--- STARTING GREEDY ---");

        Truck truck = new Truck(totalSupplies, totalDistance);

        while (!heap.isEmpty()) {
            Zone currentZone = heap.poll(); 
            // Assumed constant speed. 1 km = 1 min (60 km/h)
            long driveMinutes = (long) currentZone.distance;
            LocalDateTime arrivalTime = currentTime.plusMinutes(driveMinutes);
            
            if (arrivalTime.isBefore(currentZone.winStart)) {
                waitingRoom.add(currentZone);
            } else {
                LocalDateTime returnTime = arrivalTime.plusMinutes(driveMinutes);
                
                double roundTrip = currentZone.distance * 2;
                boolean windowValid = !arrivalTime.isAfter(currentZone.winEnd); 
                
                if (truck.canMakeTrip(currentZone.supply, roundTrip) && windowValid) {
                    truck.deliver(currentZone.supply, roundTrip);
                    totalUtility += currentZone.utility;
                    currentTime = returnTime; 
                    //zonesVisited++;
                    //totalSuppliesDelivered += currentZone.supply;
                    //totalDistanceDriven += roundTrip;
                    
                    //System.out.println("Visited: " + currentZone.ID +  " | Delivered: " + currentZone.supply + " | Round-Trip: " + roundTrip + " km" +" | Return: " + currentTime);
                    
                    heap.addAll(waitingRoom);
                    waitingRoom.clear();
                }    
            }
            
            if (heap.isEmpty() && !waitingRoom.isEmpty()) {
                Zone nextToOpen = waitingRoom.get(0);
                for (Zone z : waitingRoom) {
                    if (z.winStart.isBefore(nextToOpen.winStart)) {
                        nextToOpen = z;
                    }
                }
                
                currentTime = nextToOpen.winStart.minusMinutes((long) nextToOpen.distance);
                heap.addAll(waitingRoom);
                waitingRoom.clear();
            }
        }
        
        double utilityScore = (totalUtility / maxPossibleUtility) * 100.0;
        

        System.out.println("--- RESULTS ---");
        //System.out.println("Visited: " + zonesVisited + " out of 120 zones");
        System.out.printf("Utility Score: %.2f / 100\n", utilityScore);
        //System.out.println("Total Supplies Delivered: " + totalSuppliesDelivered + " / " + totalSupplies);
        //System.out.printf("Total Distance Driven: %.2f km\n", totalDistanceDriven);  
        //System.out.println("Remaining supplies: " + truck.currentSupplies);
        //System.out.println("Remaining range: " + truck.remainingRange);

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
        
        // Sort zones by earliest start time
        list.sort((z1, z2) -> z1.winStart.compareTo(z2.winStart));
        
        LocalDateTime globalStartTime = list.get(0).winStart;
        
        
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
                            
                            // Travel time to this zone 
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