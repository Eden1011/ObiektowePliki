package com.ug.stosrpn.application;

import com.ug.stosrpn.stack.Stack;

public class Main {
  public static void main(String[] args) {
    System.out.println("Stack demo...");

    Stack<Integer> intStack = new Stack<>(1);
    System.out.println("Basic stack operations:");

    System.out.println("Pushing elements: 10, 20, 30");
    intStack.push(10);
    intStack.push(20);
    intStack.push(30);

    System.out.println("Stack size is currently: " + intStack.size());
    System.out.println("Top element is currently: " + intStack.peek());

    System.out.println("Removing all stack elements now...");
    while (!intStack.isEmpty()) {
      System.out.println("Popped an element: " + intStack.pop());
    }

    System.out.println("Stack size is currently: " + intStack.size());
  }
}
