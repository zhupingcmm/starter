package com.example.starter.rest;

import com.sun.org.glassfish.gmbal.ParameterNames;
import lombok.Data;
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

    @Data
    static class Profile {
        private String name;
        private int age;
        private String gender;
    }
}
