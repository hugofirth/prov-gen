package uk.ac.ncl.prov.lib.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by hugofirth on 09/03/2014.
 */
public abstract class Element implements Labelable, PropertyContainer  {

    private final UUID id;
    protected final Label label;
    protected final String variable;
    private final Map<String, Object> properties;

    protected Element(Builder builder)
    {
        this.id = builder.id;
        this.label = builder.label;
        this.variable = builder.variable;
        this.properties = builder.properties;
    }

    public UUID getId()
    {
        return this.id;
    }

    @Override
    public final Label getLabel()
    {
       return this.label;
    }

    @Override
    public final void setProperty(String key, Object value)
    {
        this.properties.put(key, value);
    }

    @Override
    public final <T> T getProperty(String key, Class<T> type)
    {
        return type.cast(this.properties.get(key));
    }

    @Override
    public final <T> T removeProperty(String key, Class<T> type)
    {
        return type.cast(this.properties.remove(key));
    }

    @Override
    public final Map<String, Object> getProperties()
    {
        return this.properties;
    }

    @Override
    public final Set<String> getPropertyKeys()
    {
        return this.properties.keySet();
    }

    public final String getVariable()
    {
        return this.variable;
    }

    public abstract static class Builder {
        private final UUID id;
        private Map<String, Object> properties;
        private String variable;
        protected Label label;

        protected Builder()
        {
            this.id = UUID.randomUUID();
        }

        public final Builder properties(Map<String, Object> p)
        {
            this.properties = p;
            return this;
        }

        public final Builder var(String s)
        {
            this.variable = s;
            return this;
        }

        public Builder label(Label l)
        {
            this.label = l;
            return this;
        }

        public abstract Element build();
    }

}
