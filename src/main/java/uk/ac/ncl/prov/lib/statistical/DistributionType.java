package uk.ac.ncl.prov.lib.statistical;

import uk.ac.ncl.prov.lib.constraint.Term;
import uk.ac.ncl.prov.lib.constraint.WithInput;

/**
 * Created by hugofirth on 20/05/2014.
 */
public enum DistributionType implements Term, WithInput {

    UNIFORM("Uniform") {
        @Override
        public Boolean validateInput(Object... params)
        {
            if(params.length != 2)
            {
                throw new IllegalArgumentException("Wrong number of arguments! Uniform distribution requires (int lower, int upper).");
            }
            if(!(params[0] instanceof Integer))
            {
                throw new IllegalArgumentException("Wrong type for argument 1! Uniform distribution requires (int lower, int upper). Integer expected, instead got "+params[0].getClass().getCanonicalName());
            }
            if(!(params[1] instanceof Integer))
            {
                throw new IllegalArgumentException("Wrong type for argument 2! Uniform distribution requires (int lower, int upper). Integer expected, instead got "+params[1].getClass().getCanonicalName());
            }

            //If all validation successful
            return true;
        }
    },

    BINOMIAL("Binomial") {
        @Override
        public Boolean validateInput(Object... params)
        {
            if(params.length != 2)
            {
                throw new IllegalArgumentException("Wrong number of arguments! Binomial distribution requires (int trials, double p).");
            }
            if(!(params[0] instanceof Integer))
            {
                throw new IllegalArgumentException("Wrong type for argument 1! Binomial distribution requires (int trials, double p). Integer expected, instead got "+params[0].getClass().getCanonicalName());
            }
            if(!(params[1] instanceof Double))
            {
                throw new IllegalArgumentException("Wrong type for argument 2! Binomial distribution requires (int trials, double p). Double expected, instead got "+params[1].getClass().getCanonicalName());
            }

            //If all validation is successful
            return true;
        }
    },

    GAMMA("Gamma") {
        @Override
        public Boolean validateInput(Object... params)
        {
            if(params.length != 2)
            {
                throw new IllegalArgumentException("Wrong number of arguments! Gamma distribution requires (double shape, double scale).");
            }
            for(int i = 0; i<params.length; i++)
            {
                if(!(params[i] instanceof Double))
                {
                    throw new IllegalArgumentException("Wrong type for argument "+i+"! Gamma distribution requires (double shape, double scale). Double expected, instead got "+params[i].getClass().getCanonicalName());
                }
            }
            //If all validation is successful
            return true;
        }
    },

    HYPERGEOMETRIC("HyperGeometric") {
        @Override
        public Boolean validateInput(Object... params)
        {
            if(params.length != 3)
            {
                throw new IllegalArgumentException("Wrong number of arguments! HyperGeometric distribution requires (int populationSize, int numberOfSuccess, int sampleSize).");
            }
            for(int i = 0; i<params.length; i++)
            {
                if(!(params[i] instanceof Integer))
                {
                    throw new IllegalArgumentException("Wrongs type for argument "+i+"! HyperGeometric distribution requires (int populationSize, int numberOfSuccess, int sampleSize). Integer expected, instead got "+params[i].getClass().getCanonicalName());
                }
            }
            //If all validation is successful
            return true;
        }
    },

    PASCAL("Pascal") {
        @Override
        public Boolean validateInput(Object... params)
        {
            if(params.length != 2)
            {
                throw new IllegalArgumentException("Wrong number of arguments! Pascal distribution requires (int r, double p).");
            }
            if(!(params[0] instanceof Integer))
            {
                throw new IllegalArgumentException("Wrong type for argument 1! Pascal distribution requires (int r, double p). Integer expected, instead got "+params[0].getClass().getCanonicalName());
            }
            if(!(params[1] instanceof Double))
            {
                throw new IllegalArgumentException("Wrong type for argument 2! Pascal distribution requires (int r, double p). Double expected, instead got "+params[1].getClass().getCanonicalName());
            }

            //If all validation is successful
            return true;
        }
    },

    ZIPF("Zipf") {
        @Override
        public Boolean validateInput(Object... params)
        {
            if(params.length != 2)
            {
                throw new IllegalArgumentException("Wrong number of arguments! Zipf distribution requires (int numberOfElements, double exponent).");
            }
            if(!(params[0] instanceof Integer))
            {
                throw new IllegalArgumentException("Wrong type for argument 1! Zipf distribution requires (int numberOfElements, double exponent). Integer expected, instead got "+params[0].getClass().getCanonicalName());
            }
            if(!(params[1] instanceof Double))
            {
                throw new IllegalArgumentException("Wrong type for argument 2! Zipf distribution requires (int numberOfElements, double exponent). Double expected, instead got "+params[1].getClass().getCanonicalName());
            }

            //If all validation is successful
            return true;
        }
    };

    private final String name;

    DistributionType(String name) {
        this.name = name;
    }

    public static DistributionType withName(String name) throws IllegalArgumentException {
        if(name != null) {
            for(DistributionType d : DistributionType.values()) {
                if(name.equalsIgnoreCase(d.name)) {
                    return d;
                }
            }
        }
        throw new IllegalArgumentException("No distribution with name " + name + " found");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
