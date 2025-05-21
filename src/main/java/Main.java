import java.util.List;
public class Main{
public static void main(String[] args) {
    AlgorithmMetrics metrics = new AlgorithmMetrics();
    metrics.startTimer();
    BPlusTree<Integer> tree = new BPlusTree<>(3);

    // Insert elements
    tree.insert(10);
    tree.insert(20);
    tree.insert(5);
    tree.insert(15);
    tree.insert(25);
    tree.insert(30);

    System.out.println("B+ дерево после вставок:");
    tree.printTree();

    // Search for a key
    int searchKey = 15;
    System.out.println("\nПоиск ключа " + searchKey + ": " +
            (tree.search(searchKey) ? "Найдено" : "Не найдено"));

    // Perform a range query
    int lower = 10, upper = 25;
    List<Integer> rangeResult = tree.rangeQuery(lower, upper);
    System.out.println("\nЗапрос диапазона [" + lower + ", " + upper + "]: " + rangeResult);

    // Remove a key
    int removeKey = 20;
    tree.remove(removeKey);
    System.out.println("\nB+ Дерево после удаления " + removeKey + ":");
    tree.printTree();

    metrics.stopTimer();
    System.out.println();
    System.out.println("Время выполнения (нс): " + metrics.getElapsedTimeNanos());
    System.out.println("Время выполнения (мс): " + metrics.getElapsedTimeMillis());
    }
}