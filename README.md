##ProvGen ReadMe:

----

####Building instructions:
* Clone this repo `git clone...`
* From within the project root, run the command `mvn clean jetty:run`

####Usage:
* After having built the provGen project, use a browser to navigate to `http://localhost:8080`.
* Once on the appropriate page you may read about ProvGen, or run the demo [here](http://localhost:8080/#/demo).

####Contribution guidelines:
* The ProvGen tool is still under active development, and may contain several bugs!
* At the moment - comments and documentation throughout the codebase are nowhere near as thorough as I would like - in the near future I plan to improve this so that people can more easily get into the code ... and hopefully submit pull requests!
* In the meantime any feedback, questions, suggestions or issues you have would be gladly received. Head on over and open [an issue](https://github.com/hugofirth/prov-gen/issues). 

###The Demo

The ProvGen demo consists of a simple form, into which users may provide seed graphs (in the PROV-N format), constraint rules and execution paramters. When submitted, the form causes an Http POST request to the Java backend of provGen. After several seconds* a PROV trace is returned, serialized using the provenance notation.

\* **There is a large overhead associated with setting up and tearing down the underlying Neo4j database. As a result, even small generations are likely to take a few 10s of seconds. In testing, for example, generating both 1000 and 10,000 vertices took roughly 30 seconds.**

Below are a small selection of practical examples on how you might use the generator to create PROV graphs:


##1. 
In order to generate large examples of a simple usage and generation graph - provide the following **seed graph**:

```
document
    entity(e1, [version="original"])
    activity(a1, [fct="edit"])
    entity(e2)
    activity(a2,[fct="edit"])
    entity(e3)
    used(r1;a1,e1)
    used(r2;a2,e2)
    wasGeneratedBy(r3;e2,a1)
    wasGeneratedBy(r4;e3,a2)
endDocument
```

Along with the following **constraint rules**:
