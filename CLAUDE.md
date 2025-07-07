# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a secure Thymeleaf Asset Dialect (TAD) project that provides a custom Thymeleaf dialect for managing static assets with CDN support, local development paths, asset versioning, and comprehensive security protections. The project is structured as a multi-module Gradle build with:

- **core**: The main library containing the asset dialect implementation
- **sample**: A Spring Boot application demonstrating usage

## Common Commands

### Build and Test
```bash
# Build the entire project
./gradlew build

# Build specific module
./gradlew :core:build
./gradlew :sample:build

# Run tests
./gradlew test
./gradlew :core:test

# Run security tests specifically
./gradlew :core:test --tests "*SecurityTest"

# Run sample application
./gradlew :sample:bootRun
```

### Publishing
```bash
# Publish to local repository
./gradlew publishToMavenLocal

# Publish to remote repository (requires credentials)
./gradlew publish
```

## Architecture

### Core Components

The dialect follows a clean architecture with these key interfaces and implementations:

1. **AssetDialect** (`core/src/main/java/io/github/thymeleaf/assetdialect/tad/AssetDialect.java`): Main Thymeleaf dialect class that registers processors
2. **AssetProcessor** (`core/src/main/java/io/github/thymeleaf/assetdialect/tad/AssetProcessor.java`): Processes `asset:src` attributes in HTML templates
3. **AssetResolver** interface and **DefaultAssetResolver** implementation: Handle URL resolution logic
4. **AssetDialectConfiguration** interface with implementations for configuration management

### Configuration System

The project supports two configuration approaches:

- **Spring Boot**: Uses `AssetProperties` with `@ConfigurationProperties(prefix = "tad")` 
- **Framework-agnostic**: Uses `DefaultAssetDialectConfiguration` with builder pattern

### Key Features

- **CDN Resolution**: Transforms local asset paths to CDN URLs
- **Environment-aware**: Automatically uses local paths in development profiles
- **Asset Versioning**: Supports hash-based or timestamp-based versioning
- **Multi-CDN Support**: Different CDNs can be configured for different asset types
- **Security-First Design**: Comprehensive protection against path traversal and injection attacks
- **Input Validation**: Validates all asset paths against malicious patterns
- **File Extension Whitelist**: Only allows approved web asset file extensions
- **Path Containment**: Ensures resolved paths stay within allowed directories
- **Security Monitoring**: Logs all security violations for auditing

## Development Guidelines

### Module Structure
- Keep the `core` module framework-agnostic (minimal Spring dependencies)
- Use the `sample` module for Spring Boot-specific features and demonstrations
- Both modules target Java 21

### Configuration Properties
All configuration uses the `tad` prefix:
```properties
tad.enabled=true
tad.default-cdn=https://cdn.example.com
tad.cdns.images=https://img.example.com
tad.cdns.js=https://js.example.com
tad.local-path=/static
tad.use-local-in-dev=true
tad.version-assets=true
tad.version-strategy=hash
tad.asset-base-path=src/main/resources/static
```

### Testing
- Unit tests are in both `core` and `sample` modules
- Tests use JUnit 5, AssertJ, and Mockito
- Comprehensive security tests in `DefaultAssetResolverSecurityTest` cover all attack vectors
- Run tests with `./gradlew test`
- Run security-specific tests with `./gradlew :core:test --tests "*SecurityTest"`

### Spring Boot Integration
The sample application demonstrates proper Spring Boot configuration in `ThymeleafConfig.java`:
- Enable configuration properties with `@EnableConfigurationProperties(AssetProperties.class)`
- Register the dialect as a Spring bean
- Add dialect to SpringTemplateEngine

## Template Usage

The dialect adds the `asset` namespace with these attributes:

```html
<!-- Basic CDN resolution -->
<img src="/images/logo.png" asset:src/>

<!-- Specific CDN -->
<img src="/images/logo.png" asset:src asset:cdn="images"/>

<!-- Force local path -->
<img src="/images/logo.png" asset:src asset:local="true"/>
```

## Security Considerations

**IMPORTANT**: This library includes comprehensive security protections:

- **Path Traversal Protection**: All paths are validated against traversal attempts (`../`, encoded variants, etc.)
- **Input Sanitization**: Invalid characters and control sequences are blocked
- **File Extension Validation**: Only approved web asset extensions are allowed
- **Path Containment**: Resolved paths are verified to stay within configured base directories
- **Security Logging**: All violations are logged with appropriate detail for monitoring

When working with security-sensitive code:
1. Always run security tests before committing: `./gradlew :core:test --tests "*SecurityTest"`
2. Review any changes to validation logic in `DefaultAssetResolver`
3. Test with malicious inputs to ensure protections work
4. Monitor application logs for security violations in production