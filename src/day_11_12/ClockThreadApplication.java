package day_11_12;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClockThreadApplication implements Runnable{
    private Thread clookThread = null;

    public ClockThreadApplication(){
        clookThread = new Thread(this, "ClockThread");
        clookThread.start();
    }

    private String getTime(){
        Calendar cl = Calendar.getInstance();
        Date date = cl.getTime();
        DateFormat dateFormatatter = DateFormat.getDateTimeInstance();
        return dateFormatatter.format(date);
    }

    @Override
    public void run() {
        Thread myThread = Thread.currentThread();
        while (myThread == clookThread) {
            System.out.println(getTime());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
    public static void main(String[] args) {
        ClockThreadApplication clock = new ClockThreadApplication();
    }
}
