package uk.ac.ncl.prov.lib.graph.vertex;

import uk.ac.ncl.prov.lib.graph.Element;

/**
 * Created by hugofirth on 09/03/2014.
 */
public class Vertex extends Element {

    public enum DegreePreposition {

        IN("in"),
        OUT("out"),
        TOTAL("total");

        private final String assoc;

        DegreePreposition(String assoc) {
            this.assoc = assoc;
        }

        public static DegreePreposition withName(String s) throws IllegalArgumentException {
            if(s != null) {
                for(DegreePreposition p : DegreePreposition.values()) {
                    if(s.equalsIgnoreCase(p.assoc)) {
                        return p;
                    }
                }
            }
            throw new IllegalArgumentException("No constant with text " + s + " found");
        }

        @Override
        public String toString() {
            return this.assoc;
        }
    }

    private Vertex(Builder builder)
    {
        super(builder);
    }

    public static class V extends Element.Builder {

        public Vertex build()
        {
            return new Vertex(this);
        }
    }
}
