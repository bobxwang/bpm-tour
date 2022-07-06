package com.flowable.second.config;

import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.cmmn.spring.SpringCmmnEngineConfiguration;
import org.flowable.dmn.spring.SpringDmnEngineConfiguration;
import org.flowable.eventregistry.spring.SpringEventRegistryEngineConfiguration;
import org.flowable.form.spring.SpringFormEngineConfiguration;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineAutoConfiguration;
import org.flowable.spring.boot.eventregistry.EventRegistryAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/3
 */
@Configuration
@AutoConfigureAfter(value = {
        ProcessEngineAutoConfiguration.class,
        CmmnEngineAutoConfiguration.class,
        DmnEngineAutoConfiguration.class,
        FormEngineAutoConfiguration.class,
        EventRegistryAutoConfiguration.class,
        AppEngineAutoConfiguration.class
})
public class FlowCustomEngineConfig {

    @Bean
    @ConditionalOnMissingBean(
            name = {"bobSpringProcessEngineConfiguration"}
    )
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> bobSpringProcessEngineConfiguration() {
        return (x) -> {

            System.out.println("bobSpringProcessEngineConfiguration");
            x.setEnableLogSqlExecutionTime(true);
            if (!CollectionUtils.isEmpty(x.getCustomMybatisInterceptors())) {
                x.getCustomMybatisInterceptors()
                        .forEach(y -> System.out.println(y));
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(
            name = {"bobSpringCmmnEngineConfiguration"}
    )
    public EngineConfigurationConfigurer<SpringCmmnEngineConfiguration> bobSpringCmmnEngineConfiguration() {
        return (x) -> {

            System.out.println("bobSpringCmmnEngineConfiguration");
            x.setEnableLogSqlExecutionTime(true);
            if (!CollectionUtils.isEmpty(x.getCustomMybatisInterceptors())) {
                x.getCustomMybatisInterceptors()
                        .forEach(y -> System.out.println(y));
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean(
            name = {"bobSpringDmnEngineConfiguration"}
    )
    public EngineConfigurationConfigurer<SpringDmnEngineConfiguration> bobSpringDmnEngineConfiguration() {
        return (x) -> {

            System.out.println("bobSpringDmnEngineConfiguration");
            x.setEnableLogSqlExecutionTime(true);
            if (!CollectionUtils.isEmpty(x.getCustomMybatisInterceptors())) {
                x.getCustomMybatisInterceptors()
                        .forEach(y -> System.out.println(y));
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean(
            name = {"bobSpringFormEngineConfiguration"}
    )
    public EngineConfigurationConfigurer<SpringFormEngineConfiguration> bobSpringFormEngineConfiguration() {
        return (x) -> {

            System.out.println("bobSpringFormEngineConfiguration");
            x.setEnableLogSqlExecutionTime(true);
            if (!CollectionUtils.isEmpty(x.getCustomMybatisInterceptors())) {
                x.getCustomMybatisInterceptors()
                        .forEach(y -> System.out.println(y));
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean(
            name = {"bobSpringEventRegistryEngineConfiguration"}
    )
    public EngineConfigurationConfigurer<SpringEventRegistryEngineConfiguration> bobSpringEventRegistryEngineConfiguration() {
        return (x) -> {

            System.out.println("bobSpringEventRegistryEngineConfiguration");
            x.setEnableLogSqlExecutionTime(true);
            if (!CollectionUtils.isEmpty(x.getCustomMybatisInterceptors())) {
                x.getCustomMybatisInterceptors()
                        .forEach(y -> System.out.println(y));
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(
            name = {"bobSpringAppEngineConfiguration"}
    )
    public EngineConfigurationConfigurer<SpringAppEngineConfiguration> bobSpringAppEngineConfiguration() {
        return (x) -> {

            System.out.println("bobSpringAppEngineConfiguration");
            x.setEnableLogSqlExecutionTime(true);
            if (!CollectionUtils.isEmpty(x.getCustomMybatisInterceptors())) {
                x.getCustomMybatisInterceptors()
                        .forEach(y -> System.out.println(y));
            }
        };
    }
}
