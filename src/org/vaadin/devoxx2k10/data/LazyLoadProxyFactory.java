package org.vaadin.devoxx2k10.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Factory for creating proxies to handle lazy loading.
 * 
 * @see LazyLoad
 */
public class LazyLoadProxyFactory {

    /**
     * Returns a proxy for the given LazyLoadable that automatically takes care
     * of all lazy loading more data when required.
     * 
     * @param lazyLoadable
     * @param facade
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <V extends LazyLoadable> V getProxy(V lazyLoadable,
            RestApiFacade facade) {
        return (V) Proxy.newProxyInstance(lazyLoadable.getClass()
                .getClassLoader(), lazyLoadable.getClass().getInterfaces(),
                new LazyLoadProxy(lazyLoadable, facade));
    }

    private static class LazyLoadProxy implements InvocationHandler {

        private final LazyLoadable lazyLoadable;
        private final RestApiFacade facade;
        private volatile boolean lazyLoaded;

        public LazyLoadProxy(LazyLoadable lazyLoadable, RestApiFacade facade) {
            this.lazyLoadable = lazyLoadable;
            this.facade = facade;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            Method actualMethod = lazyLoadable.getClass().getMethod(
                    method.getName(), method.getParameterTypes());
            if (!lazyLoaded && actualMethod.isAnnotationPresent(LazyLoad.class)) {
                facade.lazyLoadFields(lazyLoadable);
                lazyLoaded = true;
            }
            return method.invoke(lazyLoadable, args);
        }
    }

}
