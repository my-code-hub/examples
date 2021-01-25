package com.accenture.bootcamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("beforeAll");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("beforeEach");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("afterEach");
    }

    @Test
    void add() {
        int result = calculator.add(5, 5);

        assertThat(result).isEqualTo(10);
    }

    @Test
    void divide() {
        int result = calculator.divide(5, 5);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void divideByZeroShouldThrowException() {
        assertThatThrownBy(() -> calculator.divide(5, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Can't divide by 0!");
    }
}
