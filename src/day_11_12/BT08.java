package day_11_12;



public class BT08 {
    public static void main(String[] args) {
        CubbyHole cubbyHole = new CubbyHole();
        Producer p = new Producer(cubbyHole, 1);
        Consumer c = new Consumer(cubbyHole, 1);
        p.start();
        c.start();
    }
}
