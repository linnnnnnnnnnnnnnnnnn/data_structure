package src.main;

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
        }
    }

    private Node rootNode;
    
    RedBlackTree() {
        rootNode = null;
    }
    RedBlackTree(K key, V value) {
        rootNode = new Node(key, value);
        rootNode.color = BLACK;
    }

    private boolean BSTinsertNode(Node node, Node root){
        if(node.key.compareTo(root.key) < 0){
            if(root.left == null){
                root.left = node;
                node.parent = root;
                return true;
            } else {
                return BSTinsertNode(node, root.left);
            }
        }else if(node.key.compareTo(root.key) > 0){
            if(root.right == null){
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
        if (root == null){
            return null;
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
        if (leftChild.right != null){
            leftChild.right.parent = root;
        }
        leftChild.parent = root.parent;
        if (root.parent == null){
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
        if (rightChild.left != null){
            rightChild.left.parent = root;
        }
        rightChild.parent = root.parent;
        if (root.parent == null){
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
        while(node != rootNode && node != null && node.parent != null && node.parent.color == RED){
            Node grandParent = node.parent.parent;
            Node parent = node.parent;

            Node uncleNode = null;
            if(node.parent == grandParent.left){
                uncleNode = grandParent.right;
            }else{
                uncleNode = grandParent.left;
            }

            if(uncleNode == null || uncleNode.color == BLACK){
                if(node.parent == grandParent.left){
                    if(node == node.parent.left){
                        rightRotate(grandParent);
                        parent.color = BLACK;
                        grandParent.color = RED;
                    }else{
                        leftRotate(node.parent);
                        rightRotate(grandParent);
                        node.color = BLACK;
                        grandParent.color = RED;
                        parent.color = BLACK;
                    }
                }else{
                    if(node == node.parent.right){
                        leftRotate(grandParent);
                        parent.color = BLACK;
                        grandParent.color = RED;
                    } else {
                        rightRotate(node.parent);
                        leftRotate(grandParent);
                        node.color = BLACK;
                        parent.color = RED;
                        grandParent.color = RED;
                    }
                }
                node = node.parent;
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
        if (rootNode == null){
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
        return BSTsearchNode(rootNode, key).value;
    }

    public Boolean getColor(K key){
        return BSTsearchNode(rootNode, key).color;
    }

    public List<List<V>> levelOrderTravelValue(){
        List<List<V>> levels = new ArrayList<>();
        if(rootNode == null){
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

                if(curr.left != null){
                    queue.offer(curr.left);
                }
                if (curr.right != null){
                    queue.offer(curr.right);
                }
            }
            levels.add(level);
        }

        return levels;
    }

    public List<List<Boolean>> levelOrderTravelColor(){
        List<List<Boolean>> levels = new ArrayList<>();
        if(rootNode == null){
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

                if(curr.left != null){
                    queue.offer(curr.left);
                }
                if (curr.right != null){
                    queue.offer(curr.right);
                }
            }
            levels.add(level);
        }

        return levels;
    }

    public boolean validateRBTree() {
        if (rootNode == null) return true;
    
        // 1) 根必须是黑
        if (rootNode.color != BLACK) return false;
    
        // 2) parent 指针一致性
        if (rootNode.parent != null) return false;
    
        // 3) 综合校验：BST + 红红冲突 + 黑高一致
        return validateAndBlackHeight(rootNode, null, null) != -1;
    }
    
    private int validateAndBlackHeight(Node node, K min, K max) {
        if (node == null) {
            // NIL 视为黑：黑高 +1（你也可以返回 1 或 0，只要全程一致即可）
            return 1;
        }
    
        // A) BST 有序性：min < key < max
        if (min != null && node.key.compareTo(min) <= 0) return -1;
        if (max != null && node.key.compareTo(max) >= 0) return -1;
    
        // B) parent 指针一致性
        if (node.left != null && node.left.parent != node) return -1;
        if (node.right != null && node.right.parent != node) return -1;
    
        // C) 红红冲突：红节点不能有红孩子
        if (node.color == RED) {
            if ((node.left != null && node.left.color == RED) ||
                (node.right != null && node.right.color == RED)) {
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
