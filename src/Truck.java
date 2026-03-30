public class Truck {
    int capacity;
    double totRange;
    int currentSupplies;
    double remainingRange;

    public Truck(int capacity, double totRange) {
        this.capacity = capacity;
        this.totRange = totRange;
        
        this.currentSupplies = capacity;
        this.remainingRange = totRange;
    }

    public boolean canMakeTrip(int supplyNeeded, double distanceNeeded) {
        boolean hasSupplies = this.currentSupplies >= supplyNeeded;
        boolean hasFuel = this.remainingRange >= distanceNeeded;
        return hasSupplies && hasFuel;
    }

    public void deliver(int supplyDelivered, double distanceDriven) {
        this.currentSupplies -= supplyDelivered;
        this.remainingRange -= distanceDriven;
    }
}
