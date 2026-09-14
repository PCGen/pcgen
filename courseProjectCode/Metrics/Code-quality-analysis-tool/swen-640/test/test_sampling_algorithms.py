from math import ceil

from src import sampling_algorithms


def test_sample_uniform_reproducible():
    items = list(range(100))
    a = sampling_algorithms.sample_uniform(items, 10, seed=42)
    b = sampling_algorithms.sample_uniform(items, 10, seed=42)
    assert a == b
    assert len(a) == 10


def test_sample_stratified_n_per_group():
    # items are tuples (group, id)
    items = [(g, i) for g in range(3) for i in range(5)]  # 3 groups of size 5
    # key returns group
    res = sampling_algorithms.sample_stratified(items, key=lambda x: x[0], n=2, seed=0)
    # expect 2 per group -> 6 total
    assert len(res) == 6
    groups = {g: [] for g in range(3)}
    for g, i in res:
        groups[g].append(i)
    for g in groups:
        assert len(groups[g]) == 2


def test_sample_stratified_frac():
    items = [(g, i) for g in range(4) for i in range(3)]  # 4 groups of size 3
    res = sampling_algorithms.sample_stratified(items, key=lambda x: x[0], frac=0.5, seed=1)
    # frac=0.5 of 3 is 1 (int), but ensure at least 1 when frac>0
    assert len(res) >= 4


def test_sample_systematic():
    items = list(range(20))
    res = sampling_algorithms.sample_systematic(items, step=5, seed=2)
    # start determined by seed; check positions are step apart
    idxs = [items.index(x) for x in res]
    if len(idxs) > 1:
        diffs = [b - a for a, b in zip(idxs, idxs[1:])]
        assert all(d == 5 for d in diffs)


def test_sample_size_proportion_basic():
    n = sampling_algorithms.sample_size_proportion(None, p=0.5, margin=0.05, z=1.96)
    assert n == 385


def test_sample_size_proportion_fpc():
    # With small N, FPC reduces required sample
    N = 500
    n_no_fpc = sampling_algorithms.sample_size_proportion(None, p=0.5, margin=0.05, z=1.96)
    n_fpc = sampling_algorithms.sample_size_proportion(N, p=0.5, margin=0.05, z=1.96)
    assert n_fpc < n_no_fpc
    assert n_fpc <= N


def test_sample_size_mean():
    n = sampling_algorithms.sample_size_mean(sigma=1.0, margin=0.1, z=1.96)
    # n0 = (1.96^2 * 1) / 0.01 = 384.16 -> ceil 385
    assert n == 385
