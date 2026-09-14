How to reproduce. 

You'll need a copy of srcml set up on your system. Might be windows only, couldn't tell you(works on my machine)

Python 3.11 was used for this, bunch of libraries needed(just install everything based on the imports)

You'll need a postgres database set up, see db.yml for exactly how it needs to be set up, then make sure that the schema
(located in data/schema.sql) is applied. 

From here, run `C:/Users/false/Desktop/RIT-Things/swen-project-trail-planner/code-analysis-pcgen/courseProjectCode/Metrics/Code-quality-analysis-tool/swen-640/main.py`
with the following flags:
`target-run --repo pcgen/pcgen --max-commits 200 --file-limit 1000 --token <your github pat here>`
youll need a github pat put in, make sure it can read all aspects of a github repo and clone/download from it. 

If you struggle to get it to work, let me know. For refrence, here is the course site for swen 640, which is what the 
code analysis tool used for this project is based off of: 
https://www.se.rit.edu/~swen-640/

all of the instructions for setting up the db and srcml can be found here.
DC0 details the postgres setup and DA1 handles srcml. 