import argparse
import csv
import math
import os
import random
import sys
import tempfile
import shutil
import json
from collections import Counter

from git import Repo

from src import git_miner, db_utils
from src.da1_identifiers import build_file_identifier_dataset
from src.db_utils import exec_sql_file
from src.git_ingest import ingest_issues, ingest_pull_requests, ingest_ci
from src.git_ingest_helpers import collect_github_issues, collect_github_pulls, collect_github_actions_runs
from src.proj_analysis import run_full_pipeline_processing, run_full_pipeline_all_data
from src.qual_clean_helpers import ensure_columns, clean_issues_db, clean_prs_db, clean_commits_db
from src import sampling_algorithms
from src.srcml_runner import run_srcml_on_repo_file
import xml.etree.ElementTree as ET
from src import da2_vocabulary
import os


def cmd_predict(args) -> None:
    """Train a commit-type classifier from DB data and write evaluation outputs.

    No network calls.  Reads commits, identifiers, and comments from the DB
    (populated by 'mine'), then runs the full M1 pipeline.

    Produces:
      <output_dir>/feature_importance.png   — which features matter most
      <output_dir>/confusion_matrix.png     — where the model gets confused
      <output_dir>/model_report.txt         — accuracy, per-class F1, interpretation

    Usage:
        python main.py predict
        python main.py predict --output-dir output/ --clusters 5
        python main.py predict --model-type random_forest --max-depth 5
    """
    from src import m1_modeling, da2_vocabulary

    out = args.output_dir
    os.makedirs(out, exist_ok=True)
    k = args.clusters

    # 1. Load commit records from DB
    print("Loading commit data from DB...")
    commit_records = m1_modeling.load_commit_data(
        commit_limit=getattr(args, "commit_limit", None)
    )
    print(f"  {len(commit_records)} commits loaded")

    if not commit_records:
        print("No commits found. Run 'mine' first.")
        return

    # 2. (Optional) Load identifier and comment tokens for overlap features
    try:
        id_rows = db_utils.exec_get_all("SELECT name FROM code_identifiers;")
        identifier_tokens = da2_vocabulary.extract_vocabulary(
            [r[0] for r in id_rows if r[0]]
        )
        cm_rows = db_utils.exec_get_all("SELECT comment_text FROM code_comments;")
        comment_tokens = da2_vocabulary.extract_vocabulary(
            [r[0] for r in cm_rows if r[0]]
        )
    except Exception as e:
        print(f"  Warning: could not load identifier/comment tokens: {e}")
        identifier_tokens = []
        comment_tokens = []

    # 3. Build feature matrix
    print(f"Building feature matrix (k={k})...")
    X, y, feature_names = m1_modeling.build_feature_matrix(
        commit_records,
        k=k,
        identifier_tokens=identifier_tokens,
        comment_tokens=comment_tokens,
    )
    print(f"  X shape: {X.shape}")
    print(f"  Label distribution: {dict(Counter(y))}")

    if len(set(y)) < 2:
        print("Only one label class found — model cannot be trained.")
        return

    # 4. Train/test split
    X_train, X_test, y_train, y_test = m1_modeling.split_dataset(
        X, y, test_size=0.2
    )

    # 5. Train
    model_type = getattr(args, "model_type", "decision_tree")
    print(f"Training {model_type}...")
    model = m1_modeling.train_classifier(X_train, y_train, model_type=model_type)

    # 6. Evaluate
    from sklearn.metrics import classification_report as sk_clf_report

    results = m1_modeling.evaluate_model(model, X_test, y_test)
    clf_report_text = sk_clf_report(
        y_test, results["y_pred"],
        labels=results["class_names"],
        zero_division=0,
    )
    print(f"  Accuracy: {results['accuracy']:.1%}")
    print()
    print("Classification report:")
    print(clf_report_text)

    # 7. Plots — wrapped in try/except so one failure doesn't block the report
    fi_path = os.path.join(out, "feature_importance.png")
    cm_path = os.path.join(out, "confusion_matrix.png")

    try:
        m1_modeling.plot_feature_importance(model, feature_names, output_path=fi_path)
        print(f"  -> {fi_path}")
    except Exception as exc:
        print(f"  Warning: could not save feature_importance.png: {exc}")

    try:
        m1_modeling.plot_confusion_matrix(
            y_test, results["y_pred"], results["class_names"], output_path=cm_path
        )
        print(f"  -> {cm_path}")
    except Exception as exc:
        print(f"  Warning: could not save confusion_matrix.png: {exc}")

    # 8. Write model report
    report_lines = [
        "M1 MODEL REPORT",
        "=" * 40,
        "",
        f"Model type:  {model_type}",
        f"Features:    {len(feature_names)}",
        f"Train size:  {len(X_train)}",
        f"Test size:   {len(X_test)}",
        f"Accuracy:    {results['accuracy']:.1%}",
        "",
        "Classification report (test set):",
        "-" * 36,
        clf_report_text,
        "Feature importances (descending):",
        "-" * 36,
    ]
    importances = model.feature_importances_
    ranked = sorted(zip(feature_names, importances), key=lambda x: -x[1])
    for name, imp in ranked:
        report_lines.append(f"  {name:<25} {imp:.4f}")
    report_lines += [
        "",
        "Interpretation:",
        "  [TODO: Write 2-3 sentences interpreting your results here]",
        "  Which clusters are most predictive? What does the confusion",
        "  matrix tell you about which commit types are hardest to classify?",
    ]

    report_path = os.path.join(out, "model_report.txt")
    with open(report_path, "w", encoding="utf-8") as f:
        f.write("\n".join(report_lines))
    for line in report_lines:
        print(line)
    print(f"  -> {report_path}")


def cmd_analyze(args) -> None:
    """Run DA2 vocabulary analysis from DB. No network calls.

    Produces:
      <output_dir>/commit_clusters.png      k-means scatter (commit vocab)
      <output_dir>/identifier_clusters.png  k-means scatter (identifier vocab)
      <output_dir>/comment_clusters.png     k-means scatter (comment vocab)
      <output_dir>/alignment_report.txt     cluster inspection + alignment metrics
    """

    out = args.output_dir
    os.makedirs(out, exist_ok=True)
    k = args.clusters

    # 1. Build vocabulary dataset from DB
    print("Building vocabulary dataset from DB...")
    dataset = da2_vocabulary.build_vocabulary_dataset(
        commit_limit=args.commit_limit,
        file_limit=args.file_limit,
    )

    commit_tokens = dataset["commit_tokens"]
    identifier_tokens = dataset["identifier_tokens"]
    comment_tokens = dataset["comment_tokens"]

    print(f"  commit tokens:     {len(commit_tokens)}")
    print(f"  identifier tokens: {len(identifier_tokens)}")
    print(f"  comment tokens:    {len(comment_tokens)}")

    # 2. k-means clustering + scatter plots
    sources = [
        ("commit", commit_tokens, "Commit Message Vocabulary"),
        ("identifier", identifier_tokens, "Code Identifier Vocabulary"),
        ("comment", comment_tokens, "Code Comment Vocabulary"),
    ]

    kmeans_labels = {}
    cluster_inspection = {}  # name -> {cluster_id: [top tokens]}
    for name, tokens, title in sources:
        if not tokens:
            print(f"  [{name}] no tokens – skipping k-means")
            kmeans_labels[name] = None
            cluster_inspection[name] = {}
            continue
        print(f"  Clustering {name} tokens (k={k})...")
        labels, vectors, _ = da2_vocabulary.cluster_vocabulary(tokens, k=k)
        kmeans_labels[name] = labels
        cluster_inspection[name] = da2_vocabulary.inspect_clusters(tokens, labels, top_n=10)
        coords = da2_vocabulary.reduce_dimensions(vectors, method="pca")
        da2_vocabulary.visualize_clusters(
            coords, labels, tokens,
            title=f"{title} – k-means (k={k})",
            output_path=os.path.join(out, f"{name}_clusters.png"),
        )
        print(f"    → {out}/{name}_clusters.png")

    # 3. Alignment metrics + report
    alignment = dataset.get("alignment", {})

    report_lines = ["VOCABULARY ALIGNMENT REPORT", "=" * 40, ""]

    source_titles = {
        "commit": "Commit Message Vocabulary",
        "identifier": "Code Identifier Vocabulary",
        "comment": "Code Comment Vocabulary",
    }
    for name, title in source_titles.items():
        clusters = cluster_inspection.get(name, {})
        if not clusters:
            continue
        report_lines += [f"{title} Clusters", "-" * 36]
        for cluster_id, top_tokens in sorted(clusters.items()):
            report_lines.append(f"  Cluster {cluster_id}: {', '.join(top_tokens)}")
        report_lines.append("")

    report_lines += ["Alignment Metrics", "=" * 40, ""]

    pair_names = {
        "commits_identifiers": ("commit", "identifier"),
        "commits_comments": ("commit", "comment"),
        "identifiers_comments": ("identifier", "comment"),
    }
    for key, (a, b) in pair_names.items():
        m = alignment.get(key)
        if not m:
            report_lines.append(f"{a} ↔ {b}: no data")
            continue
        report_lines += [
            f"{a} ↔ {b}",
            f"  Vocabulary overlap (Jaccard): {m['vocab_overlap']:.1%}",
            f"  Shared vocabulary size:       {m['shared_vocab_size']}",
            f"  Cluster similarity (ARI):     {m['cluster_similarity']:.3f}",
            "",
        ]

    report_path = os.path.join(out, "alignment_report.txt")
    with open(report_path, "w", encoding="utf-8") as f:
        f.write("\n".join(report_lines))
    print(f"  → {report_path}")

    for line in report_lines:
        print(line)


def _mine_code_artifacts(repo_path: str, file_limit: int = None) -> None:
    """Run srcML on the entire cloned repo directory, then store identifiers
    and comments to the DB.

    Must be called *before* the temp directory is cleaned up.  Clears existing
    rows first so re-running mine is idempotent.

    Silently exits if srcML is not installed rather than failing the whole
    mine stage.
    """
    from src import db_utils, srcml_runner
    from src import da1_identifiers
    from src import da2_vocabulary

    # Single srcML call on the whole directory
    print("  Running srcML on repository directory...")
    try:
        dir_xml = srcml_runner.run_srcml_on_directory(repo_path)
    except RuntimeError as exc:
        print(f"  Warning: srcML unavailable - skipping code artifact mining ({exc})",
              file=sys.stderr)
        return
    except Exception as exc:
        print(f"  Warning: srcML failed - {exc}", file=sys.stderr)
        return

    # Parse the multi-unit document; each child <unit> is one source file
    try:
        root = ET.fromstring(dir_xml.encode("utf-8"))
    except ET.ParseError as exc:
        print(f"  Warning: could not parse srcML output - {exc}", file=sys.stderr)
        return

    units = [c for c in root if c.tag.split('}')[-1] == 'unit']
    if file_limit:
        units = units[:file_limit]

    print(f"  Processing {len(units)} source files...")

    id_rows = []
    cm_rows = []
    # I left my laptop on overnight expecting the data to be done when I woke up, but found it stuck at 169/285 while
    # processing "54698 identifiers, 34530 comments from 14 files". I am quite annoyed as I just wasted a ton of time
    # before realizing I needed this check.
    IDENTIFIER_THRESHOLD = 5000
    COMMENT_THRESHOLD = 3000

    for unit in units:
        rel_path = unit.get('filename', '')
        unit_xml = ET.tostring(unit, encoding='unicode')

        function_count = 0

        # DA1 - identifiers
        try:
            for row in da1_identifiers.extract_identifiers_dom(unit_xml):
                # tokens has a stupid bit here because there's one case where it's not always a list
                id_rows.append({"fp": rel_path, "name": row["name"], "kind": row["kind"],
                                "tokens": ",".join(row["tokens"]).lower() if isinstance(row["tokens"], list) else row[
                                    "tokens"].lower()})

                if row['kind'] == 'function':
                    function_count += 1
        except Exception:
            pass

        if len(id_rows) > IDENTIFIER_THRESHOLD:
            print(
                f"Too many identifiers({len(id_rows)}, presunably attempting to process"
                f"an autogenerated file, killing analysis")
            raise ValueError("Too many identifiers to process")

        # DA2 - comments
        try:
            for text in da2_vocabulary.extract_comments_from_srcml(unit_xml):
                if text.strip():
                    cm_rows.append({"fp": rel_path, "ct": text})
        except Exception:
            pass

        if len(cm_rows) > COMMENT_THRESHOLD:
            print(
                f"Too many comments({len(cm_rows)}, presunably attempting to process"
                f"an autogenerated file, killing analysis")
            raise ValueError("Too many comments to process")

        line_count = unit_xml.count("\n")  # number of lines for file, relevant to some project pieces

        db_utils.exec_commit("""INSERT INTO file_data (file_path, file_line_count, function_count)
                       VALUES (%(file_path)s, %(line_count)s, %(function_count)s) ON CONFLICT (file_path) DO
        UPDATE SET
            file_line_count = EXCLUDED.file_line_count,
            function_count = EXCLUDED.function_count""",
                    {"file_path": rel_path, "line_count": line_count, "function_count": function_count})

    # Batch insert
    db_utils.exec_many(
        "INSERT INTO code_identifiers (file_path, name, kind, tokens) VALUES (%(fp)s, %(name)s, %(kind)s, %(tokens)s);",
        id_rows,
    )
    db_utils.exec_many(
        "INSERT INTO code_comments (file_path, comment_text) VALUES (%(fp)s, %(ct)s);",
        cm_rows,
    )

    print(f"    -> {len(id_rows)} identifiers, {len(cm_rows)} comments from {len(units)} files")


def mineCode(args):
    token = args.token or os.environ.get("GITHUB_TOKEN")
    owner_repo = args.owner_repo

    tmp_repo_dir = None
    repo_path = owner_repo

    try:
        # If given an owner/repo string and it's not a local path, clone it
        if "/" in owner_repo and not os.path.isdir(owner_repo):
            tmp_repo_dir = tempfile.mkdtemp(prefix="gitminer_")
            if token:
                clone_url = f"https://{token}@github.com/{owner_repo}.git"
            else:
                clone_url = f"https://github.com/{owner_repo}.git"
            print(f"Cloning {owner_repo} into {tmp_repo_dir}...")
            Repo.clone_from(clone_url, tmp_repo_dir)
            repo_path = tmp_repo_dir

        exec_sql_file("data/schema.sql")

        print(f"Mining repository at {repo_path}...")
        try:
            n = git_miner.mine_history(repo_path, max_commits=args.max_commits)
            head = git_miner.extract_head_commit(repo_path)
            print(f"Mined {n} commits; HEAD {head.get('hash')} by {head.get('author_name')} at {head.get('timestamp')}")
        except ValueError as e:
            raise
        except Exception as e:  # shallow clones can raise here; don't let it
            print(f"Warning: {e}")  # block the step below

        if tmp_repo_dir:  # dont blow up please
            _mine_code_artifacts(tmp_repo_dir, args.file_limit)

        provider = "github"
        if args.ingest:
            ingest_issues(provider, owner_repo, collect_github_issues(owner_repo))
            ingest_pull_requests(provider, owner_repo, collect_github_pulls(owner_repo))
            ingest_ci(provider, owner_repo, collect_github_actions_runs(owner_repo))

        ensure_columns()
        clean_issues_db()
        clean_prs_db()
        clean_commits_db()

        #  grab commit data, figure out how many commits we want, then pick that number of them(default args are a ton of commits but whatever)
        #commitData = db_utils.exec_get_all("SELECT * FROM commits")
        #popSize = sampling_algorithms.sample_size_proportion(len(commitData))
        #sampleData = sampling_algorithms.sample_systematic(commitData, len(commitData) // popSize, 222222)
        #for commit in sampleData:
        #    print(commit)

        # xmlData = run_srcml_on_repo_file(repo_path, "main/loadCharacter.cpp")
        # aggdData = build_file_identifier_dataset({"loadCharacter.cpp": xmlData})

        # with open("output/output.json", "w") as json_file:
        # json.dump(aggdData, json_file, indent=4)

        # I'm pretty sure the above bits aren't needed anymore because _mine_code_artifacts does it for me.

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        raise Exception("Git clone failed")

    finally:
        if tmp_repo_dir:
            try:
                shutil.rmtree(tmp_repo_dir)
            except Exception:
                pass


# file headers for our output csv so we can actually analyze the data
FIELDNAMES = [
    "repo",
    "final_score",
    "readability_score", "structure_score", "documentation_score",
    "correctness_test_score", "maintainability_score",
    "name_length", "case_mixing", "poor_naming", "commented_out_code",
    "file_length", "function_length", "ratio",
    "docstring", "comment_length", "token_ratio",
    "test_file", "ci_presence", "ci_passing",
    "file_modification", "commit_naming", "refactor_rate",
    "flag_col1", "flag_col2", "flag_col3", "flag_col4",
]

SUPPORTED_LANGUAGES = {"C", "C++", "Java",
                       "C#"}  # windows version of srcml cant do python, this is all we can support rn

def load_processed_repos(path):
    """
    Quick and dirty script that can open the csvs and will let us know which ones we've already processed
    :param path: path to csv
    :return: list of processed repo names
    """
    repos = set()
    if os.path.exists(path):
        with open(path, newline="", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            if reader.fieldnames and "repo" in reader.fieldnames:
                for row in reader:
                    repos.add(row["repo"].strip())
    return repos


def load_failed_repos(path):
    """
    Quick and dirty script that can open the failed repo csv and will let us know which ones we've already processed
    + what type they are
    :param path: path to csv
    :return: dict of failed repo names and types
    """
    repos = dict()
    if os.path.exists(path):
        with open(path, newline="", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            if reader.fieldnames and "repo" in reader.fieldnames and "type" in reader.fieldnames:
                for row in reader:
                    repo = row["repo"].strip()
                    type = row["type"].strip().lower()

                    repos[repo] = type

    return repos


def project_pipeline(args):
    os.makedirs(args.output_dir, exist_ok=True)
    good_path = os.path.join(args.output_dir, "good_repo_results.csv")
    bad_path = os.path.join(args.output_dir, "bad_repo_results.csv")
    failed_path = os.path.join(args.output_dir, "failed_repo_results.csv")

    rows = []
    with open(args.csv_path, newline="", encoding="utf-8") as reaper_file:
        reader = csv.reader(reaper_file)
        for line in reader:
            if not line:
                continue

            # first column is repo name that we need
            repo = line[0].strip()
            if not repo or "/" not in repo:
                continue

            # we're limited in which languages we support, trim to those only
            if line[1].strip() not in SUPPORTED_LANGUAGES:
                continue

            # in the repo reaper output csv, the last 4 cols are the relevant scores to tell if a repo is good
            last4 = line[-4:]
            flags = []
            for v in last4:
                try:
                    flags.append(int(float(v.strip())))
                except ValueError:
                    flags.append(0)
            rows.append((repo, flags))

    failed_repos = load_failed_repos(failed_path)
    processed_repos = set()
    processed_repos |= load_processed_repos(good_path)
    processed_repos |= load_processed_repos(bad_path)

    # stratified sampling impl because i am not parsing 1048575 repos. github and my computer would kill me.
    good_rows = [(r, f) for r, f in rows if sum(f) >= 3]
    bad_rows = [(r, f) for r, f in rows if sum(f) < 3]

    RAND_VALUE = 42
    rng = random.Random(RAND_VALUE)

    # we want to test an equal number of good samples, and bad samples
    SAMPLE_SIZE = 385  # 385 for statistical significance(95% +/- 5%)
    good_n = round(SAMPLE_SIZE/2)
    bad_n = SAMPLE_SIZE - good_n

    if len(processed_repos) > 0:
        print(f"data already exists, resuming where we left off({len(processed_repos)}/{SAMPLE_SIZE}) repos)")

    # actually grab our counts
    real_good = rng.sample(good_rows, min(good_n, len(good_rows)))
    real_bad = rng.sample(bad_rows, min(bad_n, len(bad_rows)))

    # pool of new values to pull from for errors
    good_reroll_pool = [item for item in good_rows if item not in real_good]
    bad_reroll_pool = [item for item in bad_rows if item not in real_bad]

    # do the randomization here so it's deterministic
    rng.shuffle(good_reroll_pool)
    rng.shuffle(bad_reroll_pool)

    rows = real_good + real_bad

    print(f"parsing {len(rows)} repos")

    good_exists = os.path.exists(good_path)
    bad_exists = os.path.exists(bad_path)
    failed_exists = os.path.exists(failed_path)
    with (
        open(good_path, "a", newline="", encoding="utf-8") as good_csv,
        open(bad_path, "a", newline="", encoding="utf-8") as bad_csv,
        open(failed_path, "a", newline="", encoding="utf-8") as failed_csv,
    ):
        good_writer = csv.DictWriter(good_csv, fieldnames=FIELDNAMES)
        bad_writer = csv.DictWriter(bad_csv, fieldnames=FIELDNAMES)
        failed_writer = csv.DictWriter(failed_csv, fieldnames=["repo", "type"])

        if not good_exists:
            good_writer.writeheader()

        if not bad_exists:
            bad_writer.writeheader()

        if not failed_exists:
            failed_writer.writeheader()

        # loop structure slightly different as we need to hot append
        idx = 0
        while rows:
            repo, flags = rows.pop(0)
            idx += 1

            print(f"analyzing repo {idx}/{SAMPLE_SIZE}: {repo}")
            if repo in processed_repos:
                print(f"repo {repo} already processed, skipping")
                continue
            try:

                if repo in failed_repos:
                    raise Exception(f"repo {repo} already failed processing, skipping")

                fake_args = argparse.Namespace(
                    token=args.token,
                    owner_repo=repo,
                    max_commits=args.max_commits,
                    file_limit=args.file_limit,
                    ingest=True,
                )
                mineCode(fake_args)

                id_count = db_utils.exec_get_one("SELECT COUNT(*) FROM code_identifiers")[0]
                cm_count = db_utils.exec_get_one("SELECT COUNT(*) FROM code_comments")[0]
                if id_count == 0 and cm_count == 0:
                    print(f"srcml presumably failed, intentionally throwing error")
                    raise Exception("srcml presumably failed")

                scores = run_full_pipeline_all_data()
            except Exception as e:
                # if it's fried still toss it into the db so we know what didnt work right
                print(f"error in {repo} caught, skipping further analysis")

                rerun_type = "good" if sum(flags) >= 3 else "bad"

                if repo not in failed_repos:
                    row = {"repo": repo, "type": rerun_type}
                    failed_writer.writerow(row)
                    failed_csv.flush()
                    failed_repos[repo] = rerun_type

                if rerun_type == "good":
                    new_repo = good_reroll_pool.pop(0)
                else:
                    new_repo = bad_reroll_pool.pop(0)

                if not good_reroll_pool and not bad_reroll_pool:
                    # if we get through 30k repos id be surprised.
                    print("no valid repos left to sample from")
                else:
                    rows.append(new_repo)
                    print(f"appending {new_repo[0]} to repo todo list as a {rerun_type} repo")

                continue

            row = {"repo": repo, **scores,
                   "flag_col1": flags[0], "flag_col2": flags[1],
                   "flag_col3": flags[2], "flag_col4": flags[3]}

            # fix floating point math being obnoxious
            for k, v in row.items():
                if isinstance(v, float) and not math.isnan(v):
                    row[k] = round(v, 6)

            if sum(flags) >= 3:
                good_writer.writerow(row)
                good_csv.flush()
            else:
                bad_writer.writerow(row)
                bad_csv.flush()

            processed_repos.add(repo)

            print(f"repo {repo} mined and written")


def main(argv=None):
    argv = argv or sys.argv[1:]

    p = argparse.ArgumentParser(description="Clone a repository (or use local path) and run git_miner")
    subparser = p.add_subparsers(dest="choice", help="Mine or Analyze")

    mineparser = subparser.add_parser("mine", help="Mine Mode")
    analyzeparser = subparser.add_parser("analyze", help="Analyze Mode")
    pp = subparser.add_parser("predict", help="Train commit-type classifier and write evaluation outputs (M1)")
    proj = subparser.add_parser("proj-run", help="Run research project functions on processed data")

    mineparser.add_argument("owner_repo", help="owner/repo (e.g. octocat/Hello-World) or local repo path")
    mineparser.add_argument("--token", help="GitHub token (or set GITHUB_TOKEN) for private repo cloning")
    mineparser.add_argument("--max-commits", type=int, default=None, help="Stop after this many commits (optional)")
    mineparser.add_argument("--file-limit", type=int, default=100, help="How many files to look at in the repo")
    mineparser.add_argument("--no-record-run", action="store_true", help="Do not write a run_log entry")
    mineparser.add_argument("--ingest", action="store_true", help="Run ingest functions")

    analyzeparser.add_argument("--output-dir", help="Output directory to write results to")
    analyzeparser.add_argument("--clusters", type=int, help="Number of clusters to run")
    analyzeparser.add_argument("--commit-limit", type=int, default=100, help="Commit limit to analyze")
    analyzeparser.add_argument("--file-limit", type=int, default=100, help="How many files to look at in the repo")

    pp.add_argument("--output-dir", default="output")
    pp.add_argument("--clusters", "-k", type=int, default=5)
    pp.add_argument("--model-type", default="decision_tree",
                    choices=["decision_tree", "random_forest"])
    pp.add_argument("--max-depth", type=int, default=None)
    pp.add_argument("--commit-limit", type=int, default=None)
    pp.set_defaults(func=cmd_predict)

    proj.add_argument("csv_path", help="Path to the input CSV")
    proj.add_argument("--output-dir", default="output",
                      help="Directory to write output csvs to")
    proj.add_argument("--token", default=None,
                      help="GitHub token)")
    proj.add_argument("--max-commits", type=int, default=None,
                      help="How many commits to mine")
    proj.add_argument("--file-limit", type=int, default=100,
                      help="How many source files to parse")

    args = p.parse_args(argv)

    if args.choice == "mine":
        mineCode(args)
    elif args.choice == "analyze":
        cmd_analyze(args)
    elif args.choice == "predict":
        cmd_predict(args)
    elif args.choice == "proj-run":
        project_pipeline(args)


if __name__ == "__main__":
    main()
