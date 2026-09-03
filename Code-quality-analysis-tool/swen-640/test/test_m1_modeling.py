"""
Tests for M1: Predictive Modeling — commit type classification.
"""
import numpy as np
import pytest

from src import m1_modeling


# ============================================================================
# TestLabelCommit — 10 tests
# ============================================================================

class TestLabelCommit:
    """Tests for heuristic commit type labeling."""

    def test_fix_keyword(self):
        """'fix' keyword -> 'fix'."""
        assert m1_modeling.label_commit("fix authentication bug") == "fix"

    def test_bug_keyword(self):
        """'bug' keyword also maps to fix."""
        assert m1_modeling.label_commit("patch null pointer bug in login") == "fix"

    def test_feature_add(self):
        """'add' keyword -> 'feature'."""
        assert m1_modeling.label_commit("add new login endpoint") == "feature"

    def test_feature_implement(self):
        """'implement' keyword -> 'feature'."""
        assert m1_modeling.label_commit("implement user registration") == "feature"

    def test_refactor(self):
        """'refactor' keyword -> 'refactor'."""
        assert m1_modeling.label_commit("refactor authentication service module") == "refactor"

    def test_test_keyword(self):
        """'test' keyword -> 'test'."""
        assert m1_modeling.label_commit("write test coverage for the parser") == "test"

    def test_docs_readme(self):
        """'readme' keyword -> 'docs'."""
        assert m1_modeling.label_commit("update readme with installation guide") == "docs"

    def test_other(self):
        """Unrecognized message -> 'other'."""
        assert m1_modeling.label_commit("wip random changes everywhere") == "other"

    def test_empty_string(self):
        """Empty string -> 'other'."""
        assert m1_modeling.label_commit("") == "other"

    def test_case_insensitive(self):
        """Matching is case-insensitive."""
        assert m1_modeling.label_commit("FIX: Critical Bug In Production") == "fix"


# ============================================================================
# TestBuildCommitFeatures — 10 tests
# ============================================================================

class TestBuildCommitFeatures:
    """Tests for per-commit feature vector construction."""

    def setup_method(self):
        """Shared fixture: small hand-crafted cluster map, avoids spaCy."""
        self.k = 3
        self.token_to_cluster = {
            # cluster 0: bug-fix vocabulary
            "fix": 0, "bug": 0, "patch": 0, "error": 0,
            # cluster 1: feature vocabulary
            "add": 1, "implement": 1, "new": 1, "create": 1,
            # cluster 2: refactor vocabulary
            "refactor": 2, "clean": 2, "rename": 2,
        }

    def test_returns_ndarray(self):
        """Result is a numpy array."""
        features = m1_modeling.build_commit_features(
            ["fix", "bug"], self.token_to_cluster, self.k
        )
        assert isinstance(features, np.ndarray)

    def test_shape_k_plus_4(self):
        """Feature vector length is k + 4."""
        features = m1_modeling.build_commit_features(
            ["fix", "bug"], self.token_to_cluster, self.k
        )
        assert features.shape == (self.k + 4,)

    def test_shape_different_k(self):
        """Shape adapts when k changes."""
        k = 5
        token_to_cluster = {f"tok{i}": i % k for i in range(10)}
        tokens = [f"tok{i}" for i in range(10)]
        features = m1_modeling.build_commit_features(tokens, token_to_cluster, k)
        assert features.shape == (k + 4,)

    def test_cluster_fracs_sum_to_one_when_all_known(self):
        """Cluster fractions sum to 1.0 when all tokens are in the map."""
        tokens = ["fix", "bug", "add"]   # 2 in cluster 0, 1 in cluster 1
        features = m1_modeling.build_commit_features(
            tokens, self.token_to_cluster, self.k
        )
        assert abs(sum(features[:self.k]) - 1.0) < 1e-9

    def test_cluster_fracs_less_than_one_with_unknown_tokens(self):
        """Unknown tokens lower the sum of cluster fractions below 1.0."""
        tokens = ["fix", "unknownxyz", "anothermissing"]
        features = m1_modeling.build_commit_features(
            tokens, self.token_to_cluster, self.k
        )
        assert sum(features[:self.k]) < 1.0

    def test_empty_tokens_returns_zeros(self):
        """Empty token list produces an all-zero feature vector."""
        features = m1_modeling.build_commit_features(
            [], self.token_to_cluster, self.k
        )
        assert np.all(features == 0.0)

    def test_log_token_count_positive_for_nonempty(self):
        """log_token_count feature is > 0 for a non-empty token list."""
        features = m1_modeling.build_commit_features(
            ["fix", "bug"], self.token_to_cluster, self.k
        )
        assert features[self.k] > 0.0

    def test_type_token_ratio_bounded(self):
        """type_token_ratio is in [0.0, 1.0]."""
        tokens = ["fix", "bug", "fix", "add"]
        features = m1_modeling.build_commit_features(
            tokens, self.token_to_cluster, self.k
        )
        assert 0.0 <= features[self.k + 1] <= 1.0

    def test_id_overlap_computed(self):
        """id_overlap is nonzero when commit and identifier tokens share words."""
        features = m1_modeling.build_commit_features(
            ["fix", "bug"],
            self.token_to_cluster,
            self.k,
            identifier_tokens=["fix", "auth", "session"],
        )
        assert features[self.k + 2] > 0.0

    def test_all_values_finite(self):
        """No NaN or Inf values in the feature vector."""
        features = m1_modeling.build_commit_features(
            ["fix", "bug", "add", "refactor"],
            self.token_to_cluster,
            self.k,
            identifier_tokens=["fix", "auth"],
            comment_tokens=["bug", "issue"],
        )
        assert np.all(np.isfinite(features))


# ============================================================================
# TestBuildFeatureMatrix — 8 tests
# ============================================================================

class TestBuildFeatureMatrix:
    """Tests for building X, y, and feature_names from commit records."""

    def setup_method(self):
        """Shared fixture: small controlled inputs, bypasses spaCy clustering."""
        self.k = 2
        self.token_to_cluster = {
            "fix": 0, "bug": 0, "auth": 0,
            "add": 1, "user": 1, "new": 1,
        }
        self.records = [
            {"message": "fix authentication bug"},
            {"message": "add new user registration"},
        ]

    def test_returns_tuple_of_three(self):
        """Return value is a 3-tuple."""
        result = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert len(result) == 3

    def test_X_is_ndarray(self):
        """X is a numpy array."""
        X, y, names = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert isinstance(X, np.ndarray)

    def test_X_shape(self):
        """X shape is (n_commits, k + 4)."""
        X, y, names = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert X.shape == (len(self.records), self.k + 4)

    def test_y_length_matches_commits(self):
        """Label vector length matches number of commit records."""
        X, y, names = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert len(y) == len(self.records)

    def test_feature_names_length(self):
        """feature_names length matches number of columns in X."""
        X, y, names = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert len(names) == X.shape[1]

    def test_y_contains_valid_labels(self):
        """All labels are valid commit type strings."""
        valid = {"fix", "feature", "refactor", "test", "docs", "other"}
        _, y, _ = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert all(label in valid for label in y)

    def test_empty_records_returns_zero_matrix(self):
        """Empty input -> X with 0 rows and k+4 columns."""
        X, y, names = m1_modeling.build_feature_matrix(
            [], k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert X.shape == (0, self.k + 4)
        assert y == []

    def test_all_X_values_finite(self):
        """No NaN or Inf values anywhere in X."""
        X, y, names = m1_modeling.build_feature_matrix(
            self.records, k=self.k, token_to_cluster=self.token_to_cluster
        )
        assert np.all(np.isfinite(X))


# ============================================================================
# TestSplitDataset — 6 tests
# ============================================================================

class TestSplitDataset:
    """Tests for stratified train/test splitting."""

    def setup_method(self):
        """100 samples across 3 classes — all classes have >= 2 samples."""
        np.random.seed(0)
        self.X = np.random.rand(100, 9)
        self.y = ["fix"] * 50 + ["feature"] * 30 + ["other"] * 20

    def test_returns_four_items(self):
        """Return value is a 4-tuple."""
        result = m1_modeling.split_dataset(self.X, self.y)
        assert len(result) == 4

    def test_test_size_approximately_correct(self):
        """Test set size is close to the requested fraction."""
        X_train, X_test, y_train, y_test = m1_modeling.split_dataset(
            self.X, self.y, test_size=0.2
        )
        actual_ratio = len(X_test) / len(self.X)
        assert abs(actual_ratio - 0.2) < 0.05

    def test_stratified_all_classes_in_test(self):
        """Stratification ensures every class appears in the test set."""
        X_train, X_test, y_train, y_test = m1_modeling.split_dataset(
            self.X, self.y, test_size=0.2
        )
        assert "fix" in y_test
        assert "feature" in y_test
        assert "other" in y_test

    def test_reproducible_with_same_seed(self):
        """Same random_state produces identical splits."""
        X_train1, X_test1, y_train1, y_test1 = m1_modeling.split_dataset(
            self.X, self.y, random_state=42
        )
        X_train2, X_test2, y_train2, y_test2 = m1_modeling.split_dataset(
            self.X, self.y, random_state=42
        )
        np.testing.assert_array_equal(X_train1, X_train2)
        np.testing.assert_array_equal(X_test1, X_test2)

    def test_sizes_add_up(self):
        """Train size + test size equals total dataset size."""
        X_train, X_test, y_train, y_test = m1_modeling.split_dataset(
            self.X, self.y
        )
        assert len(X_train) + len(X_test) == len(self.X)

    def test_feature_dimension_preserved(self):
        """Number of columns is unchanged after splitting."""
        X_train, X_test, y_train, y_test = m1_modeling.split_dataset(
            self.X, self.y
        )
        assert X_train.shape[1] == self.X.shape[1]
        assert X_test.shape[1] == self.X.shape[1]


# ============================================================================
# TestTrainClassifier — 8 tests
# ============================================================================

class TestTrainClassifier:
    """Tests for decision tree and random forest training."""

    def setup_method(self):
        """Simple linearly separable data — models should learn it easily."""
        np.random.seed(42)
        n = 60
        # Two clearly separated clusters, three classes
        X_fix      = np.random.rand(20, 5) + np.array([0, 0, 0, 0, 0])
        X_feature  = np.random.rand(20, 5) + np.array([5, 5, 5, 5, 5])
        X_other    = np.random.rand(20, 5) + np.array([10, 10, 10, 10, 10])
        self.X_train = np.vstack([X_fix, X_feature, X_other])
        self.y_train = ["fix"] * 20 + ["feature"] * 20 + ["other"] * 20

        X_fix_t    = np.random.rand(5, 5) + np.array([0, 0, 0, 0, 0])
        X_feat_t   = np.random.rand(5, 5) + np.array([5, 5, 5, 5, 5])
        X_other_t  = np.random.rand(5, 5) + np.array([10, 10, 10, 10, 10])
        self.X_test = np.vstack([X_fix_t, X_feat_t, X_other_t])
        self.y_test = ["fix"] * 5 + ["feature"] * 5 + ["other"] * 5

    def test_decision_tree_returns_fitted_model(self):
        """Decision tree model has a predict method after training."""
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train, model_type="decision_tree"
        )
        assert hasattr(model, "predict")

    def test_random_forest_returns_fitted_model(self):
        """Random forest model has a predict method after training."""
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train, model_type="random_forest"
        )
        assert hasattr(model, "predict")

    def test_unknown_model_type_raises_value_error(self):
        """Unrecognized model_type raises ValueError."""
        with pytest.raises(ValueError):
            m1_modeling.train_classifier(
                self.X_train, self.y_train, model_type="svm"
            )

    def test_predict_returns_array(self):
        """model.predict() returns an array-like object."""
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train, model_type="decision_tree"
        )
        preds = model.predict(self.X_test)
        assert hasattr(preds, "__len__")

    def test_predict_length_matches_test_set(self):
        """Prediction count equals number of test samples."""
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train, model_type="decision_tree"
        )
        preds = model.predict(self.X_test)
        assert len(preds) == len(self.X_test)

    def test_predict_values_are_valid_labels(self):
        """All predicted labels are valid commit type strings."""
        valid = {"fix", "feature", "refactor", "test", "docs", "other"}
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train, model_type="decision_tree"
        )
        preds = model.predict(self.X_test)
        assert all(p in valid for p in preds)

    def test_max_depth_respected(self):
        """Decision tree honours the max_depth parameter."""
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train,
            model_type="decision_tree", max_depth=3
        )
        assert model.max_depth == 3

    def test_model_has_feature_importances(self):
        """Fitted model exposes feature_importances_ (needed for plots)."""
        model = m1_modeling.train_classifier(
            self.X_train, self.y_train, model_type="random_forest"
        )
        assert hasattr(model, "feature_importances_")
        assert len(model.feature_importances_) == self.X_train.shape[1]


# ============================================================================
# TestEvaluateModel — 6 tests
# ============================================================================

class TestEvaluateModel:
    """Tests for model evaluation metrics."""

    def setup_method(self):
        """Train a small decision tree for reuse across tests."""
        from sklearn.tree import DecisionTreeClassifier

        np.random.seed(0)
        X_fix     = np.random.rand(15, 4) + np.array([0, 0, 0, 0])
        X_feature = np.random.rand(15, 4) + np.array([5, 5, 5, 5])
        X_other   = np.random.rand(10, 4) + np.array([10, 10, 10, 10])
        X_train = np.vstack([X_fix, X_feature, X_other])
        y_train = ["fix"] * 15 + ["feature"] * 15 + ["other"] * 10

        self.model = DecisionTreeClassifier(random_state=42)
        self.model.fit(X_train, y_train)

        # Test set from the same distributions
        self.X_test = np.vstack([
            np.random.rand(5, 4) + np.array([0, 0, 0, 0]),
            np.random.rand(5, 4) + np.array([5, 5, 5, 5]),
            np.random.rand(4, 4) + np.array([10, 10, 10, 10]),
        ])
        self.y_test = ["fix"] * 5 + ["feature"] * 5 + ["other"] * 4

    def test_returns_dict(self):
        """evaluate_model returns a dict."""
        result = m1_modeling.evaluate_model(self.model, self.X_test, self.y_test)
        assert isinstance(result, dict)

    def test_accuracy_key_in_range(self):
        """'accuracy' key is present and its value is in [0.0, 1.0]."""
        result = m1_modeling.evaluate_model(self.model, self.X_test, self.y_test)
        assert "accuracy" in result
        assert 0.0 <= result["accuracy"] <= 1.0

    def test_confusion_matrix_key_present(self):
        """'confusion_matrix' key is present and is a 2D array."""
        result = m1_modeling.evaluate_model(self.model, self.X_test, self.y_test)
        assert "confusion_matrix" in result
        assert result["confusion_matrix"].ndim == 2

    def test_class_names_key_present(self):
        """'class_names' key is present and contains strings."""
        result = m1_modeling.evaluate_model(self.model, self.X_test, self.y_test)
        assert "class_names" in result
        assert all(isinstance(c, str) for c in result["class_names"])

    def test_y_pred_key_present(self):
        """'y_pred' key is present with the same length as y_test."""
        result = m1_modeling.evaluate_model(self.model, self.X_test, self.y_test)
        assert "y_pred" in result
        assert len(result["y_pred"]) == len(self.y_test)

    def test_perfect_classifier_accuracy_one(self):
        """A model evaluated on its own training data achieves accuracy 1.0."""
        from sklearn.tree import DecisionTreeClassifier

        X = np.array([[1, 0], [0, 1], [1, 0], [0, 1]])
        y = ["fix", "feature", "fix", "feature"]

        model = DecisionTreeClassifier(random_state=42)
        model.fit(X, y)

        result = m1_modeling.evaluate_model(model, X, y)
        assert result["accuracy"] == pytest.approx(1.0)
