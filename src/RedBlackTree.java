// Taken from
// https://github.com/SvenWoltmann/binary-tree/blob/main/src/main/java/eu/happycoders/binarytree/RedBlackTree.java#L252
/**
 * A red-black tree implementation with <code>int</code> keys.
 *
 * @author <a href="sven@happycoders.eu">Sven Woltmann</a>
 */
public class RedBlackTree<T extends Comparable<T>> implements TInterface<T, T> {
    static final boolean RED = false;
    static final boolean BLACK = true;
    public class Node {
        Node(T value) {
            data = value;
        }
        public String toString() {
            if(data == null) {
                return "null [B] ";
            }
            String str = data.toString();
            str += color == RED ? "[R] " : "[B] ";
            return str;
        }
        T data;
        boolean color;
        Node parent;
        Node left;
        Node right;
    }
    private class NilNode extends Node {
        private NilNode() {
            super(null);
            this.color = BLACK;
        }
    }
    public Node searchNode(T key) {
        Node node = root;
        while (node != null) {
            if (key.compareTo(node.data) == 0) {
                return node;
            } else if (key.compareTo(node.data) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return null;
    }

    public T get(T key) {
        Node node = searchNode(key);

        return node.data;
    }

    public boolean contains(T key) {
        if (searchNode(key) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void remove(T key) {
        deleteNode(key);
    }

    // -- Insertion ----------------------------------------------------------------------------------

    public void insert(T key, T value) {
        insertNode(key);
    }

    public void insertNode(T key) {
        Node node = root;
        Node parent = null;

        // Traverse the tree to the left or right depending on the key
        while (node != null) {
            parent = node;
            if (key.compareTo(node.data) < 0) {
                node = node.left;
            } else if (key.compareTo(node.data) > 0) {
                node = node.right;
            } else {
                throw new IllegalArgumentException("BST already contains a node with key " + key);
            }
        }

        // Insert new node
        Node newNode = new Node(key);
        newNode.color = RED;
        if (parent == null) {
            root = newNode;
        } else if (key.compareTo(parent.data) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        newNode.parent = parent;

        fixRedBlackPropertiesAfterInsert(newNode);
    }

    private int countNodes(Node n) {
        if(n == null) return 0;
        return countNodes(n.left) + 1 + countNodes(n.right);
    }
    public int countNodes() {
        return countNodes(root);
    }

    private void fixRedBlackPropertiesAfterInsert(Node node) {
        Node parent = node.parent;

        // Case 1: Parent is null, we've reached the root, the end of the recursion
        if (parent == null) {
            // Uncomment the following line if you want to enforce black roots (rule 2):
            node.color = BLACK;
            return;
        }

        // Parent is black --> nothing to do
        if (parent.color == BLACK) {
            return;
        }

        // From here on, parent is red
        Node grandparent = parent.parent;

        // Get the uncle (may be null/nil, in which case its color is BLACK)
        Node uncle = getUncle(parent);

        // Case 3: Uncle is red -> recolor parent, grandparent and uncle
        if (uncle != null && uncle.color == RED) {
            parent.color = BLACK;
            grandparent.color = RED;
            uncle.color = BLACK;

            // System.out.println("Recolor parent" + parent + "node " + node);

            // Call recursively for grandparent, which is now red.
            // It might be root or have a red parent, in which case we need to fix more...
            fixRedBlackPropertiesAfterInsert(grandparent);
        }

        // Note on performance:
        // It would be faster to do the uncle color check within the following code. This way
        // we would avoid checking the grandparent-parent direction twice (once in getUncle()
        // and once in the following else-if). But for better understanding of the code,
        // I left the uncle color check as a separate step.

        // Parent is left child of grandparent
        else if (parent == grandparent.left) {
            // Case 4a: Uncle is black and node is left->right "inner child" of its grandparent
            if (node == parent.right) {
                //System.out.println("(LR) left rotate parent " + parent + "node " + node);
                rotateLeft(parent);

                // Let "parent" point to the new root node of the rotated subtree.
                // It will be recolored in the next step, which we're going to fall-through to.
                parent = node;
            }

            //System.out.println("(LL) right rotate grandparent " + grandparent + "node " + node);
            // Case 5a: Uncle is black and node is left->left "outer child" of its grandparent
            rotateRight(grandparent);

            // Recolor original parent and grandparent
            parent.color = BLACK;
            grandparent.color = RED;
        }

        // Parent is right child of grandparent
        else {
            // Case 4b: Uncle is black and node is right->left "inner child" of its grandparent
            if (node == parent.left) {
                //System.out.println("(RL) right rotate parent " + parent + "node " + node);
                rotateRight(parent);

                // Let "parent" point to the new root node of the rotated subtree.
                // It will be recolored in the next step, which we're going to fall-through to.
                parent = node;
            }

            //System.out.println("(RR) left rotate grandparent " + grandparent + "node " + node);
            // Case 5b: Uncle is black and node is right->right "outer child" of its grandparent
            rotateLeft(grandparent);

            // Recolor original parent and grandparent
            parent.color = BLACK;
            grandparent.color = RED;
        }
    }

    private Node getUncle(Node parent) {
        Node grandparent = parent.parent;
        if (grandparent.left == parent) {
            return grandparent.right;
        } else if (grandparent.right == parent) {
            return grandparent.left;
        } else {
            throw new IllegalStateException("Parent is not a child of its grandparent");
        }
    }

    // -- Deletion -----------------------------------------------------------------------------------
    public void deleteNode(T key) {
        Node node = root;

        // Find the node to be deleted
        while (node != null && !key.equals(node.data)) {
            // Traverse the tree to the left or right depending on the key
            if (key.compareTo(node.data) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        // Node not found?
        if (node == null) {
            return;
        }

        // At this point, "node" is the node to be deleted

        // In this variable, we'll store the node at which we're going to start to fix the R-B
        // properties after deleting a node.
        Node movedUpNode;
        boolean deletedNodeColor;

        // Node has zero or one child
        if (node.left == null || node.right == null) {
            //System.out.println("delete node with zero or one child " +  node);
            movedUpNode = deleteNodeWithZeroOrOneChild(node);
            deletedNodeColor = node.color;
        }

        // Node has two children
        else {
            // Find minimum node of right subtree ("inorder successor" of current node)
            Node inOrderSuccessor = findMinimum(node.right);

            //System.out.println("delete node with 2+ children " +  node + "successor " + inOrderSuccessor);
            // Copy inorder successor's data to current node (keep its color!)
            node.data = inOrderSuccessor.data;

            // Delete inorder successor just as we would delete a node with 0 or 1 child
            movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
            deletedNodeColor = inOrderSuccessor.color;
        }

        if (deletedNodeColor == BLACK) {
            //System.out.println("fixup RB properties " +  node);
            fixRedBlackPropertiesAfterDelete(movedUpNode);

            // Remove the temporary NIL node
            if (movedUpNode.getClass() == NilNode.class) {
                replaceParentsChild(movedUpNode.parent, movedUpNode, null);
            }
        }
    }

    private Node deleteNodeWithZeroOrOneChild(Node node) {
        // Node has ONLY a left child --> replace by its left child
        if (node.left != null) {
            replaceParentsChild(node.parent, node, node.left);
            return node.left; // moved-up node
        }

        // Node has ONLY a right child --> replace by its right child
        else if (node.right != null) {
            replaceParentsChild(node.parent, node, node.right);
            return node.right; // moved-up node
        }

        // Node has no children -->
        // * node is red --> just remove it
        // * node is black --> replace it by a temporary NIL node (needed to fix the R-B rules)
        else {
            Node newChild = node.color == BLACK ? new NilNode() : null;
            replaceParentsChild(node.parent, node, newChild);
            return newChild;
        }
    }

    private Node findMinimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private void fixRedBlackPropertiesAfterDelete(Node node) {
        // Case 1: Examined node is root, end of recursion
        if (node == root) {
            // Uncomment the following line if you want to enforce black roots (rule 2):
            node.color = BLACK;
            return;
        }

        Node sibling = getSibling(node);

        // Case 2: Red sibling
        if (sibling.color == RED) {
            //System.out.println("delete handle red sibling " + sibling);
            handleRedSibling(node, sibling);
            sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
        }

        // Cases 3+4: Black sibling with two black children
        if (isBlack(sibling.left) && isBlack(sibling.right)) {
            sibling.color = RED;

            // Case 3: Black sibling with two black children + red parent
            if (node.parent.color == RED) {
                node.parent.color = BLACK;
            }

            // Case 4: Black sibling with two black children + black parent
            else {
                fixRedBlackPropertiesAfterDelete(node.parent);
            }
        }

        // Case 5+6: Black sibling with at least one red child
        else {
            handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
        }
    }

    private void handleRedSibling(Node node, Node sibling) {
        // Recolor...
        sibling.color = BLACK;
        node.parent.color = RED;

        // ... and rotate
        if (node == node.parent.left) {
            //System.out.println("red sibling rotate left parent " + node.parent + "node " + node + "sibling " + sibling);
            rotateLeft(node.parent);
        } else {
            //System.out.println("red sibling rotate right parent " + node.parent + "node " + node + "sibling " + sibling);
            rotateRight(node.parent);
        }
    }

    private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
        boolean nodeIsLeftChild = node == node.parent.left;

        // Case 5: Black sibling with at least one red child + "outer nephew" is black
        // --> Recolor sibling and its child, and rotate around sibling
        if (nodeIsLeftChild && isBlack(sibling.right)) {
            sibling.left.color = BLACK;
            sibling.color = RED;
            //System.out.println("black sibling rotate right parent " + node.parent + "node " + node + "sibling " + sibling);
            rotateRight(sibling);
            sibling = node.parent.right;
        } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
            sibling.right.color = BLACK;
            sibling.color = RED;
            //System.out.println("black sibling rotate left parent " + node.parent + "node " + node + "sibling " + sibling);
            rotateLeft(sibling);
            sibling = node.parent.left;
        }

        // Fall-through to case 6...

        // Case 6: Black sibling with at least one red child + "outer nephew" is red
        // --> Recolor sibling + parent + sibling's child, and rotate around parent
        sibling.color = node.parent.color;
        node.parent.color = BLACK;
        if (nodeIsLeftChild) {
            sibling.right.color = BLACK;
            //System.out.println("black sib red child rotate left parent " + node.parent + "node " + node + "sibling " + sibling);
            rotateLeft(node.parent);
        } else {
            sibling.left.color = BLACK;
            //System.out.println("black sib red child rotate right parent " + node.parent + "node " + node + "sibling " + sibling);
            rotateRight(node.parent);
        }
    }

    private Node getSibling(Node node) {
        Node parent = node.parent;
        if (node == parent.left) {
            return parent.right;
        } else if (node == parent.right) {
            return parent.left;
        } else {
            throw new IllegalStateException("Parent is not a child of its grandparent");
        }
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == BLACK;
    }

    // -- Helpers for insertion and deletion ---------------------------------------------------------

    private void rotateRight(Node node) {
        Node parent = node.parent;
        Node leftChild = node.left;

        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceParentsChild(parent, node, leftChild);
    }

    private void rotateLeft(Node node) {
        Node parent = node.parent;
        Node rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceParentsChild(parent, node, rightChild);
    }

    private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
        if (parent == null) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild) {
            parent.right = newChild;
        } else {
            throw new IllegalStateException("Node is not a child of its parent");
        }

        if (newChild != null) {
            newChild.parent = parent;
        }
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
            if(node.left != null || (node.left == null && node.right != null)) {
                traverseNodes(sb, paddingForBoth, pointerLeft, node.left, node.right != null);
            }
            if(node.right != null || (node.right == null && node.left != null)) {
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

    private String toString(Node n) {
        if(n == null) {
            return "";
        }
        String str = (n.left != null) ? toString(n.left) : "";
        str += n.toString();
        str += (n.right != null) ? toString(n.right) : "";
        return str;
    }
    public String toString() {
        //String str = "(" + String.valueOf(countNodes()) + ") ";
        return traversePreOrder(root);
    }
    Node root;
}

class RedBlackTreeTest implements Tester{

     static void myAssert(boolean b) {
         if (!b) {
             throw new RuntimeException("Bad state");
         }
     }
 
     private static void testInsertion(RedBlackTree<Integer> rbTree) {
        Integer[] keys = {0, 1, 2, 3, 4, 5, 6, 7};
        Integer[] values = {10, 11, 12, 13, 14, 15, 16, 17};
        int size = 0;

        for (int i = 0; i < keys.length; i++) {
            myAssert(rbTree.countNodes() == size++);
            rbTree.insertNode(keys[i]);
            //System.out.println(rbTree.traversePreOrder(rbTree.root));
        }
    }

    private static void testDeletion(RedBlackTree<Integer> rbTree) {
        Integer[] keys = {0, 1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < keys.length; i++) {
            myAssert(rbTree.searchNode(keys[i]) != null);
            rbTree.deleteNode(keys[i]);
            myAssert(rbTree.searchNode(keys[i]) == null);
            //System.out.println(rbTree.traversePreOrder(rbTree.root));
        }
    }

     @Override
     public void test() {
        RedBlackTree<Integer> rbTree = new RedBlackTree<>();
        doTests(rbTree);
     }

     public static void doTests(RedBlackTree<Integer> rbTree) {
        testInsertion(rbTree);
        testDeletion(rbTree);
     }


     @Override
     public void insert() {
        
     }

     @Override
     public void remove() {
        
     }
 
     /**
      * Unit tests the {@code BST} data type.
      *
      * @param args the command-line arguments
      */
     public static void main(String[] args) {
         RedBlackTree<Integer> rbTree = new RedBlackTree<>();
         doTests(rbTree);
     }

 }