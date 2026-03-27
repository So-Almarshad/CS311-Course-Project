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
            long startTime = System.currentTimeMillis();
            
            runGreedy(heap); 
            
            long endTime = System.currentTimeMillis(); 
            
            System.out.println("Execution Time: " + (endTime - startTime) + " ms");        }
   
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
        int zonesVisited = 0;
        
        LocalDateTime currentTime = getEarliestStartTime(heap);
        ArrayList<Zone> waitingRoom = new ArrayList<>();
        
        int totalSuppliesDelivered = 0;
        double totalDistanceDriven = 0.0;
        
        System.out.println("--- STARTING GREEDY ---");

        Truck truck = new Truck(totalSupplies, totalDistance);

        while (!heap.isEmpty()) {
            Zone currentZone = heap.poll(); 
            
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
                    zonesVisited++;
                    totalSuppliesDelivered += currentZone.supply;
                    totalDistanceDriven += roundTrip;
                    
                    System.out.println("Visited: " + currentZone.ID + 
                                       " | Delivered: " + currentZone.supply + 
                                       " | Round-Trip: " + roundTrip + " km" +
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
        

        System.out.println("\n--- RESULTS ---");
        System.out.println("Visited: " + zonesVisited + " out of 120 zones");
        System.out.printf("Utility Score: %.2f / 100\n", utilityScore);
        System.out.println("Total Supplies Delivered: " + totalSuppliesDelivered + " / " + totalSupplies);
        System.out.printf("Total Distance Driven: %.2f km\n", totalDistanceDriven);  
        System.out.println("Remaining supplies: " + truck.currentSupplies);
        System.out.println("Remaining range: " + truck.remainingRange);

    }
    
    
}
