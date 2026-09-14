import nltk
import numpy as np
import pytest

from src import da2_vocabulary


# ============================================================================
# TestCommentExtraction - 10 tests
# ============================================================================
@pytest.fixture(scope="session", autouse=True)
def runOnce():
    nltk.download('stopwords')
    nltk.download('punkt')
    yield


class TestCommentExtraction:
    """Tests for extracting comments from srcML XML."""

    def test_extract_line_comment(self):
        """Extract simple line comment."""
        xml = '<unit xmlns="http://www.srcML.org/srcML/src"><comment type="line">// TODO: refactor</comment></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 1
        assert comments[0] == "TODO: refactor"

    def test_extract_block_comment(self):
        """Extract block comment."""
        xml = '<unit xmlns="http://www.srcML.org/srcML/src"><comment type="block">/* Process data */</comment></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 1
        assert comments[0] == "Process data"

    def test_extract_python_comment(self):
        """Extract Python # comment."""
        xml = '<unit xmlns="http://www.srcML.org/srcML/src"><comment type="line"># Initialize variables</comment></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 1
        assert comments[0] == "Initialize variables"

    def test_extract_multiple_comments(self):
        """Extract multiple comments from XML."""
        xml = '''<unit xmlns="http://www.srcML.org/srcML/src">
            <comment type="line">// First comment</comment>
            <comment type="line">// Second comment</comment>
        </unit>'''
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 2
        assert "First comment" in comments
        assert "Second comment" in comments

    def test_strip_comment_markers(self):
        """Comment markers should be stripped."""
        xml = '<unit xmlns="http://www.srcML.org/srcML/src"><comment type="line">// Test</comment></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert "//" not in comments[0]

    def test_empty_comment(self):
        """Empty comments should be filtered out."""
        xml = '<unit xmlns="http://www.srcML.org/srcML/src"><comment type="line">//</comment></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 0

    def test_no_comments(self):
        """XML without comments returns empty list."""
        xml = '<unit xmlns="http://www.srcML.org/srcML/src"><function><name>test</name></function></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 0

    def test_multiline_block_comment(self):
        """Multiline block comment preserved."""
        xml = '''<unit xmlns="http://www.srcML.org/srcML/src">
            <comment type="block">/* Process incoming requests
             * and route to handlers
             */</comment>
        </unit>'''
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 1
        assert "Process incoming requests" in comments[0]
        assert "route to handlers" in comments[0]

    def test_invalid_xml(self):
        """Invalid XML should not crash."""
        xml = "<unit><broken"
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert isinstance(comments, list)

    def test_no_namespace(self):
        """Should work without namespace."""
        xml = '<unit><comment type="line">// Test</comment></unit>'
        comments = da2_vocabulary.extract_comments_from_srcml(xml)
        assert len(comments) == 1
        assert comments[0] == "Test"


# ============================================================================
# TestVocabularyExtraction - 12 tests
# ============================================================================

class TestVocabularyExtraction:
    """Tests for vocabulary tokenization and normalization."""

    def test_basic_tokenization(self):
        """Tokenize simple text."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["Fix the authentication bug"],
            remove_stopwords=False,
            stem=False
        )
        assert "fix" in tokens
        assert "authentication" in tokens
        assert "bug" in tokens

    def test_lowercase_normalization(self):
        """All tokens should be lowercase."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["ProcessData and HandleRequest"],
            remove_stopwords=False,
            stem=False
        )
        assert all(t.islower() for t in tokens)
        assert "processdata" in tokens
        assert "handlerequest" in tokens

    def test_stopword_removal(self):
        """Stopwords should be filtered."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["the user is authenticated"],
            remove_stopwords=True,
            stem=False
        )
        assert "the" not in tokens
        assert "is" not in tokens
        assert "user" in tokens
        assert "authenticated" in tokens

    def test_stopwords_disabled(self):
        """Stopwords kept when disabled."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["the user"],
            remove_stopwords=False,
            stem=False
        )
        assert "the" in tokens
        assert "user" in tokens

    def test_stemming(self):
        """Stemming groups related words."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["processing processed processor"],
            remove_stopwords=False,
            stem=True
        )
        # All should stem to "process"
        assert len(set(tokens)) <= 2  # May have minor variations
        assert all("process" in t for t in tokens)

    def test_stemming_disabled(self):
        """No stemming when disabled."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["processing processed"],
            remove_stopwords=False,
            stem=False
        )
        assert "processing" in tokens
        assert "processed" in tokens

    def test_min_length_filter(self):
        """Short tokens filtered."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["a big authentication system"],
            min_length=3,
            remove_stopwords=False,
            stem=False
        )
        assert "a" not in tokens  # length 1
        assert "big" in tokens  # length 3
        assert "authentication" in tokens

    def test_number_filtering(self):
        """Pure numbers should be filtered."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["user 123 data"],
            remove_stopwords=False,
            stem=False
        )
        assert "123" not in tokens
        assert "user" in tokens
        assert "data" in tokens

    def test_empty_input(self):
        """Empty list returns empty."""
        tokens = da2_vocabulary.extract_vocabulary([])
        assert tokens == []

    def test_none_in_list(self):
        """None values handled gracefully."""
        tokens = da2_vocabulary.extract_vocabulary(
            [None, "test", None],
            remove_stopwords=False,
            stem=False
        )
        assert "test" in tokens
        assert len(tokens) == 1

    def test_preserves_duplicates(self):
        """Duplicates preserved for frequency analysis."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["user user authentication user"],
            remove_stopwords=False,
            stem=False
        )
        assert tokens.count("user") == 3
        assert tokens.count("authentication") == 1

    def test_multiple_texts(self):
        """Process multiple text strings."""
        tokens = da2_vocabulary.extract_vocabulary(
            ["first text", "second text"],
            remove_stopwords=False,
            stem=False
        )
        assert "first" in tokens
        assert "second" in tokens
        assert "text" in tokens
        assert tokens.count("text") == 2


# ============================================================================
# TestClustering - 8 tests
# ============================================================================

class TestClustering:
    """Tests for k-means clustering."""

    def test_cluster_produces_labels(self):
        """Clustering produces label array."""
        tokens = ["user", "authentication", "login", "password", "database", "query"]
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(tokens, k=2)
        assert isinstance(labels, np.ndarray)
        assert len(labels) == len(tokens)

    def test_cluster_count(self):
        """Correct number of clusters."""
        tokens = ["user", "auth", "login", "pass", "data", "query", "table", "cache"]
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(tokens, k=3)
        n_clusters = len(np.unique(labels))
        assert n_clusters <= 3  # May be less if not enough variance

    def test_vectors_shape(self):
        """Embedding vectors have correct shape (n_tokens x 300)."""
        tokens = ["user", "authentication", "database"]
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(tokens, k=2)
        assert vectors.shape[0] == len(tokens)
        assert vectors.shape[1] == 300  # spaCy en_core_web_md embedding dim

    def test_reproducibility(self):
        """Same tokens produce same clusters."""
        tokens = ["user", "auth", "login", "data", "query", "cache"]
        labels1, _, _ = da2_vocabulary.cluster_vocabulary(tokens, k=2)
        labels2, _, _ = da2_vocabulary.cluster_vocabulary(tokens, k=2)
        np.testing.assert_array_equal(labels1, labels2)

    def test_empty_tokens(self):
        """Empty input handled gracefully."""
        labels, vectors, model = da2_vocabulary.cluster_vocabulary([], k=3)
        assert len(labels) == 0
        assert vectors.shape == (0, 0)

    def test_single_token(self):
        """Single token handled."""
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(["user"], k=2)
        assert len(labels) == 1
        assert labels[0] == 0  # Single cluster

    def test_k_larger_than_tokens(self):
        """k larger than token count handled."""
        tokens = ["user", "data"]
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(tokens, k=10)
        assert len(labels) == len(tokens)
        n_clusters = len(np.unique(labels))
        assert n_clusters <= len(tokens)

    def test_vectorizer_params(self):
        """vectorizer_params is accepted without error (ignored in embedding mode)."""
        tokens = ["user", "database"]
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(
            tokens,
            k=2,
            vectorizer_params={"max_features": 5}  # ignored; embeddings have fixed dim
        )
        assert vectors.shape[0] == len(tokens)
        assert vectors.shape[1] == 300  # en_core_web_md embedding dimension


# ============================================================================
# TestDimensionalityReduction - 6 tests
# ============================================================================

class TestDimensionalityReduction:
    """Tests for PCA and t-SNE dimensionality reduction."""

    def test_pca_reduces_dimensions(self):
        """PCA reduces to 2D."""
        vectors = np.random.rand(50, 100)
        reduced = da2_vocabulary.reduce_dimensions(vectors, n_components=2, method="pca")
        assert reduced.shape == (50, 2)

    def test_tsne_reduces_dimensions(self):
        """t-SNE reduces to 2D."""
        vectors = np.random.rand(50, 100)
        reduced = da2_vocabulary.reduce_dimensions(vectors, n_components=2, method="tsne")
        assert reduced.shape == (50, 2)

    def test_invalid_method(self):
        """Invalid method raises ValueError."""
        vectors = np.random.rand(10, 20)
        with pytest.raises(ValueError):
            da2_vocabulary.reduce_dimensions(vectors, method="invalid")

    def test_empty_vectors(self):
        """Empty vectors handled."""
        vectors = np.array([]).reshape(0, 10)
        reduced = da2_vocabulary.reduce_dimensions(vectors, n_components=2)
        assert reduced.shape == (0, 2)

    def test_few_samples(self):
        """Small sample size handled."""
        vectors = np.random.rand(3, 50)
        reduced = da2_vocabulary.reduce_dimensions(vectors, n_components=2, method="pca")
        assert reduced.shape[0] == 3

    def test_pca_reproducible(self):
        """PCA produces same results."""
        vectors = np.random.rand(20, 30)
        reduced1 = da2_vocabulary.reduce_dimensions(vectors, method="pca")
        reduced2 = da2_vocabulary.reduce_dimensions(vectors, method="pca")
        np.testing.assert_array_almost_equal(reduced1, reduced2)


# ============================================================================
# TestVisualization - 4 tests
# ============================================================================

class TestVisualization:
    """Tests for cluster visualization."""

    def test_visualize_no_crash(self):
        """Visualization completes without error."""
        coords = np.array([[0, 0], [1, 1], [2, 2], [5, 5]])
        labels = np.array([0, 0, 0, 1])
        tokens = ["user", "login", "auth", "database"]

        # Should not raise
        da2_vocabulary.visualize_clusters(
            coords, labels, tokens, title="Test"
        )

    def test_visualize_saves_file(self, tmp_path):
        """Saves visualization to file."""
        coords = np.array([[0, 0], [1, 1]])
        labels = np.array([0, 1])
        tokens = ["user", "data"]

        output_path = tmp_path / "test_clusters.png"
        da2_vocabulary.visualize_clusters(
            coords, labels, tokens,
            title="Test",
            output_path=str(output_path)
        )

        assert output_path.exists()

    def test_visualize_empty_data(self):
        """Empty data handled gracefully."""
        coords = np.array([]).reshape(0, 2)
        labels = np.array([])
        tokens = []

        # Should not raise
        da2_vocabulary.visualize_clusters(coords, labels, tokens)

    def test_visualize_single_cluster(self):
        """Single cluster visualization works."""
        coords = np.array([[0, 0], [1, 1], [2, 2]])
        labels = np.array([0, 0, 0])
        tokens = ["user", "login", "auth"]

        # Should not raise
        da2_vocabulary.visualize_clusters(coords, labels, tokens)


# ============================================================================
# TestAlignment - 8 tests
# ============================================================================

class TestAlignment:
    """Tests for vocabulary alignment metrics."""

    def test_perfect_overlap(self):
        """Identical vocabularies have overlap = 1.0."""
        tokens_a = ["user", "data", "query"]
        tokens_b = ["user", "data", "query"]
        labels_a = np.array([0, 0, 1])
        labels_b = np.array([0, 0, 1])

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        assert alignment["vocab_overlap"] == 1.0
        assert alignment["shared_vocab_size"] == 3

    def test_no_overlap(self):
        """Disjoint vocabularies have overlap = 0.0."""
        tokens_a = ["user", "login"]
        tokens_b = ["database", "query"]
        labels_a = np.array([0, 0])
        labels_b = np.array([1, 1])

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        assert alignment["vocab_overlap"] == 0.0
        assert alignment["shared_vocab_size"] == 0
        assert alignment["cluster_similarity"] == 0.0

    def test_partial_overlap(self):
        """Partial overlap computed correctly."""
        tokens_a = ["user", "login", "auth"]
        tokens_b = ["user", "database", "query"]
        labels_a = np.array([0, 0, 0])
        labels_b = np.array([0, 1, 1])

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        # Overlap: 1 shared / 5 total unique = 0.2
        assert alignment["vocab_overlap"] == pytest.approx(0.2, abs=0.01)
        assert alignment["shared_vocab_size"] == 1

    def test_shared_vocab_size(self):
        """Shared vocabulary size correct."""
        tokens_a = ["user", "login", "auth", "password"]
        tokens_b = ["user", "login", "database", "query"]
        labels_a = np.array([0, 0, 0, 0])
        labels_b = np.array([1, 1, 1, 1])

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        assert alignment["shared_vocab_size"] == 2  # user, login

    def test_cluster_similarity_perfect(self):
        """Perfect cluster alignment has ARI = 1.0."""
        tokens_a = ["user", "login", "database", "query"]
        tokens_b = ["user", "login", "database", "query"]
        labels_a = np.array([0, 0, 1, 1])
        labels_b = np.array([0, 0, 1, 1])

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        assert alignment["cluster_similarity"] == pytest.approx(1.0, abs=0.01)

    def test_cluster_similarity_poor(self):
        """Poor cluster alignment has low ARI."""
        tokens_a = ["user", "login", "database", "query"]
        tokens_b = ["user", "login", "database", "query"]
        labels_a = np.array([0, 0, 1, 1])
        labels_b = np.array([0, 1, 0, 1])  # Completely different clustering

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        # ARI should be low (may be 0 or negative)
        assert alignment["cluster_similarity"] < 0.5

    def test_empty_vocabularies(self):
        """Empty vocabularies handled."""
        alignment = da2_vocabulary.measure_alignment([], [], np.array([]), np.array([]))
        assert alignment["vocab_overlap"] == 0.0
        assert alignment["shared_vocab_size"] == 0
        assert alignment["cluster_similarity"] == 0.0

    def test_single_shared_token(self):
        """Single shared token handled (edge case for ARI)."""
        tokens_a = ["user", "login"]
        tokens_b = ["user", "database"]
        labels_a = np.array([0, 0])
        labels_b = np.array([1, 1])

        alignment = da2_vocabulary.measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
        assert alignment["shared_vocab_size"] == 1
        # ARI requires >= 2 samples, so should be 0.0
        assert alignment["cluster_similarity"] == 0.0
