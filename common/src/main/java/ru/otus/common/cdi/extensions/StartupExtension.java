package ru.otus.common.cdi.extensions;

import ru.otus.common.cdi.Startup;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.ArrayList;
import java.util.List;

public class StartupExtension implements Extension {
    private List<Bean<?>> startupBeansList = new ArrayList<>();

    public <T> void collect(@Observes ProcessBean<T> event) {
        if (event.getAnnotated().isAnnotationPresent(Startup.class)
                && event.getAnnotated().isAnnotationPresent(ApplicationScoped.class)) {
            startupBeansList.add(event.getBean());
        }
    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        for (Bean<?> bean : startupBeansList) {
            beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).toString();
        }
    }

}
