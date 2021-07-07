# Deliberate Q

<a href="https://travis-ci.com/DeliberativeAnalysis/DeliberateQ"><img src="https://travis-ci.com/DeliberativeAnalysis/DeliberateQ.svg"/></a><br/>
[![codecov](https://codecov.io/gh/DeliberativeAnalysis/DeliberateQ/branch/master/graph/badge.svg)](https://codecov.io/gh/DeliberativeAnalysis/DeliberateQ)

Deliberate Q is a visualisation tool for Q Methodology analysis. It is also being developed for use in deliberative reasoning analysis (deliberative reasoning index; DRI), which involves combining Q data with additional data in the form of ranked policy or action options, where the Q items embody the relevant concourse informing decisions regarding choices to be made about "what should be done". (see e.g. https://www.youtube.com/watch?v=o1O0u6E9W28)

But it can easily be used as a dedicated tool for Q analysis as well.


It is a java swing application with these features:

* Principal Components Analysis
* Centroid Method
* Factor rotations manually or using standard algorithms (Varimax, Orthomax etc)
* Rotation graphs
* Animated "intersubjective consistency" graphs for visualisation of multi stage data
* Venn Diagram views for factor interpretation

<img src="docs/images/dq.png?raw"/>

## Getting started

Download the latest release jar from [Releases](https://github.com/DeliberativeAnalysis/DeliberateQ/releases) and run it as below:

```bash
java -jar deliberate-q-2.1-jar-with-dependencies.jar
```

## Build instructions

You need to have Java (Oracle or OpenJDK) and [Maven 3+](https://maven.apache.org/) installed.

To build the jar with all dependencies included:

```bash
mvn clean install
```

This writes the jar file to the `target` directory and you can run the application like this (from that directory):

```
java -jar deliberate-q-2.0.8-jar-with-dependencies.jar
```

Or to compile the source, run the unit tests and then run the application from the command line using maven:

```
mvn test exec:java
```

## How to release a new version
For project administrators, to build a release (tagged in git source control) on Linux/Unix:

```bash
./release.sh VERSION_HERE
```
## Mathematics libraries

T distribution routines from apache [commons-math](http://commons.apache.org/proper/commons-math/) are used in the calculation of factor scores.

[Jama](http://math.nist.gov/javanumerics/jama/) is used for eigenvalue decomposition in Principal Components Analysis.

## View and run the project in GitPod

https://gitpod.io/#/github.com/DeliberativeAnalysis/DeliberateQ
