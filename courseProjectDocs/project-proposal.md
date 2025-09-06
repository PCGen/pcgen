## Project Proposal
# Team Members
    - Shahmir Khan
    - JoJo Kaler
    - Tyler Jaafari

# Introduction

The goal of this deliverable is develop code to collect metrics relating to Maintainability and Testability.

Our proposed solution includes developing a python script to obtain, calculate, and output the metrics required.

# Metrics

For Maintainability we will be focusing on 2 key metrics.

1. Code Structure
    - Lines of code (LOC) per file
    - Comment density (comments / total lines)
2. Testability
    - Number of unit test cases / test suites
    - Test coverage

The code structure metrics are easy to parse, as we can use python to go through all files, ignoring test suites, and calculate how many lines of code are written per file and add them to a running sum. We will then append this value to out expected output.

The testability metrics will be slightly harder to obtain, as we need to individually parse the number of test cases from the testing suite. Since this project uses a gradle build, along with the JUnit library, we can use both of these to obtain the test coverage and how many tests were run. Gradle builds output a JaCoCo file that we can parse through, and find these metrics.

# Expected Output

The expected output of this program is a text file of the current metrics after calculation. 