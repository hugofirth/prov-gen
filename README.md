##ProvGen ReadMe:

----

####Building instructions:
* Clone this repo `git clone...`
* Use the following maven options `export MAVEN_OPTS="-Xms512m -Xmx1024m"`
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

```
an Entity must have property("version"="original") with probability 0.1;
an Entity must have relationship "WasGeneratedBy" exactly 1 times unless it has property("version"="original");
an Activity must have degree at most 2;
an Entity must have relationship "Used" exactly 1 times;
```

##2. 
In order to generate a slightly more complex example involving a greater range of PROV terms - provide the following **seed graph**:

```
document
    entity(e1, [type="Document", version="original"])
    entity(e2, [type="Document"])
    entity(e3, [type="Document"])
    activity(a1, [type="create"])
    activity(a2, [type="edit"])
    activity(a3, [type="edit"])
    agent(ag, [type="Person"])
    used(a2, e1)
    used(a3, e2)
    wasGeneratedBy(e2, a2, [fct="save"])
    wasGeneratedBy(e1, a1, [fct="publish"])
    wasGeneratedBy(e3, a3, [fct="save"])
    wasAssociatedWith(a3, ag, [role="contributor"])
    wasAssociatedWith(a2, ag, [role="contributor"])
    wasAssociatedWith(a1, ag, [role="creator"])
    wasDerivedFrom(e2, e1)
    wasDerivedFrom(e3, e2)
endDocument
```

Along with the following **constraint rules**:

```
an Entity must have relationship "WasDerivedFrom" exactly 2 times unless it has property("version"="original");
an Entity must have relationship "WasGeneratedBy" exactly 1 times;
an Entity must have property("version"="original") with probability 0.05;
an Entity must have out degree at most 2;
an Activity must have relationship "Used" at most 1 times;
an Activity must have property("type"="create") with probability 0.01;
an Activity must have relationship "WasAssociatedWith" exactly 1 times;
an Activity must have relationship "Used" exactly 1 times unless it has property("type"="create");
an Activity must have relationship "WasGeneratedBy" exactly 1 times;
an Agent must have relationship "WasAssociatedWith" with probability 0.1;
an Agent must have relationship "WasAssociatedWith" between 1, 120 times with distribution gamma(1.3, 2.4);
```

