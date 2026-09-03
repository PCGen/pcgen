## New Tests:
#### 1.) Multi file commit-
This test creates a repo that has both a file edit and a file creation in the same commit, with different files for each one. 
This creates a commit that should have 2 rows of file changes, and the test verifies that commit 2(in this case) actually has 
two rows of file data. 

#### 2.) File rename commit-
This test creates a repo that has a file that gets renamed, and ensures that git actually picks it up with a `change_type`
of `R`. This is the rename type, and all we need to do is check that the file table has a row with an `R` in it.
## New Tests Round 2:
#### 1.) PR with missing sections-
This test creates and inserts a pr missing a merged on & closed on field. It then verifies that 
it actually was input into the table properly, and the internal values are actually `NULL` for each.

#### 2.) Job status update-
This test verifies the update functionality, that a repeat entry with 1 different field actually updates the different 
field in the database and doesn't create a whole new entry or not update at all. A job with a status set to `open`is input, 
it gets verified that it input properly. Then the same job but with the status set to `closed` this time instead. We verify
that it is the only job with that ID present(idempotency/duplication check) and that the status is actually closed(that 
it didn't just ignore the change)

## New... Features(?):
DI1 says it wants an updated readme about running the miner with the new features, so here goes- 

It really isn't that deep, you run `ensure_columns()` to upgrade the DB schema to support them, then `clean_issues_db() 
clean_prs_db() clean_commits_db()` to actually populate them. The regex work does the rest. 

With that said, regex is my "I did not care for the godfather" moment in software. I do not enjoy writing it, I always
forget how to write it no less than 5 minutes after closing it, and it's always a complete blight on my day whenever I
need to shut up and write it anyways. ~~Actually it's maybe second to web dev for me I 
really don't for that one either lol~~ I'm sure my code is good, and I very much actually tried on it, I just needed to 
rant for a minute. 