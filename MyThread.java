import java.util.Arrays;

public class MyThread extends Thread {
    long[] numbersInRam;
    int part ;
    int length;

    public MyThread(long[] numbersInRam, int length, int part) {
        this.numbersInRam=numbersInRam;
        this.part = part;
        this.length = length;
    }
    @Override
    public void run() {
        Arrays.sort(numbersInRam, (part * length),(part + 1) * length);
    }
}