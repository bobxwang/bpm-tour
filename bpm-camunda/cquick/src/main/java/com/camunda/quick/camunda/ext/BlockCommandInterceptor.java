package com.camunda.quick.camunda.ext;

import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/12
 */
public class BlockCommandInterceptor extends CommandInterceptor {

    private Logger logger = LoggerFactory.getLogger(BlockCommandInterceptor.class);

    @Override
    public <T> T execute(Command<T> command) {

//        logger.info("before execute [{}]", command.getClass());
        T t = next.execute(command);
//        if (t != null) {
//            logger.info("after execute [{}]", t.getClass());
//        } else {
//            logger.info("after execute, t is null");
//        }
        return t;
    }
}