package com.demo.alkolicznik.web.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StoreWebController {

    private StoreService storeService;

    public StoreWebController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/store")
    public String showStores(Model model) {
        model.addAttribute("stores", storeService.getStores());
        return "stores";
    }
}
