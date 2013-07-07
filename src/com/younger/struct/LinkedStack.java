package com.younger.struct;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedStack<T> extends Stack<T>{

	
	private static final Logger log = LoggerFactory.getLogger(LinkedStack.class);
	@Override
	public boolean empty() {
		return super.empty();
	}

	@Override
	public synchronized T peek() {
		return super.peek();
	}

	@Override
	public synchronized T pop() {
		return super.pop();
	}

	@Override
	public T push(T item) {
		return super.push(item);
	}

	@Override
	public synchronized int search(Object o) {
		return super.search(o);
	}
	
}
