package com.mf.starter.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleResource {
    @GetMapping("/info")
    public String getRoles (){
        return "hello";
    }

}
