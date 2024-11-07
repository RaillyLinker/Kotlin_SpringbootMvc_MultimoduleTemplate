// Counter.java
public class Counter {
    public static long count() {
        long result = 0;
        long endTime = System.currentTimeMillis() + 2000; // 2초 동안 반복
        while (System.currentTimeMillis() < endTime) {
            result++;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(count());
    }
}