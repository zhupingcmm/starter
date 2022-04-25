package com.example.starter.rest;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserResource {
    @GetMapping("/greeting")
    public String greeting () {
        return "hello";
    }

    @PostMapping("/greeting")
    public String addGreeting(@RequestParam String name){
        return "hello" + name;
    }

    @PutMapping("/greeting/{name}")
    public String changeGreeting(@PathVariable String name, @RequestBody Profile profile){
        return "hello" + name + ":" + profile.getAge();
    }

    @GetMapping("/principal")
    public Authentication getPrincipal(Authentication authentication) {
        return authentication;
    }

    @Data
    static class Profile {
        private String name;
        private int age;
        private String gender;
    }
}
