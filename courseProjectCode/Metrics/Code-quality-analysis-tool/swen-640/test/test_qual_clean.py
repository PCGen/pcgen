"""
Tests for qual_clean.py - DI1: Qualitative Text Normalization

These tests define the expected behavior for the three functions you must implement.
Run with: pytest test/test_qual_clean.py -v
"""
import pytest

from src import qual_clean


# =============================================================================
# canonicalize_user tests
# =============================================================================

class TestCanonicalizeUser:
    """Tests for user login canonicalization and bot detection."""

    @pytest.mark.parametrize("login,expected_norm,expected_bot", [
        # Normal users - should lowercase and not flag as bot
        ("Alice", "alice", False),
        ("BOB", "bob", False),
        ("CamelCase", "camelcase", False),
        ("user123", "user123", False),

        # Whitespace handling - should strip
        ("  alice  ", "alice", False),
        ("\tspaced\t", "spaced", False),

        # Bot detection - [bot] suffix
        ("dependabot[bot]", "dependabot[bot]", True),
        ("renovate[bot]", "renovate[bot]", True),
        ("some-app[bot]", "some-app[bot]", True),

        # Bot detection - known bot names
        ("dependabot", "dependabot", True),
        ("Dependabot", "dependabot", True),  # case insensitive
        ("renovate", "renovate", True),
        ("github-actions", "github-actions", True),
        ("GitHub-Actions", "github-actions", True),
        ("gitlab-ci", "gitlab-ci", True),

        # Edge cases - falsy inputs return ('', False)
        (None, "", False),
        ("", "", False),
    ])
    def test_canonicalize_user(self, login, expected_norm, expected_bot):
        """Verify login normalization and bot detection."""
        norm, is_bot = qual_clean.canonicalize_user(login)
        assert norm == expected_norm, f"Expected normalized login '{expected_norm}', got '{norm}'"
        assert is_bot == expected_bot, f"Expected is_bot={expected_bot} for '{login}'"


# =============================================================================
# normalize_text tests
# =============================================================================

class TestNormalizeText:
    """Tests for Markdown to plain text normalization."""

    def test_none_returns_empty_string(self):
        """None input should return empty string."""
        assert qual_clean.normalize_text(None) == ""

    def test_empty_string_returns_empty(self):
        """Empty string should return empty string."""
        assert qual_clean.normalize_text("") == ""

    # --- Fenced code blocks ---

    def test_fenced_code_block_basic(self):
        """Fenced code blocks should be wrapped with <CODE> tags."""
        md = "```\nprint('hello')\n```"
        out = qual_clean.normalize_text(md)
        assert "<CODE>" in out
        assert "</CODE>" in out
        assert "print('hello')" in out
        assert "```" not in out

    def test_fenced_code_block_with_language(self):
        """Fenced code blocks with language hint should work."""
        md = "```python\nx = 1\n```"
        out = qual_clean.normalize_text(md)
        assert "<CODE>" in out
        assert "x = 1" in out
        assert "```" not in out
        # Language hint should be removed
        assert "python" not in out or "python" in "<CODE>\nx = 1\n</CODE>"

    # --- Inline code ---

    def test_inline_code_basic(self):
        """Inline code should be wrapped with <CODE> tags."""
        md = "Use `print()` to debug"
        out = qual_clean.normalize_text(md)
        assert "<CODE>print()</CODE>" in out
        assert "`" not in out

    def test_inline_code_multiple(self):
        """Multiple inline code spans should all be converted."""
        md = "Compare `foo` and `bar`"
        out = qual_clean.normalize_text(md)
        assert "<CODE>foo</CODE>" in out
        assert "<CODE>bar</CODE>" in out
        assert "`" not in out

    # --- Headings ---

    @pytest.mark.parametrize("md,expected_text", [
        ("# Heading 1", "Heading 1"),
        ("## Heading 2", "Heading 2"),
        ("### Heading 3", "Heading 3"),
        ("#### Heading 4", "Heading 4"),
        ("##### Heading 5", "Heading 5"),
        ("###### Heading 6", "Heading 6"),
    ])
    def test_heading_markers_removed(self, md, expected_text):
        """Heading markers (# symbols) should be removed, text preserved."""
        out = qual_clean.normalize_text(md)
        assert expected_text in out
        # The # should be removed (not at start of result)
        assert not out.startswith("#")

    # --- List items ---

    @pytest.mark.parametrize("md,expected_text", [
        ("- item", "item"),
        ("* item", "item"),
        ("+ item", "item"),
        ("1. first", "first"),
        ("2. second", "second"),
        ("10. tenth", "tenth"),
    ])
    def test_list_markers_removed(self, md, expected_text):
        """List markers (-, *, +, 1.) should be removed, text preserved."""
        out = qual_clean.normalize_text(md)
        assert expected_text in out

    # --- URL preservation ---

    def test_urls_preserved(self):
        """URLs should be preserved intact."""
        md = "Visit http://example.com or https://github.com/user/repo"
        out = qual_clean.normalize_text(md)
        assert "http://example.com" in out
        assert "https://github.com/user/repo" in out

    # --- Newline normalization ---

    def test_windows_newlines_normalized(self):
        """Windows CRLF (\\r\\n) should become Unix LF (\\n)."""
        md = "line1\r\nline2\r\nline3"
        out = qual_clean.normalize_text(md)
        assert "\r" not in out
        assert "line1" in out and "line2" in out

    def test_old_mac_newlines_normalized(self):
        """Old Mac CR (\\r) should become Unix LF (\\n)."""
        md = "line1\rline2"
        out = qual_clean.normalize_text(md)
        assert "\r" not in out

    def test_excessive_blank_lines_collapsed(self):
        """More than 2 consecutive blank lines should collapse to 2."""
        md = "para1\n\n\n\n\npara2"
        out = qual_clean.normalize_text(md)
        # Should have at most 2 newlines between paragraphs
        assert "\n\n\n" not in out

    # --- Whitespace collapsing ---

    def test_excessive_spaces_collapsed(self):
        """Multiple spaces/tabs should collapse to single space."""
        md = "word1    word2\t\tword3"
        out = qual_clean.normalize_text(md)
        assert "  " not in out  # no double spaces
        assert "\t" not in out  # no tabs

    # --- Control characters ---

    def test_control_chars_removed(self):
        """ASCII control characters should be removed (except newlines)."""
        md = "text\x00with\x07control\x0Bchars"
        out = qual_clean.normalize_text(md)
        assert "\x00" not in out
        assert "\x07" not in out  # bell
        assert "\x0B" not in out  # vertical tab
        assert "text" in out and "with" in out

    # --- Combined test ---

    def test_combined_markdown_document(self):
        """Test a realistic Markdown document with multiple features."""
        md = """# Title

This paragraph has `inline code` and a URL: http://example.com

```python
def hello():
    print("world")
```

- list item 1
- list item 2

Final paragraph.
"""
        out = qual_clean.normalize_text(md)

        # Heading marker removed
        assert "Title" in out
        assert not any(line.strip().startswith("#") for line in out.split("\n"))

        # Inline code wrapped
        assert "<CODE>inline code</CODE>" in out

        # URL preserved
        assert "http://example.com" in out

        # Fenced code wrapped
        assert "<CODE>" in out
        assert "def hello():" in out

        # List markers removed
        assert "list item 1" in out

        # No raw markdown syntax remaining
        assert "```" not in out
        assert "`" not in out.replace("<CODE>", "").replace("</CODE>", "")


# =============================================================================
# split_commit_message tests
# =============================================================================

class TestSplitCommitMessage:
    """Tests for Conventional Commit message parsing."""

    def test_none_returns_defaults(self):
        """None input should return empty/default values."""
        result = qual_clean.split_commit_message(None)
        assert result["subject"] == ""
        assert result["body"] == ""
        assert result["type"] is None
        assert result["scope"] is None
        assert result["breaking"] is False

    def test_empty_string_returns_defaults(self):
        """Empty string should return empty/default values."""
        result = qual_clean.split_commit_message("")
        assert result["subject"] == ""
        assert result["body"] == ""
        assert result["type"] is None

    # --- Conventional Commit format ---

    def test_cc_type_only(self):
        """CC with type only: 'fix: message'"""
        result = qual_clean.split_commit_message("fix: correct the typo")
        assert result["type"] == "fix"
        assert result["scope"] is None
        assert result["breaking"] is False
        assert result["subject"] == "correct the typo"
        assert result["body"] == ""

    def test_cc_type_and_scope(self):
        """CC with type and scope: 'feat(parser): message'"""
        result = qual_clean.split_commit_message("feat(parser): add array support")
        assert result["type"] == "feat"
        assert result["scope"] == "parser"
        assert result["breaking"] is False
        assert result["subject"] == "add array support"

    def test_cc_breaking_with_bang(self):
        """CC with breaking change indicator: 'feat!: message'"""
        result = qual_clean.split_commit_message("feat!: breaking API change")
        assert result["type"] == "feat"
        assert result["scope"] is None
        assert result["breaking"] is True
        assert result["subject"] == "breaking API change"

    def test_cc_scope_and_breaking(self):
        """CC with scope and breaking: 'fix(api)!: message'"""
        result = qual_clean.split_commit_message("fix(api)!: remove deprecated endpoint")
        assert result["type"] == "fix"
        assert result["scope"] == "api"
        assert result["breaking"] is True
        assert result["subject"] == "remove deprecated endpoint"

    def test_cc_type_is_lowercase(self):
        """CC type should be lowercased in output."""
        result = qual_clean.split_commit_message("FIX: uppercase type")
        assert result["type"] == "fix"

        result2 = qual_clean.split_commit_message("Feat(Scope): mixed case")
        assert result2["type"] == "feat"

    @pytest.mark.parametrize("type_name", [
        "feat", "fix", "docs", "style", "refactor", "perf", "test", "chore", "ci", "build"
    ])
    def test_cc_common_types(self, type_name):
        """Common CC types should be recognized."""
        result = qual_clean.split_commit_message(f"{type_name}: some change")
        assert result["type"] == type_name

    # --- Non-CC messages ---

    def test_plain_message_no_cc(self):
        """Plain message without CC format."""
        result = qual_clean.split_commit_message("Update the readme file")
        assert result["type"] is None
        assert result["scope"] is None
        assert result["breaking"] is False
        assert result["subject"] == "Update the readme file"

    def test_message_with_colon_but_not_cc(self):
        """Message with colon but not CC format (has space before colon)."""
        result = qual_clean.split_commit_message("WIP : work in progress")
        assert result["type"] is None  # space before colon breaks CC format
        assert result["subject"] == "WIP : work in progress"

    # --- Multi-line messages ---

    def test_multiline_with_body(self):
        """Multi-line message should split into subject and body."""
        msg = "fix(auth): resolve login bug\n\nThis fixes the issue where users\ncould not log in after timeout."
        result = qual_clean.split_commit_message(msg)
        assert result["subject"] == "resolve login bug"
        assert "users" in result["body"]
        assert "timeout" in result["body"]

    def test_multiline_plain_message(self):
        """Multi-line plain message (not CC) should still split correctly."""
        msg = "Update dependencies\n\nBump lodash to 4.17.21"
        result = qual_clean.split_commit_message(msg)
        assert result["type"] is None
        assert result["subject"] == "Update dependencies"
        assert "lodash" in result["body"]

    def test_windows_newlines_in_body(self):
        """Windows newlines in message should be handled."""
        msg = "fix: something\r\n\r\nBody text here"
        result = qual_clean.split_commit_message(msg)
        assert result["subject"] == "something"
        assert "Body text" in result["body"]
