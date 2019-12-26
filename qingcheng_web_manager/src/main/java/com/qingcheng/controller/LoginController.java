package com.qingcheng.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @GetMapping("/name")
    public Map shouName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap hashMap = new HashMap();
        hashMap.put("name",name);
        return hashMap;
    }
}
