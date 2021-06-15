package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qilihui
 * @date 2021/6/15 22:20
 */
@Controller
public class PageController {
    @GetMapping({"/", "/index"})
    public String index(){
        return "index";
    }
}
