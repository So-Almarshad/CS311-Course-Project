import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;




public class Main {
    public static void main(String[] args) {
        ArrayList<Zone> list = listLoadData();
        System.out.println(DynamicProgramming(list));
    }
    
    public static PriorityQueue<Zone> loadData() {
        try {
            PriorityQueue<Zone> pq = new PriorityQueue<>();
            Scanner input = new Scanner(new File("problem1.csv"));
            input.next();
            while (input.hasNext()){
                String[] line = input.next().split(",");
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
        /*
         * First get the dimensions of our benefit 3D array,
         * They are going to be: total number of zones,maximum distance per zone, and maximum supply per zone       
         */
        int numZones = list.size();
        int maxDistance = 0;
        int maxSupply = 0;
        for (Zone zone : list) {
            if (zone.distance>maxDistance)
                maxDistance = (int)Math.ceil(zone.distance);
            if (zone.supply>maxSupply)
                maxSupply=(int)Math.ceil(zone.supply);
        }
        double[][][] benefit = new double[numZones][maxDistance][maxSupply];

        for (int zone = 0 ; zone <numZones; zone++) {
            for (int distance = 0; distance < maxDistance; distance++) {
                for (int supply = 0; supply < maxSupply; supply++) {
                    int currentSupply = (int) Math.ceil(list.get(zone).supply);
                    int currentDistance = (int) Math.ceil(list.get(zone).distance);
                    double currentUtility = list.get(zone).utility;

                    if (zone==0){// fill first column
                            if (currentDistance <= distance & currentSupply <= supply){
                                benefit[zone][distance][supply] = currentUtility;
                            }
                    }
                    else if (currentSupply<=supply & currentDistance<=distance){
                            // check if current zone is worth visiting
                            if (currentUtility + benefit[zone-1][distance-currentDistance][supply-currentSupply] > benefit[zone-1][distance][supply]) {
                                benefit[zone][distance][supply] = currentUtility + benefit[zone-1][distance-currentDistance][supply-currentSupply];
                            } else
                                benefit[zone][distance][supply] = benefit[zone-1][distance][supply];
                    } else // zone cannot be visted
                        benefit[zone][distance][supply] = benefit[zone-1][distance][supply];   
                }
            }
        }
        return benefit[numZones-1][maxDistance-1][maxSupply-1];
    }
}
