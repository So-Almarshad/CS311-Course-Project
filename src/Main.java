import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.*;





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

        // Create a temporary waiting room for zones that aren't open yet
        ArrayList<Zone> waitingRoom = new ArrayList<>();
        
        System.out.println("--- STARTING GREEDY ---");

        

        while (!heap.isEmpty()) {
            Zone currentZone = heap.poll(); // get highest utility
            
            long driveMinutes = (long) currentZone.distance;
            LocalDateTime arrivalTime = currentTime.plusMinutes(driveMinutes);
             
            if (arrivalTime.isBefore(currentZone.winStart)) {
                waitingRoom.add(currentZone); // Put it in the waiting room
            } else {
                // The gate is open! Let's check if it's feasible.
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
                    
                    System.out.println("Visited: " + currentZone.ID + " | Return: " + currentTime);
                    
                    // Since time moved forward, zones in the waiting room might be open now!
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
        
        System.out.println("\n--- RESULTS ---");
        System.out.println("Visited: " + zonesVisited);
        System.out.println("Total Utility: " + totalUtility);
        System.out.println("Remaining Supplies: " + remainingCapacity);
        System.out.println("Remaining Distance: " + remainingDistance);
    }
}
