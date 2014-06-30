##ProvGen ReadMe:

----

####Building instructions:
1. Clone this repo `git clone...`
2. From within the project root, run the command `mvn clean jetty:run`

####Usage
3. After having built the provGen project, use a browser to navigate to `http://localhost:8080`.
4. Once on the appropriate page you may read about ProvGen, or run the demo [here](http://localhost:8080/#/demo).

###The Demo

The ProvGen demo consists of a simple form, into which users may provide seed graphs (in the PROV-N format), constraint rules and execution paramters. When submitted, the form causes an Http POST request to the Java backend of provGen. After several seconds* a PROV trace is returned, serialized using the provenance notation.

\* **There is a large overhead associated with setting up and tearing down the underlying Neo4j database. As a result, even small generations are likely to take a few 10s of seconds. In testing, for example, generating 1000 and 10,000 vertices takesroughly 30 seconds.**

Below are a small selection of practical examples on how you might use the generator to create PROV graphs: