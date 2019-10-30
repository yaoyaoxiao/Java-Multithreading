package Practice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class WordCounter1 implements Runnable{
    private Scanner sc;
    private final Map<String, Integer> map;
    private ReentrantLock mapLock;
    public WordCounter1(String s, Map<String, Integer> map , ReentrantLock lock){
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
        mapLock.lock();

        try{
            while (sc.hasNext()){
                String cur = sc.next();
                map.put(cur, map.getOrDefault(cur, 0) + 1);
            }
        }finally {
            mapLock.unlock();
        }
    }
}
class ScheduledReport1 extends TimerTask {
    private Map<String, Integer> map;
    private ReentrantLock mapLock;
    public ScheduledReport1(Map<String, Integer> map, ReentrantLock lock){
        this.map = map;
        this.mapLock = lock;
    }
    public void run(){ //should I use synchronized here too? Yes
        mapLock.lock();
        try {
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                System.out.println(e.getKey() + ": " + e.getValue());
            }
        }
        finally {
            mapLock.unlock();
        }
    }
}

class WordCountReentrantLock{
    public static void main( String args[]) {
        Map<String, Integer> map = new HashMap();
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread (new WordCounter1("/Developer/project/file1.txt", map, lock));
        Thread t2 = new Thread (new WordCounter1("/Developer/project/file2.txt", map, lock));

        Timer timer = new Timer();
        timer.schedule( new ScheduledReport1(map, lock), 0, 2000);

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