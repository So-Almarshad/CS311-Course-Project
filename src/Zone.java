public class Zone implements Comparable<Zone>{

    String ID;
    int population;
    double urgency;
    int distance;
    int supply; //supply required
    int winStart;// access window start
    int winEnd;// access window end
    double utility;

    public Zone(String ID,int population,double urgency, int distance, int supply, int winStart, int winEnd){
        this.ID = ID;
        this.population = population;
        this.urgency = urgency;
        this.distance = distance;
        this.supply = supply;
        this.winStart = winStart;
        this.winEnd = winEnd;
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
}
