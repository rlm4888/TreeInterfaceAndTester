Name: Roma Mudnal
EID: rlm4888

I first created a TInterface that takes a takes a key and value type which each of the classes implements. 
Then, I made instances of the BST, AVL, Red-Black, Splay Tree, and Skip List that implement 
the methods in the interface (get, remove, insert, contains). Then, I wrote
a simple tester file TTest that uses the interface to checks for the expected result versus the actual result in the assertEquals() method. 
Rather than using the JUnit framework with the built in assertEquals method, I quickly created a simple implementation myself because it 
would be easier to write rather than having to declare the types of every instance since they'r egeneric. Because the checkBalance method is 
not part of the TInterface interface, but rather a specific method implemented in the AVLTree class, I had to downcast the 
avlTree instance to the specific AVLTree type to access and call this method.
A simple toString is defined in the interface and ensures that all of the trees are visualized in the same way. 
A BinaryNode class and an AvlNode class are created, using Prof. Han's github code as a guideline. 