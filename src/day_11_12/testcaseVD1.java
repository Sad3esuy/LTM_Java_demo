package day_11_12;

public class testcaseVD1 {
    public static void main(String[] args) {
        SimpleThread t1=new SimpleThread("Thread 1");
        SimpleThread t2=new SimpleThread("Thread 2");
//        SimpleThread t3=new SimpleThread("Thread 3");
        t1.start();
        t2.start();
//        t3.start();
    }
}


