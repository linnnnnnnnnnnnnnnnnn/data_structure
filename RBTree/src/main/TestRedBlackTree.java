package src.main;

import java.util.*;

public class TestRedBlackTree {

    public static void main(String[] args) {
        try {
            testInsertAndRBProperties();
            testAscending();
            testDescending();
            testRandomStress();
            System.out.println("所有插入性质测试通过");
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
}
