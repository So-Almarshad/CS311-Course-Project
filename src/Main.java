import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;




public class Main {
    public static void main(String[] args) {
        PriorityQueue<Zone> heap = loadData();
        System.out.println(heap.poll().winStart.getDayOfMonth());
       
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
}
