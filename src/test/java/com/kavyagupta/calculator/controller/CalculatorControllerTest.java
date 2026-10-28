package com.kavyagupta.calculator.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.kavyagupta.calculator.service.CalculatorService;

@WebMvcTest(CalculatorController.class)
@Import(CalculatorService.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addReturnsResultAsJson() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(2))
                .andExpect(jsonPath("$.b").value(3))
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void subtractReturnsResult() throws Exception {
        mockMvc.perform(get("/api/v1/subtract").param("a", "10").param("b", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.result").value(5.5));
    }

    @Test
    void multiplyReturnsResult() throws Exception {
        mockMvc.perform(get("/api/v1/multiply").param("a", "2.5").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("multiply"))
                .andExpect(jsonPath("$.result").value(10));
    }

    @Test
    void divideReturnsResult() throws Exception {
        mockMvc.perform(get("/api/v1/divide").param("a", "7").param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.result").value(3.5));
    }

    @Test
    void decimalAdditionIsExact() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "0.1").param("b", "0.2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0.3));
    }

    @Test
    void divideByZeroReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/divide").param("a", "1").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Division by zero is not allowed"))
                .andExpect(jsonPath("$.path").value("/api/v1/divide"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void missingParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required parameter 'b' is missing"));
    }

    @Test
    void nonNumericParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/multiply").param("a", "abc").param("b", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'a' must be a number, but was 'abc'"));
    }

    @Test
    void nanParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/subtract").param("a", "1").param("b", "NaN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'b' must be a number, but was 'NaN'"));
    }

    @Test
    void infinityParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "Infinity").param("b", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'a' must be a number, but was 'Infinity'"));
    }
}
