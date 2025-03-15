package com.ug.stosrpn.stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {
  private Stack<Integer> stack;

  @BeforeEach
  void setUp() {
    stack = new Stack<>();
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    @Test
    @DisplayName("Default constructor creates stack that is empty")
    void defaultConstructorCreatesEmptyStack() {
      assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("Default constructor creates stack with size zero")
    void defaultConstructorCreatesStackWithSizeZero() {
      assertEquals(0, stack.size());
    }

    @Test
    @DisplayName("Constructor with capacity creates stack that is empty")
    void constructorWithCapacityCreatesEmptyStack() {
      Stack<String> customStack = new Stack<>(10);
      assertTrue(customStack.isEmpty());
    }

    @Test
    @DisplayName("Constructor with capacity creates stack with size zero")
    void constructorWithCapacityCreatesStackWithSizeZero() {
      Stack<String> customStack = new Stack<>(10);
      assertEquals(0, customStack.size());
    }

    @Test
    @DisplayName("Constructor throws exception for negative capacity")
    void constructorThrowsExceptionForNegativeCapacity() {
      assertThrows(IllegalArgumentException.class, () -> new Stack<>(-1));
    }
  }

  @Nested
  @DisplayName("Push Tests")
  class PushTests {
    @Test
    @DisplayName("Push increases size by one when pushing one item")
    void pushOneItemIncreasesSizeByOne() {
      stack.push(1);
      assertEquals(1, stack.size());
    }

    @Test
    @DisplayName("Push increases size proportionally to number of pushed items")
    void pushMultipleItemsIncreasesSize() {
      stack.push(1);
      stack.push(2);
      assertEquals(2, stack.size());
    }

    @Test
    @DisplayName("Push beyond initial capacity increases size correctly")
    void pushBeyondInitialCapacityIncreasesSize() {
      Stack<Integer> smallStack = new Stack<>(2);
      smallStack.push(1);
      smallStack.push(2);
      smallStack.push(3); // Should trigger resize
      assertEquals(3, smallStack.size());
    }

    @Test
    @DisplayName("Push beyond initial capacity maintains correctness of elements")
    void pushBeyondInitialCapacityMaintainsElements() {
      Stack<Integer> smallStack = new Stack<>(2);
      smallStack.push(1);
      smallStack.push(2);
      smallStack.push(3); // Should trigger resize
      assertEquals(3, smallStack.peek());
    }
  }

  @Nested
  @DisplayName("Pop Tests")
  class PopTests {
    @Test
    @DisplayName("Pop decreases size by one")
    void popDecreasesSizeByOne() {
      stack.push(1);
      stack.push(2);
      stack.pop();
      assertEquals(1, stack.size());
    }

    @Test
    @DisplayName("Pop returns most recently pushed item")
    void popReturnsLastPushedItem() {
      stack.push(1);
      stack.push(2);
      stack.push(3);
      assertEquals(3, stack.pop());
    }

    @Test
    @DisplayName("Sequential pops return items in LIFO order")
    void sequentialPopsReturnItemsInLifoOrder() {
      stack.push(1);
      stack.push(2);
      stack.push(3);
      stack.pop(); // Remove 3
      assertEquals(2, stack.pop());
    }

    @Test
    @DisplayName("Sequential pops return all items in correct order")
    void allSequentialPopsReturnItemsInLifoOrder() {
      stack.push(1);
      stack.push(2);
      stack.push(3);
      stack.pop(); // Remove 3
      stack.pop(); // Remove 2
      assertEquals(1, stack.pop());
    }

    @Test
    @DisplayName("Pop from empty stack throws exception")
    void popFromEmptyStackThrowsException() {
      assertThrows(EmptyStackException.class, () -> stack.pop());
    }
  }

  @Nested
  @DisplayName("Peek Tests")
  class PeekTests {
    @Test
    @DisplayName("Peek returns most recently pushed item")
    void peekReturnsLastPushedItem() {
      stack.push(1);
      assertEquals(1, stack.peek());
    }

    @Test
    @DisplayName("Peek returns updated top after multiple pushes")
    void peekReturnsUpdatedTopAfterMultiplePushes() {
      stack.push(1);
      stack.push(2);
      assertEquals(2, stack.peek());
    }

    @Test
    @DisplayName("Peek doesn't change stack size")
    void peekDoesNotChangeStackSize() {
      stack.push(1);
      stack.push(2);
      int sizeBefore = stack.size();
      stack.peek();
      assertEquals(sizeBefore, stack.size());
    }

    @Test
    @DisplayName("Peek on empty stack throws exception")
    void peekOnEmptyStackThrowsException() {
      assertThrows(EmptyStackException.class, () -> stack.peek());
    }
  }

  @Nested
  @DisplayName("IsEmpty Tests")
  class IsEmptyTests {
    @Test
    @DisplayName("IsEmpty returns true for new stack")
    void isEmptyReturnsTrueForNewStack() {
      assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("IsEmpty returns false after push")
    void isEmptyReturnsFalseAfterPush() {
      stack.push(1);
      assertFalse(stack.isEmpty());
    }

    @Test
    @DisplayName("IsEmpty returns true after all items popped")
    void isEmptyReturnsTrueAfterAllItemsPopped() {
      stack.push(1);
      stack.push(2);
      stack.pop();
      stack.pop();
      assertTrue(stack.isEmpty());
    }
  }

  @Nested
  @DisplayName("Clear Tests")
  class ClearTests {
    @Test
    @DisplayName("Clear makes stack empty")
    void clearMakesStackEmpty() {
      stack.push(1);
      stack.push(2);
      stack.push(3);
      stack.clear();
      assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("Clear sets size to zero")
    void clearSetsSizeToZero() {
      stack.push(1);
      stack.push(2);
      stack.push(3);
      stack.clear();
      assertEquals(0, stack.size());
    }

    @Test
    @DisplayName("Clear on empty stack keeps stack empty")
    void clearOnEmptyStackKeepsStackEmpty() {
      stack.clear();
      assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("Clear on empty stack keeps size zero")
    void clearOnEmptyStackKeepsSizeZero() {
      stack.clear();
      assertEquals(0, stack.size());
    }
  }

  @Nested
  @DisplayName("Generic Type Tests")
  class GenericTypeTests {
    @Test
    @DisplayName("String stack stores and retrieves strings correctly")
    void stringStackStoresAndRetrievesStringsCorrectly() {
      Stack<String> stringStack = new Stack<>();
      stringStack.push("Hello");
      assertEquals("Hello", stringStack.peek());
    }

    @Test
    @DisplayName("Double stack stores and retrieves doubles correctly")
    void doubleStackStoresAndRetrievesDoublesCorrectly() {
      Stack<Double> doubleStack = new Stack<>();
      doubleStack.push(3.14);
      assertEquals(3.14, doubleStack.peek());
    }
  }
}
