# Project Proposal
## Team Members
    - Shahmir Khan
    - JoJo Kaler
    - Tyler Jaafari

## Project Overview

PCGen allows a user to create, manage, and optimize playable characters in board games such as Dungeons and Dragons. This project uses the Java JDK which allows it to run across multiple operating systems such as Windows, Mac, and Linux. PCGen also uses gradle to build, and the developers added deployables (installers) for ease of use for the end-user.

The GUI allows the end-user to enter/reroll stats such as strength, dexterity, intelligence, etc. These are vital stats for playable characters in games such as Dungeons and Dragons, and can become tedious to keep track of, and manage, with pen and paper. The GUI also outputs full statistics for the character, such as HP, race, gender, max load (weight), etc. This allows the end-user to quickly view a "rundown" of the character they have created. PCGen makes it easy, and reliable, to manage characters in an efficient way, solving the tediousness for players looking to play many board games.

## Deliverable Overview

The goal of this deliverable is to collect metrics relating to Maintainability, which includes Tesability.

Our proposed solution includes developing a python script to obtain, calculate, and output the metrics required.

Our deliverable includes:

1. Reproducible code that computes maintainability and testability metrics.
2. A short report including the project overview and key quality metrics.
3. A README.md file detailing how to run the metrics collection.

## Metrics

For Maintainability, we will be focusing on 2 key metrics.

1. Code Structure
    - Average lines of code (LOC) per file.
    - Comment density (number of comments / total lines).
2. Testability
    - Number of unit test cases/test suites.
    - Test coverage.

### Maintainability

1. **Lines of Code (LOC) per file (production code only)**  
   - *Definition:* Count of non-blank, non-comment lines in each Java source file. 
   - *Why it matters:* Very large files are harder to maintain and follow logic.
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

The program will display the calculated metrics directly in the terminal after execution.

Example output:

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
