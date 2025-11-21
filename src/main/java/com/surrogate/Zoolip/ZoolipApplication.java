package com.surrogate.Zoolip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

@SpringBootApplication
public class ZoolipApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZoolipApplication.class, args);
        System.out.println("Usando: "+ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .map(GarbageCollectorMXBean::getName)
                .reduce("", (a, b) -> a + " " + b));


    }

}
