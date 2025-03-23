package com.ug.stosrpn.calc;

interface Operator {
  int getOperandCount();

  int execute(int[] operands);
}

abstract class BinaryOperator implements Operator {
  @Override
  public int getOperandCount() {
    return 2;
  }
}

abstract class MultiOperandOperator implements Operator {
  private final int operandCount;

  public MultiOperandOperator(int operandCount) {
    if (operandCount < 1) {
      throw new IllegalArgumentException("Operand count must be at least 1");
    }
    this.operandCount = operandCount;
  }

  @Override
  public int getOperandCount() {
    return operandCount;
  }
}

class AddOperator extends BinaryOperator {
  @Override
  public int execute(int[] operands) {
    return operands[0] + operands[1];
  }
}

class SubtractOperator extends BinaryOperator {
  @Override
  public int execute(int[] operands) {
    return operands[0] - operands[1];
  }
}

class MultiplyOperator extends BinaryOperator {
  @Override
  public int execute(int[] operands) {
    return operands[0] * operands[1];
  }
}

class DivideOperator extends BinaryOperator {
  @Override
  public int execute(int[] operands) {
    if (operands[1] == 0) {
      throw new ArithmeticException("Division by zero");
    }
    return operands[0] / operands[1];
  }
}

class SumOperator extends MultiOperandOperator {
  public SumOperator(int operandCount) {
    super(operandCount);
  }

  @Override
  public int execute(int[] operands) {
    int sum = 0;
    for (int operand : operands) {
      sum += operand;
    }
    return sum;
  }
}

class AverageOperator extends MultiOperandOperator {
  public AverageOperator(int operandCount) {
    super(operandCount);
  }

  @Override
  public int execute(int[] operands) {
    int sum = 0;
    for (int operand : operands) {
      sum += operand;
    }
    return sum / operands.length;
  }
}

class MaxOperator extends MultiOperandOperator {
  public MaxOperator(int operandCount) {
    super(operandCount);
  }

  @Override
  public int execute(int[] operands) {
    int max = operands[0];
    for (int i = 1; i < operands.length; i++) {
      if (operands[i] > max) {
        max = operands[i];
      }
    }
    return max;
  }
}
