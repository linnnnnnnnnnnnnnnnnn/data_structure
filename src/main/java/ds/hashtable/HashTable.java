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
    
    private static class HashNode <K, V> {
        final int hash;
        final K key;
        V value;
        HashNode<K,V> next;

        HashNode(int hash, K key, V value, HashNode<K,V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private HashNode<K,V>[] buckets;

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
        buckets = (HashNode<K,V>[]) new HashNode[capacity];
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

    private void insertToBucket(HashNode<K,V> node){
        int index = computeBucketIndex(node.hash);
        HashNode<K,V> iterator = buckets[index];
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
        if(length > TREEIFY_THRESHOLD && size > MIN_TREEIFY_CAPACITY){
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
        HashNode<K,V> NodeToInsert = new HashNode<K,V>(hash, key, value, null);
        addSize();
        insertToBucket(NodeToInsert);
    }

    private HashNode<K,V> selectNode(K key){
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);
        HashNode<K,V> iterator = buckets[index];
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
        HashNode<K,V> node = selectNode(key);
        if(node == null){
            return null;
        }
        return node.value;
    }

    public boolean delete(K key){
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);
        HashNode<K,V> iterator = buckets[index];
        HashNode<K,V> parent = null;
        while(iterator != null){
            if(iterator.hash == hash && iterator.key.equals(key)){
                if(parent != null){
                    parent.next = iterator.next;
                }else{
                    buckets[index] = iterator.next;
                }
                reduceSize();
                // to be implement 
                if(NeedUnTreeify(size)){
                    untreeify();
                }
                return true;
            }else{
                parent = iterator;
                iterator = iterator.next;
            }
        }
        return false;
    }

    private void resize(){
        int old_cap = capacity;
        capacity = capacity << 1;
        threshold = threshold << 1;
        HashNode<K, V>[] new_buckets = (HashNode<K, V>[])new HashNode[capacity];
        for(int i = 0; i < old_cap; i++){
            HashNode<K,V> iter = buckets[i];

            if(iter == null){
                continue;
            }

            if(iter.next == null){
                int index = computeBucketIndex(iter.hash);
                new_buckets[index] = iter;
                continue;
            }
            HashNode<K,V> lo_head = null, lo_tail = null;
            HashNode<K,V> hi_head = null, hi_tail = null;
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
