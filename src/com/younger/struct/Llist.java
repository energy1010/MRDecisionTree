package com.younger.struct;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Llist<T> implements ListInterface<T>, Cloneable, Serializable {

	private Node firstNode;

	private int length;
	private static final Logger log = LoggerFactory.getLogger(Llist.class);

	public Llist() {
		clear();
	}

	public Llist(Node firstNode, int length) {
		super();
		this.firstNode = firstNode;
		this.length = length;
	}

	@Override
	public boolean add(int position, T newEntry) {
		assert (checkPosition(position));
		if (position == 0) {
			addToHead(new Node(newEntry));
		}
		if (position == getLastIndex()) {
			addToTail(new Node(newEntry));
		} else {
			Node preNode = getNodeAt(position - 1);
			Node newNode = new Node(newEntry);
			newNode.next = preNode.next;
			preNode.next = newNode;
		}
		return true;
	}

	@Override
	/** Task 
	 * 默认尾插法·
	 */
	public boolean add(T newEntry) {
		Node newNode = new Node(newEntry);
		return addToTail(newNode);
	}

	public boolean add(Node newNode, boolean IsInsertEnd) {
		if (isEmpty()) {
			firstNode = newNode;
		} else {
			if (IsInsertEnd) {
				Node lastNode = getTailNode();
				lastNode.next = newNode;
			} else {
				Node HeadNode = getHeadNode();
				firstNode.next = newNode;
				newNode.next = HeadNode;
			}
		}
		length++;
		return true;
	}

	public boolean addToHead(Node newNode) {
		if (isEmpty()) {
			firstNode = newNode;
		} else {
			Node HeadNode = getHeadNode();
			firstNode.next = newNode;
			newNode.next = HeadNode;
		}
		length++;
		return true;
	}

	public boolean addToTail(Node newNode) {
		if (isEmpty()) {
			firstNode = newNode;
		} else {
			Node lastNode = getTailNode();
			lastNode.next = newNode;
		}
		length++;
		return true;
	}

	@Override
	public final void clear() {
		firstNode = null;
		length = 0;
	}

	@Override
	public boolean contains(T anEntry) {
		boolean result = false;
		for (int i = 0; i < getLastIndex(); i++) {
			Node currentNode = getNodeAt(i);
			if (currentNode.data.equals(anEntry)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public void display() {
		for (int i = 0; i < getLastIndex(); i++) {
			Node cuNode = getNodeAt(i);
			System.out.println(cuNode.data);
		}
	}

	@Override
	public T getEntry(int givenPosition) {
		return getNodeAt(givenPosition).data;
	}

	/**
	 * Task: 返回对指定位置节点的引用 Precondition: 线性表非空；0<=givenPosition<=length-1
	 * 
	 * @param givenPostion
	 * @return
	 */
	public Node getNodeAt(int givenPosition) {
		assert (!isEmpty() && checkPosition(givenPosition));
		Node currentNode = firstNode;
		for (int i = 0; i < givenPosition + 1; i++) {
			currentNode = currentNode.next;
		}
		assert (currentNode != null);
		return currentNode;
	}

	@Override
	public T getHead() {
		return firstNode.data;
	}

	public Node getHeadNode() {
		return firstNode;
	}

	@Override
	public int getLastIndex() {
		return length - 1;
	}

	/**
	 * check the position is valid.
	 * 
	 * @param givenPosition
	 *            givenPosition is [0, length-1]
	 * @return valid or not
	 * @author Administrator
	 */
	private boolean checkPosition(int givenPosition) {
		boolean isValid = false;
		if (givenPosition >= 0 && givenPosition < length)
			isValid = true;
		return isValid;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int getPosition(T anObject) {
		return 0;
	}

	/**
	 * if find it then reutrn its index ,else return -1
	 * 
	 * @param anNode
	 * @return
	 */
	public int getPosition(Node anNode) {
		int result = -1;
		for (int i = 0; i < getLastIndex(); i++) {
			Node currentNode = getNodeAt(i);
			if (currentNode.data.equals(anNode)) {
				result = i;
				break;
			}
		}
		return result;
	}

	@Override
	public T getTail() {
		return getTailNode().data;
	}

	public Node getTailNode() {
		return getNodeAt(length - 1);
	}

	@Override
	public boolean isEmpty() {
		boolean result = false;
		if (length == 0) {
			assert (firstNode == null);
			result = true;
		} else {
			assert firstNode != null;
			result = false;
		}
		return result;
	}

	@Override
	public boolean isFull() {
		return true;
	}

	@Override
	public void moveToEnd() {
		if (isEmpty() || length == 1)
			return;
		Node lastNode = getTailNode();
		Node HeadNode = getHeadNode();
		lastNode.next = HeadNode;
		firstNode = firstNode.next;
	}

	@Override
	public T remove(int givenPosition) {
		assert (isEmpty() != true && checkPosition(givenPosition));
		T data = null;
		if (length == 1) {
			data = firstNode.data;
			clear();
		} else {
			if (givenPosition == getLastIndex()) {
				Node newLastNode = getNodeAt(givenPosition - 1);
				newLastNode.next = null;
			}
			Node preNode = getNodeAt(givenPosition - 1);
			data = preNode.next.data;
			preNode.next = preNode.next.next;
			length--;
		}
		return data;
	}

	public Node getPreNode(Node anNode) {
		assert (isEmpty() != true && anNode != null);
		for (int i = 0; i < getLastIndex() - 1; i++) {
			Node currentNode = getNodeAt(i);
			if (currentNode.next.equals(anNode)) {
				return currentNode;
			}
		}
		return null;
	}

	public Node getPreNode(T data) {
		assert (isEmpty() != true && data != null);
		for (int i = 0; i < getLastIndex() - 1; i++) {
			Node currentNode = getNodeAt(i);
			if (currentNode.next.data.equals(data)) {
				return currentNode;
			}
		}
		return null;
	}

	@Override
	public boolean replace(int givenPosition, T newEntry) {
		Node currentNode = getNodeAt(givenPosition);
		currentNode.data = newEntry;
		return true;
	}

	
	
	private class Node implements Cloneable, Serializable {
		private T data;
		private Node next;

		private Node(T dataPortion) {
			data = dataPortion;
			next = null;
		}

		public Node(T data, Node next) {
			super();
			this.data = data;
			this.next = next;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
		}

		@Override
		public String toString() {
			return super.toString();
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public Node getNext() {
			return next;
		}

		public void setNext(Node next) {
			this.next = next;
		}

	}

	
}
