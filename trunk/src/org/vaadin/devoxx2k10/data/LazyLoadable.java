package org.vaadin.devoxx2k10.data;


/**
 * LazyLoadable provides an URI for lazily loading more details via REST API.
 * 
 * @see LazyLoad
 * @see LazyLoadProxy
 */
public interface LazyLoadable {

    /**
     * Returns an URI for loading more JSON data though a REST API.
     * 
     * @return
     */
    public String getLazyLoadingUri();

}
