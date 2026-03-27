import java.time.*;

public class Zone implements Comparable<Zone>{

    String ID;
    int population;
    double urgency;
    double distance;
    int supply; //supply required
    LocalDateTime winStart;// access window start
    LocalDateTime winEnd;// access window end
    double utility;



    public Zone(String ID, String population, String urgency, String distance, String supply, String winStart, String winEnd) {
        this.ID = ID;
        this.population = Integer.parseInt(population);
        this.urgency = Double.parseDouble(urgency);
        this.distance = Double.parseDouble(distance);
        this.supply = Integer.parseInt(supply);
        this.winStart = LocalDateTime.parse(winStart);
        this.winEnd = LocalDateTime.parse(winEnd);
        this.utility = this.ComputeUtility();
    }

    public double ComputeUtility() {
        double C = 12452.0;
        double D = 59542.7;
        
        // Cost percentage based on round-trip distance
        double costPercentage = (this.supply / C) + (23*(this.distance * 2.0) / D);
        
        // Resource-Consumption Hybrid Score
        return (this.population * this.urgency) / costPercentage;
    }

    @Override
    public int compareTo(Zone obj) {
        // FIX: Use Double.compare and reverse the order (obj vs this) 
        // to make the PriorityQueue a Max-Heap
        return Double.compare(obj.utility, this.utility);
    }
    
    @Override
    public String toString(){
        return "Zone ID:" + this.ID;
    }
}
