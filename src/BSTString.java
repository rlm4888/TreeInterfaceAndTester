abstract class BSTString<T extends Comparable<T>> {
    static final boolean RED = false;
    static final boolean BLACK = true;

    protected abstract Node<T> getRoot();

    public String traversePreOrder() {
        StringBuilder sb = new StringBuilder();
        traverseNodes(sb, "", "", getRoot(), false);
        return sb.toString();
    }

    private void traverseNodes(StringBuilder sb, String padding, String pointer, Node<T> bstString, boolean hasRightSibling) {
        if (bstString != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(bstString);

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (bstString.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, bstString.getLeft(), bstString.getRight() != null);
            traverseNodes(sb, paddingForBoth, pointerRight, bstString.getRight(), false);
        } else {
            sb.append("\n");
            sb.append(padding);
            sb.append("-");
        }
    }
}
