""" 
Authored by: 
Shahmir Khan
JoJo Kaler
Tyler Jaafari

Metrics Project
Software Quality Assurance
"""

import os
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET

def analyze_java_file(path: Path):
    """This function will go through a .java file and count the total lines of code, and comments"""
    total = 0
    code = 0
    comments = 0
    in_block = False

    with open(path, encoding="utf-8", errors="ignore") as file:
        # Counting all of the lines
        for line in file:
            total = total + 1
            line = line.strip()

            if in_block:
                comments = comments + 1
                # If we encounter the end of a comment block
                if "*/" in line:
                    in_block = False

            if line.startswith("//"):
                comments = comments + 1
            elif "/*" in line:
                comments = comments + 1
                # Catches a case where /* example comment */ occurs
                if "*/" not in line:
                    in_block = True
                # Ignore empty lines
            elif line != "":
                code += 1
    
    return total, code, comments

def collect_metrics(project_root: str):
    """This function goes through all of the relevant files and calls the analyze_java_file function for each found .java file"""
    root = Path(project_root)
    total_lines = 0
    total_code = 0
    total_comments = 0

    # We want to skip any irrelevant files such as test files, and gradle files
    skip_dirs = {"test", "tests", "itest", "utest", "build", ".gradle"}

    for path in root.rglob("*.java"):
        skip_file = False

        # If we find a file that we want to skip, break then continue through the loop
        for part in path.parts:
            if part.lower() in skip_dirs:
                skip_file = True
                break
        
        if skip_file:
            continue
        
        # Update the totals
        t, c, cm = analyze_java_file(path)
        total_lines += t
        total_code  += c
        total_comments += cm

    # Catching a divide by 0 error
    if total_lines != 0:
        density = total_comments / total_lines
    else:
        density = 0

    return total_lines, total_code, total_comments, density

def find_gradlew(project_root: Path) -> str:
    """Gets the gradle configuration file for the testing suite"""
    g = project_root / "gradlew"
    if g.exists(): 
        return str(g)
    gbat = project_root / "gradlew.bat"
    if gbat.exists(): 
        return str(gbat)
    return "gradle"

def run_gradle_tests(project_root: Path) -> int:
    """This runs the gradle tests for us, and grabs the report that is generated"""
    gradlew = find_gradlew(project_root)
    try:
        if gradlew.endswith("gradlew"):
            Path(gradlew).chmod(0o755)
    except Exception:
        pass
    proc = subprocess.run(
        [gradlew, "test", "jacocoTestReport", "--no-daemon"],
        cwd=str(project_root),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )
    return proc.returncode

def parse_test_results(project_root: Path):
    """
    This parses the test results in ~/build.test-results/test
    These are in the form of XML files

    Documentation used for research:
    https://docs.python.org/3/library/xml.etree.elementtree.html
    """
    d = project_root / "build" / "test-results" / "test"
    agg = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    if not d.exists():
        return agg
    for x in d.glob("*.xml"):
        try:
            root = ET.parse(x).getroot()
            agg["tests"]    += int(root.attrib.get("tests", "0"))
            agg["failures"] += int(root.attrib.get("failures", "0"))
            agg["errors"]   += int(root.attrib.get("errors", "0"))
            agg["skipped"]  += int(root.attrib.get("skipped", root.attrib.get("ignored","0")))
        except Exception:
            pass
    return agg

def parse_jacoco_line_coverage(project_root: Path):
    """
    This goes through the jacoco test results and grabs the testability metrics
    
    Documentation used for research:
    https://docs.gradle.org/current/userguide/jacoco_plugin.html
    """
    p = project_root / "build" / "reports" / "jacoco" / "test" / "jacocoTestReport.xml"
    if not p.exists():
        return 0, 0, 0.0
    try:
        root = ET.parse(p).getroot()
        cov = miss = 0
        for c in root.iter("counter"):
            if c.attrib.get("type") == "LINE":
                cov  += int(c.attrib.get("covered", "0"))
                miss += int(c.attrib.get("missed", "0"))
        total = cov + miss
        pct = (cov / total * 100.0) if total else 0.0
        return cov, miss, pct
    except Exception:
        return 0, 0, 0.0

if __name__ == "__main__":
    """This is the function that outputs the metric results"""
    # Getting the right files
    cwd = Path.cwd()
    project_root = cwd.parent.parent
    project_dir = project_root / "code/src/java"

    total, code, comments, density = collect_metrics(project_dir)

    # Maintainability
    print("-- Maintainability Metrics --")
    print(f"Total lines: {total}")
    print(f"Total lines of code: {code}")
    print(f"Total lines of comments: {comments}")
    print(f"Comment density: {density:.3f}")

    # Testability
    _ = run_gradle_tests(project_root)
    tests = parse_test_results(project_root)
    cov_cov, cov_miss, cov_pct = parse_jacoco_line_coverage(project_root)

    print("\n-- Testability --")
    print(f"Tests: {tests['tests']}  Failures: {tests['failures']}  Errors: {tests['errors']}  Skipped: {tests['skipped']}")
    print(f"Line coverage: {cov_pct:.2f}%  (covered={cov_cov}, missed={cov_miss})")
    

