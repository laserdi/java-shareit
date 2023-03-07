package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItGateway {
    public static void main(String[] args) {
        SpringApplication.run(ShareItGateway.class, args);
        System.out.println("*".repeat(134) + "\n" + "*".repeat(40) + "       Спринт №17. Add-Docker. Сервер Gateway запущен.      " + "*".repeat(40) + "\n" + "*".repeat(134));
    }
}