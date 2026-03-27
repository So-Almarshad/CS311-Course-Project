import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.*;
import java.util.ArrayList;





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

        int totalSupplies = 0;
        double totalDistance = 0.0;
        double maxPossibleUtility = 0.0;
        
        for (Zone z : heap) {
            totalSupplies += z.supply;
            totalDistance += (z.distance * 2); 
            maxPossibleUtility += z.utility;
        }
        
        int remainingCapacity = (int) (totalSupplies * 1);
        double remainingDistance = totalDistance * 1;
        double totalUtility = 0.0;
        int zonesVisited = 0;
        
        LocalDateTime currentTime = getEarliestStartTime(heap);
        
        ArrayList<Zone> waitingRoom = new ArrayList<>();
        
        int totalSuppliesDelivered = 0;
        double totalDistanceDriven = 0.0;
        
        System.out.println("--- STARTING GREEDY ---");

        

        while (!heap.isEmpty()) {
            // get highest utility
            Zone currentZone = heap.poll(); 
            
            long driveMinutes = (long) currentZone.distance;
            LocalDateTime arrivalTime = currentTime.plusMinutes(driveMinutes);
            
            if (arrivalTime.isBefore(currentZone.winStart)) {
                waitingRoom.add(currentZone);
            } else {
                // Gate is open. Check if it's feasible.
                LocalDateTime returnTime = arrivalTime.plusMinutes(driveMinutes);
                boolean hasCapacity = currentZone.supply <= remainingCapacity;
                boolean hasDistance = (currentZone.distance * 2) <= remainingDistance;
                boolean windowValid = !arrivalTime.isAfter(currentZone.winEnd); 
                
                if (hasCapacity && hasDistance && windowValid) {
                    // Visit the zone!
                    remainingCapacity -= currentZone.supply;
                    remainingDistance -= (currentZone.distance * 2);
                    totalUtility += currentZone.utility;
                    currentTime = returnTime; 
                    zonesVisited++;
                    totalSuppliesDelivered += currentZone.supply;
                    totalDistanceDriven += (currentZone.distance * 2);
                    
                    System.out.println("Visited: " + currentZone.ID + 
                                       " | Delivered: " + currentZone.supply + 
                                       " | Round-Trip: " + (currentZone.distance * 2) + " km" +
                                       " | Return: " + currentTime);
                    
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
        
        double roundedScore = Math.round(utilityScore * 100.0) / 100.0;
        double roundedDistance = Math.round(totalDistanceDriven * 100.0) / 100.0;

        System.out.println("\n--- RESULTS ---");
        System.out.println("Visited: " + zonesVisited + " out of 120 zones");
        System.out.println("Utility Score: " + roundedScore + " / 100");
        System.out.println("Total Supplies Delivered: " + totalSuppliesDelivered + " / " + totalSupplies);
        System.out.println("Total Distance Driven: " + roundedDistance + " km");    }
}
