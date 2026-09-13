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
For the initial assessment of the codebase in our first assignment and as a baseline for future work this semester, we plan to track both **maintainability**, **testability**, and **code structure** of the code.
In the project slides, code structure and testability are both listed under maintainability, but I have other pre-prepared metrics for both maintainability and testability, alongside the code structure metrics. 

For the sake of producing the highest quality of work possible rather than simply what's being asked, I will be splitting code structure, maintainability, and testability into 3 seperate pillars, and producing more metrics than what is asked for. 

Each pillar has 3 factors that go into judging them. These factors are indivudally calculated and scored to produce a value with a range of 0 to 1. 
Each factor gets averaged with the other factors relevant to the pillar to produce scores. 


### Maintainability
Maintainability is defined through three categories: file modification, refactor rate, and commit naming.

**File Modification**
```
     This check here is asking “how many times has this file been modified over the life of our repo?” The more its
     constantly changed, the more likely the file probably needs to be killed or broken down. We already have the data
     for this from the github repo, just need to run calculations. Only concern is some metadata file is pushed to the repo that
     constantly changes(but if we’re picking that up, its still a sign of a bad repo, no?)

     Scoring is done by seeing if the file is changed in at least a quarter of our total commits, which is absolutely a
     problematic amount.
```

**Refactor Rate**
```
    If you think your code is perfect every time, you’re either spending all day writing hello world or you’re full
    of yourself. We have addition/deletion line counts in commit_files, aggregating it should tell us something. As
    I’m writing this I have no clue what a good ratio here looks like. What we do have though is our known set of high
    quality repos. After analyzing them, I decided on 0.10-0.20 as a healthy refactoring rate. This obviously can vary
    but it's a good range, and heavy refactoring is scored lower but not as harsh as not refactoring at all.
```

**Commit Naming**
```
    One word commits aren’t usually very helpful- “fix” sucks.  “Fixed tests” is better. “Fixed failing unit tests for
    the user class” is ideal. Then we wrap around to writing an essay in the commit message field to be counterproductive.
    15ish or less words, keep it simple.

    I went with a quadratic style scoring to score this. Some messages can be "a little bit wrong, but not completely wrong"
    here. That's fine.
```

### Testability
Testability is defined through three categories: test file presence, ci presence, and ci passing rate.
Sometimes the code calls testability "correctness" instead, but it is the same thing here. 

**Test File Presence**
```
     Search the file paths/names for common naming schemes for test files used by devs. “test*” or “_test” in a file name
     is big, or even just a folder named “test”. Realistically we should have a reasonably close ratio of “for every
     source file, there should be a test file for it”

     Scoring is linear here. makes a little more sense. capped at 1:1 because splitting one src file into multiple tests
     doesnt give you a better score.
```

**CI Presence**
```
  All we need to know is if a CI is being ran on the repository or not. This is a 1 or a 0 check.
```

**CI Passing Rate**
```
    If CIs are present, they should be passing with a reasonable frequency.
    We already check status and can simply see the number with a status of “success” over not success.
    Otherwise, theres functionally no difference between having constantly failing CIs and having none at all.

    Exponential scoring because having an amount failing should be punished.
```

### Code Structure
Structure is defined through three categories: File length(loc), function length, and comment density

**File Length**
```
     Putting all your code in one file makes things obnoxious to use. Sometimes thats unavoidable with logic being
     complex and lengthy, so maybe some sort of relative scoring, where average file is fine, but as you get longer
     and longer it gets increasingly worse and worse up to like 1200 where the file is screaming in pain. According to
     a few google searches, 400-500 is the target max length of a healthy file. I say this as I'm on line 438 with
     nearly 35% of the scoring left to write, but just ignore that.
```

**Function Length**
```
    Functions should not be doing multiple things. Not a guaranteed way to squash it out but super long functions
    probably have unnecessary functionality. Probably 200+ as the threshold. Sameish scoring as file length.
```

**Comment Density**
```
    Comment density defines how much of a given file is comments. If you have a 100 line file, and 50 lines are comments, not good.
    The ideal range is defined here as over 10% and under 33%. Too many comments shouldn't be treated as harsh as barely having any.
    Wordiness sucks, but not detailing important pieces is criminal. 
```

### Further Metrics
In the near future we also plan to evaluate Modularity as documentation on the PCGen website seems to value the ability to introduce and effectively work with systems and content that aren't strictly speaking built in. Plus, as this project is open-source, the intent is that people use it, work with it, and build from it.
