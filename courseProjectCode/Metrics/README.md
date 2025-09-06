# Metrics Deliverable

## Group Members
 - Shahmir Khan
 - JoJo Kaler
 - Tyler Jafaari

## Introduction

This deliverable focuses on getting metrics from the pcgen open-source repository. We will use python and python libraries to get 2 metrics associated with Maintainability.

1. Code Structure
    - Lines of Code per file/module
    - Comment density (comments / total lines)
2. Testability
    - Number of unit test cases/test suites
    - Test coverage

## Instructions For Use

Open up your terminal and navigate to `PCGEN/courseProjectCode/Metrics`

Type this command into your terminal: `python matrics.py`

A successful run should output the following:
```
-- Maintainability Metrics --
Total lines: 398017
Total lines of code: 339724
Total lines of comments: 111386
Comment density: 0.280

-- Testability --
Tests: 16741  Failures: 0  Errors: 0  Skipped: 20
Line coverage: 26.07%  (covered=137351, missed=389414)
```

*Note*:

You may need to do a clean gradle build before running metrics.py. To do this open your terminal and navigate to the root folder.

`cd PCGEN/`

After this, run the command below.

`./gradlew build`

Finally, follow the steps listed above to run metrics.py.


