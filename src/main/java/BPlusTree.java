import java.util.*;

public class BPlusTree<T extends Comparable<T>> {
    // Node class
    private class Node {
        boolean isLeaf;
        List<T> keys;
        List<Node> children;
        Node next;

        Node(boolean leaf) {
            this.isLeaf = leaf;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.next = null;
        }
    }

    private Node root;
    private final int t; // Minimum degree

    public BPlusTree(int degree) {
        this.root = null;
        this.t = degree;
    }

    // Insert a key
    public void insert(T key) {
        if (root == null) {
            root = new Node(true);
            root.keys.add(key);
        } else {
            if (root.keys.size() == 2 * t - 1) {
                Node newRoot = new Node(false);
                newRoot.children.add(root);
                splitChild(newRoot, 0, root);
                root = newRoot;
            }
            insertNonFull(root, key);
        }
    }

    // Insert into non-full node
    private void insertNonFull(Node node, T key) {
        if (node.isLeaf) {
            int i = Collections.binarySearch(node.keys, key);
            if (i < 0) i = -(i + 1);
            node.keys.add(i, key);
        } else {
            int i = node.keys.size() - 1;
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                i--;
            }
            i++;
            if (node.children.get(i).keys.size() == 2 * t - 1) {
                splitChild(node, i, node.children.get(i));
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    // Split child node
    private void splitChild(Node parent, int index, Node child) {
        Node newChild = new Node(child.isLeaf);
        parent.children.add(index + 1, newChild);
        parent.keys.add(index, child.keys.get(t - 1));

        newChild.keys.addAll(child.keys.subList(t, child.keys.size()));
        child.keys.subList(t - 1, child.keys.size()).clear();

        if (!child.isLeaf) {
            newChild.children.addAll(child.children.subList(t, child.children.size()));
            child.children.subList(t, child.children.size()).clear();
        }

        if (child.isLeaf) {
            newChild.next = child.next;
            child.next = newChild;
        }
    }

    // Search for a key
    public boolean search(T key) {
        Node current = root;
        while (current != null) {
            int i = 0;
            while (i < current.keys.size() && key.compareTo(current.keys.get(i)) > 0) {
                i++;
            }
            if (i < current.keys.size() && key.equals(current.keys.get(i))) {
                return true;
            }
            if (current.isLeaf) {
                return false;
            }
            current = current.children.get(i);
        }
        return false;
    }

    // Remove a key
    public void remove(T key) {
        if (root == null) {
            return;
        }
        remove(root, key);
        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
        }
    }

    private void remove(Node node, T key) {
        if (node.isLeaf) {
            int index = Collections.binarySearch(node.keys, key);
            if (index >= 0) {
                node.keys.remove(index);
            }
        } else {
            int idx = 0;
            while (idx < node.keys.size() && key.compareTo(node.keys.get(idx)) > 0) {
                idx++;
            }

            if (idx < node.keys.size() && key.equals(node.keys.get(idx))) {
                if (node.children.get(idx).keys.size() >= t) {
                    Node predNode = node.children.get(idx);
                    while (!predNode.isLeaf) {
                        predNode = predNode.children.get(predNode.children.size() - 1);
                    }
                    T pred = predNode.keys.get(predNode.keys.size() - 1);
                    node.keys.set(idx, pred);
                    remove(node.children.get(idx), pred);
                } else if (node.children.get(idx + 1).keys.size() >= t) {
                    Node succNode = node.children.get(idx + 1);
                    while (!succNode.isLeaf) {
                        succNode = succNode.children.get(0);
                    }
                    T succ = succNode.keys.get(0);
                    node.keys.set(idx, succ);
                    remove(node.children.get(idx + 1), succ);
                } else {
                    merge(node, idx);
                    remove(node.children.get(idx), key);
                }
            } else {
                if (node.children.get(idx).keys.size() < t) {
                    if (idx > 0 && node.children.get(idx - 1).keys.size() >= t) {
                        borrowFromPrev(node, idx);
                    } else if (idx < node.children.size() - 1 &&
                            node.children.get(idx + 1).keys.size() >= t) {
                        borrowFromNext(node, idx);
                    } else {
                        if (idx < node.children.size() - 1) {
                            merge(node, idx);
                        } else {
                            merge(node, idx - 1);
                        }
                    }
                }
                remove(node.children.get(idx), key);
            }
        }
    }

    private void borrowFromPrev(Node node, int index) {
        Node child = node.children.get(index);
        Node sibling = node.children.get(index - 1);

        child.keys.add(0, node.keys.get(index - 1));
        node.keys.set(index - 1, sibling.keys.get(sibling.keys.size() - 1));
        sibling.keys.remove(sibling.keys.size() - 1);

        if (!child.isLeaf) {
            child.children.add(0, sibling.children.get(sibling.children.size() - 1));
            sibling.children.remove(sibling.children.size() - 1);
        }
    }

    private void borrowFromNext(Node node, int index) {
        Node child = node.children.get(index);
        Node sibling = node.children.get(index + 1);

        child.keys.add(node.keys.get(index));
        node.keys.set(index, sibling.keys.get(0));
        sibling.keys.remove(0);

        if (!child.isLeaf) {
            child.children.add(sibling.children.get(0));
            sibling.children.remove(0);
        }
    }

    private void merge(Node node, int index) {
        Node child = node.children.get(index);
        Node sibling = node.children.get(index + 1);

        child.keys.add(node.keys.get(index));
        child.keys.addAll(sibling.keys);

        if (!child.isLeaf) {
            child.children.addAll(sibling.children);
        }

        node.keys.remove(index);
        node.children.remove(index + 1);
    }

    // Range query
    public List<T> rangeQuery(T lower, T upper) {
        List<T> result = new ArrayList<>();
        Node current = root;

        while (current != null && !current.isLeaf) {
            int i = 0;
            while (i < current.keys.size() && lower.compareTo(current.keys.get(i)) > 0) {
                i++;
            }
            current = current.children.get(i);
        }

        while (current != null) {
            for (T key : current.keys) {
                if (key.compareTo(lower) >= 0 && key.compareTo(upper) <= 0) {
                    result.add(key);
                }
                if (key.compareTo(upper) > 0) {
                    return result;
                }
            }
            current = current.next;
        }
        return result;
    }

    // Print the tree
    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(Node node, int level) {
        if (node != null) {
            System.out.print("Level " + level + ": ");
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