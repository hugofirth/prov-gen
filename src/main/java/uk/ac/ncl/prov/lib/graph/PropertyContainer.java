package uk.ac.ncl.prov.lib.graph;

import java.util.Map;
import java.util.Set;

/**
 * Created by hugofirth on 15/03/2014.
 */
public interface PropertyContainer {
    void setProperty(String key, Object value);
    <T> T getProperty(String key, Class<T> type);
    <T> T removeProperty(String key, Class<T> type);
    Map<String, Object> getProperties();
    Set<String> getPropertyKeys();
}
