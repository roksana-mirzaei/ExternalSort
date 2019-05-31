
import java.io.*;

public class Tournament {
    // Address of the final output .
    private String answerPath;

    // Temporary address of chunks -- used for merging the buckets
    //    and build the final sorted file .
    private String chunksPath;

    // Used for reading from chunks in tournament sort .
    private BufferedReader[] chunks;

    // Used for writing in the final file
    private static BufferedWriter answerWriter;

    // The tournament array. used for sorting .
    private int[] tourney;

    // Number of chunk we use for sorting. given from main class.
    private static int numberOfChunk;

    // Used in tournament sort . chunks numbers are key and long numbers are value .
    private static long[] map;


    /**
     * @param chunkNums
     * @param chunksAddress
     * @param answerAddress
     */
    public Tournament(int chunkNums, String chunksAddress, String answerAddress) {
        numberOfChunk = chunkNums;
        tourney = new int[getTourneyLength(numberOfChunk)];
        chunks = new BufferedReader[numberOfChunk];
        map = new long[numberOfChunk];
        answerPath = answerAddress;
        chunksPath = chunksAddress;
        try {
            answerWriter = new BufferedWriter(new FileWriter(answerPath));
            for (int i = 0; i < chunkNums; i++) {
                chunks[i] = new BufferedReader(new FileReader(chunksPath + i));
            }
        } catch (IOException e) {
            System.out.println("Error : " + e.toString());
            try {
                answerWriter.close();
                closeChunks();
                removeChunks();
            } catch (IOException ex) {
                System.out.println("Error : " + ex.toString());
            }
        }
    }

    /**
     * This method's work is sorting the file with long numbers with "allNums" row .
     *
     * @param allNums
     */
    public void sort(long allNums) {
        // First getting data from chunks ;
        filltourney(tourney);
        // First filling the map
        fillHashMap(map);

        // This the current player that play with current + 1 .
        int current = 0;
        // Place of the winner between current and current+1
        int winnerPlace = numberOfChunk;

        while (winnerPlace < tourney.length) {
            tourney[winnerPlace++] = min(tourney[current], tourney[current + 1]);
            current += 2;
        }
        int winnerKey = tourney[tourney.length - 1];
        write(map[winnerKey]);

        try {
            map[winnerKey] = Long.parseLong(chunks[winnerKey].readLine());
        } catch (IOException e) {
            System.out.println("Error : " + e.toString());
        }

        current = winnerKey;
        for (long k = 0; k < allNums - 1; k++) {
            winnerPlace = 0;
            while (winnerPlace < tourney.length - 1) {
                if ((current & 1) == 0) {
                    winnerPlace = (numberOfChunk + current - (current / 2));
                    if (winnerPlace < tourney.length)
                        tourney[winnerPlace] = min(tourney[current], tourney[current + 1]);
                } else {
                    winnerPlace = (numberOfChunk + (current - 1) - ((current - 1) / 2));
                    if (winnerPlace < tourney.length)
                        tourney[winnerPlace] = min(tourney[current], tourney[current - 1]);
                }
                current = winnerPlace;
            }

            winnerKey = tourney[tourney.length - 1];
            current = winnerKey;

            write(map[winnerKey]);
            try {
                String str = chunks[winnerKey].readLine();
                if (str != null)
                    map[winnerKey] = Long.parseLong(str);
                else map[winnerKey] = Long.MAX_VALUE;
            } catch (IOException e) {
                System.out.println("Error : " + e.toString());
            }
        }

        try {
            answerWriter.close();
            closeChunks();
            removeChunks();
        } catch (IOException e) {
            System.out.println("Error : " + e.toString());
        }
    }

    public static void sort(long[] chunk, int length, String chunkAddress){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chunkAddress));
            int[] pointers = new int[chunk.length / length];
            for (int i = 1; i < pointers.length; i++)
                pointers[i] = pointers[i - 1] + length;
            int min = 0;
            for (int i = 0; i < chunk.length; i++) {
                for (int j = 0; j < pointers.length; j++) {
                    if ( pointers[j] != -1 && pointers[min] == -1 )
                        min = j;
                    else if ( pointers[j] != -1 && chunk[pointers[j]] < chunk[pointers[min]])
                        min = j;
                }
                bufferedWriter.write(chunk[pointers[min]] + "\n");
                pointers[min]++;
                if (pointers[min] / length > min)
                    pointers[min] = -1;
            }
            bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Find min number between value of key1 and value of key2
     *
     * @param key1
     * @param key2
     *
     * @return
     */
    private int min(int key1, int key2) {
        return map[key1] <= map[key2] ? key1 : key2;
    }
    /**
     * first filling tourney array to use.
     *
     * @param tourney
     */
    private void filltourney(int[] tourney) {
        n: for (int i = 0; i < numberOfChunk; i++)
            tourney[i] = i;
    }

    /**
     * Filling the map for first time.
     * used just ones.
     * <p>
     * Size of the map will be number of chunks
     *
     * @param map
     */
    private void fillHashMap(long[] map) {
        try {
            for (int i = 0; i < numberOfChunk; i++)
                map[i] = Long.parseLong(chunks[i].readLine());
        } catch (IOException e) {
            System.out.println("Error : " + e.toString());
        }
    }

    /**
     * getting the length of tourney array.
     *
     * @param n
     * @return
     */
    private static int getTourneyLength(int n) {
        if (n==1)
            return 2;
        int count = 1;
        for (int i = 1; i < n; i++)
            count += 2;
        return count;
    }

    /**
     * Write the data to answer file.
     *
     * @param data
     */
    private void write(long data) {
        try {
            StringBuilder str = new StringBuilder();
            str.append(data);
            str.append("\n");
            answerWriter.write(str.toString());
        } catch (IOException e) {
            System.out.println("Error : " + e.toString());
        }
    }

    private void closeChunks() throws IOException {
        for (int i = 0; i < numberOfChunk; i++) {
            chunks[i].close();
        }
    }

    private void removeChunks() {
        for (int i = 0; i <= numberOfChunk; i++) {
            File file = new File(chunksPath + i);
            file.delete();
        }
    }
}