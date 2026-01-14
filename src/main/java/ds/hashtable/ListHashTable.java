package ds.hashtable;

public class ListHashTable<K,V> extends HashTable<K,V> {
    private int size;

    private int capacity;
    private static final int DEFAULT_CAPACITY = 16;

    private static final float LOAD_FACTOR = 0.75f;
    private int threshold;
    private static final int TREEIFY_THRESHOLD = 8;
    private static final int UNTREEIFY_THRESHOLD = 6;
    private static final int MIN_TREEIFY_CAPACITY = 64;

    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    static final class TreeNode<K, V> extends Node<K, V> {
        TreeNode<K, V> parent, left, right, prev;
        boolean red;

        TreeNode(int hash, K key, V value, Node<K, V> next) {
            super(hash, key, value, next);
        }
    }

    private Node<K, V>[] buckets;

    public ListHashTable() {
        init(DEFAULT_CAPACITY);
    }

    public ListHashTable(int capacity) {
        init(capacity);
    }

    private void init(int capacity) {
        size = 0;
        this.capacity = capacity;
        threshold = (int) (capacity * LOAD_FACTOR);
        buckets = (Node<K, V>[]) new Node[capacity];
    }

    private void addSize() {
        size++;
        if (size > threshold) {
            resize();
        }
    }

    private void reduceSize() {
        size--;
    }

    private int computeHash(K key) {
        return key.hashCode();
    }

    private int computeBucketIndex(int hash) {
        return hash & (capacity - 1);
    }

    private void insertToBucket(Node<K, V> node) {
        int index = computeBucketIndex(node.hash);
        Node<K, V> iterator = buckets[index];
        if (iterator == null) {
            buckets[index] = node;
            return;
        }

        int length = 2;
        while (iterator.next != null) {
            length++;
            iterator = iterator.next;
        }
        iterator.next = node;

        if (NeedTreeify(length)) {
            treeify(index);
        }
    }

    private boolean NeedTreeify(int length) {
        return false;
    }

    private boolean NeedUnTreeify(K key, int hash, int index) {
        return false;
    }

    private void treeify(int index) {
        return;
    }

    /**
     * Convert a tree bin back to a plain linked list bin.
     *
     * @param head A TreeNode in this bucket (typically buckets[index]).
     */
    private void untreeify(TreeNode<K, V> head) {

    }

    // -------------------- Red-Black Tree helpers (tree bins) --------------------

    private static int compareTreeNode(int h1, Object k1, int h2, Object k2) {
        if (h1 < h2) return -1;
        if (h1 > h2) return 1;
        if (k1 == k2) return 0;
        if (k1 != null && k2 != null) {
            // If both keys are comparable and of the same class, use their natural order.
            if (k1.getClass() == k2.getClass() && k1 instanceof Comparable<?>) {
                @SuppressWarnings("unchecked")
                Comparable<Object> c1 = (Comparable<Object>) k1;
                int cmp = c1.compareTo(k2);
                if (cmp != 0) return cmp;
            }
        }
        // Tie-breaker to keep deterministic ordering for non-comparable keys with same hash.
        int i1 = System.identityHashCode(k1);
        int i2 = System.identityHashCode(k2);
        return (i1 < i2) ? -1 : (i1 > i2 ? 1 : 0);
    }

    private static <K, V> TreeNode<K, V> rootOf(TreeNode<K, V> x) {
        if (x == null) return null;
        TreeNode<K, V> r = x;
        while (r.parent != null) {
            r = r.parent;
        }
        return r;
    }

    private static <K, V> TreeNode<K, V> minimum(TreeNode<K, V> node) {
        TreeNode<K, V> p = node;
        while (p != null && p.left != null) {
            p = p.left;
        }
        return p;
    }

    private static <K, V> TreeNode<K, V> transplant(TreeNode<K, V> root, TreeNode<K, V> u, TreeNode<K, V> v) {
        TreeNode<K, V> up = u.parent;
        if (up == null) {
            root = v;
        } else if (u == up.left) {
            up.left = v;
        } else {
            up.right = v;
        }
        if (v != null) {
            v.parent = up;
        }
        return root;
    }

    private static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
        if (p != null) {
            TreeNode<K, V> r = p.right;
            if (r != null) {
                TreeNode<K, V> rl = r.left;
                p.right = rl;
                if (rl != null) rl.parent = p;

                TreeNode<K, V> pp = p.parent;
                r.parent = pp;
                if (pp == null) {
                    root = r;
                } else if (p == pp.left) {
                    pp.left = r;
                } else {
                    pp.right = r;
                }

                r.left = p;
                p.parent = r;
            }
        }
        return root;
    }

    private static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
        if (p != null) {
            TreeNode<K, V> l = p.left;
            if (l != null) {
                TreeNode<K, V> lr = l.right;
                p.left = lr;
                if (lr != null) lr.parent = p;

                TreeNode<K, V> pp = p.parent;
                l.parent = pp;
                if (pp == null) {
                    root = l;
                } else if (p == pp.right) {
                    pp.right = l;
                } else {
                    pp.left = l;
                }

                l.right = p;
                p.parent = l;
            }
        }
        return root;
    }

    private static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
        x.red = true;
        while (true) {
            TreeNode<K, V> xp = x.parent;
            if (xp == null) {
                x.red = false;
                return x;
            }
            if (!xp.red) {
                return root;
            }
            TreeNode<K, V> xpp = xp.parent;
            if (xpp == null) {
                xp.red = false;
                return root;
            }
            if (xp == xpp.left) {
                TreeNode<K, V> y = xpp.right;
                if (y != null && y.red) {
                    y.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                    continue;
                }
                if (x == xp.right) {
                    root = rotateLeft(root, xp);
                    TreeNode<K, V> tmp = xp;
                    xp = x;
                    x = tmp;
                }
                xp.red = false;
                xpp.red = true;
                root = rotateRight(root, xpp);
            } else {
                TreeNode<K, V> y = xpp.left;
                if (y != null && y.red) {
                    y.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                    continue;
                }
                if (x == xp.left) {
                    root = rotateRight(root, xp);
                    TreeNode<K, V> tmp = xp;
                    xp = x;
                    x = tmp;
                }
                xp.red = false;
                xpp.red = true;
                root = rotateLeft(root, xpp);
            }
            return root;
        }
    }

    private static <K, V> boolean isRed(TreeNode<K, V> n) {
        return n != null && n.red;
    }

    private static <K, V> boolean isBlack(TreeNode<K, V> n) {
        return n == null || !n.red;
    }

    /**
     * Deletion fix-up that supports x being null by also passing x's parent.
     * This follows the standard red-black tree delete-fix logic (CLRS-style).
     */
    private static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x, TreeNode<K, V> xParent) {
        while (x != root && isBlack(x)) {
            if (xParent == null) {
                break;
            }
            if (x == xParent.left) {
                TreeNode<K, V> sib = xParent.right;

                if (isRed(sib)) {
                    sib.red = false;
                    xParent.red = true;
                    root = rotateLeft(root, xParent);
                    sib = xParent.right;
                }

                TreeNode<K, V> sl = (sib == null) ? null : sib.left;
                TreeNode<K, V> sr = (sib == null) ? null : sib.right;

                if (isBlack(sl) && isBlack(sr)) {
                    if (sib != null) sib.red = true;
                    x = xParent;
                    xParent = xParent.parent;
                } else {
                    if (isBlack(sr)) {
                        if (sl != null) sl.red = false;
                        if (sib != null) {
                            sib.red = true;
                            root = rotateRight(root, sib);
                            sib = xParent.right;
                        }
                        sl = (sib == null) ? null : sib.left;
                        sr = (sib == null) ? null : sib.right;
                    }
                    if (sib != null) sib.red = xParent.red;
                    xParent.red = false;
                    if (sr != null) sr.red = false;
                    root = rotateLeft(root, xParent);
                    x = root;
                    break;
                }
            } else {
                TreeNode<K, V> sib = xParent.left;

                if (isRed(sib)) {
                    sib.red = false;
                    xParent.red = true;
                    root = rotateRight(root, xParent);
                    sib = xParent.left;
                }

                TreeNode<K, V> sl = (sib == null) ? null : sib.left;
                TreeNode<K, V> sr = (sib == null) ? null : sib.right;

                if (isBlack(sl) && isBlack(sr)) {
                    if (sib != null) sib.red = true;
                    x = xParent;
                    xParent = xParent.parent;
                } else {
                    if (isBlack(sl)) {
                        if (sr != null) sr.red = false;
                        if (sib != null) {
                            sib.red = true;
                            root = rotateLeft(root, sib);
                            sib = xParent.left;
                        }
                        sl = (sib == null) ? null : sib.left;
                        sr = (sib == null) ? null : sib.right;
                    }
                    if (sib != null) sib.red = xParent.red;
                    xParent.red = false;
                    if (sl != null) sl.red = false;
                    root = rotateRight(root, xParent);
                    x = root;
                    break;
                }
            }
        }
        if (x != null) x.red = false;
        return root;
    }

    private static <K, V> TreeNode<K, V> findTreeNode(TreeNode<K, V> root, int hash, Object key) {
        TreeNode<K, V> p = root;
        while (p != null) {
            int ph = p.hash;
            if (hash < ph) {
                p = p.left;
            } else if (hash > ph) {
                p = p.right;
            } else {
                Object pk = p.key;
                if (pk == key || (key != null && key.equals(pk))) {
                    return p;
                }
                int dir = compareTreeNode(hash, key, ph, pk);
                p = (dir < 0) ? p.left : p.right;
            }
        }
        return null;
    }

    private void insertTreeNode(int index, TreeNode<K, V> x) {
        TreeNode<K, V> head = (TreeNode<K, V>) buckets[index];
        if (head == null) {
            x.red = false;
            buckets[index] = x;
            return;
        }

        // Append to linked list tail (for selectNode() and iteration).
        TreeNode<K, V> tail = head;
        while (tail.next != null) {
            tail = (TreeNode<K, V>) tail.next;
        }
        tail.next = x;
        x.prev = tail;
        x.next = null;

        // Insert into the red-black tree.
        TreeNode<K, V> root = rootOf(head);
        x.left = x.right = x.parent = null;

        TreeNode<K, V> p = root;
        TreeNode<K, V> parent;
        int dir = 0;
        while (p != null) {
            parent = p;
            dir = compareTreeNode(x.hash, x.key, p.hash, p.key);
            if (dir < 0) {
                p = p.left;
            } else {
                p = p.right;
            }
            if (p == null) {
                x.parent = parent;
                if (dir < 0) {
                    parent.left = x;
                } else {
                    parent.right = x;
                }
                break;
            }
        }
        balanceInsertion(root, x);
        // root is reachable via parent pointers; bucket keeps head pointer unchanged.
    }

    public void insert(K key, V value) {
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);

        // Overwrite existing value if key already exists.
        Node<K, V> existing = selectNode(key);
        if (existing != null) {
            existing.value = value;
            return;
        }

        addSize();

        if (buckets[index] instanceof TreeNode) {
            TreeNode<K, V> node = new TreeNode<>(hash, key, value, null);
            insertTreeNode(index, node);
        } else {
            Node<K, V> node = new Node<>(hash, key, value, null);
            insertToBucket(node);
        }
    }

    private Node<K, V> selectNode(K key) {
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);
        Node<K, V> iterator = buckets[index];
        while (iterator != null) {
            if (iterator.hash == hash && iterator.key.equals(key)) {
                return iterator;
            } else {
                iterator = iterator.next;
            }
        }
        return null;
    }

    public V get(K key) {
        Node<K, V> node = selectNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    private boolean deleteListNode(K key, int hash, int index) {
        if (buckets[index] instanceof TreeNode) {
            return false;
        }
        Node<K, V> iterator = buckets[index];
        Node<K, V> parent = null;
        while (iterator != null) {
            if (iterator.hash == hash && iterator.key.equals(key)) {
                if (parent != null) {
                    parent.next = iterator.next;
                } else {
                    buckets[index] = iterator.next;
                }
                reduceSize();
                return true;
            } else {
                parent = iterator;
                iterator = iterator.next;
            }
        }
        return false;
    }

    private boolean deleteTreeNode(K key, int hash, int index) {
        if (!(buckets[index] instanceof TreeNode)) {
            return false;
        }

        TreeNode<K, V> head = (TreeNode<K, V>) buckets[index];
        TreeNode<K, V> root = rootOf(head);

        TreeNode<K, V> z = findTreeNode(root, hash, key);
        if (z == null) {
            return false;
        }

        // Unlink from linked list (next/prev) first.
        TreeNode<K, V> zNext = (TreeNode<K, V>) z.next;
        TreeNode<K, V> zPrev = z.prev;
        if (zPrev == null) {
            buckets[index] = zNext;
        } else {
            zPrev.next = zNext;
        }
        if (zNext != null) {
            zNext.prev = zPrev;
        }

        // --- Red-black delete (CLRS style) ---
        TreeNode<K, V> y = z;
        boolean yOriginalRed = y.red;

        TreeNode<K, V> x;
        TreeNode<K, V> xParent;

        if (z.left == null) {
            x = z.right;
            xParent = z.parent;
            root = transplant(root, z, z.right);
        } else if (z.right == null) {
            x = z.left;
            xParent = z.parent;
            root = transplant(root, z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalRed = y.red;
            x = y.right;

            if (y.parent == z) {
                xParent = y;
            } else {
                xParent = y.parent;
                root = transplant(root, y, y.right);
                y.right = z.right;
                if (y.right != null) y.right.parent = y;
            }

            root = transplant(root, z, y);
            y.left = z.left;
            if (y.left != null) y.left.parent = y;
            y.red = z.red;
        }

        // Help GC / avoid accidental misuse
        z.left = z.right = z.parent = null;

        if (!yOriginalRed) {
            root = balanceDeletion(root, x, xParent);
        }

        reduceSize();

        // If bucket is empty now
        if (buckets[index] == null) {
            return true;
        }

        if (NeedUnTreeify(key, hash, index)) {
            untreeify((TreeNode<K, V>) buckets[index]);
        }
        return true;
    }

    public boolean delete(K key) {
        int hash = computeHash(key);
        int index = computeBucketIndex(hash);
        if (buckets[index] instanceof TreeNode) {
            return deleteTreeNode(key, hash, index);
        } else {
            return deleteListNode(key, hash, index);
        }
    }

    private void resize() {
        int old_cap = capacity;
        int new_cap = old_cap << 1;

        capacity = new_cap;
        threshold = threshold << 1;

        Node<K, V>[] new_buckets = (Node<K, V>[]) new Node[new_cap];

        // Simplified rehashing:
        // - Iterate each old bucket via its linked list (works for both Node and TreeNode bins because TreeNode extends Node).
        // - Recreate plain Nodes in new buckets (discarding any tree pointers), and let future insertions re-treeify if needed.
        for (int i = 0; i < old_cap; i++) {
            Node<K, V> iter = buckets[i];
            if (iter == null) {
                continue;
            }

            Node<K, V> lo_head = null, lo_tail = null;
            Node<K, V> hi_head = null, hi_tail = null;

            while (iter != null) {
                Node<K, V> next = iter.next;
                Node<K, V> n = new Node<>(iter.hash, iter.key, iter.value, null);

                if ((iter.hash & old_cap) == 0) {
                    if (lo_tail == null) {
                        lo_head = n;
                    } else {
                        lo_tail.next = n;
                    }
                    lo_tail = n;
                } else {
                    if (hi_tail == null) {
                        hi_head = n;
                    } else {
                        hi_tail.next = n;
                    }
                    hi_tail = n;
                }

                iter = next;
            }

            if (lo_head != null) {
                new_buckets[i] = lo_head;
            }
            if (hi_head != null) {
                new_buckets[i + old_cap] = hi_head;
            }
        }

        buckets = new_buckets;
    }
}
