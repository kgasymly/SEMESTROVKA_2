public class AlgorithmMetrics {

    private long startTime;
    private long stopTime;

    public void startTimer() {
        startTime = System.nanoTime(); // или System.currentTimeMillis()
    }

    public void stopTimer() {
        stopTime = System.nanoTime(); // или System.currentTimeMillis()
    }

    public long getTimeNano() {
        return stopTime - startTime;
    }

    public long getTimeMillis() {
        return (stopTime - startTime) / 1000000;
    }
}
