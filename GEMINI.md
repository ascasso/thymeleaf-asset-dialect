# GEMINI.md

This file provides guidance to Google's Gemini models when working with code in this repository.

## Project Overview

This is a Thymeleaf Asset Dialect (TAD) project that provides a custom Thymeleaf dialect for managing static assets. The project is designed with security as a primary concern and includes features for CDN support, local development paths, and asset versioning.

The project is a multi-module Gradle build:
- **core**: The main library containing the asset dialect implementation.
- **sample**: A Spring Boot application demonstrating the usage of the dialect.

## Assessment

Based on my analysis of the codebase and documentation, here is my assessment:

### Code and Documentation Consistency

The code is highly consistent with the documentation (`README.md`, `CHANGELOG.md`, `CLAUDE.md`, `NOTES.md`, and `PLAN.md`). The documented security features, such as path traversal protection, input validation, and file extension whitelisting, are implemented in the `core` module, primarily within the `DefaultAssetResolver.java` class.

The security test suite (`DefaultAssetResolverSecurityTest.java`) is comprehensive and validates the effectiveness of the security measures described in the documentation.

### Security

The project demonstrates a strong commitment to security. The developers have clearly made an effort to identify and mitigate potential security vulnerabilities, particularly those related to path traversal. The use of a detailed security checklist in `isValidAssetPath` and path containment checks in `calculateFileHash` are excellent security practices.

### Recommendations

The project is well-structured and the code quality is high. My main recommendation is to continue maintaining the excellent alignment between the code and documentation. Any new features or changes to existing functionality should be reflected in the documentation and tested thoroughly, with a particular focus on security.

## Improvement Proposal: Caching for Performance

While the current implementation is secure and functional, its performance can be significantly improved. The `calculateFileHash()` method in `DefaultAssetResolver` calculates an asset's hash on every request, which is resource-intensive.

I propose the following changes to address this:

1.  **Implement a Caching Layer**: Introduce a cache to store the computed hashes of assets. This will prevent re-calculating the hash for the same file repeatedly.
    *   A `ConcurrentHashMap` can be used as a simple, thread-safe in-memory cache.
    *   For more advanced caching features like eviction policies (e.g., time-based or size-based), a library like [Caffeine](https://github.com/ben-manes/caffeine) could be integrated.

2.  **Optimize File Hashing**: Modify the `calculateFileHash` method to read files in chunks rather than loading the entire file into memory. This will reduce memory consumption, especially for large files.

3.  **Configuration for Caching**: Add configuration properties to `AssetProperties` to allow users to enable or disable caching and configure its parameters (e.g., cache size, expiration time).

These changes will make the dialect more performant and suitable for high-traffic production environments.

### Unit Test Plan for Caching

To ensure the caching implementation is robust and correct, the following test scenarios should be implemented in `DefaultAssetResolverTest.java` or a new dedicated test class.

#### 1. Core Cache Functionality

*   **Test Cache Hit**:
    *   **Objective**: Verify that a file hash is calculated only once for the same path and subsequent requests are served from the cache.
    *   **Implementation**:
        1.  Create a spy of the `DefaultAssetResolver`.
        2.  Call `resolve()` for an asset path.
        3.  Call `resolve()` for the same asset path again.
        4.  Use Mockito's `verify()` to confirm that the internal hash calculation method was called exactly once.

*   **Test Cache Miss**:
    *   **Objective**: Ensure that different asset paths result in separate hash calculations.
    *   **Implementation**:
        1.  Create a spy of the `DefaultAssetResolver`.
        2.  Call `resolve()` for "path/to/asset1.css".
        3.  Call `resolve()` for "path/to/asset2.js".
        4.  Verify that the internal hash calculation method was called twice.

#### 2. Configuration

*   **Test Caching Disabled**:
    *   **Objective**: Confirm that the cache is bypassed when `tad.caching.enabled=false`.
    *   **Implementation**:
        1.  Set the caching property to `false`.
        2.  Create a spy of the `DefaultAssetResolver`.
        3.  Call `resolve()` for the same asset path multiple times.
        4.  Verify that the internal hash calculation method is called each time.

#### 3. Concurrency

*   **Test Concurrent Access**:
    *   **Objective**: Ensure the cache is thread-safe and the hash is still only calculated once under concurrent load.
    *   **Implementation**:
        1.  Use a `CountDownLatch` and an `ExecutorService` to spawn multiple threads.
        2.  Have all threads request the hash for the same asset path concurrently.
        3.  Use a spy and `verify()` to confirm the hash calculation method was invoked only once.
        4.  Assert that all threads received the same correct hash value.

#### 4. Edge Cases and Error Handling

*   **Test Non-Existent Files**:
    *   **Objective**: Verify that requests for non-existent files do not populate the cache.
    *   **Implementation**:
        1.  Request the hash for a path that does not correspond to a real file.
        2.  Confirm that the hash calculation returns `null`.
        3.  Verify that the cache does not contain an entry for this path.

*   **Test Cache Invalidation (Manual)**:
    *   **Objective**: If a manual cache clearing mechanism is added, test that it works.
    *   **Implementation**:
        1.  Request an asset to populate the cache.
        2.  Call a `clearCache()` method.
        3.  Request the same asset again and verify that the hash is recalculated (cache miss).

#### 5. Limitations to Acknowledge

*   The test plan assumes a simple in-memory cache. It does not account for automatic file modification detection. The current design would require an application restart or a manual cache clear to pick up file changes, and the tests should reflect this behavior.

## Common Commands

### Build and Test
```bash
# Build the entire project
./gradlew build

# Run tests
./gradlew test

# Run security-specific tests
./gradlew :core:test --tests "*SecurityTest"
```

### Running the Sample Application
```bash
./gradlew :sample:bootRun
```
