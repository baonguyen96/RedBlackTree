/**
 * Bao Nguyen
 * BCN140030
 * SE 3345.004
 * <p>
 * Project 3
 * Implementing the insert, contains, and print functions of the Red-black Tree
 */

public class RedBlackTree {

    /**
     * Nested class RBTreeNode
     * Represents individual node of the RedBlackTree
     */
    private class RBTreeNode {
        int element;         // key
        RBTreeNode left;     // left subtree
        RBTreeNode right;    // right subtree
        RBTreeNode parent;   // its parent node
        boolean isRed;       // color of the node


        /**
         * default constructor
         */
        public RBTreeNode() {
            this(0, null, false);
        }


        /**
         * overloaded constructor
         */
        public RBTreeNode(int element, RBTreeNode parent, boolean isRed) {
            this.element = element;
            this.left = null;
            this.right = null;
            this.parent = parent;
            this.isRed = isRed;
        }
    }


    private RBTreeNode root;        // root of the red black tree
    private boolean singlyRotated;  // check if the tree is singly rotated
    private boolean doublyRotated;  // check if the tree is doubly rotated


    /**
     * default constructor
     */
    public RedBlackTree() {
        root = null;
    }


    /***
     * method: insert
     * @param element: node to insert into the tree
     * @return true if insert successfully and false if not
     */
    public boolean insert(int element) {
        boolean insertedSuccessfully = false;
        RBTreeNode current = root;

        // empty tree
        if (root == null) {
            root = new RBTreeNode(element, null, false);
            return true;
        }

        /*
         * the tree is not empty
         * find the correct position for the new node
         * newly inserted nodes are at leaves and are red (initially)
         * if found, mark the insertion successful and end the loop
         * if find duplicate, break immediately
         */
        while (!insertedSuccessfully) {
            if (element == current.element) {
                break;
            }
            else if (element < current.element) {
                if (current.left == null) {
                    current.left = new RBTreeNode(element, current, true);
                    insertedSuccessfully = true;
                }

                current = current.left;
            }
            else {
                if (current.right == null) {
                    current.right = new RBTreeNode(element, current, true);
                    insertedSuccessfully = true;
                }

                current = current.right;
            }

        }   // end while (not insert successfully)

        if (insertedSuccessfully) {
            /*
             * check from the current node (leaf node) all the way
             * up to the root to see if there is any double red error
             */
            while (current != null) {

                // the current node and its parent are both red
                if (isDoubleRed(current)) {

                    RBTreeNode uncle = getSibling(current.parent);

                    /*
                     * if the current's uncle is null or black,
                     * then start restructuring the tree
                     * else if the current's uncle is red
                     * then start recoloring that segment of the tree
                     */
                    if (uncle == null || !uncle.isRed) {
                        RBTreeNode grandparent = current.parent.parent;
                        RBTreeNode greatGrandparent = grandparent.parent;

                        /*
                         * if the grandparent node is null, it means the
                         * current node is 1 level away from the root
                         * (i.e. it is the root's child), then set the root
                         * to be the result of the tree restructuring
                         * otherwise, set the child of greatGrandparent
                         * (left or right depends on the location of current's
                         * grandParent) to the result of the tree restructuring.
                         */
                        if (greatGrandparent == null) {
                            root = restructure(current);
                        }
                        else if (isLeftChild(grandparent, greatGrandparent)) {
                            greatGrandparent.left = restructure(current);
                        }
                        else {
                            greatGrandparent.right = restructure(current);
                        }

                        /*
                         * recolor newly adjusted segment
                         * if restructured using single rotation and
                         * the segment has double red error, recoloring
                         * the segment with deepest node as the current
                         * if restructured using double rotation,
                         * find the red child of the current node
                         * if the red child is null, then the segment
                         * does not contain double red error
                         * -> don't have to do anything
                         * if the red child is not null, then there is
                         * a double red error between the redChild and
                         * the current node
                         * -> perform recoloring the segment with the
                         * deepest node as the redChild
                         */
                        if (singlyRotated && isDoubleRed(current)) {
                            recoloring(current, false);
                        }
                        else if (doublyRotated) {
                            RBTreeNode redChild = getRedChild(current);

                            if (redChild != null) {
                                recoloring(redChild, false);
                            }
                        }

                        // reset rotation flags
                        singlyRotated = doublyRotated = false;
                    }
                    // current's uncle is red
                    else {
                        recoloring(current);
                    }

                }   // end if(double red error)

                current = current.parent;   // keep checking upward

            }   // end while(current is not the root)

        }   // end if(insert successfully)

        return insertedSuccessfully;
    }


    /***
     * method: contains
     * check to see if the tree contains the parametric element
     * @param element: key to search for
     * @return true if the tree contains the key element, false if not
     */
    public boolean contains(int element) {
        RBTreeNode current = root;

        while (current != null) {
            if (element == current.element) {
                return true;
            }
            else if (element < current.element) {
                current = current.left;
            }
            else {
                current = current.right;
            }
        }

        return false;
    }


    /***
     * method: print
     * print inorder traversal
     */
    public void print() {
        if (root == null) {
            System.out.println("Empty tree.");
        }
        else {
            System.out.print("RB tree:  ");
            print(root);
        }
    }


    /***
     * method: print (overloaded)
     * @param root: the root of the red-black tree
     * print inorder traversal with the * in front of every red node
     */
    private void print(RBTreeNode root) {
        // print left subtree
        if (root.left != null) {
            print(root.left);
        }

        // print current node
        System.out.printf((root.isRed ? "*%d  " : "%d  "), root.element);

        // print right subtree
        if (root.right != null) {
            print(root.right);
        }
    }


    /***
     * method: singleRotateLeftLeft
     * single rotation with left subtree
     * @param grandparent: first imbalanced node
     * @return the parent node (left child of grandparent node)
     */
    private RBTreeNode singleRotateLeftLeft(RBTreeNode grandparent) {
        RBTreeNode parent = grandparent.left;
        grandparent.left = parent.right;
        parent.right = grandparent;
        parent.parent = grandparent.parent;
        grandparent.parent = parent;
        return parent;
    }


    /***
     * method: singleRotateRightRight
     * single rotation with right subtree
     * @param grandparent: first imbalanced node
     * @return the parent node (right child of grandparent node)
     */
    private RBTreeNode singleRotateRightRight(RBTreeNode grandparent) {
        RBTreeNode parent = grandparent.right;
        grandparent.right = parent.left;
        parent.left = grandparent;
        parent.parent = grandparent.parent;
        grandparent.parent = parent;
        return parent;
    }


    /***
     * method: doubleRotateLeftRight
     * first rotate the left child with its right child,
     * then rotate the node itself with its new left child
     * @param grandparent: first node where imbalance happens
     * @return the grandchild node (grandparent's left child's right child)
     */
    private RBTreeNode doubleRotateLeftRight(RBTreeNode grandparent) {
        grandparent.left = singleRotateRightRight(grandparent.left);
        return singleRotateLeftLeft(grandparent);
    }


    /***
     * method: doubleRotateRightLeft
     * first rotate the right child with its left child,
     * then rotate the node itself with its new right child
     * @param grandparent: first node where imbalance happens
     * @return the grandchild node (grandparent's right child's left child)
     */
    private RBTreeNode doubleRotateRightLeft(RBTreeNode grandparent) {
        grandparent.right = singleRotateLeftLeft(grandparent.right);
        return singleRotateRightRight(grandparent);
    }


    /***
     * method: recoloring
     * recoloring the tree to uphold the Red-black tree properties
     * make grandparent become red unless it is the root (does not have any parent)
     * make parent & its sibling become black
     *
     * @param current: the lowest node (child) in 3 generations that need to be recolored
     */
    private void recoloring(RBTreeNode current) {
        RBTreeNode grandparent = current.parent.parent;

        grandparent.isRed = grandparent.parent != null;
        grandparent.left.isRed = false;
        grandparent.right.isRed = false;
    }


    /***
     * method: recoloring (overloaded)
     * recoloring the tree to uphold the Red-black tree properties
     * make its parent become black
     * make its sibling (if any) become red
     *
     * @param current: the last node in 2 generations that need to be recolored
     *        dummyParam: a boolean parameter to differentiate this overloaded method
     *                    with the original one
     */
    private void recoloring(RBTreeNode current, boolean dummyParam) {
        current.parent.isRed = false;

        RBTreeNode currentSibling = getSibling(current);

        if (currentSibling != null) {
            currentSibling.isRed = true;
        }
    }


    /***
     * method: isDoubleRed
     * check if the Red-black tree has the double red error
     * @param current: current node in the tree
     * @return true if current & its parent are both red, false otherwise
     */
    private boolean isDoubleRed(RBTreeNode current) {
        return !(current == null || current.parent == null) &&
                current.isRed && current.parent.isRed;
    }


    /***
     * method: getSibling
     * return the getSibling node of the current node
     * @param current: current node of the Red-black tree
     * @return current's sibling
     */
    private RBTreeNode getSibling(RBTreeNode current) {
        // root node has no sibling
        if (current.parent == null) {
            return null;
        }

        RBTreeNode parent = current.parent;
        return isLeftChild(current, parent) ? parent.right : parent.left;
    }


    /***
     * method: restructure
     * restructuring the tree segment with 4 cases:
     *      left-left
     *      right-right
     *      left-right
     *      right-left
     * @param current: child node of the tree segment
     * @return the root of the new balanced subtree
     */
    private RBTreeNode restructure(RBTreeNode current) {
        if (current == null || current.parent == null || current.parent.parent == null) {
            return null;
        }

        RBTreeNode parent = current.parent;
        RBTreeNode grandparent = parent.parent;

        // single rotation left-left
        if (isLeftChild(parent, grandparent) && isLeftChild(current, parent)) {
            singlyRotated = true;
            doublyRotated = false;
            return singleRotateLeftLeft(grandparent);
        }

        // single rotation right-right
        else if (isRightChild(parent, grandparent) && isRightChild(current, parent)) {
            singlyRotated = true;
            doublyRotated = false;
            return singleRotateRightRight(grandparent);
        }

        // double rotation left-right
        else if (isLeftChild(parent, grandparent) && isRightChild(current, parent)) {
            doublyRotated = true;
            singlyRotated = false;
            return doubleRotateLeftRight(grandparent);
        }

        // double rotation right-left
        else if (isRightChild(parent, grandparent) && isLeftChild(current, parent)) {
            doublyRotated = true;
            singlyRotated = false;
            return doubleRotateRightLeft(grandparent);
        }

        // can't happen but the compiler will complain if leave out
        else {
            return null;
        }
    }


    /***
     * method: isLeftChild
     * check if node1 is a left child of node2
     * @param node1: Red-black tree node
     * @param node2: Red-black tree node
     * @return true if none is null and node1 is the left child of node2, false otherwise
     */
    private boolean isLeftChild(RBTreeNode node1, RBTreeNode node2) {
        return !(node1 == null || node2 == null) && node2.left == node1;
    }


    /***
     * method: isRightChild
     * check if node1 is the right child of node2
     * @param node1: Red-black tree node
     * @param node2: Red-black tree node
     * @return true if none is null and node1 is the right child of node2, false otherwise
     */
    private boolean isRightChild(RBTreeNode node1, RBTreeNode node2) {
        return !(node1 == null || node2 == null) && node2.right == node1;
    }


    /***
     * method: getRedChild
     * return the red child of the current node or null if none exists
     * @param current: red-black tree node to get its red child
     * @return the red child of current node
     */
    private RBTreeNode getRedChild(RBTreeNode current) {
        // current is null or it does not have any children
        if (current == null) {
            return null;
        }
        // left child is not null and is red
        else if (current.left != null && current.left.isRed) {
            return current.left;
        }
        // right child is not null and is red
        else if (current.right != null && current.right.isRed) {
            return current.right;
        }
        // the current node does not contain any red child
        else {
            return null;
        }
    }

}
