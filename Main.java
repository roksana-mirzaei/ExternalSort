import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static String randomeNumbersFile;
    private static String sortedNumbersFile;
    private static BufferedReader input;

    public static void main(String[] args) {

        while (settingUp()) {

            try {
                System.out.println("-->> Sorting Started <<--");
                long start = System.currentTimeMillis();

                long fileSize = 0;
                int numOfNumbersInRam = 100_000_000;
                input = new BufferedReader(new FileReader(randomeNumbersFile));
                int numOfChunks = 0;

                long[] numbersInRam = null;
                int j;
                while (true) {
                    j = 0;
                    try {
                        numbersInRam = new long[numOfNumbersInRam];
                        for (; j < numbersInRam.length; j++) {
                            numbersInRam[j] = Long.parseLong(input.readLine());
                            fileSize++;
                        }
//                        thread(numbersInRam, numOfChunks++);
                        Arrays.parallelSort(numbersInRam);
                        writeChunk(numbersInRam, "Chunk" + numOfChunks++);
                    } catch (NumberFormatException ee) {
                        if (j > 0) {
                            long[] numbers = new long[j];
                            System.arraycopy(numbersInRam, 0, numbers, 0, j);
                            Arrays.parallelSort(numbers);
                            try {
                                writeChunk(numbers, "Chunk" + numOfChunks++);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;
                    } catch (IOException e) {
                        System.out.println("Error : " + e.toString());
                    }
                    numbersInRam = null;
                    System.gc();
                }
                long pre = System.currentTimeMillis();
                System.out.println("time1 : " + (pre - start));

                System.gc();
                tournamentSort(numOfChunks, fileSize, sortedNumbersFile);

                long end = System.currentTimeMillis();
                System.out.println("Sorting successfully completed.");
                System.out.println("Operation time : " + (end - start));

                //testTheAnswer(new File(sortedNumbersFile), fileSize);

            } catch (IOException e) {
                System.out.println("Error : " + e.toString());
            }
        }
    }

    private static void testTheAnswer(File file, long size){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            long past = Long.parseLong(bufferedReader.readLine());

            for (int i=1; i<size; i++){
                long now = Long.parseLong(bufferedReader.readLine());
                if (now<past){
                    System.out.println("ey baba dar " + i);
                    System.out.println(now + "  ----   " + past);
                }
                past = now;
            }

            System.out.println("eyyyyvaaaaaaaaaaaaal");
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean settingUp() {
        System.out.print("Give me file path, please:  (Write 'end' to close the program) \nPath:");
        Scanner input = new Scanner(System.in);
        String str = input.nextLine();
        if (str.equals("end") || str.equals("End"))
            return false;
        randomeNumbersFile = str;
        sortedNumbersFile = randomeNumbersFile + "sorted.txt";
        return true;
    }

    private static void thread(long[] numbersInRam, int i) {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        try {
            MyThread[] myThreads = new MyThread[cpuCores];
            for (int k = 0; k < cpuCores; k++) {
                myThreads[k] = new MyThread(numbersInRam, numbersInRam.length/cpuCores, k);
                myThreads[k].start();
            }
            for (int k = 0; k < cpuCores; k++)
                myThreads[k].join();
            Tournament.sort(numbersInRam, numbersInRam.length/cpuCores, System.getProperty("user.dir") + "\\" + "Chunk" + i);

        } catch (Exception e) {
            System.out.println("Error : " + e.toString());
        }

    }

    /**
     * sort the file .
     *
     * @param numOfChunks
     * @param fileSize
     */
    private static void tournamentSort(int numOfChunks, long fileSize, String answerAddress) {
        Tournament tournament = new Tournament(numOfChunks, System.getProperty("user.dir") + "\\Chunk", answerAddress);
        tournament.sort(fileSize);
    }

    private static void writeChunk(long[] numbersInRam, String name) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\" + name));
        for (long aNumbersInRam : numbersInRam)
            bufferedWriter.write(String.valueOf(aNumbersInRam) + "\n");
        bufferedWriter.close();
    }
}