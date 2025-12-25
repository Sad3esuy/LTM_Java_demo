package day_11_12;

public class MyRunnableEx1 {
    public static void main(String[] args) {
        Runnable r1 = new MyRunnable();
        Runnable r2 = new MyRunnable();
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
    }
}
