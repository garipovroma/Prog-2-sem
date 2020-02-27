package queue;

import java.util.ArrayDeque;
import java.util.Queue;

public class MyArrayQueueTests {
    public static int testCount = 1;
    public static int[] sizes = {1, 2, 3, 4, 5, 100, 199, 228, 1000, 10000, 900000, 9002400};
    public static int[] getMas(int n) {
        int x = 228;
        int p = 1337;
        int mod = (int) (1e9 + 7);
        int[] mas = new int[n];
        mas[0] = 1;
        for (int i = 1; i < n; i++) {
            mas[i] = (mas[i - 1] * 228 + 1337) % mod;
        }
        return mas;
    }
    public static void main(String[] args) {
        testArrayQueueTest();
        testArrayQueueModuleTest();
        testArrayQueueADTTest();
    }
    public static int[] getArrayQueueSeq(ArrayQueue q, int curSize) {
        int[] res = new int[curSize];
        for (int i = 0; i < curSize; i++) {
            res[i] = (int) q.dequeue();
        }
        return res;
    }
    public static int[] getArrayQueueModuleSeq(ArrayQueueModule q, int curSize) {
        int[] res = new int[curSize];
        for (int i = 0; i < curSize; i++) {
            res[i] = (int) q.dequeue();
        }
        return res;
    }
    public static int[] getArrayQueueADTSeq(ArrayQueueADT q, int curSize) {
        int[] res = new int[curSize];
        for (int i = 0; i < curSize; i++) {
            res[i] = (int) q.dequeue(q);
        }
        return res;
    }

    public static void fillArrayQueue(int[] curMas, int curSize, ArrayQueue q) {
        for (int j = 0; j < curSize; j++) {
            q.enqueue(curMas[j]);
        }
    }
    private static void fillArrayQueueModule(int[] curMas, int curSize, ArrayQueueModule q) {
        for (int i = 0; i < curSize; i++) {
            q.enqueue(curMas[i]);
        }
    }
    private static void fillArrayQueueADT(int[] curMas, int curSize, ArrayQueueADT q) {
        for (int i = 0; i < curSize; i++) {
            q.enqueue(q, curMas[i]);
        }
    }
    public static void fillRealQueue(int[] curMas, int curSize, Queue <Integer> q) {
        for (int j = 0; j < curSize; j++) {
            q.add(curMas[j]);
        }
    }
    public static int[] getRealQueueSeq(Queue <Integer> q, int curSize) {
        int[] res = new int[curSize];
        for (int i = 0; i < curSize; i++) {
            res[i] = q.remove();
        }
        return res;
    }

    public static void testArrayQueueTest() {
        System.out.println("------ArrayQueueTest------");
        for (int i = 0; i < sizes.length; i++) {
            int curSize = sizes[i];
            ArrayQueue q = new ArrayQueue();
            Queue <Integer> realQueue = new ArrayDeque<>();
            int[] curMas = getMas(curSize);
            fillArrayQueue(curMas, curSize, q);
            fillRealQueue(curMas, curSize, realQueue);
            int[] expected = getRealQueueSeq(realQueue, curSize);
            int[] found = getArrayQueueSeq(q, curSize);
            printResult(expected, found, curMas);
        }
    }

    public static void testArrayQueueModuleTest() {
        System.out.println("------ArrayQueueModuleTest------");
        for (int i = 0; i < sizes.length; i++) {
            int curSize = sizes[i];
            ArrayQueueModule q = new ArrayQueueModule();
            Queue <Integer> realQueue = new ArrayDeque<>();
            int[] curMas = getMas(curSize);
            fillArrayQueueModule(curMas, curSize, q);
            fillRealQueue(curMas, curSize, realQueue);
            int[] expected = getRealQueueSeq(realQueue, curSize);
            int[] found = getArrayQueueModuleSeq(q, curSize);
            printResult(expected, found, curMas);
        }
    }
    public static void testArrayQueueADTTest() {
        System.out.println("------ArrayQueueADTTest------");
        for (int i = 0; i < sizes.length; i++) {
            int curSize = sizes[i];
            ArrayQueueADT q = new ArrayQueueADT();
            Queue <Integer> realQueue = new ArrayDeque<>();
            int[] curMas = getMas(curSize);
            fillArrayQueueADT(curMas, curSize, q);
            fillRealQueue(curMas, curSize, realQueue);
            int[] expected = getRealQueueSeq(realQueue, curSize);
            int[] found = getArrayQueueADTSeq(q, curSize);
            printResult(expected, found, curMas);
        }
    }
    public static void printResult(int[] expected, int found[], int curMas[]) {
        if (check(expected, found)) {
            System.out.println("test # " + testCount + " : OK, queue_size = " + curMas.length);
            testCount++;
        } else {
            System.out.println("test # " + testCount +
                    " : WA, expected : q[" + errorPos + "] = " +
                    expected[errorPos] + ", found : q[" + errorPos + "] = " + found[errorPos]);

        }
    }
    public static int errorPos = -1;

    private static boolean check(int[] expected, int[] found) {
        errorPos = -1;
        int n = expected.length;
        int m = found.length;
        if (n != m) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (expected[i] != found[i]) {
                errorPos = i;
                return false;
            }
        }
        return true;
    }
}
