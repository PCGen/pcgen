# Project Proposal: PCGen Team 5

## Members
- Ethan Patterson
- Collin Cleary
- Coray Bennett
## overview

We've selected PCGen as our codebase (https://github.com/pcgen/pcgen)

PCGen is a desktop app made for TTRPG (Table Top Roll Playing Game) players to create and track the details of their player characters (or PCs). It's written primarily in Java, open-source, and it's existed for well over 20 years of active development. Its main testing framework is Junit. It's built in Gradle. PCGen's developers have designed the application to be compatible with Pathfinder and Dungeons and Dragons, but there are built-in tools to make "homebrew" systems work. The application also has a storied history of licensing disagreements with Wizards of the Coast (the creators and owners of Dungeons and Dragons)

We mainly selected PCGen as our target for testing because it's mostly written in java, a coding language with which all our group members are familiar, and one which we have all have experience analyzing programmatically. Plus, some of our members are TTRPG fans.

Our project will generally consist of a thorough analysis of the PCGen application guided by the assignments of this course which will give us a comprehensive understanding of it and practical experience with the skills needed to perform analysis of unfamiliar codebases and their quality attributes.

## Key Quality Metrics
For the initial assessment of the codebase in our first assignment and as a baseline for future work this semester, we plan to track the **maintainability** of the code:

**Code Structure**
- Lines of Code (LOC) per file/module
- Comment Density 

**Testability**
- Number of unit test cases
- Test coverage

In the near future we also plan to evaluate Modularity as documentation on the PCGen website seems to value the ability to introduce and effectively work with systems and content that aren't strictly speaking built in. Plus, as this project is open-source, the intent is that people use it, work with it, and build from it.

Another key quality metric that we are considering outside the umbrella of Maintainability is Interaction capability
