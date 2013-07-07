package com.younger.struct;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  T 为节点数据类型，eg. Int , Double , ....
 * @author Administrator
 *
 * @param <T>
 */
public class BinaryTree<T> implements BinaryTreeInterface<T> ,java.io.Serializable{

	  private static final Logger log = LoggerFactory.getLogger(BinaryTree.class);
	
	private BinaryNodeInterface<T> root;
	
	public BinaryTree(){
		root = null;
	}

	public BinaryTree(T rootData){
		root = new BinaryNode<T>(rootData);
	}
	
	public BinaryTree(BinaryNodeInterface<T> rootNode){
		root = rootNode;
	}
	
	@Override
	public void setTree(T rootData) {
		root =  (BinaryNodeInterface<T>) new BinaryTree<T>(rootData);
	}

	@Override
	public void setTree(T rootData, BinaryTreeInterface<T> leftTree,
			BinaryTreeInterface<T> rightTree) {
		privateSetTree(rootData, (BinaryTree<T>)leftTree,(BinaryTree<T>) rightTree);
	}

	protected void setRootData(T rootData){
		root.setData(rootData);
	}
	
	protected void setRootNode(BinaryNodeInterface<T> rootNode){
		root=rootNode;
	}
	
	
	@Override
	public void clear() {
		root = null;
	}

	
	@Override
	/** Task: 获得树的高度
	 * root的高度为1， Height>=1
	 */
	public int getHeight() {
		log.debug("the height of the binarytree is "+root.getHeight());
		return this.root.getHeight();
	}

	@Override
	public int getNumberOfNodes() {
		log.debug("the number of the binarytree is "+root.getNumberOfNodes());
		return this.root.getNumberOfNodes();
	}

	public BinaryNodeInterface<T> getRoot() {
		return root;
	}

	public void setRoot(BinaryNodeInterface<T> root) {
		this.root = root;
	}

	@Override
	public T getRootData() {
		T rootData=null;
		if(root!=null)
			rootData = root.getData();
		return rootData;
	}

	@Override
	public boolean isEmpty() {
		return root ==null;
	}

	@Override
	public Iterator<T> getInOrderIterator() {
		return null;
	}

	@Override
	public Iterator<T> getLevelOrderIterator() {
		return null;
	}

	@Override
	public Iterator<T> getPostOrderIterator() {
		return null;
	}

	@Override
	public Iterator<T> getPreOrderIterator() {
		return null;
	}

	private void privateSetTree(T rootData,BinaryTree<T> leftTree,BinaryTree<T> rightTree){
		root = new BinaryNode<T>(rootData);
		if((leftTree!=null)&&!leftTree.isEmpty()){
			root.setLeftChild(leftTree.root.copy());
		}
		if((rightTree!=null)&&!rightTree.isEmpty()){
			root.setLeftChild(rightTree.root.copy());
		}
	}
	
	
	public void levelorderTraverse(){
		levelorderTraverse(root);
	}
	
	public void preorderTraverse(){
		preorderTraverse(root);
	}
	
	public void postorderTraverse(){
		postorderTraverse(root);
	}
	
	public void inorderTraverse(){
		inorderTraverse(root);
	}
	
	public void inorderTraverse(BinaryNodeInterface<T> node){
		if(node!=null){
			inorderTraverse((BinaryNode)node.getLeftChild());
			System.out.println(node.getData());
			inorderTraverse((BinaryNode)node.getRightChild());
		}
	}
	
	public void levelorderTraverse(BinaryNodeInterface<T> node){
		if(node!=null){
			Queue<BinaryNodeInterface<T>> queue = new LinkedList<BinaryNodeInterface<T>>();
			queue.add(node);
			while(!queue.isEmpty()){
			BinaryNodeInterface<T> headNode=  queue.poll();
			System.out.println(headNode.getData());
			if(headNode.getLeftChild()!=null){
			queue.add(headNode.getLeftChild());
			}
			if(headNode.getRightChild()!=null)
			queue.add(headNode.getRightChild());
			}
		}
	}
	
	public void preorderTraverse(BinaryNodeInterface<T> node){
		if(node!=null){
			System.out.println(node.getData());
			preorderTraverse((BinaryNode)node.getLeftChild());
			preorderTraverse((BinaryNode)node.getRightChild());
		}
	}
	
	public void postorderTraverse(BinaryNodeInterface<T> node){
		if(node!=null){
			postorderTraverse((BinaryNode)node.getLeftChild());
			postorderTraverse((BinaryNode)node.getRightChild());
			System.out.println(node.getData());
		}
	}
	
	public static void main(String[] args) {

		BinaryNodeInterface root = new BinaryNode(0);
		BinaryNodeInterface l1 = new BinaryNode(1);
		BinaryNodeInterface r1 = new BinaryNode(2);
		BinaryNodeInterface l11 = new BinaryNode(3);
		BinaryNodeInterface r12 = new BinaryNode(4);
		BinaryNodeInterface l21 = new BinaryNode(5);
		BinaryNodeInterface r22 = new BinaryNode(6);
		BinaryNodeInterface r211 = new BinaryNode(7);
		root.setLeftChild(l1);
		root.setRightChild(r1);
		l1.setLeftChild(l11);
		l1.setRightChild(r12);
		r1.setLeftChild(l21);
		r1.setRightChild(r22);
		l21.setRightChild(r211);
		BinaryTree tree = new BinaryTree(root);
		System.out.println(tree.getRoot().getLeftChild().getHeight());
		System.out.println(tree.getRoot().getLeftChild().getNumberOfNodes());
		tree.preorderTraverse();
//		tree.postorderTraverse();
//		tree.levelorderTraverse();
	}
	
	
}
