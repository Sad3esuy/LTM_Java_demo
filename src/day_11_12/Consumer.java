package day_11_12;

public class Consumer extends Thread{
    private CubbyHole cubbyHole;
    private int consumerID;

    public Consumer(CubbyHole c, int id) {
        cubbyHole = c;
        consumerID = id;
    }
    @Override
    public void run() {
        int value = 0;
        for (int i = 0; i < 10; i++) {
            value =cubbyHole.get(consumerID);
        }
    }
}
