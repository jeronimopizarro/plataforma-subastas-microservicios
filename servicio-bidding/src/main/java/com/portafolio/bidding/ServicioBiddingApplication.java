package com.portafolio.bidding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServicioBiddingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioBiddingApplication.class, args);
    }

}
