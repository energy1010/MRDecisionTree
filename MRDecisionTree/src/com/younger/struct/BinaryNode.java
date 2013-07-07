package com.younger.struct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryNode<T> implements BinaryNodeInterface<T>,java.io.Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(BinaryNode.class);
	private T data;
	private BinaryNode<T> left = null;
	private BinaryNode<T> right= null;
	public BinaryNode(){
		
		this(null);//调用下一个构造函数
	}
	
	public BinaryNode(T elementData){
		this(elementData,null,null); //调用下一个构造函数
	}
	
	public BinaryNode(T elementData,BinaryNode<T> leftChild,BinaryNode<T> rightChild){
		data = elementData;
		left = leftChild;
		right = rightChild;
	}
	
	@Override
	public BinaryNodeInterface<T> copy() {
		BinaryNode<T> newRoot = new BinaryNode<T>(data);
		if(left!=null)
			newRoot.left = (BinaryNode<T>) left.copy();
		if(right!=null)
			newRoot.right = (BinaryNode<T>)right.copy();
		return newRoot;
	}

	@Override
	public T getData() {
		return data;
	}

	
	private int getHeight(BinaryNode<T> node ) {
		int height =  0;
		if(node!=null){
			height = 1+Math.max(getHeight(node.left),getHeight(node.right));
		}
		return height;
	}

	@Override
	public BinaryNodeInterface<T> getLeftChild() {
		return left;
	}

	@Override
	public BinaryNodeInterface<T> getRightChild() {
		return right;
	}

	@Override
	public boolean hasLeftChild() {
		return (left!=null);
	}

	@Override
	public boolean hasRightChild() {
		return (right!=null);
	}

	@Override
	public boolean isLeaf() {
		return (left==null)&&(right==null);
	}

	@Override
	public void setData(T newData) {
		data = newData;
	}

	@Override
	public void setLeftChild(BinaryNodeInterface<T> LeftChild) {
		left =(BinaryNode<T>) LeftChild;
	}

	@Override
	public void setRightChild(BinaryNodeInterface<T> RightChild) {
		right = (BinaryNode<T>) RightChild;
		
	}

	@Override
	public int getHeight() {
		return this.getHeight(this);
	}
	
    @Override
    /** Task :get the total num of nodes of the tree 
     * 包括节点自身，若只有root ，则为1，若为空树则为0
     */
	public int getNumberOfNodes(){
    	if(this==null) return 0;
		int leftNum =0;
		int rightNum = 0;
		if(left!=null) leftNum = left.getNumberOfNodes();
		if(right!=null) rightNum= right.getNumberOfNodes();
		return 1+leftNum+rightNum;
	}

	@Override
	public String toString() {
		return "BinaryNode [data=" + data + ", left=" + left.getData() + ", right="
				+ right.getData() + "]";
	}

	@Override
	public int getNumberOfChildNodes() {
		return 0;
	}
	
	
	
}
