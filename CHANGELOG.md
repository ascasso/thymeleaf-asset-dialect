# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Security
- **CRITICAL**: Fixed path traversal vulnerability in `DefaultAssetResolver.calculateFileHash()` method
- Added comprehensive input validation to prevent directory traversal attacks
- Implemented secure path validation that detects various attack patterns:
  - Basic traversal sequences: `../`, `..\\`, `..`
  - URL-encoded variants: `%2e%2e/`, `%2e%2e\\`, `%2e%2e`
  - Double-encoded patterns: `%252f`, `%255c`
  - Complex bypass attempts: `....//`, `....\\\\`
- Added invalid character filtering to block dangerous characters:
  - Null bytes (`\0`) and control characters (`\r`, `\n`)
  - File system unsafe characters (`<`, `>`, `"`, `|`, `?`, `*`)
- Implemented absolute path prevention for Windows/Unix systems
- Added early validation at entry point with clear error messages

### Changed
- `DefaultAssetResolver.resolve()` now validates all input paths and throws `IllegalArgumentException` for invalid paths
- Enhanced security posture by rejecting malicious inputs instead of silent failures
- Updated Spring Boot from 3.4.2 to 3.4.7 (security updates)
- Updated logback-classic from 1.5.16 to 1.5.18 (vulnerability fix)
- Updated JUnit Jupiter from 5.11.4 to 5.13.3
- Updated Mockito from 5.15.2 to 5.18.0

### Technical Details
- Added `isValidAssetPath()` method for comprehensive path validation
- Added `containsPathTraversalSequences()` method to detect various traversal patterns
- Added `containsInvalidCharacters()` method to filter dangerous characters
- All validation happens before any file system operations
