package com.example.springboot.demo.web;

import com.example.springboot.demo.annotations.SydcRateLimit;
import com.example.springboot.demo.enums.AlgorithmType;
import com.example.springboot.demo.enums.LimitType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RateLimitController {

    @GetMapping("/greeting")
    @SydcRateLimit(algorithm = AlgorithmType.COUNTER,limitType = LimitType.URI,limit = 2,interval = 5)
    public String greeting(@RequestParam(name = "name",required = false,defaultValue = "World") String name, Model model){
        model.addAttribute("name",name);
        return "greeting";
    }
}
