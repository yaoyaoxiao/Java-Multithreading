import java.util.*;
import java.io.*;

class WordCounter implements Runnable{
    private Scanner sc;
    private final Map<String, Integer> map;
    public WordCounter(String s, Map<String, Integer> map){
        this.map = map;
        try {
            File f = new File(s);
            sc = new Scanner(f);
        }catch (FileNotFoundException f){
            System.out.println("file not found");
        }
    }
    public void run(){
        sc.useDelimiter(",");

        synchronized (this) {
            while ( sc.hasNext()){
                String cur = sc.next();
                map.put(cur, map.getOrDefault(cur, 0) + 1);
            }
        }
    }
}
class ScheduledReport extends TimerTask{
    private final Map<String, Integer> map;
    public ScheduledReport(Map<String, Integer> map){
        this.map = map;
    }
    public void run(){ //should I use synchronized here too?
        for  (Map.Entry<String, Integer> e : map.entrySet()){
            System.out.println( e.getKey() + ": " + e.getValue());
        }
    }
}

class WordCountMain{

    public volatile Map<String, Integer> map;
    public static void main( String args[]) {
        WordCountMain wcm = new WordCountMain();
        wcm.map = new HashMap();
        Thread t1 = new Thread (new WordCounter("/Developer/Study/Java Multithreading Example/file1.txt", wcm.map));
        Thread t2 = new Thread (new WordCounter("/Developer/Study/Java Multithreading Example/file2.txt", wcm.map));

        Timer timer = new Timer();
        timer.schedule( new ScheduledReport(wcm.map), 0, 2000);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }

        System.out.println("All threads finished reading!");
    }
}