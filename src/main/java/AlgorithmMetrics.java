public class AlgorithmMetrics {

    private long startTime;
    private long stopTime;

    // Метод для запуска таймера
    public void startTimer() {
        startTime = System.nanoTime(); // или System.currentTimeMillis()
    }

    // Метод для остановки таймера
    public void stopTimer() {
        stopTime = System.nanoTime(); // или System.currentTimeMillis()
    }

    // Метод для получения времени выполнения в наносекундах
    public long getElapsedTimeNanos() {
        return stopTime - startTime;
    }

    // Метод для получения времени выполнения в миллисекундах
    public long getElapsedTimeMillis() {
        return (stopTime - startTime) / 1_000_000;
    }
}
