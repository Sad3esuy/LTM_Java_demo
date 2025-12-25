package day_11_12;

public class Producer extends Thread{
    private CubbyHole cubbyhole;
    private int number;
    public Producer(CubbyHole cubbyhole, int number){
        this.cubbyhole = cubbyhole;
        this.number = number;
    }
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            cubbyhole.put(number, i);
            System.out.println("Producer " + this.number + " put: " + i);
            try {
                sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
