package com.bigdatatag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by bsmk on 9/9/15.
 */
@SpringBootApplication
public class App extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

}
