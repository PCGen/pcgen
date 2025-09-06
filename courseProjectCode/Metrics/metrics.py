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




