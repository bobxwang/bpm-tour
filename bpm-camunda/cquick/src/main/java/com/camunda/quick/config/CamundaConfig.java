package com.camunda.quick.config;

import com.camunda.quick.camunda.ext.BlockCommandInterceptorPlugin;
import com.camunda.quick.camunda.ext.InformAssigneeParseListenerPlugin;
import com.camunda.quick.camunda.ext.InformAssigneeTaskListener;
import com.camunda.quick.camunda.ext.CustomTaskServiceImpl;
import org.camunda.bpm.engine.impl.cfg.CompositeProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.camunda.bpm.spring.boot.starter.util.CamundaSpringBootUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/12
 */
@Configuration
@Order
public class CamundaConfig {

    private Logger logger = LoggerFactory.getLogger(CamundaConfig.class);

    @Bean
    public SpringProcessEngineConfiguration processEngineConfigurationImpl(List<ProcessEnginePlugin> processEnginePlugins) {
        SpringProcessEngineConfiguration configuration = CamundaSpringBootUtil.springProcessEngineConfiguration();
        configuration.getProcessEnginePlugins().add(new CompositeProcessEnginePlugin(processEnginePlugins));
        configuration.setTaskService(new CustomTaskServiceImpl());
        return configuration;
    }

    /**
     * 监听自动布署事件
     *
     * @param unused
     */
    @EventListener
    public void notify(final PostDeployEvent unused) {

    }

    @Bean
    public BlockCommandInterceptorPlugin blockCommandInterceptorPlugin() {
        return new BlockCommandInterceptorPlugin();
    }

    @Bean
    public InformAssigneeTaskListener informAssigneeTaskListener() {
        return new InformAssigneeTaskListener();
    }

    @Bean
    public InformAssigneeParseListenerPlugin informAssigneeParseListenerPlugin
            (InformAssigneeTaskListener informAssigneeTaskListener) {

        return new InformAssigneeParseListenerPlugin(informAssigneeTaskListener);
    }
}
