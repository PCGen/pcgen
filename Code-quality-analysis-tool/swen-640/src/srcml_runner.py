from __future__ import annotations

import os
import shutil
import subprocess
import tempfile
from typing import Optional

from git import Repo


def find_srcml_executable() -> str:
    """Return srcML executable path from PATH.

    Raises RuntimeError if not found.
    """
    candidates = ["srcml.exe", "srcml"] if os.name == "nt" else ["srcml", "srcml.exe"]
    for candidate in candidates:
        resolved = shutil.which(candidate)
        if resolved:
            return resolved
    raise RuntimeError("srcml executable not found in PATH; install srcML or provide srcml_path")


def get_file_content_at_commit(repo_path: str, file_path: str, commit: str = "HEAD") -> str:
    """Return file content as of a specific commit from a git repository."""
    repo = Repo(repo_path)
    try:
        return repo.git.show(f"{commit}:{file_path}")
    finally:
        repo.close()


def run_srcml_on_text(text: str, filename_hint: Optional[str] = None, srcml_path: Optional[str] = None) -> str:
    """Run srcML on text and return XML output as string."""
    srcml = srcml_path or find_srcml_executable()
    suffix = ""
    if filename_hint and "." in filename_hint:
        suffix = os.path.splitext(filename_hint)[1]

    fd_in, in_path = tempfile.mkstemp(suffix=suffix)
    os.close(fd_in)
    try:
        with open(in_path, "w", encoding="utf-8") as f:
            f.write(text)

        fd_out, out_path = tempfile.mkstemp(suffix=".srcml")
        os.close(fd_out)
        try:
            subprocess.run([srcml, in_path, "-o", out_path], check=True)
            with open(out_path, "r", encoding="utf-8") as f:
                return f.read()
        finally:
            try:
                os.remove(out_path)
            except Exception:
                pass
    finally:
        try:
            os.remove(in_path)
        except Exception:
            pass


def run_srcml_on_repo_file(
    repo_path: str,
    file_path: str,
    commit: str = "HEAD",
    srcml_path: Optional[str] = None,
) -> str:
    """Read file content at commit and return srcML XML output."""
    text = get_file_content_at_commit(repo_path, file_path, commit=commit)
    return run_srcml_on_text(text, filename_hint=file_path, srcml_path=srcml_path)


def run_srcml_on_directory(dir_path: str, srcml_path: Optional[str] = None) -> str:
    """Run srcML on an entire directory and return the combined XML as a string.

    srcML processes all recognised source files (.py, .java, .cpp,
    etc.) and returns a multi-unit document where the outer <unit> element
    contains one child <unit filename="..."> per source file.  File types that
    srcML does not recognise are silently skipped.

    Raises RuntimeError if the srcML binary is not found.
    """
    srcml = srcml_path or find_srcml_executable()
    fd_out, out_path = tempfile.mkstemp(suffix=".srcml")
    os.close(fd_out)
    try:
        subprocess.run([srcml, dir_path, "-o", out_path], check=True)
        with open(out_path, "r", encoding="utf-8") as f:
            return f.read()
    finally:
        try:
            os.remove(out_path)
        except Exception:
            pass