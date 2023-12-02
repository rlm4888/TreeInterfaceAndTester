public interface TInterface<Key extends Comparable<Key>, Value> {
    public void insert(Key key, Value value);
    public void remove(Key key);
    public Value get(Key key);
    public boolean contains(Key key);

    public String toString();
}
