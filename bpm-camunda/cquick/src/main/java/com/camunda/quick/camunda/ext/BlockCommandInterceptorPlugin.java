package com.camunda.quick.camunda.ext;

import com.camunda.quick.mybatis.ext.SqlPrintInterceptor;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandInterceptor;
import org.camunda.bpm.engine.impl.interceptor.LogInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/12
 */
public class BlockCommandInterceptorPlugin extends AbstractProcessEnginePlugin {

    protected BlockCommandInterceptor blockCommandInterceptor = new BlockCommandInterceptor();
    protected LogInterceptor logInterceptor = new LogInterceptor();

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

        super.preInit(processEngineConfiguration);

        List<CommandInterceptor> customPreCommandInterceptorsTxRequired = processEngineConfiguration
                .getCustomPreCommandInterceptorsTxRequired();
        if (customPreCommandInterceptorsTxRequired == null) {
            customPreCommandInterceptorsTxRequired = new ArrayList<>();
            processEngineConfiguration
                    .setCustomPreCommandInterceptorsTxRequired(customPreCommandInterceptorsTxRequired);
        }
        customPreCommandInterceptorsTxRequired.add(blockCommandInterceptor);
        customPreCommandInterceptorsTxRequired.add(logInterceptor);

        List<CommandInterceptor> customPreCommandInterceptorsTxRequiresNew = processEngineConfiguration
                .getCustomPreCommandInterceptorsTxRequiresNew();
        if (customPreCommandInterceptorsTxRequiresNew == null) {
            customPreCommandInterceptorsTxRequiresNew = new ArrayList<>();
            processEngineConfiguration.setCustomPreCommandInterceptorsTxRequiresNew(customPreCommandInterceptorsTxRequiresNew);
        }
        customPreCommandInterceptorsTxRequiresNew.add(blockCommandInterceptor);
        customPreCommandInterceptorsTxRequiresNew.add(logInterceptor);
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

        super.postInit(processEngineConfiguration);

        processEngineConfiguration.getSqlSessionFactory()
                .getConfiguration().addInterceptor(new SqlPrintInterceptor());
    }
}
