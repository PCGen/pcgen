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


