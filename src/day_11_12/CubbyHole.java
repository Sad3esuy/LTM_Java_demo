package day_11_12;

public class CubbyHole {
    private int contents;
    private boolean avalable = false;
    public synchronized void put(int producerID, int value){
        if(avalable){
            try{
                wait();
            }catch (InterruptedException e){
                System.out.println(e);
            }
        }
        contents = value;
        System.out.println("Producer #" + producerID + " put: " + contents);
        avalable = true;
        notifyAll();
    }
    public synchronized int get(int consumerID){
        if(!avalable){
            try{
                wait();
            }catch (InterruptedException e){
                System.out.println(e);
            }
        }
        System.out.println("Consumer #" + consumerID + " got: " + contents);
        avalable = false;
        notifyAll();
        return contents;
    }
}
