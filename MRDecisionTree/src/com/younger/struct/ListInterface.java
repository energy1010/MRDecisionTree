package com.younger.struct;

/**
 * ADT ���Ա�ӿ�
 * ���Ա��Ԫ��λ�ô�1��ʼ
 * @author Administrator
 *
 */
public interface ListInterface<T> {
	
	
	public boolean add(int position , T newEntry);
	
	public T remove(int givenPosition);
	
	public void clear();
	
	public boolean replace(int givenPosition,T newEntry);
	
	public T getEntry(int givenPosition);
	
	public boolean contains(T anEntry);
	
//	public int findEntry(T anEntry);
	
	public int getLength();
	
	public boolean isEmpty();
	
	public boolean isFull();
	
	public void display();

	public boolean add(T newEntry);
	
	public int getPosition(T anObject);
	
	public void moveToEnd();
	
	public T getHead();
	
	public T getTail();
	
	public int getLastIndex();
	
	
}
