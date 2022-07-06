package com.flowable.second.ext;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/4
 */
public class AJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {

        System.out.println("print the variables " + delegateExecution.getVariableInstances());
    }
}