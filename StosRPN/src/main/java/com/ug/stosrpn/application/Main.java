package com.ug.stosrpn.application;

import com.ug.stosrpn.stack.Stack;
import com.ug.stosrpn.calc.Calc;

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

    System.out.println("\n\nCalculator demo...");
    Calc calculator = new Calc();

    System.out.println("\n1. Basic binary operations:");
    demonstrateExpression(calculator, "5 3 +");
    demonstrateExpression(calculator, "10 2 -");
    demonstrateExpression(calculator, "4 5 *");
    demonstrateExpression(calculator, "20 4 /");

    System.out.println("\n2. Compound expressions:");
    demonstrateExpression(calculator, "3 4 + 5 *");
    demonstrateExpression(calculator, "10 5 / 6 +");
    demonstrateExpression(calculator, "7 2 3 * -");

    System.out.println("\n3. Multi-operand operations:");
    demonstrateExpression(calculator, "5 10 15 sum3");
    demonstrateExpression(calculator, "7 2 9 4 1 sum5");
    demonstrateExpression(calculator, "10 20 30 avg3");
    demonstrateExpression(calculator, "5 25 10 max3");

    System.out.println("\n4. Error handling examples:");
    try {
      String invalidExpression = "5 +";
      System.out.print("Evaluating: \"" + invalidExpression + "\" -> ");
      calculator.evaluate(invalidExpression);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }

    try {
      String invalidExpression = "5 0 /";
      System.out.print("Evaluating: \"" + invalidExpression + "\" -> ");
      calculator.evaluate(invalidExpression);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private static void demonstrateExpression(Calc calculator, String expression) {
    try {
      int result = calculator.evaluate(expression);
      System.out.println("Evaluating: \"" + expression + "\" -> " + result);
    } catch (Exception e) {
      System.out.println("Error evaluating \"" + expression + "\": " + e.getMessage());
    }
  }
}
