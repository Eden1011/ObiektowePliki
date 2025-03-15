package com.ug.stosrpn.stack;

interface StackOperations<T> {
  void push(T item);

  T pop();

  T peek();

  boolean isEmpty();

  int size();

  void clear();
}

class EmptyStackException extends RuntimeException {
  public EmptyStackException(String message) {
    super(message);
  }
}

public class Stack<T> implements StackOperations<T> {
  private Object[] elements;
  private int size;
  private static final int DEFAULT_INITIAL_CAPACITY = 1;
  private static final int GROWTH_FACTOR = 2;

  public Stack() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public Stack(int initialCapacity) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Initial capacity can not be below 0");
    }
    this.elements = new Object[initialCapacity];
    this.size = 0;
  }

  private void ensureCapacity() {
    if (size == elements.length) {
      int newCapacity = elements.length == 0 ? DEFAULT_INITIAL_CAPACITY : elements.length * GROWTH_FACTOR;
      Object[] newElements = new Object[newCapacity];
      System.arraycopy(elements, 0, newElements, 0, size);
      elements = newElements;
    }
  }

  @Override
  public void push(T item) {
    ensureCapacity();
    elements[size++] = item;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T pop() {
    if (isEmpty()) {
      throw new EmptyStackException("Cannot pop from empty stack");
    }
    T item = (T) elements[--size];
    elements[size] = null;
    return item;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T peek() {
    if (isEmpty()) {
      throw new EmptyStackException("Cannot peek at empty stack");
    }
    return (T) elements[size - 1];
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public void clear() {
    for (int i = 0; i < size; i++) {
      elements[i] = null;
    }
    size = 0;
  }
}
