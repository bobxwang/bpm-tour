package com.flowable.second.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/4
 */
@Configuration
@ConditionalOnClass({Docket.class})
@ComponentScan()
public class SwaggerConfiguration {

    private static final String splitor = ";";

//    basePackage("org.flowable.rest.service.api.identity")

    @Bean
    public Docket docket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("测试 APIs")
                .description("测试api接口文档")
                .termsOfServiceUrl("http://www.baidu.com")
                .version("1.0")
                .build();
    }

    private Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            System.out.println(input.getPackage().getName());
            // 循环判断匹配
            String[] bps = basePackage.split(splitor);
            for (String strPackage : bps) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

}
