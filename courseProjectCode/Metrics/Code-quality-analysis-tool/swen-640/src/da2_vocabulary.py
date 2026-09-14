from typing import Any, Dict, List, Tuple, Optional
import numpy as np
import regex
import sklearn
import spacy
from matplotlib import pyplot as plt
import xml.etree.ElementTree as ET

from nltk import PorterStemmer
from nltk.corpus import stopwords
from sklearn.cluster import KMeans
from sklearn.metrics import adjusted_rand_score

from src import db_utils
from src.qual_clean import normalize_text


def extract_comments_from_srcml(xml_str: str) -> List[str]:
    """Extract all comment text from srcML XML.

    Why this matters for MSR:
    - Comments are natural language documentation written by developers
    - Comparing comment vocabulary to code vocabulary reveals documentation quality
    - Inline comments often explain intent that's not obvious from identifiers
    - srcML preserves comments as <comment> tags with type="line" or type="block"

    Parameters:
    - xml_str: srcML XML document as string

    Returns:
    - List of comment strings (one per <comment> element found)
    - Strip comment markers (// /* */ #) from each comment
    - Preserve multi-line comments as single strings

    Examples:
    >>> xml = '<unit><comment type="line">// TODO: refactor this</comment></unit>'
    >>> extract_comments_from_srcml(xml)
    ['TODO: refactor this']

    >>> xml = '<unit><comment type="block">/* Process user input */</comment></unit>'
    >>> extract_comments_from_srcml(xml)
    ['Process user input']

    Implementation hints:
    - Use same XML parsing approach from DA1 (DOM or SAX)
    - Find all <comment> elements regardless of type attribute
    - Strip leading/trailing whitespace
    - Remove comment syntax: //, /* */, #, etc.
    - Return empty list if no comments found
    """
    try:
        root = ET.fromstring(xml_str)
    except ET.ParseError:
        return []

    #  strip namespace prefixes
    for item in root.iter():
        if "}" in item.tag:
            item.tag = item.tag.rsplit('}', 1)[1]

    comments = []
    for comment in root.findall('.//comment'):
        commentData = comment.text.strip()
        commentData = regex.sub(r'^#', '', commentData)
        commentData = regex.sub(r'^//', '', commentData)
        commentData = regex.sub(r'^/\*', '', commentData)
        commentData = regex.sub(r'\*/$', '', commentData)
        commentData = commentData.strip()  # one more strip just in case
        if commentData != "":
            comments.append(commentData)

    return comments


def extract_vocabulary(
        text_list: List[str],
        min_length: int = 3,
        remove_stopwords: bool = True,
        stem: bool = True
) -> List[str]:
    """Tokenize and normalize text into vocabulary tokens.

    Why this matters for MSR:
    - Raw text contains noise (stopwords, punctuation, inconsistent casing)
    - Normalization enables meaningful comparison across sources
    - Stemming groups related words (e.g., "process", "processing", "processed")
    - Token filtering reduces dimensionality for clustering

    Parameters:
    - text_list: list of text strings to process
    - min_length: minimum token length to keep (default 3)
    - remove_stopwords: filter common English words (default True)
    - stem: apply Porter stemming (default True)

    Returns:
    - Flat list of normalized tokens (all lowercase)
    - Tokens appear in order they were encountered
    - Duplicates are preserved (frequency matters for later analysis)

    Behavior:
    - Tokenize on word boundaries (alphanumeric sequences)
    - Convert to lowercase
    - Remove tokens shorter than min_length
    - Remove stopwords if enabled (use NLTK or custom list)
    - Apply stemming if enabled (use NLTK PorterStemmer)
    - Filter out pure numbers

    Examples:
    >>> extract_vocabulary(["Fix the authentication bug"], stem=False)
    ['fix', 'authentication', 'bug']

    >>> extract_vocabulary(["Processing user data"], stem=True)
    ['process', 'user', 'data']

    Implementation hints:
    - Use re.findall(r'\b\w+\b', text.lower()) for tokenization
    - Common stopwords: the, a, an, is, are, was, were, this, that, etc.
    - nltk.stem.PorterStemmer for stemming
    - Keep it simple: basic preprocessing is sufficient
    """
    outputTokens = []

    for text in text_list:
        if text is None:
            continue

        tokenizedForm = regex.findall(r'\b\w+\b', text.lower())

        cleanedTokens = []
        for token in tokenizedForm:
            if len(token) >= min_length:
                cleanedTokens.append(token)

        tokenizedForm = cleanedTokens

        if remove_stopwords:
            cleanedTokens = []
            stop_words = set(stopwords.words('english'))

            for token in tokenizedForm:
                if token not in stop_words:
                    cleanedTokens.append(token)

            tokenizedForm = cleanedTokens

        if stem:
            cleanedTokens = []
            ps = PorterStemmer()

            for token in tokenizedForm:
                stemmedToken = ps.stem(token)
                if stemmedToken not in cleanedTokens:
                    cleanedTokens.append(stemmedToken)

            tokenizedForm = cleanedTokens

        for token in tokenizedForm:
            if not str.isdigit(token):
                outputTokens.append(token)

    return outputTokens


def cluster_vocabulary(
        tokens: List[str],
        k: int = 5,
        vectorizer_params: Optional[Dict[str, Any]] = None
) -> Tuple[np.ndarray, np.ndarray, Any]:
    """Perform k-means clustering on vocabulary tokens.

    Why this matters for MSR:
    - Clustering reveals semantic groupings without supervision
    - k-means is simple, fast, and interpretable
    - Pre-trained word embeddings encode distributional semantics (words that appear
      in similar contexts have similar vectors)
    - Cluster assignments enable downstream alignment analysis

    Vectorization note:
    - We use **pre-trained word embeddings** from spaCy's `en_core_web_md` model
      (300-dimensional GloVe-style vectors)
    - Embeddings capture semantic relationships: 'fix' and 'bug' cluster together
      because they appear in similar contexts, even though they share no characters.
      This produces thematic clusters ('auth/login/session', 'fix/bug/error') that
      are directly useful as features for M1 prediction.
    - Out-of-vocabulary tokens receive a zero vector and will cluster together.

    Parameters:
    - tokens: flat list of vocabulary tokens
    - k: number of clusters (default 5)
    - vectorizer_params: accepted for API compatibility; not used in embedding mode

    Returns:
    - (labels, vectors, model) tuple where:
      - labels: np.ndarray of cluster assignments (shape: n_tokens,)
      - vectors: embedding matrix (shape: n_tokens × 300)
      - model: fitted KMeans object (or None for k=1)

    Behavior:
    - Look up each token in spaCy's en_core_web_md vocabulary to get a 300-dim vector
    - Stack vectors into a matrix
    - Fit KMeans with n_clusters=k, random_state=42 (for reproducibility)
    - Return cluster labels corresponding to each input token

    Examples:
    >>> tokens = ['user', 'authentication', 'login', 'password', 'database', 'query']
    >>> labels, vectors, model = cluster_vocabulary(tokens, k=2)
    >>> len(labels)
    6
    >>> labels.shape
    (6,)
    >>> vectors.shape[1]  # embedding dimension
    300

    Implementation hints:
    - import spacy; nlp = spacy.load('en_core_web_md')
    - vectors = np.array([nlp(token).vector for token in tokens])
    - from sklearn.cluster import KMeans
    - If tokens list is very short, reduce k to avoid empty clusters
    - Use random_state=42 for reproducible results
    """
    if not tokens:
        return np.array([]), np.array([]).reshape(0, 0), None

    nlp = spacy.load('en_core_web_md')
    vectors = np.array([nlp(token).vector for token in tokens])

    cluster = KMeans(n_clusters=k if k <= len(tokens) else len(tokens), random_state=42)
    cluster.fit(vectors)

    return cluster.labels_, vectors, cluster


def reduce_dimensions(
        vectors: np.ndarray,
        n_components: int = 2,
        method: str = "pca"
) -> np.ndarray:
    """Reduce high-dimensional vectors to 2D for visualization.

    Why this matters for MSR:
    - Human interpretation requires 2D or 3D projections
    - 300-dimensional embedding vectors cannot be read directly from a scatter plot
    - PCA preserves global structure (fast, deterministic)
    - t-SNE preserves local structure (slower, better visual separation of clusters)

    Parameters:
    - vectors: np.ndarray of shape (n_samples, n_features)
    - n_components: target dimensionality (default 2 for scatter plots)
    - method: either "pca" or "tsne" (default "pca")

    Returns:
    - np.ndarray of shape (n_samples, n_components)

    Behavior:
    - For "pca": use sklearn.decomposition.PCA
    - For "tsne": use sklearn.manifold.TSNE with random_state=42
    - Raise ValueError if method is not recognized

    Examples:
    >>> vectors = np.random.rand(100, 500)  # 100 samples, 500 features
    >>> reduced = reduce_dimensions(vectors, n_components=2, method="pca")
    >>> reduced.shape
    (100, 2)

    Implementation hints:
    - from sklearn.decomposition import PCA
    - from sklearn.manifold import TSNE
    - PCA is faster and stable (good default)
    - t-SNE takes longer but often reveals better clusters visually
    - Always set random_state=42 for TSNE reproducibility
    """
    if len(vectors) == 0:
        return np.array([]).reshape(0, 2)

    if method == "pca":
        PCA = sklearn.decomposition.PCA(n_components=n_components)
        PCA.fit(vectors)
        return PCA.transform(vectors)
    elif method == "tsne":
        TSNE = sklearn.manifold.TSNE(n_components=n_components, random_state=42)
        return TSNE.fit_transform(vectors)
    else:
        raise ValueError()


def visualize_clusters(
        coords_2d: np.ndarray,
        labels: np.ndarray,
        tokens: List[str],
        title: str = "Vocabulary Clusters",
        output_path: Optional[str] = None
) -> None:
    """Create scatter plot of clustered vocabulary in 2D space.

    **Provided - copy this into your da2_vocabulary.py as-is.**
    """
    if coords_2d.shape[0] == 0:
        plt.figure(figsize=(10, 8))
        plt.title(title)
        plt.xlabel("Dimension 1")
        plt.ylabel("Dimension 2")
        if output_path:
            plt.savefig(output_path, dpi=300, bbox_inches='tight')
            plt.close()
        return

    plt.figure(figsize=(10, 8))
    scatter = plt.scatter(
        coords_2d[:, 0],
        coords_2d[:, 1],
        c=labels,
        cmap='tab10',
        alpha=0.6,
        s=50,
        edgecolors='black',
        linewidth=0.5
    )

    n_clusters = len(np.unique(labels))
    if n_clusters <= 10:
        plt.colorbar(scatter, label='Cluster ID', ticks=range(n_clusters))
    else:
        plt.colorbar(scatter, label='Cluster ID')

    # Annotate the token closest to each cluster centroid
    for cluster_id in np.unique(labels):
        cluster_mask = labels == cluster_id
        cluster_indices = np.where(cluster_mask)[0]
        if len(cluster_indices) > 0:
            cluster_coords = coords_2d[cluster_mask]
            centroid = cluster_coords.mean(axis=0)
            distances = np.linalg.norm(cluster_coords - centroid, axis=1)
            rep_idx = cluster_indices[np.argmin(distances)]
            if rep_idx < len(tokens):
                plt.annotate(
                    tokens[rep_idx],
                    xy=(coords_2d[rep_idx, 0], coords_2d[rep_idx, 1]),
                    xytext=(5, 5),
                    textcoords='offset points',
                    fontsize=8,
                    alpha=0.7
                )

    plt.title(title, fontsize=14, fontweight='bold')
    plt.xlabel("Dimension 1", fontsize=12)
    plt.ylabel("Dimension 2", fontsize=12)
    plt.grid(True, alpha=0.3)
    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        plt.close()
    else:
        plt.show()


def measure_alignment(
        tokens_a: List[str],
        tokens_b: List[str],
        labels_a: np.ndarray,
        labels_b: np.ndarray
) -> Dict[str, float]:
    """Compute alignment metrics between two clustered vocabularies.

    Why this matters for MSR:
    - Measures whether commit messages and code use the same terminology
    - Quantifies documentation drift (misalignment = poor docs)
    - Enables hypothesis testing (are comments closer to code than commits?)
    - Supports longitudinal studies (does alignment improve over time?)

    Parameters:
    - tokens_a: vocabulary from source A (e.g., commit messages)
    - tokens_b: vocabulary from source B (e.g., identifier names)
    - labels_a: cluster assignments for tokens_a
    - labels_b: cluster assignments for tokens_b

    Returns:
    - dict with keys:
      - vocab_overlap (float): Jaccard similarity of unique tokens
      - shared_vocab_size (int): number of tokens appearing in both
      - cluster_similarity (float): adjusted Rand index of cluster assignments for shared tokens

    Behavior:
    - vocab_overlap = |A ∩ B| / |A ∪ B| (Jaccard coefficient)
    - shared_vocab_size = |A ∩ B|
    - For cluster_similarity:
      - Find tokens appearing in both A and B
      - Compare their cluster assignments using adjusted Rand index
      - Returns 1.0 if perfectly aligned, 0.0 if random, negative if worse than random

    Examples:
    >>> tokens_a = ['user', 'login', 'auth', 'password']
    >>> tokens_b = ['user', 'login', 'database', 'query']
    >>> labels_a = np.array([0, 0, 0, 0])
    >>> labels_b = np.array([1, 1, 2, 2])
    >>> alignment = measure_alignment(tokens_a, tokens_b, labels_a, labels_b)
    >>> alignment['vocab_overlap']
    0.33...  # 2 shared / 6 total unique
    >>> alignment['shared_vocab_size']
    2

    Implementation hints:
    - Convert token lists to sets for overlap calculation
    - Jaccard = len(set_a & set_b) / len(set_a | set_b)
    - from sklearn.metrics import adjusted_rand_score
    - For cluster comparison, align tokens by building shared subset
    - Handle edge case: if no shared vocabulary, cluster_similarity = 0.0
    """
    if not tokens_a or not tokens_b:
        return {"vocab_overlap": 0.0, "shared_vocab_size": 0, "cluster_similarity": 0.0}

    set_a = set(tokens_a)
    set_b = set(tokens_b)

    shared_list = set_a & set_b
    shared_vocab_size = len(shared_list)
    vocab_overlap = shared_vocab_size / len(set_a | set_b)

    shared_labels_a = []
    shared_labels_b = []

    for i, value in enumerate(tokens_a):
        if value in shared_list:
            j = tokens_b.index(value)
            shared_labels_a.append(labels_a[i])
            shared_labels_b.append(labels_b[j])

    cluster_similarity = adjusted_rand_score(shared_labels_a, shared_labels_b) if len(shared_labels_a) > 1 and len(shared_labels_b) > 1 else 0.0

    return {"vocab_overlap": vocab_overlap, "shared_vocab_size": shared_vocab_size, "cluster_similarity": cluster_similarity}


def build_vocabulary_dataset(
        repo_path: str = ".",
        commit_limit: Optional[int] = None,
        file_limit: Optional[int] = None
) -> Dict[str, Any]:
    """Build complete vocabulary dataset from repository.

    Why this matters for MSR:
    - Integrates multiple data sources (commits, identifiers, comments)
    - Provides end-to-end pipeline from raw repo to analysis-ready data
    - Enables reproducible cross-repo studies

    Parameters:
    - repo_path: path to git repository (default ".")
    - commit_limit: max commits to process (default None = all)
    - file_limit: max source files to analyze (default None = all)

    Returns:
    - dict with keys:
      - commit_tokens: vocabulary from commit messages
      - identifier_tokens: vocabulary from code identifiers
      - comment_tokens: vocabulary from code comments
      - commit_labels: cluster assignments for commits
      - identifier_labels: cluster assignments for identifiers
      - comment_labels: cluster assignments for comments
      - alignment: dict of alignment metrics (commits vs identifiers, etc.)

    Behavior:
    - Query commits table for messages (use DI1 text normalization)
    - Extract identifiers from source files (reuse DA1 functions)
    - Extract comments from source files (use extract_comments_from_srcml)
    - Cluster each vocabulary separately
    - Measure pairwise alignment (commits-identifiers, commits-comments, identifiers-comments)

    Implementation hints:
    - Use db_utils to query commits table
    - Use srcml_runner and DA1 functions for code analysis
    - Filter to .py, .java, .cpp, .c, .js files only
    - Call extract_vocabulary on each text source
    - Call cluster_vocabulary(tokens, k=5) for each
    - Compute all pairwise alignments
    """
    if commit_limit is not None:
        commitData = db_utils.exec_get_all("""SELECT message FROM commits LIMIT %s""", (commit_limit,))
    else:
        commitData = db_utils.exec_get_all("""SELECT message FROM commits""")

    cleanedCommits = []
    for commit in commitData:
        cleanedCommits.append(normalize_text(commit[0]))

    if file_limit is not None:
        # handles file type selection and our file limit(only the first x files)
        identData = db_utils.exec_get_all("""SELECT name FROM code_identifiers WHERE (file_path LIKE '%%.py' OR file_path LIKE '%%.java'
                                          OR file_path LIKE '%%.cpp' OR file_path LIKE '%%.c' OR file_path LIKE '%%.js')
                                          AND file_path in (SELECT DISTINCT file_path FROM code_identifiers LIMIT %s)""", (file_limit,))
        commentData = db_utils.exec_get_all("""SELECT comment_text FROM code_comments WHERE (file_path LIKE '%%.py' OR file_path LIKE '%%.java'
                                          OR file_path LIKE '%%.cpp' OR file_path LIKE '%%.c' OR file_path LIKE '%%.js')
                                          AND file_path in (SELECT DISTINCT file_path FROM code_comments LIMIT %s)""", (file_limit,))
    else:
        identData = db_utils.exec_get_all("""SELECT name FROM code_identifiers""")
        commentData = db_utils.exec_get_all("""SELECT comment_text FROM code_comments""")

    cleanedIdentifiers = []
    for identifier in identData:
        cleanedIdentifiers.append(normalize_text(identifier[0]))

    cleanedComments = []
    for comment in commentData:
        cleanedComments.append(normalize_text(comment[0]))

    commit_tokens = extract_vocabulary(cleanedCommits)
    identifier_tokens = extract_vocabulary(cleanedIdentifiers)
    comment_tokens = extract_vocabulary(cleanedComments)

    commit_labels = cluster_vocabulary(commit_tokens, 5)
    identifier_labels = cluster_vocabulary(identifier_tokens, 5)
    comment_labels = cluster_vocabulary(comment_tokens, 5)

    alignmentDict = {"commits_identifiers": measure_alignment(commit_tokens, identifier_tokens, commit_labels[0], identifier_labels[0]),
                      "commits_comments": measure_alignment(commit_tokens, comment_tokens, commit_labels[0], comment_labels[0]),
                      "identifiers_comments": measure_alignment(identifier_tokens, comment_tokens, identifier_labels[0], comment_labels[0])}

    outputDict = {"commit_tokens": commit_tokens, "identifier_tokens": identifier_tokens, "comment_tokens": comment_tokens,
                  "commit_labels": commit_labels[0], "identifier_labels": identifier_labels[0], "comment_labels": comment_labels[0],
                  "alignment": alignmentDict}

    return outputDict


def inspect_clusters(
    tokens: List[str],
    labels: np.ndarray,
    top_n: int = 10,
) -> Dict[int, List[str]]:
    """Return and print the most frequent tokens for each cluster.

    Call this immediately after cluster_vocabulary() to read what each cluster
    contains.  The printed output lets you give each cluster a human-readable
    theme name (e.g., 'bug-fix', 'auth', 'database') that you can reference in
    your alignment report.  The returned dict will be the starting point for our
    next assignment on feature engineering.

    Parameters:
    - tokens: flat token list that was passed to cluster_vocabulary()
    - labels: cluster label array returned by cluster_vocabulary()
    - top_n: number of representative tokens to show per cluster (default 10)

    Returns:
    - dict mapping cluster_id (int) -> list of top_n tokens by frequency
    """
    from collections import Counter

    # Group every token occurrence by its cluster id
    cluster_tokens: Dict[int, List[str]] = {}
    for token, label in zip(tokens, labels):
        cluster_id = int(label)
        cluster_tokens.setdefault(cluster_id, []).append(token)

    result: Dict[int, List[str]] = {}
    for cluster_id in sorted(cluster_tokens):
        top = [tok for tok, _ in Counter(cluster_tokens[cluster_id]).most_common(top_n)]
        result[cluster_id] = top
        print(f"  Cluster {cluster_id}: {', '.join(top)}")

    return result