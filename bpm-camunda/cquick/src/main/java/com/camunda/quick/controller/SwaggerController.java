package com.camunda.quick.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/15
 */
@RestController
public class SwaggerController {

    @GetMapping("/camunda/api-docs")
    public ResponseEntity<Object> camundaSwagger() throws Exception {

        ClassPathResource cpr = new ClassPathResource("openapi.json");
        String text = IOUtils.toString(cpr.getInputStream(), "UTF-8");
        return new ResponseEntity(new Json(text), HttpStatus.OK);
    }
}
