package com.bipedalprogrammer.journal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeController {
    @GetMapping("/browse")
    public String getBrowsePage(Model model) {
        return "browse";
    }

    @GetMapping("/")
    public String getIndex(Model model) { return "index"; }
}
