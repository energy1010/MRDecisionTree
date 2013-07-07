package com.younger.struct;


public interface BinaryNodeInterface<T> {

	public T getData();
	
	public void setData(T newData);
	
	public BinaryNodeInterface<T> getLeftChild();
	
	public BinaryNodeInterface<T> getRightChild();
	
	public void setLeftChild(BinaryNodeInterface<T> LeftChild);
	
	public void setRightChild(BinaryNodeInterface<T> RightChild);
	
	public boolean hasLeftChild();
	
	public boolean hasRightChild();
	
	public boolean isLeaf();
	
	public int getHeight();
	
	public BinaryNodeInterface<T>  copy();
	
	public int getNumberOfChildNodes();

	public  int getNumberOfNodes();
}
