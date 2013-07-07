package com.younger.struct;

import java.util.Iterator;

public interface TreeIteratorInterface <T>{

	public Iterator<T> getPreOrderIterator();
	
	public Iterator<T> getPostOrderIterator();
	
	public  Iterator<T> getInOrderIterator();
	
	public Iterator<T> getLevelOrderIterator();
}
