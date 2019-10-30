package Practice;

import java.util.*;
import java.io.*;

class WordCounter implements Runnable{
    private Scanner sc;
    private final Map<String, Integer> map;
    private Object mapLock;
    public WordCounter(String s, Map<String, Integer> map , Object lock){
        this.map = map;
        this.mapLock = lock;
        try {
            File f = new File(s);
            sc = new Scanner(f);
        }catch (FileNotFoundException f){
            System.out.println("file not found");
        }
    }
    public void run(){
        sc.useDelimiter(",");

        synchronized (mapLock) {
            while ( sc.hasNext()){
                String cur = sc.next();
                map.put(cur, map.getOrDefault(cur, 0) + 1);
            }
        }
    }
}
class ScheduledReport extends TimerTask{
    private Map<String, Integer> map;
    private Object mapLock;
    public ScheduledReport(Map<String, Integer> map, Object lock){
        this.map = map;
        this.mapLock = lock;
    }
    public void run(){ //should I use synchronized here too? Yes
        synchronized (mapLock){
            for  (Map.Entry<String, Integer> e : map.entrySet()){
                System.out.println( e.getKey() + ": " + e.getValue());
            }
        }
    }
}

class WordCountMain{
    public static void main( String args[]) {
        // WordCountMain wcm = new WordCountMain();
        Map<String, Integer> map = new HashMap();
        Object lock = new Object();

        Thread t1 = new Thread (new WordCounter("/Developer/project/file1.txt", map, lock));
        Thread t2 = new Thread (new WordCounter("/Developer/project/file2.txt", map, lock));

        Timer timer = new Timer();
        timer.schedule( new ScheduledReport(map, lock), 0, 2000);

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
