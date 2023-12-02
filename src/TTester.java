import java.util.Random;

public class TTester {

    public static void main(String[] args) {
        testBSTPrinceton();
        testSplayTree();
        testAVLTree();
        testRedBlackTree();
        testSkipList();
    }

    private static void testBSTPrinceton() {
        System.out.println("Testing BST Princeton:");

        TInterface<String, Integer> bst = new BSTPrinceton<>();

        // Insertion and retrieval tests
        bst.insert("Apple", 5);
        bst.insert("Banana", 8);
        bst.insert("Orange", 15);
        bst.insert("Grapes", 20);

        System.out.println(assertEquals(bst.get("Apple"), 5));
        System.out.println(assertEquals(bst.get("Banana"), 8));
        System.out.println(assertEquals(bst.get("Orange"), 15));
        System.out.println(assertEquals(bst.get("Grapes"), 20));

        System.out.println(bst);

        // Removal test
        bst.remove("Apple");
        System.out.println(assertEquals(bst.get("Apple"), null));

        System.out.println("----\n");
    }

    private static void testSplayTree() {
        System.out.println("Testing Splay Tree:");

        TInterface<String, Integer> splayTree = new SplayTree<>();

        // Insertion and retrieval tests
        splayTree.insert("Apple", 5);
        splayTree.insert("Banana", 8);
        splayTree.insert("Orange", 15);
        splayTree.insert("Grapes", 20);

        System.out.println(assertEquals(splayTree.get("Apple"), 5));
        System.out.println(assertEquals(splayTree.get("Banana"), 8));
        System.out.println(assertEquals(splayTree.get("Orange"), 15));
        System.out.println(assertEquals(splayTree.get("Grapes"), 20));
        System.out.println(splayTree);

        // Removal test
        splayTree.remove("Apple");
        System.out.println(assertEquals(splayTree.get("Apple"), null));

        System.out.println("----\n");
    }

    private static void testAVLTree() {
        System.out.println("Testing AVL Tree:");

        TInterface<String, String> avlTree = new AVLTree<>();

        // Insertion test
        for (int i = 0; i < 10; i++) {
            avlTree.insert("Key" + i, "Value" + i);
        }

        // Check balance
        AVLTree<String> downCastAvlTree = (AVLTree<String>) avlTree;
        downCastAvlTree.checkBalance();
        System.out.println("If no exception is thrown, balancing worked.");

        // Removal test
        System.out.println(assertEquals(avlTree.contains("Key4"), true));
        avlTree.remove("Key4");
        System.out.println(assertEquals(avlTree.contains("Key4"), false));

        System.out.println("----\n");
    }

    private static void testRedBlackTree() {
        System.out.println("Testing Red-Black Tree:");
    
        // Explicitly specify the generic types for RedBlackTree
        TInterface<Integer, Integer> rbTree = new RedBlackTree<>();
    
        // Insertion test
        for (int i = 0; i < 10; i++) {
            rbTree.insert(i, i * i);
        }
    
        // Check if all items exist
        boolean allItemsExist = true;
        for (int i = 0; i < 10; i++) {
            if (!rbTree.contains(i)) {
                allItemsExist = false;
                break;
            }
        }
        System.out.println(allItemsExist);
    
        System.out.println("----\n");
    }
    
    
    private static void testSkipList() {
        System.out.println("Testing SkipList:");

        TInterface<Integer, String> skipList = new SkipList<>();

        // Insertion and retrieval tests
        skipList.insert(5, "Apple");
        skipList.insert(8, "Banana");
        skipList.insert(15, "Orange");
        skipList.insert(20, "Grapes");

        System.out.println(assertEquals(skipList.get(5), "Apple"));
        System.out.println(assertEquals(skipList.get(8), "Banana"));
        System.out.println(assertEquals(skipList.get(15), "Orange"));
        System.out.println(assertEquals(skipList.get(20), "Grapes"));

        // Removal test
        skipList.remove(5);
        System.out.println(assertEquals(skipList.get(5), null));

        System.out.println(skipList);

        System.out.println("----\n");
    }

    private static boolean assertEquals(Object objectOne, Object objectTwo) {
        if (objectOne == null) {
            return objectTwo == null;
        }
        return objectOne.equals(objectTwo);
    }
}
