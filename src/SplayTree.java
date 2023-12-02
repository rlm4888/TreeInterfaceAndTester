/******************************************************************************
 *  Splay tree. Supports splay-insert, -search, and -delete.
 *  Splays on every operation, regardless of the presence of the associated
 *  key prior to that operation.
 *
 *  Written by Josh Israel.
 *  Copyright © 2000–2019, Robert Sedgewick and Kevin Wayne.
 *
 ******************************************************************************/

// Check out this visualization
    // https://www.cs.usfca.edu/%7Egalles/visualization/SplayTree.html


public class SplayTree<Key extends Comparable<Key>, Value> implements TInterface<Key, Value>{
    private Node root;   // root of the BST

    // BST helper node data type
    private class Node {
        private Key key;            // key
        private Value value;        // associated data
        private Node left, right;   // left and right subtrees

        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[K=");
            sb.append(key);
            sb.append(" V=");
            sb.append(value);
            sb.append("]");
            return sb.toString();
        }
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

    // return value associated with the given key
    // if no such value, return null
    public Value get(Key key) {
        root = splay(root, key);
        if (root == null) {
            return null;
        }

        if (key.equals((root.key))) {
            return root.value;
        }
        return null;
    }

    /***************************************************************************
     *  Splay tree insertion.
     ***************************************************************************/
    public void put(Key key, Value value) {
        // splay key to root
        if (root == null) {
            root = new Node(key, value);
            return;
        }

        root = splay(root, key);

        int cmp = key.compareTo(root.key);

        // Insert new node at root
        if (cmp < 0) {
            Node n = new Node(key, value);
            n.left = root.left;
            n.right = root;
            root.left = null;
            root = n;
        }

        // Insert new node at root
        else if (cmp > 0) {
            Node n = new Node(key, value);
            n.right = root.right;
            n.left = root;
            root.right = null;
            root = n;
        }

        // It was a duplicate key. Simply replace the value
        else {
            root.value = value;
        }

    }

    public void insert(Key key, Value value) {
        put(key, value);
    }

    /***************************************************************************
     *  Splay tree deletion.
     ***************************************************************************/
    /* This splays the key, then does a slightly modified Hibbard deletion on
     * the root (if it is the node to be deleted; if it is not, the key was
     * not in the tree). The modification is that rather than swapping the
     * root (call it node A) with its successor, it's successor (call it Node B)
     * is moved to the root position by splaying for the deletion key in A's
     * right (edit: left) subtree. Finally, A's right child is made the new root's right
     * child.
     */
    public void remove(Key key) {
        if (root == null) return; // empty tree

        root = splay(root, key);

        int cmp = key.compareTo(root.key);

        if (cmp == 0) {
            if (root.left == null) {
                root = root.right;
            } else {
                System.out.println("In Delete");
                System.out.println(root);
                Node x = root.right;
                root = root.left;
                // NB: key == root->key, so after splay(key, root.left),
                // the tree we get will have no right child tree
                // and maximum node in left subtree will get splayed
                // to root
                splay(root, key);
                root.right = x;
            }
        }
        // else: it wasn't in the tree to remove
    }


    /***************************************************************************
     * Splay tree function.
     * **********************************************************************/
    // splay key in the tree rooted at Node h. If a node with that key exists,
    //   it is splayed to the root of the tree. If it does not, the last node
    //   along the search path for the key is splayed to the root.
    private Node splay(Node h, Key key) {
        if (h == null) return null;

        int cmp1 = key.compareTo(h.key);

        if (cmp1 < 0) {
            // key not in tree, so we're done
            if (h.left == null) {
                return h;
            }
            int cmp2 = key.compareTo(h.left.key);
            boolean doubleRotation = false;
            if (cmp2 < 0) {
                h.left.left = splay(h.left.left, key);
                h = rotateRight(h);
                if (h.left == null) {
                    System.out.println("LL rot R (Zig) " + key + " to " + h.key);
                } else {
                    System.out.println("LL rot R (Zig-Zig) " + key + " to " + h.key);
                    doubleRotation = true;
                }
            } else if (cmp2 > 0) {
                h.left.right = splay(h.left.right, key);
                if (h.left.right != null) {
                    h.left = rotateLeft(h.left);
                    // h.left != null
                    System.out.println("LR rot L (Zig-Zag) " + key + " to " + h.key);
                    doubleRotation = true;
                }
            }
            if (h.left == null) {
                return h;
            } else {
                if(!doubleRotation) {
                    System.out.println("rot R (Zig) " + key + " to " + h.key);
                }
                return rotateRight(h);
            }
        } else if (cmp1 > 0) {
            // key not in tree, so we're done
            if (h.right == null) {
                return h;
            }

            int cmp2 = key.compareTo(h.right.key);
            boolean doubleRotation = false;
            if (cmp2 < 0) {
                h.right.left = splay(h.right.left, key);
                if (h.right.left != null) {
                    h.right = rotateRight(h.right);
                    System.out.println("RL rot R (Zig-Zag) " + key + " to " + h.key);
                    doubleRotation = true;
                }
            } else if (cmp2 > 0) {
                h.right.right = splay(h.right.right, key);
                System.out.println("RR rot L (Zig-Zig) " + key + " to " + h.key);
                doubleRotation = true;
                h = rotateLeft(h);
            }

            if (h.right == null) {
                return h;
            } else {
                if(!doubleRotation) {
                    System.out.println("rot L (Zig) " + key + " to " + h.key);
                }
                return rotateLeft(h);
            }
        } else return h;
    }


    /***************************************************************************
     *  Helper functions.
     ***************************************************************************/

    // height of tree (1-node tree has height 0)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }


    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return 1 + size(x.left) + size(x.right);
    }

    // right rotate
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        return x;
    }

    // left rotate
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        return x;
    }
    // -- toString() -----------------------------------------------------------------------------
    // Adapted from https://www.baeldung.com/java-print-binary-tree-diagram
    public void traverseNodes(StringBuilder sb, String padding, String pointer, Node node,
                              boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node);

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.right != null) ? "├──" : "└──";

            // Only print explicit nulls if we have one null child
            if(node.left != null || node.right != null) {
                traverseNodes(sb, paddingForBoth, pointerLeft, node.left, node.right != null);
            }
            if(node.right != null || node.left != null) {
                traverseNodes(sb, paddingForBoth, pointerRight, node.right, false);
            }
        } else {
            // Print null
            sb.append("\n");
            sb.append(padding);
            sb.append("-");
        }
    }

    public String traversePreOrder(Node root) {
        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root);

        String pointerRight = "└──";
        String pointerLeft = (root.right != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.left, root.right != null);
        traverseNodes(sb, "", pointerRight, root.right, false);

        return sb.toString();
    }
    public String toString() {
        return traversePreOrder(root);
    }
}

// test client
class SplayTreeTest implements Tester{
    static void myAssert(boolean b) {
        if (!b) {
            throw new RuntimeException("Bad state");
        }
    }

    private static void insertKV(SplayTree<Integer, Integer> splayTree, Integer key, Integer value) {
        myAssert(!splayTree.contains(key));
        splayTree.put(key, value);
        myAssert(splayTree.contains(key));
    }
    private static void deleteK(SplayTree<Integer, Integer> splayTree, Integer key) {
        myAssert(splayTree.contains(key));
        splayTree.remove(key);
        myAssert(!splayTree.contains(key));
    }
    
    @Override
     public void test() {
        SplayTree splayTree = new SplayTree<>();
        doTests(splayTree);
     }


     @Override
     public void insert() {
        
     }

     @Override
     public void remove() {
        
     }

    public static void doTests(SplayTree splayTree) {
        Integer[] keys = {0, 1, 2, 3, 4, 5, 6, 7};
        Integer[] values = {10, 11, 12, 13, 14, 15, 16, 17};
        int size = 0;
        for (int i = 0; i < keys.length; i++) {
            myAssert(splayTree.size() == size++);
            insertKV(splayTree, keys[i], values[i]);
            System.out.println(splayTree);
        }
        for (int i = 0; i < keys.length; i++) {
            deleteK(splayTree, keys[i]);
        }
    }
    public static void testZigZig() {
        SplayTree<Integer, Integer> splayTree = new SplayTree<>();
        insertKV(splayTree, 0, 10);
        insertKV(splayTree, 1, 11);
        insertKV(splayTree, 2, 20);
        System.out.println(splayTree);
        System.out.println("GET 0");
        splayTree.get(0);
        System.out.println(splayTree);
    }
    public static void testZagZag() {
        SplayTree<Integer, Integer> splayTree = new SplayTree<>();
        insertKV(splayTree, 2, 10);
        insertKV(splayTree, 1, 11);
        insertKV(splayTree, 0, 20);
        System.out.println(splayTree);
        System.out.println("GET 2");
        splayTree.get(2);
        System.out.println(splayTree);
    }
    public static void testZigZag() {
        SplayTree<Integer, Integer> splayTree = new SplayTree<>();
        insertKV(splayTree, 9, 20);
        insertKV(splayTree, 8, 10);
        insertKV(splayTree, 7, 30);
        insertKV(splayTree, 6, 30);
        //insertKV(splayTree, 4, 30);
        System.out.println(splayTree);
        System.out.println("GET 9");
        splayTree.get(9);
        System.out.println(splayTree);
        System.out.println("GET 8");
        splayTree.get(8);
        System.out.println(splayTree);
    }
    public static void main(String[] args) {
        SplayTree<Integer, Integer> splayTree = new SplayTree<>();
        doTests(splayTree);
        testZagZag();
        testZigZig();
        testZigZag();
    }
}
