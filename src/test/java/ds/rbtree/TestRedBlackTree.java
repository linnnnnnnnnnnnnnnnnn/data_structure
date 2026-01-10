package ds.rbtree;

import java.util.*;

public class TestRedBlackTree {

    public static void main(String[] args) {
        try {
            testInsertAndRBProperties();
            testAscending();
            testDescending();
            testRandomStress();
            System.out.println("所有插入性质测试通过");

            testDeleteFixedCases();
            testDeleteAscending();
            testDeleteDescending();
            testDeleteRandomStressAgainstTreeMap();
            System.out.println("所有删除性质测试通过");
        } catch (AssertionError e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private static void testInsertAndRBProperties() {
        RedBlackTree<Integer, String> t = new RedBlackTree<>();
        int[] keys = {10, 5, 15, 1, 6, 12, 18, 3, 8, 7};

        for (int k : keys) {
            insertAndAssert(t, k, "V" + k, "fixed-seq");
        }
    }

    private static void testAscending() {
        RedBlackTree<Integer, String> t = new RedBlackTree<>();
        for (int k = 1; k <= 200; k++) {
            insertAndAssert(t, k, "V" + k, "ascending");
        }
    }

    private static void testDescending() {
        RedBlackTree<Integer, String> t = new RedBlackTree<>();
        for (int k = 200; k >= 1; k--) {
            insertAndAssert(t, k, "V" + k, "descending");
        }
    }

    private static void testRandomStress() {
        for (int round = 1; round <= 20; round++) {
            RedBlackTree<Integer, Integer> t = new RedBlackTree<>();
            Random rnd = new Random(2026L + round);

            Set<Integer> used = new HashSet<>();
            while (used.size() < 1000) {
                int k = rnd.nextInt(1_000_000);
                if (used.add(k)) {
                    insertAndAssert(t, k, k, "random round=" + round);
                }
            }
        }
    }

    private static <K extends Comparable<K>, V> void insertAndAssert(
            RedBlackTree<K, V> t, K key, V value, String tag
    ) {
        boolean ok = t.insert(key, value);
        if (!ok) {
            dumpTree(t);
            throw new AssertionError("insert failed: " + tag + " key=" + key);
        }

        boolean valid = t.validateRBTree();
        if (!valid) {
            dumpTree(t);
            throw new AssertionError("RB invariant violated: " + tag + " after insert key=" + key);
        }

        V got = t.get(key);
        if (!Objects.equals(value, got)) {
            dumpTree(t);
            throw new AssertionError("get mismatch: " + tag + " key=" + key
                    + " expected=" + value + " actual=" + got);
        }
    }

    private static <K extends Comparable<K>, V> void dumpTree(RedBlackTree<K, V> t) {
        System.err.println("\n========== RBTree Dump ==========");

        try {
            List<List<V>> levelsV = t.levelOrderTravelValue();
            List<List<Boolean>> levelsC = t.levelOrderTravelColor();

            int L = Math.max(levelsV == null ? 0 : levelsV.size(),
                             levelsC == null ? 0 : levelsC.size());

            for (int i = 0; i < L; i++) {
                List<V> lv = (levelsV != null && i < levelsV.size()) ? levelsV.get(i) : Collections.emptyList();
                List<Boolean> lc = (levelsC != null && i < levelsC.size()) ? levelsC.get(i) : Collections.emptyList();

                StringBuilder sb = new StringBuilder();
                sb.append("L").append(i).append(": ");

                int w = Math.max(lv.size(), lc.size());
                for (int j = 0; j < w; j++) {
                    V v = j < lv.size() ? lv.get(j) : null;
                    Boolean c = j < lc.size() ? lc.get(j) : null;
                    sb.append("[")
                      .append(v)
                      .append("(")
                      .append(colorToStr(c))
                      .append(")] ");
                }
                System.err.println(sb.toString().trim());
            }

            System.err.println("=================================\n");
        } catch (Exception ex) {
            System.err.println("dumpTree failed: " + ex);
            System.err.println("=================================\n");
        }
    }

    private static String colorToStr(Boolean c) {
        if (c == null) return "?";
        return c ? "R" : "B"; // RED=true, BLACK=false
    }

    private static void testDeleteFixedCases() {
        RedBlackTree<Integer, String> t = new RedBlackTree<>();
        int[] keys = {10, 5, 15, 1, 6, 12, 18, 3, 8, 7};
        for (int k : keys) t.insert(k, "V" + k);

        deleteExpect(t, 999, false, "fixed delete missing");

        int[] delOrder = {1, 3, 6, 10, 15, 7, 8, 5, 12, 18};
        for (int k : delOrder) {
            deleteAndAssertAbsent(t, k, "fixed delete");
        }

        deleteExpect(t, 10, false, "fixed delete after empty");
        assertValid(t, "fixed final");
    }

    private static void testDeleteAscending() {
        RedBlackTree<Integer, String> t = new RedBlackTree<>();
        for (int k = 1; k <= 300; k++) t.insert(k, "V" + k);

        for (int k = 1; k <= 300; k++) {
            deleteAndAssertAbsent(t, k, "delete ascending");
        }
        assertValid(t, "delete ascending final");
    }

    private static void testDeleteDescending() {
        RedBlackTree<Integer, String> t = new RedBlackTree<>();
        for (int k = 1; k <= 300; k++) t.insert(k, "V" + k);

        for (int k = 300; k >= 1; k--) {
            deleteAndAssertAbsent(t, k, "delete descending");
        }
        assertValid(t, "delete descending final");
    }

    private static void testDeleteRandomStressAgainstTreeMap() {
        for (int round = 1; round <= 20; round++) {
            RedBlackTree<Integer, Integer> t = new RedBlackTree<>();
            TreeMap<Integer, Integer> ref = new TreeMap<>();
            Random rnd = new Random(9090L + round);

            // 1) insert
            while (ref.size() < 2000) {
                int k = rnd.nextInt(2_000_000);
                if (!ref.containsKey(k)) {
                    int v = k ^ (round * 131);
                    ref.put(k, v);

                    boolean ok = t.insert(k, v);
                    if (!ok) {
                        dumpTree(t);
                        throw new AssertionError("insert failed: del-stress round=" + round + " key=" + k);
                    }
                    assertValid(t, "del-stress insert round=" + round);

                    Integer got = t.get(k);
                    if (!Objects.equals(v, got)) {
                        dumpTree(t);
                        throw new AssertionError("get mismatch after insert: del-stress round=" + round +
                                " key=" + k + " expected=" + v + " actual=" + got);
                    }
                }
            }

            // 2) delete half (shuffle)
            List<Integer> allKeys = new ArrayList<>(ref.keySet());
            Collections.shuffle(allKeys, rnd);

            int deletes = allKeys.size() / 2;
            for (int i = 0; i < deletes; i++) {
                int k = allKeys.get(i);

                boolean ok = t.delete(k);
                if (!ok) {
                    dumpTree(t);
                    throw new AssertionError("delete failed: del-stress round=" + round + " key=" + k);
                }
                ref.remove(k);

                assertValid(t, "del-stress delete round=" + round + " key=" + k);

                // deleted key should be absent
                Integer got = t.get(k);
                if (got != null) {
                    dumpTree(t);
                    throw new AssertionError("key still present after delete: del-stress round=" + round +
                            " key=" + k + " got=" + got);
                }

                // spot-check some remaining keys 
                for (int s = 0; s < 5 && !ref.isEmpty(); s++) {
                    int idx = rnd.nextInt(allKeys.size());
                    int probe = allKeys.get(idx);
                    Integer exp = ref.get(probe);
                    Integer act = t.get(probe);
                    if (!Objects.equals(exp, act)) {
                        dumpTree(t);
                        throw new AssertionError("spot-check mismatch: del-stress round=" + round +
                                " probe=" + probe + " expected=" + exp + " actual=" + act);
                    }
                }
            }

            // 3) delete missing keys checks
            for (int j = 0; j < 50; j++) {
                int miss = 3_000_000 + rnd.nextInt(1_000_000);
                deleteExpect(t, miss, false, "del-stress missing round=" + round);
            }

            assertValid(t, "del-stress final round=" + round);
        }
    }

    private static <K extends Comparable<K>, V> void deleteAndAssertAbsent(
            RedBlackTree<K, V> t, K key, String tag
    ) {
        boolean ok = t.delete(key);
        if (!ok) {
            dumpTree(t);
            throw new AssertionError("delete failed: " + tag + " key=" + key);
        }

        boolean valid = t.validateRBTree();
        if (!valid) {
            dumpTree(t);
            throw new AssertionError("RB invariant violated: " + tag + " after delete key=" + key);
        }

        V got = t.get(key);
        if (got != null) {
            dumpTree(t);
            throw new AssertionError("delete ineffective: " + tag + " key=" + key + " still get=" + got);
        }
    }

    private static <K extends Comparable<K>, V> void deleteExpect(
            RedBlackTree<K, V> t, K key, boolean expected, String tag
    ) {
        boolean ok = t.delete(key);
        if (ok != expected) {
            dumpTree(t);
            throw new AssertionError("delete return mismatch: " + tag + " key=" + key +
                    " expected=" + expected + " actual=" + ok);
        }
        assertValid(t, tag + " key=" + key);
    }

    private static <K extends Comparable<K>, V> void assertValid(RedBlackTree<K, V> t, String tag) {
        boolean valid = t.validateRBTree();
        if (!valid) {
            dumpTree(t);
            throw new AssertionError("RB invariant violated: " + tag);
        }
    }

}
