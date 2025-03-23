package com.ug.stosrpn.calc;

import com.ug.stosrpn.stack.Stack;
import java.util.HashMap;
import java.util.Map;

public class Calc {
  private final Stack<Integer> stack;
  private final Map<String, Operator> operators;

  public Calc() {
    this.stack = new Stack<>();
    this.operators = new HashMap<>();

    registerOperator("+", new AddOperator());
    registerOperator("-", new SubtractOperator());
    registerOperator("*", new MultiplyOperator());
    registerOperator("/", new DivideOperator());
    registerOperator("sum3", new SumOperator(3));
    registerOperator("sum5", new SumOperator(5));
    registerOperator("avg3", new AverageOperator(3));
    registerOperator("max3", new MaxOperator(3));
  }

  public void registerOperator(String symbol, Operator operator) {
    operators.put(symbol, operator);
  }

  public int evaluate(String expression) {
    if (expression == null || expression.trim().isEmpty()) {
      throw new IllegalArgumentException("Expression cannot be null or empty");
    }

    stack.clear();
    String[] tokens = expression.trim().split("\\s+");

    for (String token : tokens) {
      if (operators.containsKey(token)) {
        processOperator(token);
      } else {
        processOperand(token);
      }
    }

    if (stack.size() != 1) {
      throw new IllegalArgumentException("Invalid expression: " + expression);
    }

    return stack.pop();
  }

  private void processOperator(String symbol) {
    Operator operator = operators.get(symbol);
    int operandCount = operator.getOperandCount();

    if (stack.size() < operandCount) {
      throw new IllegalArgumentException(
          "Invalid expression: not enough operands for operator " + symbol +
              " (requires " + operandCount + " operands)");
    }

    int[] operands = new int[operandCount];
    for (int i = operandCount - 1; i >= 0; i--) {
      operands[i] = stack.pop();
    }

    int result = operator.execute(operands);
    stack.push(result);
  }

  private void processOperand(String token) {
    try {
      int value = Integer.parseInt(token);
      stack.push(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid token: " + token);
    }
  }
}
