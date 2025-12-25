package day_11_12;

import static java.lang.Thread.sleep;

public class SimpleThread extends Thread{
    public SimpleThread(String name){
        super(name);
    }
    public void run(){
        for(int i=0;i<10;i++){
            System.out.println(getName()+"-"+i);
            try{
                sleep((int)(Math.random()*1000));
            }catch(InterruptedException e){
                System.out.println(e);
            }
            System.out.println(getName()+" finished");
        }
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        int i = 0;
        while (i<10){
            System.out.println(i);
            i++;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}

class MyThread2 extends Thread {
    public static int x = 5;
    public static synchronized void Trans(){
        if(x>0){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            x--;
        }
    }
    @Override
    public void run() {
        do{
            Trans();
            System.out.println(getName() + ": x= " + x);
        }while (x>0);
    }
}