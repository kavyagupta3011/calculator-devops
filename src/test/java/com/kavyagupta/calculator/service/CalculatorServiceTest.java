package com.kavyagupta.calculator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculatorServiceTest {

    private final CalculatorService calculator = new CalculatorService();

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "2, 3, 5",
            "-4, 10, 6",
            "0.1, 0.2, 0.3",
            "-2.5, -2.5, -5",
            "0, 0, 0"
    })
    void add(String a, String b, String expected) {
        assertThat(calculator.add(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "{0} - {1} = {2}")
    @CsvSource({
            "10, 4, 6",
            "3, 5, -2",
            "0.3, 0.1, 0.2",
            "-1, -1, 0"
    })
    void subtract(String a, String b, String expected) {
        assertThat(calculator.subtract(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "{0} * {1} = {2}")
    @CsvSource({
            "6, 7, 42",
            "-3, 4, -12",
            "2.5, 4, 10",
            "123, 0, 0"
    })
    void multiply(String a, String b, String expected) {
        assertThat(calculator.multiply(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "{0} / {1} = {2}")
    @CsvSource({
            "10, 2, 5",
            "7, 2, 3.5",
            "-9, 3, -3",
            "1, 3, 0.3333333333333333",
            "0, 5, 0"
    })
    void divide(String a, String b, String expected) {
        assertThat(calculator.divide(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "{0} % {1} = {2}")
    @CsvSource({
            "10, 3, 1",
            "-10, 3, -1",
            "7.5, 2, 1.5",
            "6, 3, 0"
    })
    void modulo(String a, String b, String expected) {
        assertThat(calculator.modulo(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @Test
    @DisplayName("modulo by zero throws ArithmeticException")
    void moduloByZero() {
        assertThatThrownBy(() -> calculator.modulo(BigDecimal.TEN, BigDecimal.ZERO))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Modulo by zero is not allowed");
    }

    @Test
    @DisplayName("divide by zero throws ArithmeticException")
    void divideByZero() {
        assertThatThrownBy(() -> calculator.divide(BigDecimal.ONE, BigDecimal.ZERO))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Division by zero is not allowed");
    }

    @Test
    @DisplayName("divide by 0.0 is also rejected")
    void divideByZeroWithDecimalScale() {
        assertThatThrownBy(() -> calculator.divide(BigDecimal.TEN, new BigDecimal("0.0")))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    @DisplayName("results drop trailing zeros")
    void resultIsNormalized() {
        assertThat(calculator.add(new BigDecimal("2.50"), new BigDecimal("2.50")).toPlainString())
                .isEqualTo("5");
    }
}
