package ds.hashtable;

public class TestHashTable {
    public static void main(String[] args) {
        basicInsertSelectDeleteTest();
        testHashCollisionAndResizePerformance();
        testTreeifyPerformance();
    }

    public static void basicInsertSelectDeleteTest(){
        HashTable<Integer, String> hashTable = new HashTable<>(4);
        
        hashTable.insert(1, "Value 1");
        hashTable.insert(2, "Value 2");
        hashTable.insert(3, "Value 3");
        hashTable.insert(4, "Value 4");
        
        assert hashTable.get(1).equals("Value 1") : "测试失败: 键1的值不正确";  
        assert hashTable.get(2).equals("Value 2") : "测试失败: 键2的值不正确";  
        
        hashTable.insert(5, "Value 5");
        assert hashTable.get(5).equals("Value 5") : "测试失败: 扩容后键5的值不正确"; 
        
        assert hashTable.delete(2) : "测试失败: 删除键2失败"; 
        assert !hashTable.delete(10) : "测试失败: 删除键10时应返回false";  
        
        assert hashTable.get(2) == null : "测试失败: 键2未被删除";  
    }

    public static void testHashCollisionAndResizePerformance() {

        System.out.println("\n====  Resize 性能测试 ====");
        int initCapacity = 16;
        HashTable<Integer, String> ht = new HashTable<>(initCapacity);
    
        final int STEP = 1 << 20; 

        int n1 = 12;
        for (int i = 0; i < n1; i++) {
            int key = i * STEP;
            ht.insert(key, "Value " + i);
        }
    
        for (int r = 0; r < 3_000; r++) {
            ht.get(0);
        }
    
        long before = measureLookupNanos(ht, STEP, n1, 20_000);
    
        int n2 = 200;
        for (int i = n1; i < n2; i++) {
            int key = i * STEP;
            ht.insert(key, "Value " + i);
        }
    
        long after = measureLookupNanos(ht, STEP, n1, 20_000);
    
        for (int i = 0; i < n2; i++) {
            int key = i * STEP;
            String v = ht.get(key);
            assert v != null : "key=" + key + " 返回null";
            assert v.equals("Value " + i) : "key=" + key + " 值不正确，期望 " + i + " 实际 " + v;
        }
    
        System.out.println("resize 前查找用时(ns): " + before + "\nresize 后查找用时(ns): " + after);
    }
    
    private static long measureLookupNanos(HashTable<Integer, String> ht, int step, int keysCount, int rounds) {
        long start = System.nanoTime();
        for (int r = 0; r < rounds; r++) {
            for (int i = 0; i < keysCount; i++) {
                int key = i * step;
                String v = ht.get(key);
                assert v != null : "测量时 key=" + key + " 返回null";
            }
        }
        long end = System.nanoTime();
        return (end - start) / rounds;
    }

    public static void testTreeifyPerformance() {
        System.out.println("\n====  Treeify 性能测试 ====");
    
        final int CAPACITY = 128;           // 足够大，避免 resize 干扰
        final int STEP = 1 << 20;
        final int ROUNDS = 20_000;
    
        HashTable<Integer, String> listTable = new ListHashTable<>(CAPACITY);
    
        int listSize = 64; // < TREEIFY_THRESHOLD
        for (int i = 0; i < listSize; i++) {
            listTable.insert(i * STEP, "V" + i);
        }
    
        long listLookup = measureLookupNanos(listTable, STEP, listSize, ROUNDS);
        long listDelete = measureDeleteNanos(listTable, STEP, listSize, ROUNDS);
    
        System.out.println("[链表桶]");
        System.out.println("查找(ns): " + listLookup);
        System.out.println("删除(ns): " + listDelete);
    
        // -------- 2. 红黑树桶（treeify 后） --------
        HashTable<Integer, String> treeTable = new HashTable<>(CAPACITY);
    
        int treeSize = 64; // >> TREEIFY_THRESHOLD
        for (int i = 0; i < treeSize; i++) {
            treeTable.insert(i * STEP, "V" + i);
        }
    
        long treeLookup = measureLookupNanos(treeTable, STEP, treeSize, ROUNDS);
        long treeDelete = measureDeleteNanos(treeTable, STEP, treeSize, ROUNDS);
    
        System.out.println("[红黑树桶]");
        System.out.println("查找(ns): " + treeLookup);
        System.out.println("删除(ns): " + treeDelete);
    }
    
    private static long measureDeleteNanos(
        HashTable<Integer, String> ht,
        int step,
        int keysCount,
        int rounds
    ) {
        long total = 0;
        for (int r = 0; r < rounds; r++) {
            HashTable<Integer, String> table = new HashTable<>(64);
            for (int i = 0; i < keysCount; i++) {
                table.insert(i * step, "V" + i);
            }

            long start = System.nanoTime();
            for (int i = 0; i < keysCount; i++) {
                table.delete(i * step);
            }
            long end = System.nanoTime();
            total += (end - start);
        }
        return total / rounds;
    }
}

