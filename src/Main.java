import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;




public class Main {
    public static void main(String[] args) {
        PriorityQueue<Zone> heap = loadData();
        
        if (heap != null && !heap.isEmpty()) {
            runGreedy(heap);
        }
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
        
        // loop through all zones to find the earliest time
        for (Zone z : heap) {
            if (earliest == null || z.winStart.isBefore(earliest)) {
                earliest = z.winStart;
            }
        }
        
        return earliest;
    }

    public static void runGreedy(PriorityQueue<Zone> heap) {

        int totalSupply = 0;
        double totalDistance = 0.0;
        
        for (Zone z : heap) {
            totalSupply += z.supply;
            totalDistance += (z.distance * 2); 
        }
        
        int remainingCapacity = (int) (totalSupply * 1);
        double remainingDistance = totalDistance * 1;
        double totalUtility = 0.0;
        int zonesVisited = 0;
        
        // starting clock (update to match first window start)
        LocalDateTime currentTime = getEarliestStartTime(heap);
        
        System.out.println("--- STARTING GREEDY ---");

        while (!heap.isEmpty()) {
            Zone currentZone = heap.poll(); // get highest utility
            
            // time math (1 km = 1 min)
            long driveMinutes = (long) currentZone.distance;
            LocalDateTime arrivalTime = currentTime.plusMinutes(driveMinutes);
            
            // wait if arrived early
            LocalDateTime startServiceTime = arrivalTime;
            if (arrivalTime.isBefore(currentZone.winStart)) {
                startServiceTime = currentZone.winStart;
            }
            
            // return to depot
            LocalDateTime returnTime = startServiceTime.plusMinutes(driveMinutes);

            // feasibility checks
            boolean hasCapacity = currentZone.supply <= remainingCapacity;
            boolean hasDistance = (currentZone.distance * 2) <= remainingDistance;
            boolean windowValid = !arrivalTime.isAfter(currentZone.winEnd); 
            
            if (hasCapacity && hasDistance && windowValid) {
                // visit zone
                remainingCapacity -= currentZone.supply;
                remainingDistance -= (currentZone.distance * 2);
                totalUtility += currentZone.utility;
                currentTime = returnTime; 
                zonesVisited++;
                
                // basic println formatting
                System.out.println("Visited: " + currentZone.ID + " | Utility: " + currentZone.utility + " | Return: " + currentTime);
            }
        }
        
        System.out.println("\n--- RESULTS ---");
        System.out.println("Visited: " + zonesVisited);
        System.out.println("Total Utility: " + totalUtility);
        System.out.println("Remaining Supplies: " + remainingCapacity);
        System.out.println("Remaining Distance: " + remainingDistance);
    }
}
