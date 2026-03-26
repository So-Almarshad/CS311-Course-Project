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



    public Zone(String ID,String population,String urgency, String distance, String supply, String winStart, String winEnd){
        this.ID = ID;
        this.population = Integer.parseInt(population);
        this.urgency = Double.parseDouble(urgency);
        this.distance = Double.parseDouble(distance);
        this.supply = Integer.parseInt(supply);
        this.winStart = LocalDateTime.parse(winStart);
        this.winEnd = LocalDateTime.parse(winEnd);
        this.utility = this.ComputeUtility();
    }

    public double ComputeUtility(){
        //return this.population*this.urgency;
        return Math.pow(this.population*this.urgency,2)/distance;
    }

    @Override
    public int compareTo(Zone obj){
        return (int)(this.utility-obj.utility);
    }
    
    @Override
    public String toString(){
        return "Zone ID:" + this.ID;
    }
}
