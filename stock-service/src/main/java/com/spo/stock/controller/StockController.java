package com.spo.stock.controller;

import com.spo.stock.dto.StockResponse;
import com.spo.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StockResponse> isInStock(@RequestParam List<String> sku) {
        return stockService.isInStock(sku);
    }
}
