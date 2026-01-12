package ds.hashtable;

public class HashTable <K, V>{
    private int size;

    private int capacity;
    private static final int DEFAULT_CAPACITY = 16;

    private static final float LOAD_FACTOR = 0.75f;
    private int threshold;
    private static final int TREEIFY_THRESHOLD = 8;
    private static final int UNTREEIFY_THRESHOLD = 6;
    private static final int MIN_TREEIFY_CAPACITY = 64;
    
    private static class Node <K, V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    static final class TreeNode<K,V> extends Node<K,V> {
        TreeNode<K,V> parent, left, right, prev;
        boolean red;

        TreeNode(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }


    private Node<K,V>[] buckets;

    public HashTable(){
        init(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity){
        init(capacity);
    }

    private void init(int capacity){
        size = 0;
        this.capacity = capacity;
        threshold = (int)(capacity*LOAD_FACTOR);
        buckets = (Node<K,V>[]) new Node[capacity];
    }

    private void addSize(){
        size++;
        if(size > threshold){
            resize();
        }
    }
    
    private void reduceSize(){
        size--;
    }

    private int computeHash(K key){
        return key.hashCode();
    }

    private int computeBucketIndex(int hash){
        return hash&(capacity - 1);
    }

    private void insertToBucket(Node<K,V> node){
        int index = computeBucketIndex(node.hash);
        Node<K,V> iterator = buckets[index];
        if(iterator == null){
            buckets[index] = node;
            return;
        }
        int length = 2;
        while(iterator.next != null){
            length++;
            iterator = iterator.next;
        }
        iterator.next = node;
        
        if(NeedTreeify(length)){
            treeify();
        }
    }

    private boolean NeedTreeify(int length){
        if(length > TREEIFY_THRESHOLD && capacity > MIN_TREEIFY_CAPACITY){
            return true;
        }else{
            return false;
        }
    }

    private boolean NeedUnTreeify(int length){
        if(length < UNTREEIFY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }

    private void treeify(){

    }

    private void untreeify(){

    }

    public void insert(K key, V value){
        int hash = computeHash(key);
        Node<K,V> NodeToInsert = new Node<K,V>(hash, key, value, null);
        addSize();
        insertToBucket(NodeToInsert);
    }

    private Node<K,V> selectNode(K key){
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);
        Node<K,V> iterator = buckets[index];
        while(iterator != null){
            if(iterator.hash == hash && iterator.key.equals(key)){
                return iterator;
            }else{
                iterator = iterator.next;
            }
        }
        return null;
    }

    public V get(K key){
        Node<K,V> node = selectNode(key);
        if(node == null){
            return null;
        }
        return node.value;
    }

    private boolean deleteListNode(K key, int hash, int index){
        Node<K,V> iterator = buckets[index];
        Node<K,V> parent = null;
        while(iterator != null){
            if(iterator.hash == hash && iterator.key.equals(key)){
                if(parent != null){
                    parent.next = iterator.next;
                }else{
                    buckets[index] = iterator.next;
                }
                reduceSize();
                return true;
            }else{
                parent = iterator;
                iterator = iterator.next;
            }
        }
        return false;
    }
    
    public boolean delete(K key){
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);
        return deleteListNode(key, hash, index);
    }

    private void resize(){
        int old_cap = capacity;
        capacity = capacity << 1;
        threshold = threshold << 1;
        Node<K, V>[] new_buckets = (Node<K, V>[])new Node[capacity];
        for(int i = 0; i < old_cap; i++){
            Node<K,V> iter = buckets[i];

            if(iter == null){
                continue;
            }

            if(iter.next == null){
                int index = computeBucketIndex(iter.hash);
                new_buckets[index] = iter;
                continue;
            }
            Node<K,V> lo_head = null, lo_tail = null;
            Node<K,V> hi_head = null, hi_tail = null;
            while(iter != null){
                if((iter.hash&old_cap) == 0){
                    if(lo_head == null){
                        lo_head = iter;
                    }else{
                        lo_tail.next = iter;
                    }
                    lo_tail = iter;
                }else{
                    if(hi_head == null){
                        hi_head = iter;
                    }else{
                        hi_tail.next = iter;
                    }
                    hi_tail = iter;
                }
                iter = iter.next;
            }
            if(lo_head != null){
                lo_tail.next = null;
                new_buckets[i] = lo_head;
            }
            if(hi_head != null){
                hi_tail.next = null;
                new_buckets[i + old_cap] = hi_head;
            }
        }
        buckets = new_buckets;
    }
}   
