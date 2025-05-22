import java.util.List;



/*
 Обычный main, который вызывает наши методы,
  задача для нашей структуры находится в файле TaskMain
 */

 public class Main {
 public static void main(String[] args) {

     AlgorithmMetrics metrics = new AlgorithmMetrics();
     AlgorithmMetrics metricsInsert = new AlgorithmMetrics();
     AlgorithmMetrics metricsSearchKey = new AlgorithmMetrics();
     AlgorithmMetrics metricsRangeResult = new AlgorithmMetrics();
     AlgorithmMetrics metricsRemoveKey = new AlgorithmMetrics();

     metrics.startTimer();

     BPlusTree<Integer> tree = new BPlusTree<>(3);



     // Добавление элементов
     metricsInsert.startTimer();

     tree.insert(10);
     tree.insert(20);
     tree.insert(5);
     tree.insert(15);
     tree.insert(25);
     tree.insert(30);

     metricsInsert.stopTimer();

     System.out.println("B+ дерево после добавлений:");
     tree.printTree();


     // Поиск ключа
     int searchKey = 15;

     metricsSearchKey.startTimer();

     System.out.println("\nПоиск ключа " + searchKey + ": " +
             (tree.search(searchKey) ? "Найдено" : "Не найдено"));

     metricsSearchKey.stopTimer();


     // Выполнение запроса диапазона
     int lower = 10, upper = 25;

     metricsRangeResult.startTimer();

     List<Integer> rangeResult = tree.rangeQuery(lower, upper);

     metricsRangeResult.stopTimer();

     System.out.println("\nЗапрос диапазона [" + lower + ", " + upper + "]: " + rangeResult);


     // Удаление ключа
     int removeKey = 20;

     metricsRemoveKey.startTimer();

     tree.remove(removeKey);
     metricsRemoveKey.stopTimer();


     System.out.println("\nB+ Дерево после удаления " + removeKey + ":");
     tree.printTree();

     metrics.stopTimer();
     System.out.println();

     System.out.println("Время выполнения добавлениях всех элементов в дерево (нс): " + metricsInsert.getTimeNano() + "\n");

     System.out.println("Время выполнения поиска ключа (нс): " + metricsSearchKey.getTimeNano() + "\n");

     System.out.println("Время выполнения запроса диапазона (нс): " + metricsRangeResult.getTimeNano() + "\n");

     System.out.println("Время выполнения удаления элемента из дерева (нс): " + metricsRemoveKey.getTimeNano() + "\n");

     System.out.println("Время выполнения всей программы (нс): " + metrics.getTimeNano());
     System.out.println("Время выполнения всей программы (мс): " + metrics.getTimeMillis());
     }


 }