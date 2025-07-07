# Notes

## What this project is for?

This is a Thymeleaf Asset Dialect (TAD) project that solves a common web development problem: 
managing static assets across different environments.

What it does:

Primary Purpose: Provides a custom Thymeleaf dialect that automatically transforms static asset URLs in
HTML templates based on environment and configuration.

Key Problems it Solves:
1. Environment-specific asset serving: Automatically uses CDN URLs in production but local paths in
   development
2. Asset versioning: Adds hash or timestamp-based versioning to prevent caching issues
3. Multi-CDN support: Different asset types (images, CSS, JS) can use different CDNs
4. Development convenience: Developers write simple local paths, the dialect handles the complexity

How it works:

Instead of writing:
<img src="https://cdn.example.com/images/logo.123abc.png"/>

Developers write:
<img src="/images/logo.png" asset:src/>

The dialect automatically:
- Resolves the appropriate CDN URL based on configuration
- Adds version hashes/timestamps
- Uses local paths in development environments
- Supports per-asset CDN overrides

Target Use Cases:

- Web applications that need to serve assets from CDNs in production
- Teams that want to simplify asset management across environments
- Applications requiring asset versioning for cache busting
- Projects using Thymeleaf templates (with or without Spring Boot)

This is a very practical tool for production web applications where asset delivery performance and cache
management are important concerns.

