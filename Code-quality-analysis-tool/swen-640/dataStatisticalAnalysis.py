import csv
import os
import statistics
from collections import defaultdict

import scipy.stats

FIELDNAMES = [
    "readability_score", "structure_score", "documentation_score",
    "correctness_test_score", "maintainability_score",
    "name_length", "case_mixing", "poor_naming", "commented_out_code",
    "file_length", "function_length", "ratio",
    "docstring", "comment_length", "token_ratio",
    "test_file", "ci_presence", "ci_passing",
    "file_modification", "commit_naming", "refactor_rate"
]


def main():
    bad_data = []
    field_data_bad = defaultdict(list)
    with open("output/bad_repo_results.csv", newline="", encoding="utf-8") as reaper_file:
        reader = csv.reader(reaper_file)
        header = next(reader, None)  # skip header

        for line in reader:
            if not line:
                continue

            bad_data.append(float(line[1].strip()))

            for i in range(len(FIELDNAMES)):
                field_data_bad[FIELDNAMES[i]].append(float(line[i+2].strip()))

    good_data = []
    field_data_good = defaultdict(list)
    with open("output/good_repo_results.csv", newline="", encoding="utf-8") as reaper_file:
        reader = csv.reader(reaper_file)
        header = next(reader, None)  # skip header

        for line in reader:
            if not line:
                continue

            good_data.append(float(line[1].strip()))

            for i in range(len(FIELDNAMES)):
                field_data_good[FIELDNAMES[i]].append(float(line[i+2].strip()))

    print("Repo Data Overviews:")
    print(f"Engineered Repos- mean: {statistics.mean(good_data)}, median: {statistics.median(good_data)}, size: {len(good_data)}")
    print(f"Nonengineered Repos- mean: {statistics.mean(bad_data)}, median: {statistics.median(bad_data)}, size: {len(bad_data)}")

    result = scipy.stats.mannwhitneyu(good_data, bad_data, alternative="greater")

    print("Mann Whitney U Test Results:")
    print("U Statistic: " + str(result.statistic))
    print("P-Value: " + str(result.pvalue))

    n1 = len(good_data)
    n2 = len(bad_data)
    effect_size = result.statistic / (n1 * n2)
    print("Effect size: " + str(effect_size))

    print("Various Field Results:")

    results_path = "output/field_results.csv"
    results_exists = os.path.exists(results_path)

    with (
        open(results_path, "a", newline="", encoding="utf-8") as results_csv,
    ):
        fields = ["Category", "Engineered Mean", "Nonengineered Mean", "% change", "Engineered Median", "Nonengineered Median"]
        results_writer = csv.DictWriter(results_csv, fieldnames=fields)

        if not results_exists:
            results_writer.writeheader()

        for field in FIELDNAMES:
            good_mean = statistics.mean(field_data_good[field])
            good_median = statistics.median(field_data_good[field])
            bad_mean = statistics.mean(field_data_bad[field])
            bad_median = statistics.median(field_data_bad[field])

            if bad_mean == 0.0:
                percent_change = 100
            else:
                percent_change = ((good_mean - bad_mean) / bad_mean) * 100

            row = {fields[0]: field, fields[1]: good_mean, fields[2]: bad_mean, fields[3]: percent_change, fields[4]: good_median, fields[5]: bad_median}
            results_writer.writerow(row)

            print(f"Field: {field}, engineered mean: {good_mean}, engineered median: {good_median}, percent change: {percent_change}, nonengineered mean: {bad_mean}, nonengineered median: {bad_median}")


if __name__ == "__main__":
    main()