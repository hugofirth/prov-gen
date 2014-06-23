package uk.ac.ncl.prov.lib.constraint;

/**
 * Created by hugofirth on 23/05/2014.
 */
public interface Term {

    //Not possible as of Java 7 though has been introduced in Java 8. Left in case we switch Language levels later on.
    /*public static <E extends Enum<E> & Term> E withName(Class<E> enumType, String name)
    {
        if(name != null) {
            return (E) Enum.valueOf(enumType, name.toUpperCase());
        }
        throw new IllegalArgumentException("No constant name provided!");
    }*/


}
