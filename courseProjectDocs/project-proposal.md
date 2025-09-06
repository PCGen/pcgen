# Project Proposal
## Team Members
    - Shahmir Khan
    - JoJo Kaler
    - Tyler Jaafari

## Project Overview

The goal of this deliverable is develop code to collect metrics relating to Maintainability and Testability.

Our proposed solution includes developing a python script to obtain, calculate, and output the metrics required.

Our deliverable includes:

1. Reproducible code that computes maintainability and testability metrics.
2. A short report discussing results, limitations, and recommendations.

## Metrics

For Maintainability we will be focusing on 2 key metrics.

1. Code Structure
    - Lines of code (LOC) per file.
    - Comment density (comments / total lines).
2. Testability
    - Number of unit test cases / test suites.
    - Test coverage.

### Maintainability

1. **Lines of Code (LOC) per file (production code only)**  
   - *Definition:* Count of non-blank, non-comment lines in each Java source file. 
   - *Why it matters:* Very large files are harder to maintain, and follow logic.
   - *Computation:* Ignore `//` line comments and `/* ... */` block comments. Ignore blank lines.

2. **Comment Density**  
   - *Definition:* `commented lines / total lines` for each Java file.  
   - *Why it matters:* Too few comments can hurt understandability; too many can indicate code smell or duplication of obvious logic.  
   - *Computation:* Count lines considered comments (single-line or block) divided by total lines (including comments).

### Testability
1. **Unit Test Count**  
   - *Definition:* Number of test cases discovered and executed by Gradle/JUnit (JUnit is the testing suite that this open source project uses).  
   - *Why it matters:* More, and smaller, tests tend to improve defect localization and refactor safety.  
   - *Computation:* Parse Gradle’s XML test results (per-suite).

2. **Line Coverage (JaCoCo)**  
   - *Definition:* Percentage of production lines covered by tests.  
   - *Why it matters:* Higher test coverage generally means that the logic is safer for production.  
   - *Computation:* Run `gradlew test jacocoTestReport` and parse the JaCoCo XML.

## Tools & Environment
- **Languages/Build:** Java, Gradle, JUnit, JaCoCo  
- **Scripting:** Python 3+  
- **OS:** macOS/Windows/Linux

## Deliverables & Workflow
- Code in `courseProjectCode/Metrics/` with a README and example output.
- “Project Proposal” PR into the team’s main branch.
- Later deliverable: short report explaining findings and improvement suggestions.

# Expected Output

The expected output of this program is a text or XML file of the current metrics after calculation. 