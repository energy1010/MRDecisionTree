package com.younger.struct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   Array List ¶¯Ì¬À©Õ¹
 * @author Administrator
 *
 * @param <T>
 */
public class Alist<T> implements ListInterface<T> {

	private static final Logger log = LoggerFactory.getLogger(Alist.class);
	
	public T[] entry;
	
	/**
	 * length start from 1,namely,size of Alist, begin 0
	 */
	private int length;
	
	/**
	 * the max size of Alist now,when double list ,it will change.
	 */
	private int capacity; 
	private static final int DEFAULT_INITIAL_CAPACITY = 50;
	
	public T[] getEntry() {
		return entry;
	}

	public void setEntry(T[] entry) {
		this.entry = entry;
	}

//	public static int getMaxSize() {
//		return MAX_SIZE;
//	}

	public void setLength(int length) {
		this.length = length;
	}

	public Alist() {
		super();
		length = 0;
		capacity = DEFAULT_INITIAL_CAPACITY;
		entry = (T[])new Object[DEFAULT_INITIAL_CAPACITY];
	}
	

	public Alist(int initialCapacity) {
		length = 0;
		entry = (T[])new Object[initialCapacity];
		capacity = initialCapacity;
	}

	@Override
	public boolean add(T newEntry) {
		log.debug("add newEntry ");
		assert(newEntry!=null);
		boolean isSuccessful = true;
		if(isFull()){
		doubleArray();
		}
		entry[length] = newEntry;
		length++;
		return true;
	}

	@Override
	/**
	 * @param newPosition    newPosition>=0&&newPosition<=length-1
	 * newPosition [0,length-1]
	 * @author Administrator
	 */
	public boolean add(int newPosition, T newEntry) {
		log.debug("add newEntry  at position "+newPosition);
		boolean isSuccessful = true;
		if( !isFull()&&checkPosition(newPosition) ){
			makeRoom(newPosition);
			entry[newPosition]= newEntry;
			length++;
		}else{
			isSuccessful = false;
		}
		return isSuccessful;
	}

	private void makeRoom(int newPosition){
		for(int index = length ; index>=newPosition;index--)
			entry[index]=entry[index-1];
	}
	
	@Override
	public void clear() {
		
	}
	

	
	@Override
	public boolean contains(T anEntry){
		assert(anEntry!=null);
		for(int i=0;i<length;i++){
			if(entry[i].equals(anEntry)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void display() {
		for(int i=0;i<length;i++){
			System.out.println(entry[i]);
		}
	}

	@Override
	public T getEntry(int givenPosition) {
		assert(checkPosition(givenPosition));
		return entry[givenPosition-1];
	}

	/**
	 * check the position is valid.
	 * @param givenPosition givenPosition is [0, length-1]
	 * @return valid or not 
	 * @author Administrator
	 */
	public boolean checkPosition(int givenPosition){
		boolean isValid= false;
		if(givenPosition>=0&&givenPosition<length)
			isValid = true;
		return isValid;
	}
	
	@Override
	public int getLength() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length==0;
	}

	@Override
	public boolean isFull() {
		return length == capacity;
	}

	@Override
	/**
	 * delete a element from Alist ,givenPosition is [0,length-1]
	 */
	public T remove(int givenPosition) {
		T result = null;
		if( checkPosition(givenPosition) ){
			assert(!isEmpty());
			result = entry[givenPosition-1];
			if(givenPosition<length)
				removeGap(givenPosition);
			length--;
		}
		return result;
	}
	
	/**
	 * for(int index = removeIndex;index <=lastIndex;index++)  entry[index] = entry[index+1];
	 * @param givenPosition
	 */
	private void removeGap(int givenPosition){
		assert( checkPosition(givenPosition) );
		int removeIndex = givenPosition ;
		int lastIndex = length-1;
		for(int index = removeIndex;index <=lastIndex;index++){
			entry[index] = entry[index+1];
		}
	}

	@Override
	public boolean replace(int givenPosition, T newEntry) {
		boolean isSuccessful = true;
		if(checkPosition(givenPosition) )
			entry[givenPosition-1] = newEntry;
		else
			isSuccessful=false;
		return isSuccessful;
	}
	
	private void doubleArray(){
		T[] oldList = entry;
		int oldSize = oldList.length;
		entry =(T[]) new Object[2*oldSize];
		System.arraycopy(oldList, 0, entry, 0, oldList.length);
	    capacity*=2;
	}

	public static void main(String[] args) {
		ListInterface alist = new Alist(10);
		alist.add(2);
		alist.add(5);
		alist.add(12);
		alist.add(1);
		alist.add(2);
		alist.add(5);
		alist.add(12);
		alist.add(1);
		
		alist.add(2);
		alist.add(5);
		alist.add(12);
		alist.add(1);
		
		alist.add(2);
		alist.add(5);
		alist.add(12);
		alist.add(1);
		if(alist.contains(0)){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		alist.display();
		System.out.println("----------------");
		alist.add(2, 0);
		if(alist.contains(0)){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		alist.display();
		System.out.println("----------------");
		alist.remove(2);
		if(alist.contains(0)){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		alist.display();
		}
		

	public int getPosition(T anObject){
		int position = -1;
		for(int i=0;i<length;i++){
			if(this.entry[i].equals(anObject)){
				position =  i;
			}
		}
		return position;
	}

	public void moveToEnd(){
		if(length==1){ return ;}
		T head = entry[0];
		removeGap(0);
		entry[length-1] = head;
	}
	
	public T getHead(){
		return entry[0];
	}
	
	public T getTail(){
		return entry[length-1];
	}
	
	public int getLastIndex(){
		return length-1;
	}

}
