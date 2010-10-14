package org.vaadin.devoxx2k10.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * 
 * @see LazyLoad
 */
public class LazyLoadProxy<T extends LazyLoadable> implements InvocationHandler {

    private final T lazyLoadable;
    private final RestApiFacade facade;
    private volatile boolean lazyLoaded;

    private LazyLoadProxy(T presentation, RestApiFacade facade) {
        this.lazyLoadable = presentation;
        this.facade = facade;
    }

    /**
     * Returns a proxy wrapper for the give LazyLoadable that takes care of
     * lazily loading more data when required.
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
                new LazyLoadProxy<V>(lazyLoadable, facade));
    }

    @Override
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
