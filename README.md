Deliberate Q
==============
<a href="https://travis-ci.org/DeliberativeAnalysis/DeliberateQ"><img src="https://travis-ci.org/DeliberativeAnalysis/DeliberateQ.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.DeliberativeAnalysis/DeliberateQ/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.DeliberativeAnalysis/DeliberateQ)<br/>
[![codecov](https://codecov.io/gh/DeliberativeAnalysis/DeliberateQ/branch/master/graph/badge.svg)](https://codecov.io/gh/DeliberativeAnalysis/DeliberateQ)

Deliberate Q is a visualisation tool for Q Methodology analysis. It is a java swing application with these features:

* Principal Components Analysis
* Centroid Method
* Factor rotations manually or using standard algorithms (Varimax, Orthomax etc)
* Rotation graphs
* Animated intersubjective correlation graphs for visualisation of multi stage data
* Venn Diagram views for factor interpretation

<img src="docs/images/dq.png?raw"/>

Mathematics libraries
---------------------
T distribution routines from apache [commons-math](http://commons.apache.org/proper/commons-math/) are used in the calculation of factor scores.

[Jama](http://math.nist.gov/javanumerics/jama/) is used for eigenvalue decomposition in Principal Components Analysis.

Build instructions
--------------------
You need to have Maven 3 installed.

To build the jar with all dependencies included:

```bash
mvn clean install
```

This writes the jar file to the `target` directory and you can run the application like this (from that directory):

```
java -jar deliberate-q-2.0.8-jar-with-dependencies.jar
```

To compile the source, run the unit tests and then run the application from the command line using maven:

```
mvn test exec:java
```
