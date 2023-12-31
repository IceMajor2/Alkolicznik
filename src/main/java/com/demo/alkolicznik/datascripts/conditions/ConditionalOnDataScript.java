package com.demo.alkolicznik.datascripts.conditions;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;

public class ConditionalOnDataScript extends AnyNestedCondition {

    ConditionalOnDataScript() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnBean(com.demo.alkolicznik.datascripts.DeleteScript.class)
    static class DeleteScript {}

    @ConditionalOnBean(com.demo.alkolicznik.datascripts.ReloadScript.class)
    static class ReloadScript {}

    @Profile("demo")
    static class demoProfile {}
}
