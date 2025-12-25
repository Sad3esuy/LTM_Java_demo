package day_11_12;

public class MyThreadEx2 {
    public static void main(String[] args) {
        MyThread2 T[] = new MyThread2[3];
        for(int i=0;i<T.length;i++) {
            T[i] = new MyThread2();
            T[i].start();
        }
    }
}
