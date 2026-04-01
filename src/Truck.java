public class Truck {
    int capacity;
    double totRange; // Changed to double for decimal precision!

    // Trackers for the truck's current state during the route
    int currentSupplies;
    double remainingRange;

    public Truck(int capacity, double totRange) {
        this.capacity = capacity;
        this.totRange = totRange;
        
        // When the truck is created, it starts fully loaded and fully fueled
        this.currentSupplies = capacity;
        this.remainingRange = totRange;
    }

    // Checks if the truck has enough supplies and range for this specific zone
    public boolean canMakeTrip(int supplyNeeded, double distanceNeeded) {
        boolean hasSupplies = this.currentSupplies >= supplyNeeded;
        boolean hasFuel = this.remainingRange >= distanceNeeded;
        return hasSupplies && hasFuel;
    }

    // Updates the truck's inventory and odometer after a delivery
    public void deliver(int supplyDelivered, double distanceDriven) {
        this.currentSupplies -= supplyDelivered;
        this.remainingRange -= distanceDriven;
    }
}
