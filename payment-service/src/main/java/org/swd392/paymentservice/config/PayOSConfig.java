package org.swd392.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    private final String CLIENT_ID = "d29b3b39-37ab-4f39-9f71-67a6c2aa115e";
    private final String API_KEY = "6efcfc9b-b0cd-454e-858b-1cecc8820362";
    private final String CHECK_SUM_KEY = "3869704876d05fb921ee674e6abd079ea45788a9e15e72a63c7c7ef88c54b28f";

    @Bean
    public PayOS payOS() {
        return new PayOS(CLIENT_ID, API_KEY, CHECK_SUM_KEY);
    }
}
