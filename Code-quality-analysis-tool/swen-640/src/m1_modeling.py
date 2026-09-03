import collections
from typing import Any, Dict, List, Optional, Tuple
import numpy as np
import sklearn
from matplotlib import pyplot as plt
import regex as re

from src import da2_vocabulary

# ---------------------------------------------------------------------------
# Commit type classification constants
# ---------------------------------------------------------------------------

COMMIT_TYPE_KEYWORDS: Dict[str, List[str]] = {
    "fix": ["fix", "bug", "patch", "hotfix", "error", "repair",
            "correct", "typo", "broke", "broken", "revert", "crash", "fail"],
    "feature": ["feat", "add", "new", "implement", "introduce", "create",
                "support", "feature", "initial", "allow", "enable"],
    "refactor": ["refactor", "cleanup", "clean", "reorganize", "rename",
                 "restructure", "move", "extract", "simplify", "rewrite", "split"],
    "test": ["test", "tests", "spec", "specs", "assert", "coverage",
             "mock", "pytest", "unittest"],
    "docs": ["doc", "docs", "readme", "changelog", "documentation",
             "guide", "docstring"],
}

_TYPE_PRIORITY = ["fix", "feature", "refactor", "test", "docs"]


def label_commit(message: str) -> str:
    """Classify a commit message into one of six commit types.

    Why this matters for MSR:
    - Commit type is a widely studied metadata attribute in empirical SE
    - Heuristic labeling enables supervised learning without manual annotation
    - Imperfect labels motivate precision/recall analysis vs. keyword matching
    - Studying label quality is itself a valid research contribution

    Parameters:
    - message: raw commit message string

    Returns:
    - One of: "fix", "feature", "refactor", "test", "docs", "other"

    Behavior:
    - Lowercase the message before matching
    - Check types in priority order: fix > feature > refactor > test > docs
    - Use whole-word matching (\b...\b) to avoid false positives -- check regex docs if you don't understand \b
    - Return "other" if no keywords match or message is empty/whitespace

    Examples:
    >>> label_commit("Fix null pointer bug in authentication")
    'fix'
    >>> label_commit("Add new user registration endpoint")
    'feature'
    >>> label_commit("Update README with installation instructions")
    'docs'
    >>> label_commit("")
    'other'

    Implementation hints:
    - message.lower() for case normalization
    - re.search(r'\b' + re.escape(kw) + r'\b', text) for whole-word matching
    - Iterate _TYPE_PRIORITY; return the first matching label. That is, if a commit has multiple labels, we pick the first that matches.
    - COMMIT_TYPE_KEYWORDS[label] gives the keyword list for each label
    """
    text = message.lower()
    for key in _TYPE_PRIORITY:
        for keyword in COMMIT_TYPE_KEYWORDS[key]:
            if re.search(r'\b' + re.escape(keyword) + r'\b', text):
                return key
    return "other"


def build_commit_features(
        tokens: List[str],
        token_to_cluster: Dict[str, int],
        k: int,
        identifier_tokens: Optional[List[str]] = None,
        comment_tokens: Optional[List[str]] = None,
) -> np.ndarray:
    """Build a numerical feature vector for a single commit.

    Why this matters for MSR:
    - Transforms raw text into structured numerical features for sklearn
    - Cluster membership captures semantic themes (e.g., 'bug-fix vocabulary')
    - Cross-source overlap reveals how closely commits relate to code artifacts
    - Type-token ratio captures lexical richness of commit messages

    Parameters:
    - tokens: normalized tokens for this commit (from extract_vocabulary)
    - token_to_cluster: dict mapping token -> cluster_id (from DA2 clustering)
    - k: number of clusters (must match DA2 k)
    - identifier_tokens: vocabulary from code identifiers (for overlap feature)
    - comment_tokens: vocabulary from code comments (for overlap feature)

    Returns:
    - np.ndarray of shape (k + 4,) with these features in order:
        [cluster_0_frac, cluster_1_frac, ..., cluster_{k-1}_frac,
         log_token_count, type_token_ratio, id_overlap, comment_overlap]

    Feature descriptions:
    - cluster_i_frac: fraction of *all* commit tokens assigned to cluster i.
        Tokens not in token_to_cluster are not assigned to any cluster, so
        they lower the cluster fractions without contributing to any numerator.
        Fracs sum to 1.0 only when every token is in token_to_cluster,
        and to less than 1.0 when any tokens are out-of-vocabulary.
    - log_token_count: log(1 + len(tokens)), captures message verbosity.
        np.log1p handles the empty-list case (returns 0.0).
    - type_token_ratio: len(unique tokens) / len(tokens), lexical diversity.
        0.0 for an empty token list.
    - id_overlap: Jaccard similarity of commit token set vs. identifier token set.
        |set(tokens) ∩ set(id_tokens)| / |set(tokens) ∪ set(id_tokens)|
        0.0 if both sets are empty.
    - comment_overlap: same calculation but vs. comment tokens.

    Examples:
    >>> token_to_cluster = {"fix": 0, "bug": 0, "auth": 1}
    >>> tokens = ["fix", "bug", "auth"]
    >>> features = build_commit_features(tokens, token_to_cluster, k=2)
    >>> features.shape
    (6,)
    >>> round(features[0], 4)   # cluster_0_frac: 2 of 3 tokens in cluster 0
    0.6667
    >>> round(features[1], 4)   # cluster_1_frac: 1 of 3 tokens in cluster 1
    0.3333

    Implementation hints:
    - np.zeros(k + 4, dtype=float) to initialise the output
    - Use collections.Counter or a loop to count tokens per cluster
    - Only increment a cluster's counter if the token is in token_to_cluster
    - Divide cluster counts by len(tokens) (total tokens, not just matched ones)
    - np.log1p(n) == log(1 + n)
    - For Jaccard: use Python set operations (&, |)
    - Return zeros for empty token list (don't divide by zero)
    """
    output = np.zeros(k + 4, dtype=float)
    for i in range(k):
        occurrences = 0
        for token in tokens:
            if token in token_to_cluster and token_to_cluster[token] == i:
                occurrences += 1
        output[i] = occurrences / len(tokens) if len(tokens) > 0 else 0.0
    output[k] = np.log1p(len(tokens))
    output[k + 1] = len(set(tokens)) / len(tokens) if len(tokens) > 0 else 0.0
    output[k + 2] = len(set(tokens) & set(identifier_tokens)) / len(
        set(tokens) | set(identifier_tokens)) if identifier_tokens and not (
                (len(set(identifier_tokens)) <= 0) and (len(set(tokens)) <= 0)) else 0.0
    output[k + 3] = len(set(tokens) & set(comment_tokens)) / len(
        set(tokens) | set(comment_tokens)) if comment_tokens and not (
                (len(set(comment_tokens)) <= 0) and (len(set(tokens))) <= 0) else 0.0

    return output


def build_feature_matrix(
        commit_records: List[Dict[str, str]],
        k: int = 5,
        token_to_cluster: Optional[Dict[str, int]] = None,
        identifier_tokens: Optional[List[str]] = None,
        comment_tokens: Optional[List[str]] = None,
) -> Tuple[np.ndarray, List[str], List[str]]:
    """Build feature matrix and label vector for all commits.

    Why this matters for MSR:
    - Translates the entire commit corpus into a form usable by sklearn
    - Connects DA2 vocabulary analysis to supervised classification
    - Enables reproducible feature engineering pipelines across repos

    Parameters:
    - commit_records: list of dicts, each with at least a "message" key.
        Use load_commit_data() (provided) to obtain this from the DB.
    - k: number of vocabulary clusters — should match the k used in DA2
    - token_to_cluster: pre-built token->cluster mapping from DA2 (you pass your DA2 data into here).
        If None, re-run your DA2 clustering via `extract_vocabulary` and `cluster_vocabulary` inline with this function
    - identifier_tokens: full identifier token list from DB (for overlap)
    - comment_tokens: full comment token list from DB (for overlap)

    Returns:
    - X: np.ndarray of shape (n_commits, k + 4) — feature matrix
    - y: List[str] of length n_commits — commit type labels
    - feature_names: List[str] of length k + 4 — column names for X

    Feature names:
    ["cluster_0_frac", ..., "cluster_{k-1}_frac",
     "log_token_count", "type_token_ratio", "id_overlap", "comment_overlap"]

    Connecting to DA2:
    If you cannot easily pass token_to_cluster into this function from main, you can just re-run da2's code here.
    Build token_to_cluster from your DA2 cluster_vocabulary() results:

        commit_tokens = da2_vocabulary.extract_vocabulary(commit_messages)
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(commit_tokens, k=5)
        token_to_cluster = {t: int(l) for t, l in zip(commit_tokens, labels)}

    Then pass it here so your M1 features directly reflect your DA2 clusters.

    Examples:
    >>> records = [{"message": "fix authentication bug"},
    ...            {"message": "add user registration"}]
    >>> t2c = {"fix": 0, "bug": 0, "auth": 0, "add": 1, "user": 1}
    >>> X, y, names = build_feature_matrix(records, k=2, token_to_cluster=t2c)
    >>> X.shape
    (2, 6)
    >>> y
    ['fix', 'feature']

    Implementation hints:
    - if you are not passing DA2's data in as a parameter
        - from src.da2_vocabulary import extract_vocabulary, cluster_vocabulary
        - Call extract_vocabulary([msg]) for each commit individually (not pooled)
        - If token_to_cluster is None: pool all tokens, cluster them, build dict
    - Stack build_commit_features() results with np.array([...]) -- that is, you can build an np.array with the results of build_commit_features i.e., via looping.
    - Use label_commit(msg) for each message to get y
    - Return np.zeros((0, k + 4)), [], feature_names for empty input
    """
    n_commits = len(commit_records) if commit_records is not None else 0
    x = np.zeros((n_commits, k + 4))
    y = []
    feature_names = []

    if n_commits == 0:
        return x, y, feature_names

    if token_to_cluster is None:
        commit_messages = []
        for commit in commit_records:
            commit_messages.append(commit["message"])
        commit_tokens = da2_vocabulary.extract_vocabulary(commit_messages)
        labels, vectors, model = da2_vocabulary.cluster_vocabulary(commit_tokens, k)
        token_to_cluster = {t: int(l) for t, l in zip(commit_tokens, labels)}

    i = 0
    for commit in commit_records:
        msg = commit["message"]
        vocab = da2_vocabulary.extract_vocabulary([msg])
        x[i] = np.array(build_commit_features(vocab, token_to_cluster, k, identifier_tokens, comment_tokens))
        y.append(label_commit(msg))
        i += 1

    for j in range(k):
        feature_names.append(f"cluster_{j}_frac")
    feature_names.append("log_token_count")
    feature_names.append("type_token_ratio")
    feature_names.append("id_overlap")
    feature_names.append("comment_overlap")

    return x, y, feature_names


def split_dataset(
        X: np.ndarray,
        y: List[str],
        test_size: float = 0.2,
        random_state: int = 42,
) -> Tuple[np.ndarray, np.ndarray, List[str], List[str]]:
    """Split features and labels into stratified training and test sets.

    Why this matters for MSR:
    - Held-out test data prevents overfitting evaluation (information leakage)
    - Stratification preserves class proportions — critical for imbalanced data
    - Commit type distributions are typically skewed ('fix' >> 'docs')
    - Reproducible splits (random_state) allow fair comparison of models

    Parameters:
    - X: feature matrix of shape (n_samples, n_features)
    - y: label list of length n_samples
    - test_size: fraction of data for testing (default 0.2 = 20%)
    - random_state: random seed for reproducibility (default 42)

    Returns:
    - (X_train, X_test, y_train, y_test)

    Behavior:
    - Attempt stratified split (preserves class proportions)
    - If any class has < 2 samples, fall back to non-stratified and print a warning

    Examples:
    >>> X = np.random.rand(100, 9)
    >>> y = ["fix"] * 50 + ["feature"] * 30 + ["other"] * 20
    >>> X_train, X_test, y_train, y_test = split_dataset(X, y)
    >>> len(X_train) + len(X_test)
    100

    Implementation hints:
    - from sklearn.model_selection import train_test_split
    - from collections import Counter
    - Check: all(c >= 2 for c in Counter(y).values())
    - If check fails: call train_test_split with stratify=None and print warning
    - Return (X_train, X_test, y_train, y_test) in that order
    """

    if all(c >= 2 for c in collections.Counter(y).values()):
        return sklearn.model_selection.train_test_split(X, y, test_size=test_size, random_state=random_state, stratify=y)
    else:
        print("Warning: At least one class has <2 samples, falling back to non stratified split")
        return sklearn.model_selection.train_test_split(X, y, test_size=test_size, random_state=random_state, stratify=None)


def train_classifier(
        X_train: np.ndarray,
        y_train: List[str],
        model_type: str = "decision_tree",
        max_depth: Optional[int] = None,
) -> Any:
    """Train a decision tree or random forest classifier.

    Why this matters for MSR:
    - Decision trees are interpretable: each split is a human-readable rule
    - Random forests average many trees, trading interpretability for accuracy
    - Feature importances from either model connect predictions to DA2 clusters by showing which was most helpful
    - sklearn's consistent fit/predict API makes swapping models trivial

    Parameters:
    - X_train: training feature matrix of shape (n_samples, n_features)
    - y_train: training labels of length n_samples
    - model_type: "decision_tree" or "random_forest"
    - max_depth: maximum tree depth (None = unlimited; try 5 to limit overfitting)

    Returns:
    - Fitted sklearn classifier (has .predict() and .feature_importances_)

    Raises:
    - ValueError if model_type is not "decision_tree" or "random_forest"

    Examples:
    >>> model = train_classifier(X_train, y_train, model_type="decision_tree")
    >>> model.predict(X_test[:3])
    array(['fix', 'feature', 'other'], dtype=object)

    Implementation hints:
    - from sklearn.tree import DecisionTreeClassifier
    - from sklearn.ensemble import RandomForestClassifier
    - Use random_state=42 for reproducibility in both models
    - RandomForestClassifier: n_estimators=100 is a good default
    - Call model.fit(X_train, y_train) before returning
    - Raise ValueError with a helpful message for unknown model_type
    """
    if model_type == "decision_tree":
        model = sklearn.tree.DecisionTreeClassifier(max_depth=max_depth, random_state=42)
    elif model_type == "random_forest":
        model = sklearn.ensemble.RandomForestClassifier(max_depth=max_depth, random_state=42, n_estimators=100)
    else:
        raise ValueError("model_type must be 'decision_tree' or 'random_forest'")
    return model.fit(X_train, y_train)


def evaluate_model(
        model: Any,
        X_test: np.ndarray,
        y_test: List[str],
) -> Dict[str, Any]:
    """Evaluate a trained classifier and return standard metrics.

    Why this matters for MSR:
    - Accuracy alone is misleading when classes are imbalanced
    - Per-class precision/recall/F1 reveals which commit types are hardest to predict
    - The confusion matrix shows which types the model most often confuses
    - These metrics are standard in empirical software engineering papers

    Parameters:
    - model: fitted sklearn classifier with a .predict() method
    - X_test: test feature matrix
    - y_test: true test labels

    Returns:
    - dict with keys:
        - "accuracy": float — overall fraction of correct predictions
        - "classification_report": dict — per-class precision/recall/F1
            (sklearn classification_report output with output_dict=True)
        - "confusion_matrix": np.ndarray — shape (n_classes, n_classes)
        - "class_names": List[str] — sorted unique labels from y_test
        - "y_pred": np.ndarray — model predictions on X_test

    Examples:
    >>> results = evaluate_model(model, X_test, y_test)
    >>> results["accuracy"]
    0.73
    >>> results["classification_report"]["fix"]["f1-score"]
    0.81

    Implementation hints:
    - from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
    - y_pred = model.predict(X_test)
    - class_names = sorted(set(y_test))
    - classification_report(..., output_dict=True, zero_division=0)
    - confusion_matrix(..., labels=class_names)
    """
    y_pred = model.predict(X_test)
    class_names = sorted(set(y_test))
    accuracy = sklearn.metrics.accuracy_score(y_test, y_pred)
    classification = sklearn.metrics.classification_report(y_test, y_pred, output_dict=True, zero_division=0)
    confusion = sklearn.metrics.confusion_matrix(y_test, y_pred, labels=class_names)

    return {"accuracy": accuracy, "classification_report": classification, "confusion_matrix": confusion, "class_names": class_names, "y_pred": y_pred}


def plot_feature_importance(
        model: Any,
        feature_names: List[str],
        output_path: Optional[str] = None,
) -> None:
    """Bar chart of feature importances, sorted descending.

    **Provided — copy this into your m1_modeling.py as-is.**
    """
    importances = model.feature_importances_
    indices = np.argsort(importances)[::-1]

    plt.figure(figsize=(10, 6))
    plt.bar(
        range(len(importances)),
        importances[indices],
        color='steelblue',
        edgecolor='black',
        linewidth=0.5,
    )
    plt.xticks(
        range(len(importances)),
        [feature_names[i] for i in indices],
        rotation=45,
        ha='right',
        fontsize=9,
    )
    plt.xlabel("Feature", fontsize=12)
    plt.ylabel("Importance", fontsize=12)
    plt.title("Feature Importances", fontsize=14, fontweight='bold')
    plt.grid(axis='y', alpha=0.3)
    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        plt.close()
    else:
        plt.show()


def plot_confusion_matrix(
        y_true: List[str],
        y_pred: Any,
        class_names: List[str],
        output_path: Optional[str] = None,
) -> None:
    """Annotated heatmap of predicted vs. true commit type labels.

    **Provided — copy this into your m1_modeling.py as-is.**
    """
    from sklearn.metrics import confusion_matrix as sk_cm

    cm = sk_cm(y_true, y_pred, labels=class_names)

    plt.figure(figsize=(8, 6))
    im = plt.imshow(cm, interpolation='nearest', cmap='Blues')
    plt.colorbar(im, label='Count')
    plt.title('Confusion Matrix', fontsize=14, fontweight='bold')
    plt.xlabel('Predicted Label', fontsize=12)
    plt.ylabel('True Label', fontsize=12)

    tick_marks = np.arange(len(class_names))
    plt.xticks(tick_marks, class_names, rotation=45, ha='right', fontsize=9)
    plt.yticks(tick_marks, class_names, fontsize=9)

    thresh = cm.max() / 2.0
    for i in range(cm.shape[0]):
        for j in range(cm.shape[1]):
            plt.text(
                j, i, str(cm[i, j]),
                ha='center', va='center',
                color='white' if cm[i, j] > thresh else 'black',
                fontsize=10,
            )

    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        plt.close()
    else:
        plt.show()


def load_commit_data(commit_limit: Optional[int] = None) -> List[Dict[str, str]]:
    """Load commit records from the database.

    **Provided — copy this into your m1_modeling.py as-is.**

    Returns a list of {"message": str} dicts, ready for build_feature_matrix().
    """
    from src import db_utils

    query = (
        "SELECT message FROM commits "
        "WHERE message IS NOT NULL AND message <> ''"
    )
    if commit_limit:
        query += f" LIMIT {int(commit_limit)}"
    query += ";"

    try:
        rows = db_utils.exec_get_all(query)
    except Exception as exc:
        print(f"[load_commit_data] DB query failed: {exc}")
        return []

    return [
        {"message": str(row[0])}
        for row in rows
        if row[0] and str(row[0]).strip()
    ]
