from __future__ import annotations

import math
from math import ceil
from typing import Any, Callable, Dict, List, Optional, Sequence, TypeVar
import random

T = TypeVar("T")


# ---- Uniform Sampling ----

def sample_uniform(items: Sequence[T], k: int, seed: Optional[int] = None) -> List[T]:
    """Draw k items uniformly at random without replacement.

    Why this matters for MSR:
    - Random sampling is the foundation of statistical inference
    - Reproducibility requires deterministic seeds for replication studies
    - Many research tasks need unbiased subsets of large datasets

    Parameters:
    - items: the population to sample from
    - k: number of items to select
    - seed: random seed for reproducibility (None = non-deterministic)

    Returns:
    - List of k sampled items (or all items if k >= len(items))

    Behavior:
    - If k >= len(items), return a shallow copy of all items
    - When seed is provided, the same seed must produce identical results

    Examples:
    >>> sample_uniform([1, 2, 3, 4, 5], k=3, seed=42)
    [4, 5, 2]  # deterministic with seed=42
    >>> sample_uniform([1, 2], k=5, seed=0)
    [1, 2]  # k > len, returns all

    Implementation hints:
    - Use random.Random(seed) to create an isolated RNG instance
    - The random module's .sample() method does sampling without replacement
    """
    if seed is not None:
        module = random.Random(seed)
    else:
        module = random.Random()

    return module.sample(items, k)


# ---- Stratified Sampling ----

def sample_stratified(
        items: Sequence[T],
        key: Callable[[T], Any],
        *,
        n: Optional[int] = None,
        frac: Optional[float] = None,
        seed: Optional[int] = None,
) -> List[T]:
    """Stratified sampling by key(item).

    Why this matters for MSR:
    - Ensures representation across groups (languages, authors, time periods)
    - Reduces variance when strata are internally homogeneous
    - Prevents dominant groups from overwhelming the sample

    Parameters:
    - items: the population to sample from
    - key: function that returns the stratum/group for each item
      (Using a callable instead of a string allows grouping by computed values
      and supports any data type, similar to Python's `sorted`)
    - n: exact number of samples per stratum (**mutually exclusive** with frac)
    - frac: fraction of each stratum to sample (**mutually exclusive** with n)
    - seed: random seed for reproducibility
    - * means that every parameter that comes after (to the right) must be named explicitly (frac=.5, seed=1, etc).

    Returns:
    - List of sampled items from all strata combined

    Behavior:
    - Exactly one of `n` or `frac` must be provided (raise ValueError otherwise)
    - For small strata, return up to the available members (don't error)
    - When frac > 0 but would yield 0 items, return at least 1 item
    - Selection must be reproducible with the same seed

    Examples:
    >>> items = [('py', 1), ('py', 2), ('js', 3), ('js', 4)]
    >>> sample_stratified(items, key=lambda x: x[0], n=1, seed=0)
    [('py', 2), ('js', 4)]  # 1 from each stratum

    >>> sample_stratified(items, key=lambda x: x[0], frac=0.5, seed=0)
    [('py', 1), ('js', 3)]  # 50% from each stratum

    Implementation hints:
    - Group items by key(item) into a dictionary
    - Sample within each group independently
    - For reproducibility, derive per-group seeds from the main seed
      (e.g., hash((group_key, seed)) to get consistent sub-seeds)
    """
    strata = {}
    for item in items:
        strataKey = key(item)
        if strataKey not in strata:
            strata[strataKey] = []
        strata[strataKey].append(item)

    if n is None:
        if frac is None:
            raise ValueError("If n is None, frac must be provided")
        else:
            isFrac = True
    else:
        isFrac = False

    outputGroup = []

    for group in strata:
        if seed is not None:
            module = random.Random(hash((group, seed)))
        else:
            module = random.Random()
        outputGroup += module.sample(strata[group], n if not isFrac else round(len(strata[group]) * frac))

    return outputGroup


# ---- Systematic Sampling ----

def sample_systematic(items: Sequence[T], step: int, seed: Optional[int] = None) -> List[T]:
    """Systematic sampling: random start, then every step-th item.

    Why this matters for MSR:
    - Efficient for large ordered populations (e.g., commit history)
    - Simpler than full random sampling for streaming data
    - Approximates uniform sampling when population is randomly ordered

    Parameters:
    - items: the population to sample from (order matters)
    - step: interval between selected items (must be >= 1)
    - seed: random seed for reproducibility of starting position

    Returns:
    - List of sampled items

    Behavior:
    - Choose a random start position in [0, step-1]
    - Select every step-th item from that starting point
    - Raise ValueError if step <= 0

    Examples:
    >>> sample_systematic(list(range(20)), step=5, seed=0)
    [3, 8, 13, 18]  # start=3 with seed=0, then +5 each time

    Implementation hints:
    - Use random.Random(seed).randrange(step) for the start position
    - Iterate with idx += step until idx >= len(items)
    """
    if seed is not None:
        module = random.Random(seed)
    else:
        module = random.Random()

    outputGroup = []
    index = module.randrange(step)

    while index < len(items):
        outputGroup.append(items[index])
        index += step

    return outputGroup


# ---- Sample Size for Proportions ----

def sample_size_proportion(
        N: Optional[int], p: float = 0.5, margin: float = 0.05, z: float = 1.96
) -> int:
    """Compute required sample size to estimate a proportion.

    Why this matters for MSR:
    - Answers: "How many commits must I label to estimate the bug rate?"
    - Ensures statistical validity of research findings
    - Finite population correction prevents over-sampling small repos

    Parameters:
    - N: population size (None = infinite population, no FPC)
    - p: expected proportion (0.5 is most conservative when unknown)
    - margin: desired margin of error (e.g., 0.05 = ±5%)
    - z: z-score for confidence level (1.96 = 95% confidence)

    Returns:
    - Required sample size as an integer (ceiling), capped at N if provided

    Formulas:
    - Baseline (infinite population): n0 = z² * p * (1-p) / margin²
    - With FPC: n = n0 / (1 + (n0 - 1) / N)

    Examples:
    >>> sample_size_proportion(None, p=0.5, margin=0.05, z=1.96)
    385  # classic value for 95% CI, ±5%

    >>> sample_size_proportion(500, p=0.5, margin=0.05, z=1.96)
    218  # FPC reduces required sample for small population

    Implementation hints:
    - Validate that margin > 0 and p is in valid range
    - Apply ceiling (math.ceil) to get integer
    - Cap result at N when N is provided
    """
    if margin <= 0:
        raise ValueError("Margin must be > 0")

    if p < 0 or p > 1:
        raise ValueError("p must be between 0 and 1")

    n0 = math.pow(z, 2) * p * (1 - p) / math.pow(margin, 2)

    if N is not None:
        n = n0 / (1 + (n0 - 1) / N)
        if n > N:  # cap if our pop to sample is greater than total pop
            n = N
    else:
        n = n0

    return math.ceil(n)


# ---- Sample Size for Means ----

def sample_size_mean(
        sigma: float, margin: float = 0.05, z: float = 1.96, N: Optional[int] = None
) -> int:
    """Compute required sample size to estimate a mean.

    Why this matters for MSR:
    - Answers: "How many files must I measure to estimate average complexity?"
    - Requires an estimate of population standard deviation

    Parameters:
    - sigma: known or estimated population standard deviation
    - margin: desired margin of error (same units as sigma)
    - z: z-score for confidence level (1.96 = 95% confidence)
    - N: population size (None = infinite population, no FPC)

    Returns:
    - Required sample size as an integer (ceiling), capped at N if provided

    Formulas:
    - Baseline: n0 = z² * σ² / margin²
    - With FPC: n = n0 / (1 + (n0 - 1) / N)

    Examples:
    >>> sample_size_mean(sigma=1.0, margin=0.1, z=1.96)
    385  # same as proportion with p=0.5 when units align

    Implementation hints:
    - Validate that sigma > 0 and margin > 0
    - Same FPC formula as sample_size_proportion
    """
    if margin <= 0:
        raise ValueError("Margin must be > 0")

    if sigma <= 0:
        raise ValueError("Sigma must be > 0")

    n0 = math.pow(z, 2) * math.pow(sigma, 2) / math.pow(margin, 2)

    if N is not None:
        n = n0 / (1 + (n0 - 1) / N)
        if n > N:  # cap if our pop to sample is greater than total pop
            n = N
    else:
        n = n0

    return math.ceil(n)
