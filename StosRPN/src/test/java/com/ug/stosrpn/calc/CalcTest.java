package com.ug.stosrpn.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CalcTest {
  private Calc calculator;

  @BeforeEach
  void setUp() {
    calculator = new Calc();
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    @Test
    @DisplayName("Basic operators available after construction")
    void basicOperatorsAvailableAfterConstruction() {
      assertEquals(5, calculator.evaluate("2 3 +"));
    }

    @Test
    @DisplayName("Multi-operand operators available after construction")
    void multiOperandOperatorsAvailableAfterConstruction() {
      assertEquals(10, calculator.evaluate("2 3 5 sum3"));
    }
  }

  @Nested
  @DisplayName("Operator Registration Tests")
  class OperatorRegistrationTests {
    @Test
    @DisplayName("Can register and use new operator")
    void canRegisterAndUseNewOperator() {
      calculator.registerOperator("min2", new TestMinOperator(2));
      assertEquals(2, calculator.evaluate("2 7 min2"));
    }

    @Test
    @DisplayName("Can register unary operator")
    void canRegisterUnaryOperator() {
      calculator.registerOperator("square", new TestSquareOperator());
      assertEquals(25, calculator.evaluate("5 square"));
    }
  }

  @Nested
  @DisplayName("Expression Evaluation Tests")
  class ExpressionEvaluationTests {
    @Test
    @DisplayName("Evaluates basic binary operations")
    void evaluatesBasicBinaryOperations() {
      assertEquals(8, calculator.evaluate("5 3 +"));
    }

    @Test
    @DisplayName("Evaluates basic multiplication operation")
    void evaluatesBasicMultiplicationOperation() {
      assertEquals(15, calculator.evaluate("3 5 *"));
    }

    @Test
    @DisplayName("Evaluates basic division operation")
    void evaluatesBasicDivisionOperation() {
      assertEquals(4, calculator.evaluate("12 3 /"));
    }

    @Test
    @DisplayName("Evaluates single number expression")
    void evaluatesSingleNumberExpression() {
      assertEquals(5, calculator.evaluate("5"));
    }

    @Test
    @DisplayName("Evaluates complex expression")
    void evaluatesComplexExpression() {
      assertEquals(35, calculator.evaluate("3 4 + 5 *"));
    }

    @Test
    @DisplayName("Evaluates multi-operand expressions")
    void evaluatesMultiOperandExpressions() {
      assertEquals(30, calculator.evaluate("5 10 15 sum3"));
    }

    @Test
    @DisplayName("Evaluates multi-operand result in further operations")
    void evaluatesMultiOperandResultInFurtherOperations() {
      assertEquals(60, calculator.evaluate("5 10 15 sum3 2 *"));
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {
    @Test
    @DisplayName("Evaluate throws exception for null or empty expression")
    void evaluateThrowsExceptionForNullOrEmptyExpression() {
      assertThrows(IllegalArgumentException.class, () -> calculator.evaluate(null));
    }

    @Test
    @DisplayName("Evaluate throws exception for invalid token")
    void evaluateThrowsExceptionForInvalidToken() {
      assertThrows(IllegalArgumentException.class, () -> calculator.evaluate("5 a +"));
    }

    @Test
    @DisplayName("Evaluate throws exception for insufficient operands")
    void evaluateThrowsExceptionForInsufficientOperands() {
      assertThrows(IllegalArgumentException.class, () -> calculator.evaluate("5 +"));
    }

    @Test
    @DisplayName("Evaluate throws exception for division by zero")
    void evaluateThrowsExceptionForDivisionByZero() {
      assertThrows(ArithmeticException.class, () -> calculator.evaluate("5 0 /"));
    }

    @Test
    @DisplayName("Evaluate throws exception for stack imbalance")
    void evaluateThrowsExceptionForStackImbalance() {
      assertThrows(IllegalArgumentException.class, () -> calculator.evaluate("5 6"));
    }
  }

  @Nested
  @DisplayName("Further tests")
  class FurtherTests {
    @Test
    @DisplayName("Can add new operators without modification")
    void newOperatorWithoutMods() {
      calculator.registerOperator("pow", new TestPowerOperator());
      assertEquals(8, calculator.evaluate("2 3 pow"));
    }

    @Test
    @DisplayName("Custom operator can be substituted")
    void customOperationSubstitution() {
      calculator.registerOperator("+", new TestCustomAddOperator());
      assertEquals(5, calculator.evaluate("2 3 +"));
    }

    @Test
    @DisplayName("Works with operator abstraction")
    void abstractOperatorTest() {
      calculator.registerOperator("test1", new TestMinOperator(2));
      assertEquals(3, calculator.evaluate("3 7 test1"));
    }
  }

  private static class TestMinOperator implements Operator {
    private final int operandCount;

    public TestMinOperator(int operandCount) {
      this.operandCount = operandCount;
    }

    @Override
    public int getOperandCount() {
      return operandCount;
    }

    @Override
    public int execute(int[] operands) {
      int min = operands[0];
      for (int i = 1; i < operands.length; i++) {
        if (operands[i] < min) {
          min = operands[i];
        }
      }
      return min;
    }
  }

  private static class TestSquareOperator implements Operator {
    @Override
    public int getOperandCount() {
      return 1;
    }

    @Override
    public int execute(int[] operands) {
      return operands[0] * operands[0];
    }
  }

  private static class TestPowerOperator implements Operator {
    @Override
    public int getOperandCount() {
      return 2;
    }

    @Override
    public int execute(int[] operands) {
      return (int) Math.pow(operands[0], operands[1]);
    }
  }

  private static class TestCustomAddOperator implements Operator {
    @Override
    public int getOperandCount() {
      return 2;
    }

    @Override
    public int execute(int[] operands) {
      return operands[0] + operands[1];
    }
  }
}
