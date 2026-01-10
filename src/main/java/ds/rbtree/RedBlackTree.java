package ds.rbtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RedBlackTree <K extends Comparable<K>, V> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        K key;
        V value;
        Node left, right, parent;
        boolean color;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.color = RED;
            this.left = nilNode;
            this.right = nilNode;
            this.parent = nilNode;
        }

        Node() {
            this.key = null;
            this.value = null;
            this.color = BLACK;
            this.left = this;
            this.right = this;
            this.parent = this;
        }
    }

    private Node rootNode;
    private Node nilNode = new Node();
    
    RedBlackTree() {
        rootNode = nilNode;
    }
    RedBlackTree(K key, V value) {
        rootNode = new Node(key, value);
        rootNode.color = BLACK;
    }

    private boolean BSTinsertNode(Node node, Node root){
        if(root == nilNode){
            return false;
        }

        if(node.key.compareTo(root.key) < 0){
            if(root.left == nilNode){
                root.left = node;
                node.parent = root;
                return true;
            } else {
                return BSTinsertNode(node, root.left);
            }
        }else if(node.key.compareTo(root.key) > 0){
            if(root.right == nilNode){
                root.right = node;
                node.parent = root;
                return true;
            } else {
                return BSTinsertNode(node, root.right);
            }
        } else {
            return false;
        }
    }

    private Node BSTsearchNode(Node root, K key){
        if (root == nilNode){
            return nilNode;
        }
        if (root.key.compareTo(key) == 0){
            return root;
        } else if (root.key.compareTo(key) > 0){
            return BSTsearchNode(root.left, key);
        } else {
            return BSTsearchNode(root.right, key);
        }
    }

    private void rightRotate(Node root){
        Node leftChild = root.left;
        root.left = leftChild.right;
        if (leftChild.right != nilNode){
            leftChild.right.parent = root;
        }
        leftChild.parent = root.parent;
        if (root.parent == nilNode){
            rootNode = leftChild;
        } else if (root == root.parent.right){
            root.parent.right = leftChild;
        } else {
            root.parent.left = leftChild;
        }
        leftChild.right = root;
        root.parent = leftChild;
    }

    private void leftRotate(Node root){
        Node rightChild = root.right;
        root.right = rightChild.left;
        if (rightChild.left != nilNode){
            rightChild.left.parent = root;
        }
        rightChild.parent = root.parent;
        if (root.parent == nilNode){
            rootNode = rightChild;
        } else if (root == root.parent.left){
            root.parent.left = rightChild;
        } else {
            root.parent.right = rightChild;
        }
        rightChild.left = root;
        root.parent = rightChild;
    }

    private void fixViolation(Node node){
        while(node != rootNode && node != nilNode && node.parent != nilNode && node.parent.color == RED){
            Node grandParent = node.parent.parent;
            Node parent = node.parent;

            if(grandParent == nilNode){
                break;
            }

            Node uncleNode = null;
            if(node.parent == grandParent.left){
                uncleNode = grandParent.right;
            }else{
                uncleNode = grandParent.left;
            }

            if(uncleNode == nilNode || uncleNode.color == BLACK){
                if(node.parent == grandParent.left){
                    if(node == node.parent.left){
                        rightRotate(grandParent);
                        parent.color = BLACK;
                        grandParent.color = RED;
                        break;
                    }else{
                        leftRotate(node.parent);
                        rightRotate(grandParent);
                        node.color = BLACK;
                        grandParent.color = RED;
                        parent.color = RED;
                        break;
                    }
                }else{
                    if(node == node.parent.right){
                        leftRotate(grandParent);
                        parent.color = BLACK;
                        grandParent.color = RED;
                        break;
                    } else {
                        rightRotate(node.parent);
                        leftRotate(grandParent);
                        node.color = BLACK;
                        parent.color = RED;
                        grandParent.color = RED;
                        break;
                    }
                }
            } else if (uncleNode.color == RED) {
                uncleNode.color = BLACK;
                node.parent.color = BLACK;
                grandParent.color = RED;
                node = node.parent.parent;
            }
        }

        rootNode.color = BLACK;
    }

    public boolean insert(K key, V value){
        Node newNode = new Node(key, value);
        if (rootNode == nilNode){
            newNode.color = BLACK;
            rootNode = newNode;
            return true;
        }
        boolean inserted = BSTinsertNode(newNode, rootNode);
        if (inserted){
            // there must be a rootnode, which means insertedNode must have a parent.
            fixViolation(newNode);
            rootNode.color = BLACK;
        }
        return inserted;
    }

    public V get(K key) {
        Node n = BSTsearchNode(rootNode, key);
        if (n == nilNode) {
            return null;
        }
        return n.value;
    }

    public Boolean getColor(K key){
        Node n = BSTsearchNode(rootNode, key);
        if (n == nilNode){
            return null;
        }
        return n.color;
    }
    
    private void transplant(Node u, Node v){
        if(u.parent == nilNode){
            rootNode = v;
        } else if (u == u.parent.left){
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    private Node minimum(Node node){
        while(node.left != nilNode){
            node = node.left;
        }
        return node;
    }

    private void deleteFixup(Node x){
        while (x != rootNode && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right; // sibling
                // Case 1: sibling is RED
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                // Case 2: sibling BLACK and both children BLACK
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    // Case 3: sibling BLACK, sibling.right BLACK, sibling.left RED
                    if (w.right.color == BLACK) {
                        w.left.color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    // Case 4: sibling BLACK, sibling.right RED
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = rootNode;
                }
            } else {
                // symmetric
                Node w = x.parent.left;
                // Case 1
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                // Case 2
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    // Case 3
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    // Case 4
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = rootNode;
                }
            }
        }
        x.color = BLACK;
    }
    
    private boolean deleteNode(Node z) {
        if (rootNode == nilNode || z == nilNode) return false;
    
        Node y = z;                
        boolean yOriginalColor = y.color;
        Node x;                
    
        if (z.left == nilNode) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == nilNode) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right; 
    
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
    
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
    
        if (yOriginalColor == BLACK) {
            deleteFixup(x);
        }
    
        if (rootNode != nilNode) rootNode.parent = nilNode;
    
        return true;
    }
    
    public boolean delete(K key){
        Node nodeToDelete = BSTsearchNode(rootNode, key);
        return deleteNode(nodeToDelete);
    }

    public List<List<V>> levelOrderTravelValue(){
        List<List<V>> levels = new ArrayList<>();
        if(rootNode == nilNode){
            return levels;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.offer(rootNode);
        while(!queue.isEmpty()){
            int size = queue.size();
            List<V> level = new ArrayList<>();
            for(int i = 0; i < size; i++){
                Node curr = queue.poll();
                level.add(curr.value);

                if(curr.left != nilNode){
                    queue.offer(curr.left);
                }
                if (curr.right != nilNode){
                    queue.offer(curr.right);
                }
            }
            levels.add(level);
        }

        return levels;
    }

    public List<List<Boolean>> levelOrderTravelColor(){
        List<List<Boolean>> levels = new ArrayList<>();
        if(rootNode == nilNode){
            return levels;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.offer(rootNode);

        while(!queue.isEmpty()){
            int size = queue.size();
            List<Boolean> level = new ArrayList<>();
            for(int i = 0; i < size; i++){
                Node curr = queue.poll();
                level.add(curr.color);

                if(curr.left != nilNode){
                    queue.offer(curr.left);
                }
                if (curr.right != nilNode){
                    queue.offer(curr.right);
                }
            }
            levels.add(level);
        }

        return levels;
    }

    public boolean validateRBTree() {
        if (rootNode == nilNode) return true;
    
        // 1) 根必须是黑
        if (rootNode.color != BLACK) return false;
    
        // 2) parent 指针一致性
        if (rootNode.parent != nilNode) return false;
    
        // 3) 综合校验：BST + 红红冲突 + 黑高一致
        return validateAndBlackHeight(rootNode, null, null) != -1;
    }
    
    private int validateAndBlackHeight(Node node, K min, K max) {
        if (node == nilNode) {
            // NIL 视为黑：黑高 +1（你也可以返回 1 或 0，只要全程一致即可）
            return 1;
        }
    
        // A) BST 有序性：min < key < max
        if (min != null && node.key.compareTo(min) <= 0) return -1;
        if (max != null && node.key.compareTo(max) >= 0) return -1;
    
        // B) parent 指针一致性
        if (node.left != nilNode && node.left.parent != node) return -1;
        if (node.right != nilNode && node.right.parent != node) return -1;
    
        // C) 红红冲突：红节点不能有红孩子
        if (node.color == RED) {
            if ((node.left != nilNode && node.left.color == RED) ||
                (node.right != nilNode && node.right.color == RED)) {
                return -1;
            }
        }
    
        // D) 递归检查左右子树
        int leftBH = validateAndBlackHeight(node.left, min, node.key);
        if (leftBH == -1) return -1;
    
        int rightBH = validateAndBlackHeight(node.right, node.key, max);
        if (rightBH == -1) return -1;
    
        // E) 黑高一致
        if (leftBH != rightBH) return -1;
    
        // F) 返回本节点的黑高
        return leftBH + (node.color == BLACK ? 1 : 0);
    }
}
