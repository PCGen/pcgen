import re
from typing import Any, Dict, Optional, Tuple


# ---- User Canonicalization ----

def canonicalize_user(login: Optional[str]) -> Tuple[str, bool]:
    """Canonicalize a user login and detect bot accounts.

    Why this matters for MSR:
    - Contributors appear with inconsistent casing ("Alice" vs "alice")
    - Bot accounts should often be filtered from human contributor analysis
    - Consistent formats enable accurate contributor metrics

    Parameters:
    - login: raw login string (may be None or empty)

    Returns:
    - (login_norm, is_bot) tuple where:
      - login_norm: lowercase, stripped login (empty string for falsy inputs)
      - is_bot: True if login matches common bot patterns

    Bot detection hints - look for these patterns:
    - "[bot]" suffix (e.g., "dependabot[bot]")
    - Known names: "dependabot", "renovate", "github-actions", "gitlab-ci"

    Examples:
    >>> canonicalize_user("Alice")
    ('alice', False)
    >>> canonicalize_user("dependabot[bot]")
    ('dependabot[bot]', True)
    >>> canonicalize_user(None)
    ('', False)
    """
    if login is None:
        currentStringMod = ""
    else:
        currentStringMod = login.lower().strip()

    isBot = re.search(r"\[bot]", currentStringMod) is not None

    known_bots = {"dependabot", "renovate", "github-actions", "gitlab-ci"}
    for botName in known_bots:
        if re.search(botName, currentStringMod) is not None:
            isBot = True
            break

    return currentStringMod, isBot


# ---- Text Normalization ----

def normalize_text(md: Optional[str]) -> str:
    """Convert Markdown-like text into clean plain text for analysis.

    Why this matters for MSR:
    - Issue/PR bodies contain Markdown that obscures actual content
    - Code blocks should be preserved but marked for analysis
    - Consistent whitespace enables text comparison and NLP

    Parameters:
    - md: raw text that may contain Markdown (None returns empty string)

    Returns:
    - Cleaned text with these transformations:
      - Fenced code blocks (```...```) → <CODE>...</CODE>
      - Inline code (`...`) → <CODE>...</CODE>
      - Heading markers (e.g., "## ") removed
      - List bullets (e.g., "- ", "* ", "1. ") removed
      - Windows/Mac newlines → Unix \\n
      - Runs of 3+ blank lines → 2 blank lines
      - Runs of 2+ spaces/tabs → 1 space
      - ASCII control characters removed (except newlines)
      - URLs preserved as-is

    Examples:
    >>> normalize_text("# Hello World")
    'Hello World'
    >>> normalize_text("Use `print()` to debug")
    'Use <CODE>print()</CODE> to debug'
    >>> normalize_text(None)
    ''

    Implementation hints:
    - Use re.compile() for regex patterns
    - Process fenced code blocks BEFORE inline code (order matters!)
    - re.DOTALL makes . match newlines
    - re.MULTILINE makes ^ match line starts
    """
    currentStringMod = md
    if currentStringMod is None:
        return ""

    currentStringMod = re.sub(r"```(.*?\n)(.*?)```", r"<CODE>\n\2\n</CODE>", currentStringMod, flags=re.DOTALL)  # ```python\ncode\n``` style

    currentStringMod = re.sub(r"`(.*?)`", r"<CODE>\1</CODE>", currentStringMod)  # `code` style

    currentStringMod = re.sub(r"^(#+ )", "", currentStringMod, count=1)  # MD headers

    currentStringMod = re.sub(r"\r\n", r"\n", currentStringMod)  # \\r \\n -> \\n style newlines

    currentStringMod = re.sub(r"\r", r"\n", currentStringMod)  # \\r -> \\n style newlines

    currentStringMod = re.sub(r"\s{2,}", r" ", currentStringMod)  # \\s\\s (2 or more times) -> \\s

    currentStringMod = re.sub(r"[\x00-\x08\x0B-\x0C\x0E-\x1F]", r" ", currentStringMod)  # no more control chars

    return currentStringMod


# ---- Commit Message Parsing ----

def split_commit_message(msg: Optional[str]) -> Dict[str, Any]:
    """Parse a commit message into Conventional Commit components.

    Why this matters for MSR:
    - Conventional Commits provide semantic meaning (feat, fix, etc.)
    - Enables automated analysis of development practices
    - Breaking changes can be systematically identified

    Conventional Commits format: <type>[optional scope][!]: <subject>
    Examples:
    - "feat(parser): add array support" → type=feat, scope=parser
    - "fix: correct typo" → type=fix, scope=None
    - "feat!: breaking API change" → type=feat, breaking=True

    Parameters:
    - msg: raw commit message (subject + optional body). None treated as empty.

    Returns:
    - dict with keys: subject, body, type, scope, breaking
      - subject: first line (or CC description if CC format)
      - body: everything after first line, stripped
      - type: CC type lowercase (e.g., 'feat', 'fix') or None
      - scope: CC scope if present, or None
      - breaking: True if '!' indicates breaking change

    Examples:
    >>> split_commit_message("fix(auth): resolve login bug")
    {'subject': 'resolve login bug', 'body': '', 'type': 'fix', 'scope': 'auth', 'breaking': False}

    >>> split_commit_message("Update readme\\n\\nMore details here")
    {'subject': 'Update readme', 'body': 'More details here', 'type': None, 'scope': None, 'breaking': False}

    >>> split_commit_message(None)
    {'subject': '', 'body': '', 'type': None, 'scope': None, 'breaking': False}

    Implementation hints:
    - First normalize newlines and split into subject (line 1) and body
    - Use a regex to match CC pattern: type(scope)!: subject
    - Remember to lowercase the type in output
    """
    commitDict = {
        "subject": "",
        "body": "",
        "type": None,
        "scope": None,
        "breaking": False,
    }

    if msg is None:
        return commitDict

    cleanedString = re.sub(r"\r\n", r"\n", msg)

    stringBreakdown = re.match(r"^(?:(?P<type>\w+)(?:\((?P<scope>[^)]+)\))?(?P<breaking>!)?:\s+)?(?P<subject>[^\n]+)(?P<body>\n\n.*)?$", cleanedString, flags=re.DOTALL)

    if stringBreakdown is None:
        return commitDict

    commitDict["type"] = stringBreakdown.group("type").lower() if stringBreakdown.group("type") is not None else None
    commitDict["scope"] = stringBreakdown.group("scope")
    commitDict["subject"] = stringBreakdown.group("subject")
    commitDict["breaking"] = stringBreakdown.group("breaking") is not None
    commitDict["body"] = stringBreakdown.group("body") if stringBreakdown.group("body") is not None else ""
    return commitDict


