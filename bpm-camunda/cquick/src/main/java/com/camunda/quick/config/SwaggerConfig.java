package com.camunda.quick.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/15
 */
@Configuration
@ConditionalOnClass({Docket.class, InMemorySwaggerResourcesProvider.class})
@ConditionalOnBean(InMemorySwaggerResourcesProvider.class)
public class SwaggerConfig {

    @Autowired
    private InMemorySwaggerResourcesProvider inMemorySwaggerResourcesProvider;

    @Bean
    @Primary
    public BobSwaggerResourcesProvider bobSwaggerResourcesProvider() {

        return new BobSwaggerResourcesProvider(inMemorySwaggerResourcesProvider);
    }

    public static class BobSwaggerResourcesProvider implements SwaggerResourcesProvider {

        private InMemorySwaggerResourcesProvider inMemorySwaggerResourcesProvider;

        public BobSwaggerResourcesProvider(InMemorySwaggerResourcesProvider inMemorySwaggerResourcesProvider) {
            this.inMemorySwaggerResourcesProvider = inMemorySwaggerResourcesProvider;
        }

        @Override
        public List<SwaggerResource> get() {

            List<SwaggerResource> list = inMemorySwaggerResourcesProvider.get();

            SwaggerResource swaggerResource = new SwaggerResource();

            swaggerResource.setName("camunda");
            swaggerResource.setSwaggerVersion(DocumentationType.SWAGGER_2.getVersion());
            swaggerResource.setLocation("/camunda/api-docs");

            list.add(swaggerResource);

            return list;
        }
    }
}