import argparse
import csv
import math
import os
import random
import shutil
import sys
import tempfile

from git import Repo

from main import SUPPORTED_LANGUAGES, load_failed_repos, load_processed_repos, FIELDNAMES, _mine_code_artifacts
from src import git_miner, db_utils
from src.db_utils import exec_sql_file
from src.git_ingest import ingest_ci
from src.git_ingest_helpers import collect_github_actions_runs
from src.proj_analysis import ci_passing_check, ci_presence_check, commented_out_code_check


def recalc_scores(row):
    test_file = float(row["test_file"])
    ci_presence = float(row["ci_presence"])
    ci_passing = float(row["ci_passing"])
    correctness = (test_file + ci_presence + ci_passing) / 3

    name_length = float(row["name_length"])
    case_mixing = float(row["case_mixing"])
    poor_naming = float(row["poor_naming"])
    commented_code = float(row["commented_out_code"])
    readability = (name_length + case_mixing + poor_naming + commented_code) / 4

    final = (
            readability * 0.338 +
            float(row["structure_score"]) * 0.268 +
            float(row["documentation_score"]) * 0.156 +
            correctness * 0.126 +
            float(row["maintainability_score"]) * 0.111
    )

    row["readability_score"] = readability
    row["correctness_test_score"] = correctness
    row["final_score"] = final
    return row


def project_pipeline(args):
    os.makedirs(args.output_dir, exist_ok=True)
    good_path = os.path.join(args.output_dir, "good_ci_patch_results.csv")
    bad_path = os.path.join(args.output_dir, "bad_ci_patch_results.csv")
    failed_path = os.path.join(args.output_dir, "failed_repo_results.csv")
    original_good_path = "output/good_repo_results.csv"
    original_bad_path = "output/bad_repo_results.csv"


    rows = []
    with open(original_good_path, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            repo = row.get("repo", "").strip()
            if repo:
                rows.append([repo, [1, 1, 1, 1]])

    with open(original_bad_path, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            repo = row.get("repo", "").strip()
            if repo:
                rows.append([repo, [0, 0, 0, 0]])

    print(f"parsing {len(rows)} repos")

    good_exists = os.path.exists(good_path)
    bad_exists = os.path.exists(bad_path)
    with (
        open(good_path, "a", newline="", encoding="utf-8") as good_csv,
        open(bad_path, "a", newline="", encoding="utf-8") as bad_csv,
    ):
        good_writer = csv.DictWriter(good_csv, fieldnames=["repo", "ci_presence", "ci_passing", "commented_out_code"])
        bad_writer = csv.DictWriter(bad_csv, fieldnames=["repo", "ci_presence", "ci_passing", "commented_out_code"])

        if not good_exists:
            good_writer.writeheader()

        if not bad_exists:
            bad_writer.writeheader()

        SAMPLE_SIZE = len(rows)
        # loop structure slightly different as we need to hot append
        idx = 0
        while rows:
            repo, flags = rows.pop(0)
            idx += 1

            print(f"analyzing repo {idx}/{SAMPLE_SIZE}: {repo}")
            tmp_repo_dir = None

            try:

                max_commits = args.max_commits
                file_limit = args.file_limit
                token = args.token
                owner_repo = repo
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

                if tmp_repo_dir:  # dont blow up please
                    _mine_code_artifacts(tmp_repo_dir, args.file_limit)

                provider = "github"

                ingest_ci(provider, repo, collect_github_actions_runs(repo, token = args.token))

                ci_presence = ci_presence_check()
                ci_passing = ci_passing_check()
                commented_code = commented_out_code_check()
                print(f"real presence score is {ci_presence}, real passing score is {str(ci_passing)}")

            except Exception as e:
                # if it's fried still toss it into the db so we know what didnt work right
                print(f"error in {repo} caught, skipping further analysis")

                continue

            finally:
                if tmp_repo_dir:
                    try:
                        shutil.rmtree(tmp_repo_dir)
                    except Exception:
                        pass

            row = {"repo": repo, "ci_presence": ci_presence, "ci_passing": ci_passing, "commented_out_code": commented_code}

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
            print(f"repo {repo} mined and written")

    good_patch = {}
    with open(good_path, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            repo = row["repo"].strip()
            good_patch[repo] = {
                "ci_presence": row["ci_presence"],
                "ci_passing":  row["ci_passing"],
                "commented_out_code": row["commented_out_code"],
            }

    with open(original_good_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        fieldnames = reader.fieldnames
        good_rows = list(reader)

    patched_rows_good = []
    for row in good_rows:
        repo = row["repo"].strip()
        if repo in good_patch:
            row["ci_presence"] = good_patch[repo]["ci_presence"]
            row["ci_passing"] = good_patch[repo]["ci_passing"]
            row["commented_out_code"] = good_patch[repo]["commented_out_code"]
            row = recalc_scores(row)

        for k, v in row.items():
            if isinstance(v, float) and not math.isnan(v):
                row[k] = round(v, 6)

        patched_rows_good.append(row)

    new_good_path = "output/patched_good_repo_results.csv"
    with open(new_good_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(patched_rows_good)

    bad_patch = {}
    with open(bad_path, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            repo = row["repo"].strip()
            bad_patch[repo] = {
                "ci_presence": row["ci_presence"],
                "ci_passing":  row["ci_passing"],
                "commented_out_code": row["commented_out_code"],
            }

    with open(original_bad_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        fieldnames = reader.fieldnames
        bad_rows = list(reader)

    patched_rows_bad = []
    for row in bad_rows:
        repo = row["repo"].strip()
        if repo in bad_patch:
            row["ci_presence"] = bad_patch[repo]["ci_presence"]
            row["ci_passing"] = bad_patch[repo]["ci_passing"]
            row["commented_out_code"] = bad_patch[repo]["commented_out_code"]
            row = recalc_scores(row)

        for k, v in row.items():
            if isinstance(v, float) and not math.isnan(v):
                row[k] = round(v, 6)

        patched_rows_bad.append(row)

    new_bad_path = "output/patched_bad_repo_results.csv"
    with open(new_bad_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(patched_rows_bad)

def main(argv=None):
    argv = argv or sys.argv[1:]

    p = argparse.ArgumentParser(description="pull and merge(not the git kind) ci data into main data csv")
    subparser = p.add_subparsers(dest="choice", help="Mine or Analyze")

    proj = subparser.add_parser("proj-run", help="Run research project functions on processed data")

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

    if args.choice == "proj-run":
        print("patching stupid bugs")
        project_pipeline(args)


if __name__ == "__main__":
    main()
