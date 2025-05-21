import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPlusTree<T extends Comparable<T>> {

    private class Node {
        boolean isLeaf;
        List<T> keys;         // Ключи узла (разделители для внутренних узлов)
        List<Node> children;  // Дочерние узлы (только для внутренних узлов)
        Node next;            // Ссылка на следующий лист (только для листовых узлов)
        List<T> values;       // Значения (только для листовых узлов)

        Node(boolean leaf) {
            this.isLeaf = leaf;
            this.keys = new ArrayList<>();
            if (leaf) {
                this.values = new ArrayList<>();
                this.children = null;
            } else {
                this.children = new ArrayList<>();
                this.values = null;
            }
            this.next = null;
        }
    }

    private Node root;
    private final int t; // Минимальная степень дерева (определяет минимальное/максимальное количество ключей)

    public BPlusTree(int degree) {
        this.root = null;
        this.t = degree;
    }

    /**
     * Вставка ключа в дерево
     */
    public void insert(T key) {
        if (root == null) {
            // Создаем первый лист если дерево пустое
            root = new Node(true);
            root.keys.add(key);
            root.values.add(key);
        } else {
            // Если корень переполнен - разделяем его
            if (root.keys.size() == 2 * t - 1) {
                Node newRoot = new Node(false);
                newRoot.children.add(root);
                splitChild(newRoot, 0, root);
                root = newRoot;
            }
            // Вставляем в неполный узел
            insertNonFull(root, key);
        }
    }

    /**
     * Вставка в неполный узел (рекурсивная)
     */
    private void insertNonFull(Node node, T key) {
        if (node.isLeaf) {
            // Вставка в листовой узел
            int i = Collections.binarySearch(node.keys, key);
            if (i < 0) i = -(i + 1); // Находим позицию для вставки
            node.keys.add(i, key);
            node.values.add(i, key); // Сохраняем значение
        } else {
            // Вставка во внутренний узел
            int i = node.keys.size() - 1;
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                i--;
            }
            i++;

            // Если дочерний узел полный - разделяем его
            if (node.children.get(i).keys.size() == 2 * t - 1) {
                splitChild(node, i, node.children.get(i));
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    /**
     * Разделение дочернего узла
     * @param parent Родительский узел
     * @param index Индекс дочернего узла
     * @param child Дочерний узел для разделения
     */
    private void splitChild(Node parent, int index, Node child) {
        Node newChild = new Node(child.isLeaf);
        parent.children.add(index + 1, newChild);

        if (child.isLeaf) {
            // Разделение листового узла:
            // Средний ключ копируется в родителя
            parent.keys.add(index, child.keys.get(t));
            // Вторая половина ключей и значений переносится в новый узел
            newChild.keys.addAll(child.keys.subList(t, child.keys.size()));
            newChild.values.addAll(child.values.subList(t, child.values.size()));
            // Удаляем перенесенные данные из исходного узла
            child.keys.subList(t, child.keys.size()).clear();
            child.values.subList(t, child.values.size()).clear();
            // Обновляем связи между листьями
            newChild.next = child.next;
            child.next = newChild;
        } else {
            // Разделение внутреннего узла:
            // Средний ключ поднимается в родителя
            parent.keys.add(index, child.keys.get(t - 1));
            // Вторая половина ключей переносится
            newChild.keys.addAll(child.keys.subList(t, child.keys.size()));
            // Вторая половина дочерних узлов переносится
            newChild.children.addAll(child.children.subList(t, child.children.size()));
            // Удаляем перенесенные данные
            child.keys.subList(t - 1, child.keys.size()).clear();
            child.children.subList(t, child.children.size()).clear();
        }
    }

    /**
     * Поиск ключа в дереве
     * @return true если ключ найден
     */
    public boolean search(T key) {
        Node current = root;
        // Спускаемся по дереву до листа
        while (current != null && !current.isLeaf) {
            int i = 0;
            while (i < current.keys.size() && key.compareTo(current.keys.get(i)) >= 0) {
                i++;
            }
            current = current.children.get(i);
        }

        // Ищем ключ в листовом узле
        if (current != null) {
            return Collections.binarySearch(current.keys, key) >= 0;
        }
        return false;
    }

    /**
     * Удаление ключа из дерева
     */
    public void remove(T key) {
        if (root == null) return;

        remove(root, key);

        // Если корень пуст после удаления
        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
        }
    }

    /**
     * Рекурсивное удаление ключа
     */
    private void remove(Node node, T key) {
        if (node.isLeaf) {
            // Удаление из листа
            int index = Collections.binarySearch(node.keys, key);
            if (index >= 0) {
                node.keys.remove(index);
                node.values.remove(index);
            }
        } else {
            // Удаление из внутреннего узла
            int idx = 0;
            while (idx < node.keys.size() && key.compareTo(node.keys.get(idx)) >= 0) {
                idx++;
            }

            Node child = node.children.get(idx);
            if (child.keys.size() < t) {
                // Обработка нехватки ключей в дочернем узле
                if (idx > 0 && node.children.get(idx - 1).keys.size() >= t) {
                    borrowFromPrev(node, idx); // Заимствуем у левого соседа
                } else if (idx < node.children.size() - 1 &&
                        node.children.get(idx + 1).keys.size() >= t) {
                    borrowFromNext(node, idx); // Заимствуем у правого соседа
                } else {
                    // Если соседи тоже имеют минимальное количество ключей - объединяем
                    if (idx < node.children.size() - 1) {
                        merge(node, idx);
                    } else {
                        merge(node, idx - 1);
                        idx--;
                    }
                }
            }
            remove(node.children.get(idx), key);
        }
    }

    /**
     * Заимствование ключа у левого соседа
     */
    private void borrowFromPrev(Node node, int index) {
        Node child = node.children.get(index);
        Node sibling = node.children.get(index - 1);

        if (child.isLeaf) {
            // Для листьев: переносим ключ и значение
            T lastKey = sibling.keys.remove(sibling.keys.size() - 1);
            T lastValue = sibling.values.remove(sibling.values.size() - 1);

            child.keys.add(0, lastKey);
            child.values.add(0, lastValue);

            node.keys.set(index - 1, sibling.keys.get(sibling.keys.size() - 1));
        } else {
            // Для внутренних узлов
            child.keys.add(0, node.keys.get(index - 1));
            node.keys.set(index - 1, sibling.keys.remove(sibling.keys.size() - 1));
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
        }
    }

    /**
     * Заимствование ключа у правого соседа
     */
    private void borrowFromNext(Node node, int index) {
        Node child = node.children.get(index);
        Node sibling = node.children.get(index + 1);

        if (child.isLeaf) {
            // Для листьев
            T firstKey = sibling.keys.remove(0);
            T firstValue = sibling.values.remove(0);

            child.keys.add(firstKey);
            child.values.add(firstValue);

            node.keys.set(index, sibling.keys.get(0));
        } else {
            // Для внутренних узлов
            child.keys.add(node.keys.get(index));
            node.keys.set(index, sibling.keys.remove(0));
            child.children.add(sibling.children.remove(0));
        }
    }

    /**
     * Объединение узлов
     */
    private void merge(Node node, int index) {
        Node child = node.children.get(index);
        Node sibling = node.children.get(index + 1);

        if (child.isLeaf) {
            // Объединение листьев
            child.keys.addAll(sibling.keys);
            child.values.addAll(sibling.values);
            child.next = sibling.next;
        } else {
            // Объединение внутренних узлов
            child.keys.add(node.keys.get(index));
            child.keys.addAll(sibling.keys);
            child.children.addAll(sibling.children);
        }

        node.keys.remove(index);
        node.children.remove(index + 1);
    }

    /**
     * Диапазонный запрос
     * @return Список значений в диапазоне [lower, upper]
     */
    public List<T> rangeQuery(T lower, T upper) {
        List<T> result = new ArrayList<>();
        Node current = root;

        // Находим первый подходящий лист
        while (current != null && !current.isLeaf) {
            int i = 0;
            while (i < current.keys.size() && lower.compareTo(current.keys.get(i)) > 0) {
                i++;
            }
            current = current.children.get(i);
        }

        // Сканируем листья пока ключи в диапазоне
        while (current != null) {
            for (int i = 0; i < current.keys.size(); i++) {
                T key = current.keys.get(i);
                if (key.compareTo(lower) >= 0 && key.compareTo(upper) <= 0) {
                    result.add(current.values.get(i));
                }
                if (key.compareTo(upper) > 0) {
                    return result;
                }
            }
            current = current.next;
        }
        return result;
    }

    /**
     * Вывод структуры дерева (для отладки)
     */
    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(Node node, int level) {
        if (node != null) {
            System.out.print("Уровень " + level + " " + (node.isLeaf ? "Листок" : "") + ": ");
            for (T key : node.keys) {
                System.out.print(key + " ");
            }
            System.out.println();

            if (!node.isLeaf) {
                for (Node child : node.children) {
                    printTree(child, level + 1);
                }
            }
        }
    }
}