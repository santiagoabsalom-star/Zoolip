package com.surrogate.Zoolip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

@SpringBootApplication
public class ZoolipApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZoolipApplication.class, args);
        String gcName = ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .map(GarbageCollectorMXBean::getName)
                .reduce("", (a, b) -> a + " " + b);

        if (gcName.contains("G1")) {
            System.out.println("Usando G1GC");
        } else if (gcName.contains("ZGC")) {
            System.out.println("Usando ZGC");
        } else if (gcName.contains("Shenandoah")) {
            System.out.println("Usando Shenandoah GC");
        } else if (gcName.contains("PS")) {
            System.out.println("Usando Parallel GC");
        } else {
            System.out.println("GC desconocido: " + gcName);
        }
    }

}
