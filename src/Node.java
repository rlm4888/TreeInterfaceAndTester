public interface Node<T extends Comparable<T>> {
    T getData();
    void setData(T data);
    boolean getColor();
    void setColor(boolean color);
    Node<T> getParent();
    void setParent(Node<T> parent);
    Node<T> getLeft();
    void setLeft(Node<T> left);
    Node<T> getRight();
    void setRight(Node<T> right);

    class DefaultNode<T extends Comparable<T>> implements Node<T> {
        T data;
        boolean color;
        Node<T> parent;
        Node<T> left;
        Node<T> right;

        public DefaultNode(T data) {
            this.data = data;
        }

        @Override
        public T getData() {
            return data;
        }

        @Override
        public void setData(T data) {
            this.data = data;
        }

        @Override
        public boolean getColor() {
            return color;
        }

        @Override
        public void setColor(boolean color) {
            this.color = color;
        }

        @Override
        public Node<T> getParent() {
            return parent;
        }

        @Override
        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        @Override
        public Node<T> getLeft() {
            return left;
        }

        @Override
        public void setLeft(Node<T> left) {
            this.left = left;
        }

        @Override
        public Node<T> getRight() {
            return right;
        }

        @Override
        public void setRight(Node<T> right) {
            this.right = right;
        }

        @Override
        public String toString() {
            if (data == null) {
                return "null [B] ";
            }
            String str = data.toString();
            str += color ? "[B] " : "[R] ";
            return str;
        }
    }
}
