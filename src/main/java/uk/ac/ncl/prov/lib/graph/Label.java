package uk.ac.ncl.prov.lib.graph;

/**
 * Created by hugofirth on 09/03/2014.
 */
public interface Label {

    /**
     * Returns the name of the Label. Labels are assigned to Vertices and Edges and provide 'sub-typing'.
     * If two labels of the same subtype have {@link String#equals(Object) equal} names then they are
     * considered equal.
     * @return String the name of the Label
     */
    String getName();
}
