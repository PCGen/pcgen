import math
import re
from src.da2_vocabulary import extract_vocabulary
from src.db_utils import exec_get_all, exec_get_one
from pygments.lexers import guess_lexer, ClassNotFound


def run_full_pipeline_all_data():
    """
    Same as the pipeline function below but return all relevant scores.
    :return: All the scores
    """

    name_length = name_length_check()
    case_mixing = case_mixing_check()
    poor_naming = poor_naming_check()
    commented_code = commented_out_code_check()
    readability = (name_length + case_mixing + poor_naming + commented_code) / 4

    file_length = file_length_check()
    function_length = function_length_check()
    ratio = ratio_check()
    structure = (file_length + function_length + ratio) / 3

    docstring = docstring_check()
    comment_length = comment_length_check()
    token_ratio = token_ratio_check()
    documentation = (docstring + comment_length + token_ratio) / 3

    test_file = test_file_check()
    ci_presence = ci_presence_check()
    ci_passing = ci_passing_check()
    correctness_test = (test_file + ci_presence + ci_passing) / 3

    file_modification = file_modification_check()
    commit_naming = commit_naming_check()
    refactor_rate = refactor_rate_check()
    maintainability = (file_modification + commit_naming + refactor_rate) / 3

    final_score = (
            readability * 0.338 +
            structure * 0.268 +
            documentation * 0.156 +
            correctness_test * 0.126 +
            maintainability * 0.111
    )

    return {
        "final_score": final_score,
        "readability_score": readability,
        "structure_score": structure,
        "documentation_score": documentation,
        "correctness_test_score": correctness_test,
        "maintainability_score": maintainability,
        # subscores
        "name_length": name_length,
        "case_mixing": case_mixing,
        "poor_naming": poor_naming,
        "commented_out_code": commented_code,
        "file_length": file_length,
        "function_length": function_length,
        "ratio": ratio,
        "docstring": docstring,
        "comment_length": comment_length,
        "token_ratio": token_ratio,
        "test_file": test_file,
        "ci_presence": ci_presence,
        "ci_passing": ci_passing,
        "file_modification": file_modification,
        "commit_naming": commit_naming,
        "refactor_rate": refactor_rate,
    }


def new_pipeline():
    file_length = file_length_check()
    function_length = function_length_check()
    density = comment_density_check()
    structure = (file_length + function_length + density) / 3

    test_file = test_file_check()
    ci_presence = ci_presence_check()
    ci_passing = ci_passing_check()
    correctness_test = (test_file + ci_presence + ci_passing) / 3

    file_modification = file_modification_check()
    commit_naming = commit_naming_check()
    refactor_rate = refactor_rate_check()
    maintainability = (file_modification + commit_naming + refactor_rate) / 3

    return {
        "structure_score": structure,
        "correctness_test_score": correctness_test,
        "maintainability_score": maintainability,
        # subscores
        "file_length": file_length,
        "function_length": function_length,
        "density": density,
        "test_file": test_file,
        "ci_presence": ci_presence,
        "ci_passing": ci_passing,
        "file_modification": file_modification,
        "commit_naming": commit_naming,
        "refactor_rate": refactor_rate,
    }


def run_full_pipeline_processing():
    """
    Main pipeline. What am I doing here?

    Mostly summing the 5 composite sum pillars and then doing some weighting magic to get a final score out.

    For weights, I have this fancy chart from a paper with percentages of our pillars here.
    The best way to go about it is compute values for our sub categories, sum them
    into the final pillars, then weight them based on the initial %s there. Make all %s add to 1, then just multiply the
     scores for each pillars by that to get an actual score.

     Weights from fig 3 in the paper are something like
     Readability- 82%
     Structure- 65%
     Documentation- 38%
     Correctness/Testability- 32% and 29% respectively, averaged to 30.5%
     Maintainability- 27%

     These sum to 242.5% which give us

     Readability- 0.338
     Structure- 0.268
     Documentation- 0.156
     Correctness/Testability- 0.126
     Maintainability- 0.111
    :return: Final score for the repository. Normalized from 0 to 1.
    """

    readability_score = readability_pipeline()
    structure_score = structure_pipeline()
    documentation_score = documentation_pipeline()
    correctness_test_score = correctness_testability_pipeline()
    maintainability_score = maintainability_pipeline()

    readability_score *= 0.338
    structure_score *= 0.268
    documentation_score *= 0.156
    correctness_test_score *= 0.126
    maintainability_score *= 0.111

    final_score = readability_score + structure_score + documentation_score + correctness_test_score + maintainability_score

    return final_score


def readability_pipeline():
    name_score = name_length_check()
    case_mix_score = case_mixing_check()
    poor_name_score = poor_naming_check()
    commented_code_score = commented_out_code_check()

    final_score = name_score + case_mix_score + poor_name_score + commented_code_score
    final_score /= 4
    return final_score


def name_length_check():
    """
    probably assume length should be 3 or more characters. Anything less is hard to derive meaning from.
    I’ll live with the occasional false positive here. Just need to watch out for loops with int i=0; i++, so I might
    penalize 2 character variable names and exempt 1 and 3. Caps at 30ish characters as stupidly long
    names are just as incomprehensible.

    Uses an exponential function for scoring so a few instances get penalized a bit, but the penalty scales up harsher,
    quicker. Uses a -3 as the scaling factor here. Tune it as you see fit if the scoring isn't as imagined.
    :return: A score for name length
    """
    code_id_data = exec_get_all("""SELECT name
                                   FROM code_identifiers""")

    if not code_id_data:
        return 1.0

    problem_count = 0

    for identifier in code_id_data:
        name_size = len(identifier[0])

        if name_size == 2 or name_size >= 30:
            problem_count += 1

    return math.exp(-3 * (problem_count / len(code_id_data)))  # adjust -3 if you want  to tweak penalty harshness


def case_mixing_check():
    """
    bouncing between camel and snake case can make things more confusing(im guilty of this in my python code too much).
    Check casing, but dont compare casing for a function and casing for a var. Some false positives may be present in
    constant names that are typically screaming snake, but I’ll live.

    Score is exponential again, but significantly harsher as there's a lot more data points tracked. If one thing is wrong,
    we don't want it buried under the weight of everything else.
    :return: A score for case mixing
    """
    code_id_data = exec_get_all("""SELECT file_path, name, kind
                                   FROM code_identifiers""")
    problem_count = 0
    file_info = {}
    for identifier in code_id_data:
        file_path = identifier[0]
        identifier_name = identifier[1]
        identifier_type = identifier[2]
        if file_path not in file_info:
            file_info[file_path] = {'function': [], 'variable': [], 'parameter': [], 'class': []}

        # lifted from another part of my code
        # snake case checker
        if re.fullmatch(r"^[a-z0-9]*(?:_[a-z0-9]+)*$", identifier_name):
            convention = "snake_case"

        # pascal case checker
        elif re.fullmatch(r"^(?:[A-Z][a-z0-9]*)*$", identifier_name):
            convention = "PascalCase"

        # camel case checker
        elif re.fullmatch(r"^[a-z0-9]*(?:[A-Z][a-z0-9]*)*$", identifier_name):
            convention = "camelCase"

        # screaming snake checker
        elif re.fullmatch(r"^[A-Z0-9]*(?:_[A-Z0-9]+)*$", identifier_name):
            convention = "SCREAMING_SNAKE"

        else:
            continue

        if convention not in file_info[file_path][identifier_type]:
            file_info[file_path][identifier_type].append(convention)

    list_sum = 0
    for file in file_info:
        for identifier_type in file_info[file]:
            list_sum += 1
            if len(file_info[file][identifier_type]) > 1:
                problem_count += 1

    if list_sum == 0:
        return 1.0

    # high severity here because individual files arent weighted a lot
    return math.exp(-8 * (problem_count / list_sum))


def poor_naming_check():
    """
    Horrible names with no meaning- “temp” or “var” or a pattern of
     “letter-number”- a1, a2, b1 are remarkably poor names. Words like “test”  or “data” should be individually caught
     as well, but only in isolated scenarios as things like “test_result” is relevant in a unit test scenario.

    Score is exponential again, but significantly harsher as names matter a good bit.
    :return: A score for case mixing
     """
    code_id_data = exec_get_all("""SELECT name
                                   FROM code_identifiers""")
    problem_count = 0

    if not code_id_data:
        return 1.0

    # handful of generically bad variable/function namings
    bad_names_list = ["test", "temp", "var", "data", "obj", "foo", "bar", "stuff", "thing", "val"]

    for identifier in code_id_data:
        name = identifier[0].lower()

        # compare to generically bad names or a pattern for "character-digit"(a1, b1, b2, etc) names
        if name in bad_names_list or re.fullmatch(r'^[a-z]\d+$', name):
            problem_count += 1

    return math.exp(-8 * (problem_count / len(code_id_data)))  # harsh since lots of names


def commented_out_code_check():
    """
    Commented out code blocks- why are they there? Should they be deleted? Etc etc. Not a great practice.
    Read comments for common code identifiers. Some false positives in explaining code things, but once again it’s the
    price I pay.
    :return: Commented out code score
    """
    code_comment_data = exec_get_all("""SELECT comment_text
                                   FROM code_comments""")

    problem_count = 0
    skip_count = 0

    if not code_comment_data:
        return 1.0

    for comment in code_comment_data:
        commentText = comment[0]

        # false positive catcher, super short strings cant be determined with any degree of accuracy
        if len(commentText.strip()) < 15:
            skip_count += 1
            continue

        try:
            # python library that can guess language from string snippets, basically a black box here but it works.
            lexer = guess_lexer(commentText)
            if lexer.name != 'Text only':
                problem_count += 1
        except ClassNotFound:
            # didn't find anything
            continue

    real_count = len(code_comment_data) - skip_count
    if real_count == 0:
        return 1.0

    # high severity here because this isn't an "oopsie" thing, we just shouldnt be doing this
    return math.exp(-8 * (problem_count / real_count))


def correctness_testability_pipeline():
    """
    presence of test files is a major one: Presence of test files, existence of ci pipelines, and confirmation.
    One note is that not every style of code will have tests. The lua things I wanted to test on, certainly do not.
    Compared to something like “readability”, this isnt as important but still is. As such I’ll probably weight this one lower.

    unlike the previous set of scores, this one is linear and ideal is going to have 1 test file for every source file
    :return: correctness/testability score
    """
    test_score = test_file_check()
    ci_score = ci_presence_check()
    pass_score = ci_passing_check()

    final_score = test_score + ci_score + pass_score
    final_score /= 3
    return final_score


def test_file_check():
    """
     search the file paths/names for common naming schemes for test files used by devs. “test*” or “_test” in a file name
     is big, or even just a folder named “test”. Realistically we should have a reasonably close ratio of “for every
     source file, there should be a test file for it”

     Scoring is linear here. makes a little more sense. capped at 1:1 because splitting one src file into multiple tests
     doesnt give you a better score.
    :return: test count score
    """
    # DISTINCT should get all unique file paths
    file_path_data = exec_get_all("""SELECT DISTINCT file_path
                                   FROM commit_files""")
    test_count = 0
    for file_path in file_path_data:
        # generic regex string to catch on a few variants of "test" without catching in "contest" or something like that
        if re.search(r'(^|/)tests?/|test_\w+\.|_test\.|Test\w+\.|\w+Tests?\.', file_path[0]):
            test_count += 1

    source_count = len(file_path_data) - test_count
    if source_count == 0:
        return 1.0
    print("test files: " + str(test_count) + " source files: " + str(source_count))
    # return ratio of test files : src files capped at 1:1(covers potential issues and more files would be weird)
    return min(test_count/(len(file_path_data)-test_count), 1.0)


def ci_presence_check():
    """
    All we need to know is if they're here or not. This is a 1 or a 0 check.
    :return: ci presence scoring
    """
    ci_data = exec_get_one("""SELECT COUNT(*)
                                     FROM ci_pipelines""")

    if ci_data[0] > 0:
        return 1
    else:
        return 0


def ci_passing_check():
    """
    if CIs are present, they should be passing with a reasonable frequency.
    We already check status and can simply see the number with a status of “success” over not success.
    Otherwise, theres functionally no difference between having constantly failing CIs and having none at all.

    return of exponential scoring because having an amount failing should be punished
    :return: ci passing check
    """
    ci_runs_data = exec_get_all("""SELECT status
                                   FROM ci_pipelines""")

    if not ci_runs_data:
        return 0.0

    bad_runs = 0
    failure_statuses = {"failure", "failed", "error", "cancelled"} # some generic ci failure msgs

    for ci_run in ci_runs_data:
        if ci_run[0].lower() in failure_statuses:
            bad_runs += 1

    return math.exp(-4 * (bad_runs / len(ci_runs_data)))


def maintainability_pipeline():
    """
    how “good” is our code? Is it falling apart at the seams and needs 78 patches before you can even consider
    shipping it? Or is it watertight from the moment it’s pushed? Source control quality is usually the biggest
    indicator of such, as that’s pretty much the point of using it. We’ll check three major things: how many commits
    does it take before a file is “done”, commit message quality, and refactoring.

    :return: maintainability score
    """
    file_score = file_modification_check()
    commit_name_score = commit_naming_check()
    refactor_score = refactor_rate_check()

    final_score = file_score + commit_name_score + refactor_score
    final_score /= 3
    return final_score


def file_modification_check():
    """
     This check here is asking “how many times has this file been modified over the life of our repo?” The more its
     constantly changed, the more likely the file probably needs to be killed or broken down. We already have the data
     for this in our db, just need to run calculations. Only concern is some metadata file is pushed to the repo that
     constantly changes(but if we’re picking that up, its still a sign of a bad repo, no?)

     Scoring is done by seeing if the file is changed in at least a quarter of our total commits, which is absolutely a
     problematic amount

    :return: file modification score
    """
    file_path_data = exec_get_all("""SELECT file_path
                                     FROM commit_files""")

    seen_paths = {}
    for file_path in file_path_data:
        if file_path[0] in seen_paths:
            seen_paths[file_path[0]] += 1
        else:
            seen_paths[file_path[0]] = 1

    problematic_modification_count = 0
    commit_count = exec_get_one("""SELECT COUNT(*) FROM commits""")[0]  # total number of commits
    for file in seen_paths:
        if (seen_paths[file] / commit_count) >= 0.25:  # File changed in at least a quarter or more of our commits
            problematic_modification_count += 1

    # linear, not that big of a deal overall
    return 1 - (problematic_modification_count / len(seen_paths))


def commit_naming_check():
    """
    This is the same gimmick done with individual names in actual code, but on a commit scale. One word commits
    aren’t usually very helpful- “fix” sucks.  “Fixed tests” is better. “Fixed failing unit tests for the user class”
    is ideal. Then we wrap around to writing an essay in the commit message field to be counterproductive. 15ish or less
    words, keep it simple.

    I went with a quadratic style scoring to score this. Some messages can be "a little bit wrong, but not completely wrong"
    here. That's fine. Specifics of the curve can be adjusted.

    :return: commit naming score
    """
    commit_msg_dat = exec_get_all("""SELECT message
                                   FROM commits""")

    if not commit_msg_dat:
        return 1.0

    problem_count = 0

    for msg in commit_msg_dat:
        name_size = len(msg[0].split())

        # quadratic style scoring, sweet spot for commit msg length, some punishments for too long or too short msgs
        # some messages can be a "partial problem" this way, but that's fine.
        best_score_length = 8
        curve_range = 7
        score = 1 - ((name_size - best_score_length) / curve_range) ** 2

        problem_count += max(0.0, score)

    # fine being linear, not a big deal
    return problem_count / len(commit_msg_dat)


def refactor_rate_check():
    """
    If you think your code is perfect every time, you’re either spending all day writing hello world or you’re full
    of yourself. We have addition/deletion line counts in commit_files, aggregating it should tell us something. As
    I’m writing this I have no clue what a good ratio here looks like. What we do have though is our known set of high
    quality repos. After analyzing them, I decided on 0.10-0.20 as a healthy refactoring rate. This obviously can vary
    but it's a good range, and heavy refactoring is scored lower but not as harsh as not refactoring at all.

    :return: refactoring rate score
    """
    file_refactor_data = exec_get_all("""SELECT additions, deletions
                                     FROM commit_files""")

    aSum = 0
    dSum = 0
    for line_change_data in file_refactor_data:
        additions, deletions = line_change_data
        aSum += additions
        dSum += deletions

    if aSum == 0:
        return 0.0

    ratio = dSum / aSum

    if ratio < 0.10:
        # punishing having a low refactor late, linearly done as not every project does but it is still an issue
        return ratio / 0.10

    if ratio <= 0.20:
        # healthy rate, good score
        return 1.0

    # heavy rate, punished a bit but not particularly harshly because refactoring is a normal part of development
    return math.exp(-2 * (ratio - 0.20))


def structure_pipeline():
    """
    another reasonably important one overall alongside readability. If I could track things like single responsiblity,
    that would be great, but I can’t, and I don’t want to design this horribly complex way to track it either. We’ll be
    mostly looking at sizes here to approximate that and make some slightly-educated-dart-throws at structure. 3 file
    size relevant things to check here: file length(avoid super long god files), function length(same issues), and
    function count/file length ratio to provide context to the previous 2.

    :return: structure score
    """
    file_score = file_length_check()
    function_score = function_length_check()
    ratio_score = ratio_check()

    final_score = file_score + function_score + ratio_score
    final_score /= 3
    return final_score


def file_length_check():
    """
     putting all your code in one file makes things obnoxious to use. Sometimes thats unavoidable with logic being
     complex and lengthy, so maybe some sort of relative scoring, where average file is fine, but as you get longer
     and longer it gets increasingly worse and worse up to like 1200 where the file is screaming in pain. According to
     a few google searches, 400-500 is the target max length of a healthy file. I say this as I'm on line 438 with
     nearly 35% of the scoring left to write, but just ignore that.

    :return: file length score
    """
    file_length_data = exec_get_all("""SELECT file_line_count
                                     FROM file_data""")

    length_score = 0

    if not file_length_data:
        return 1.0

    for file_length in file_length_data:
        length = file_length[0]

        if length <= 400:
            # healthy, full points
            length_score += 1
        elif length >= 1200:
            # file size of doom and despair, yes I just added a whole zero points.
            length_score += 0
        else:
            # edecay between 400 and 1200, get worse quickly
            ratio = (length - 400) / (1200 - 400)
            length_score += math.exp(-3 * ratio)

    # return mean of all file scores
    return length_score / len(file_length_data)


def function_length_check():
    """
    functions should not be doing multiple things, not a guaranteed way to squash it out but super long functions
    probably have unnecessary functionality. Probably 200+ as the threshold. Sameish scoring as file length.

    :return: function length score
    """
    file_length_function_data = exec_get_all("""SELECT function_count, file_line_count
                                       FROM file_data""")

    if not file_length_function_data:
        return 1.0

    size_score = 0.0
    tracked_files = 0
    for length_function in file_length_function_data:
        function, length = length_function

        if function == 0:
            continue

        count = length/function
        tracked_files += 1  # tracked manually versus len because files with no functions would be in the calcs

        if count < 150:  # functions over 150 are the bad spot
            size_score += 1
        else:
            # edecay between 150 and 300, get worse quickly
            ratio = (count - 150) / (300 - 150)
            size_score += math.exp(-3 * ratio)

    if tracked_files == 0:
        return 1.0

    return size_score / tracked_files


def ratio_check():
    """
    lots of functions in a long file is fine(ish), only a few functions in a long file is a problem,
     even if the functions themselves are ok-ish
    :return: function:length ratio score
    """
    file_length_function_data = exec_get_all("""SELECT function_count, file_line_count
                                       FROM file_data""")

    if not file_length_function_data:
        return 1.0

    size_score = 0.0
    tracked_files = 0
    for length_function in file_length_function_data:
        function, length = length_function

        # skipping short files by nature, not relevant to what we want to score
        if length < 100:
            continue

        if function == 0:
            continue

        ratio = function/length * 100
        tracked_files += 1  # tracked manually versus len because files with no functions would be in the calcs

        if ratio >= 2.0:  # approx 2 functions in 100 lines, pretty good rate
            size_score += 1
        else:
            # quadratic decay, low density bad
            size_score += (ratio / 2.0) ** 2

    if tracked_files == 0:
        return 1.0

    return size_score / tracked_files


def documentation_pipeline():
    """
    Documentation- this is one of the more obvious pillars. Docstrings are huge, they help significantly for clarifying
     function purpose and inputs/outputs. Once again, one-two word comments are poor quality, favoring about 8+ish word
     comments is probably the ideal target. We already cover commented out code in readability, and I consider that not
     exactly a documentation thing either, thats a “dev disabling something” despite using the comment framework.
     Additionally though, we can get funny with our code from DA1/2 and get some relevant data out of it about how much
     comments actually cover relevant concepts.
    :return: documentation score
    """
    docstring_score = docstring_check()
    comment_score = comment_length_check()
    token_score = token_ratio_check()

    final_score = docstring_score + comment_score + token_score
    final_score /= 3
    return final_score


def docstring_check():
    """
    Does a function have a docstring with it? Probably going to cheat to measure this and just search for the function
    name in the comments and go through the list. We’ll trim that further to try and cut out some false positives by
     expecting a (reasonably) longer comment of 10 additional words or more. Should cut out “goes to functionName”
     or other short reminder comments.
    :return: docstring presence score
    """
    functions = exec_get_all("""SELECT file_path, name FROM code_identifiers WHERE kind = 'function'""")

    # if we get nothing
    if not functions:
        return 1.0

    documented_count = 0
    for function in functions:
        file_path, fn_name = function

        comment_min_length = len(fn_name) + 10

        # find function name anywhere in the string. not the greatest
        match = exec_get_one("""SELECT COUNT(*) FROM code_comments WHERE file_path = %s AND LOWER(comment_text) LIKE 
                             %s AND LENGTH(comment_text) > %s""",
                             (file_path, f'%{fn_name.lower()}%' , comment_min_length))

        if match and match[0] > 0:
            # any response is good enough
            documented_count += 1

    return documented_count / len(functions)


def comment_length_check():
    """
    Easy enough to check, short comments are useless,
    longer is more useful. I believe docstrings are under this too so no penalty for being too wordy. Threshold at
    the moment is 8 words for a 'good' comment.
    """
    comments = exec_get_all("""SELECT comment_text FROM code_comments""")

    if not comments:
        return 1.0

    valid_comment_score = 0
    word_threshold = 8
    for comment in comments:
        word_count = len(comment[0].split())

        if word_count > word_threshold:
            score = 1
        elif word_count <= 1:
            score = 0
        else:
            score = (word_count - 1) / (word_threshold - 1)

    valid_comment_score += score

    return valid_comment_score / len(comments)


def token_ratio_check():
    """
    In da1/2(forget which) we compute a list of relevant identifier tokens for function names or other name sources.
    We can take this, and compare it to the actual comment text(after a bit of normalization). From here we can figure
    out how many words in the comment are just names of other functions/vars/params in the file, and how many are
    unique. The more that comments arent just reusing other things in the file, the more “useful” they are. This is
    a bit rough and can have some false positives/negatives, but I feel like it’s a particularly interesting piece
    of analysis.
    :return: token ratio score
    """

    files = exec_get_all("""SELECT file_path FROM code_identifiers""")

    if not files:
        return 1.0

    file_score = 0
    scored_files = 0

    for (file,) in files:

        tokens = exec_get_all("""SELECT tokens FROM code_identifiers WHERE file_path = %s""", (file,))

        if not tokens:
            return 1.0

        # split out of string based csv format into a set we can actually work on
        identifier_tokens = set()
        for (token_str,) in tokens:
            for t in token_str.split(','):
                identifier_tokens.add(t.strip())

        comments = exec_get_all("""SELECT comment_text FROM code_comments WHERE file_path = %s""", (file,))

        if not comments:
            return 1.0

        # da2 functionality to grab comment tokens
        comment_tokens = extract_vocabulary(
            [row[0] for row in comments],
            stem=False
        )

        if not comment_tokens:
            continue

        scored_files += 1

        # count words not shared in both comments or identifiers
        unique_words = sum(1 for t in comment_tokens if t not in identifier_tokens)

        ratio = unique_words / len(comment_tokens)
        file_score += ratio

    if scored_files == 0:
        return 1.0

    return file_score / scored_files


def comment_density_check():
    """
    Comment density defines how much of a given file is comments. If you have a 100 line file, and 50 lines are comments, not good.
    The ideal range is defined here as over 10% and under 33%. Too many comments shouldn't be treated as harsh as barely having any.
    Wordiness sucks, but not detailing important pieces is criminal.     :return:
    """
    file_length_path_data = exec_get_all("""SELECT file_path, file_line_count
                                       FROM file_data""")

    if not file_length_path_data:
        return 1.0

    density_score = 0
    scored_files = 0

    for length_path in file_length_path_data:
        path, length = length_path

        # skipping empty files
        if length == 0:
            continue

        comment_num = exec_get_one("""SELECT COUNT(*) FROM code_comments WHERE file_path = %s""",
                             (path,))[0]

        density = comment_num/length
        scored_files += 1

        if density < 0.10:  # sub 10% comment rate, not useful to actually figure out whats going on
            density_score += density / 0.10  # linear decrease
        elif density >= 0.33:
            # quadratic decay, high density bad
            density_score += math.exp(-4 * (density - 0.33))
        else:
            # nominal
            density_score += 1.0

    if scored_files == 0:
        return 1.0

    return density_score / scored_files

