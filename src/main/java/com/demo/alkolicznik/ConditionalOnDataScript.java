package com.demo.alkolicznik;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;

public class ConditionalOnDataScript extends AnyNestedCondition {

    ConditionalOnDataScript() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnBean(com.demo.alkolicznik.DeleteScript.class)
    static class DeleteScript {}

    @ConditionalOnBean(com.demo.alkolicznik.ReloadScript.class)
    static class ReloadScript {}

    @Profile("demo")
    static class demoProfile {}
}
