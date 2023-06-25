package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StoreHtmlController {

    private StoreService storeService;

    public StoreHtmlController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/store")
    public String showStores(Model model) {
        model.addAttribute("stores", storeService.getStores());
        return "stores";
    }
}
