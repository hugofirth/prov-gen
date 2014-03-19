package uk.ac.ncl.prov.lib.query;

import java.util.List;
import java.util.Map;

/**
 * Created by hugofirth on 09/03/2014.
 */
public interface Result {
    public Boolean isEmpty();
    public Boolean didUpdate();
    public Map<String, List<Object>> getResultList();
}
