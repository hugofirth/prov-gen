package uk.ac.ncl.prov.lib.graph;

import java.util.*;

/**
 * Created by hugofirth on 09/03/2014.
 */
public abstract class Element implements Labelable, PropertyContainer  {
    //TODO: make this atomic and multi thread this program
    private final long id;
    protected final Label label;
    protected final String variable;
    private final Map<String, Object> properties;

    protected Element(Builder builder)
    {
        this.id = Builder.id;
        this.label = builder.label;
        this.variable = builder.variable;
        this.properties = builder.properties;
    }

    public Long getId()
    {
        return this.id;
    }

    @Override
    public final Label getLabel()
    {
       return this.label;
    }

    @Override
    public final boolean hasProperty(String key)
    {
        return (this.properties.get(key) != null);
    }

    @Override
    public final void setProperty(String key, Object value)
    {
        this.properties.put(key, value);
    }

    @Override
    public final Object getProperty(String key)
    {
        return this.properties.get(key);
    }

    @Override
    public final Object removeProperty(String key)
    {
        return this.properties.remove(key);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (id != element.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * (int) (id ^ (id >>> 32));
    }

    public boolean isSimilar(Object o)
    {
        if(this.equals(o)) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if(this.getProperties().size() != element.getProperties().size()) return false;
        for(String key: element.getPropertyKeys())
        {
            if(!element.getProperty(key).equals(this.getProperty(key))) return false;
        }
        if(!element.getLabel().equals(this.getLabel())) return false;

        return true;
    }

    public boolean isSimilarIgnoreProperties(Object o)
    {
        if(this.equals(o)) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if(!element.getLabel().equals(this.getLabel())) return false;

        return true;
    }

    public abstract static class Builder<T extends Element>{
        private static Long id = 0L;
        protected Map<String, Object> properties;
        protected String variable;
        protected Label label;

        protected Builder()
        {
            this.properties = new HashMap<>();
            Builder.id++;
        }

        protected Builder properties(Map<String, Object> p)
        {
            this.properties = p;
            return this;
        }

        public String getVariable()
        {
            return this.variable;
        }

        public abstract T build();
    }

}
