
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
// https://gist.github.com/SylvanasSun/f2a3e30e3657d8727006887751c1d1de
/**
 * A skip list is a data structure that allows fast search within
 * an ordered sequence of elements. Fast search is made possible
 * by maintaining a linked hierarchy of subsequences, with each
 * successive subsequence skipping over fewer elements than the
 * previous one. Searching starts in the sparsest subsequence until
 * two consecutive elements have been found, one smaller and one larger
 * than or equal to the element searched for.
 * <p>
 * cite: <a href="https://en.wikipedia.org/wiki/Skip_list">Skip List - Wikipedia</a>
 * <br>
 *
 * @author SylvanasSun <sylvanas.sun@gmail.com>
 */
public class SkipList<K extends Comparable<K>, V> implements Iterable<K>, TInterface<K, V> {
    protected static final Random randomGenerator = new Random();
    protected static final double DEFAULT_PROBABILITY = 0.5;
    private Node<K, V> head;
    private double probability;
    private int size;

    public SkipList() {
        this(DEFAULT_PROBABILITY);
    }

    public SkipList(double probability) {
        this.head = new Node<K, V>(null, null, 0);
        this.probability = probability;
        this.size = 0;
    }

    public V get(K key) {
        checkKeyValidity(key);
        Node<K, V> node = findNode(key);
        if(node == null) return null;
        if(node.key == null) return null;
        if (node.getKey().compareTo(key) == 0)
            return node.getValue();
        else
            return null;
    }

    public void insert(K key, V value) {
        put(key, value);
    }

    public void put(K key, V value) {
        checkKeyValidity(key);
        Node<K, V> node = findNode(key);
        if (node.getKey() != null && node.getKey().compareTo(key) == 0) {
            node.setValue(value);
            return;
        }

        Node<K, V> newNode = new Node<K, V>(key, value, node.getLevel());
        horizontalInsert(node, newNode);
        // Decide level according to the probability function
        int currentLevel = node.getLevel();
        int headLevel = head.getLevel();
        while (isBuildLevel()) {
            // buiding a new level
            if (currentLevel >= headLevel) {
                Node<K, V> newHead = new Node<K, V>(null, null, headLevel + 1);
                verticalLink(newHead, head);
                head = newHead;
                headLevel = head.getLevel();
            }
            // copy node and newNode to the upper level
            while (node.getUp() == null) {
                node = node.getPrevious();
            }
            node = node.getUp();

            Node<K, V> tmp = new Node<K, V>(key, value, node.getLevel());
            horizontalInsert(node, tmp);
            verticalLink(tmp, newNode);
            newNode = tmp;
            currentLevel++;
        }
        size++;
    }

    public void remove(K key) {
        checkKeyValidity(key);
        Node<K, V> node = findNode(key);
        if (node == null || node.getKey().compareTo(key) != 0)
            throw new NoSuchElementException("The key is not exist!");

        // Move to the bottom
        while (node.getDown() != null)
            node = node.getDown();
        // Because node is on the lowest level so we need remove by down-top
        Node<K, V> prev = null;
        Node<K, V> next = null;
        for (; node != null; node = node.getUp()) {
            prev = node.getPrevious();
            next = node.getNext();
            if (prev != null)
                prev.setNext(next);
            if (next != null)
                next.setPrevious(prev);
        }

        // Adjust head
        while (head.getNext() == null && head.getDown() != null) {
            head = head.getDown();
            head.setUp(null);
        }
        size--;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    protected Node<K, V> findNode(K key) {
        Node<K, V> node = head;
        Node<K, V> next = null;
        Node<K, V> down = null;
        K nodeKey = null;

        while (true) {
            // Searching nearest (less than or equal) node with special key
            next = node.getNext();
            while (next != null && lessThanOrEqual(next.getKey(), key)) {
                node = next;
                next = node.getNext();
            }
            nodeKey = node.getKey();
            if (nodeKey != null && nodeKey.compareTo(key) == 0)
                break;
            // Descend to the bottom for continue search
            down = node.getDown();
            if (down != null) {
                node = down;
            } else {
                break;
            }
        }

        return node;
    }

    protected void checkKeyValidity(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key must be not null!");
    }

    protected boolean lessThanOrEqual(K a, K b) {
        return a.compareTo(b) <= 0;
    }

    protected boolean isBuildLevel() {
        return randomGenerator.nextDouble() < probability;
    }

    protected void horizontalInsert(Node<K, V> x, Node<K, V> y) {
        y.setPrevious(x);
        y.setNext(x.getNext());
        if (x.getNext() != null)
            x.getNext().setPrevious(y);
        x.setNext(y);
    }

    protected void verticalLink(Node<K, V> x, Node<K, V> y) {
        x.setDown(y);
        y.setUp(x);
    }

    //@Override
    private String toStringLastLevel() {
        StringBuilder sb = new StringBuilder();
        Node<K, V> node = head;

        // Move into the lower left bottom
        while (node.getDown() != null)
            node = node.getDown();

        while (node.getPrevious() != null)
            node = node.getPrevious();

        // Head node with each level the key is null
        // so need to move into the next node
        if (node.getNext() != null)
            node = node.getNext();

        while (node != null) {
            sb.append(node).append("\n");
            node = node.getNext();
        }

        return sb.toString();
    }

    private String levelToString(int level, Node<K, V> node) {
        StringBuilder sb = new StringBuilder();
        sb.append("L");
        sb.append(level);
        sb.append(" ");
        // Get to start
        while (node.getPrevious() != null) {
            node = node.getPrevious();
        }
        // Head node with each level the key is null
        // so need to move into the next node
        if (node.getNext() != null)
            node = node.getNext();
        while (node != null) {
            sb.append(node).append(" ");
            node = node.getNext();
        }
        sb.append("\n");
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<K, V> node = head;
        int level = 0;

        while(node.getDown() != null) {
            sb.append(levelToString(level++, node));
            node = node.getDown();
        }
        sb.append(levelToString(level, node));
        return sb.toString();
    }

    @Override
    public Iterator<K> iterator() {
        return new SkipListIterator<K, V>(head);
    }

    protected static class SkipListIterator<K extends Comparable<K>, V> implements Iterator<K> {

        private Node<K, V> node;

        public SkipListIterator(Node<K, V> node) {
            while (node.getDown() != null)
                node = node.getDown();

            while (node.getPrevious() != null)
                node = node.getPrevious();

            if (node.getNext() != null)
                node = node.getNext();

            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return this.node != null;
        }

        @Override
        public K next() {
            K result = node.getKey();
            node = node.getNext();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected static class Node<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private int level;
        private Node<K, V> up, down, next, previous;

        public Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.level = level;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[")
                    .append("K:");
            if (this.key == null)
                sb.append("null");
            else
                sb.append(this.key);

            sb.append(" V:");
            if (this.value == null)
                sb.append("null");
            else
                sb.append(this.value);
            sb.append("]");
            return sb.toString();
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public Node<K, V> getUp() {
            return up;
        }

        public void setUp(Node<K, V> up) {
            this.up = up;
        }

        public Node<K, V> getDown() {
            return down;
        }

        public void setDown(Node<K, V> down) {
            this.down = down;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        public Node<K, V> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<K, V> previous) {
            this.previous = previous;
        }
    }
}

class SkipListTest implements Tester{
    static void myAssert(boolean b) {
        if (!b) {
            throw new RuntimeException("Bad state");
        }
    }

    private static void insertKV(SkipList<Integer, Integer> skipList, Integer key, Integer value) {
        myAssert(!skipList.contains(key));
        skipList.put(key, value);
        myAssert(skipList.contains(key));
    }

    private static void deleteK(SkipList<Integer, Integer> skipList, Integer key) {
        myAssert(skipList.contains(key));
        skipList.remove(key);
        myAssert(!skipList.contains(key));
    }

    public static void doTests(SkipList skipList) {
        Integer[] keys = {0, 1, 2, 3, 4, 5, 6, 7};
        Integer[] values = {10, 11, 12, 13, 14, 15, 16, 17};
        int size = 0;
        for (int i = 0; i < keys.length; i++) {
            myAssert(skipList.size() == size++);
            insertKV(skipList, keys[i], values[i]);
            System.out.println(skipList);
        }
        for (Integer key : keys) {
            deleteK(skipList, key);
        }
    }

    @Override
     public void test() {
        SkipList skipList = new SkipList();
        doTests(skipList);
     }


     @Override
     public void insert() {
        
     }

     @Override
     public void remove() {
        
     }

    public static void main(String[] args) {
        SkipList<Integer, String> skipList = new SkipList<>();
        doTests(skipList);
    }
}
