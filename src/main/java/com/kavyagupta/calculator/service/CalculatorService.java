package com.kavyagupta.calculator.service;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    // 16 significant digits, same precision as a double, but in decimal.
    private static final MathContext PRECISION = MathContext.DECIMAL64;

    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return normalize(a.add(b, PRECISION));
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return normalize(a.subtract(b, PRECISION));
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return normalize(a.multiply(b, PRECISION));
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return normalize(a.divide(b, PRECISION));
    }

    // 5.000 -> 5, 2.50 -> 2.5
    private BigDecimal normalize(BigDecimal value) {
        return value.signum() == 0 ? BigDecimal.ZERO : value.stripTrailingZeros();
    }
}
