# Thymeleaf Asset Dialect (TAD)

A secure Thymeleaf dialect that simplifies asset management in web applications, providing CDN support, local development paths, and asset versioning with comprehensive security protections.

## Features

✅ **CDN URL resolution** with support for multiple CDNs  
✅ **Automatic local path resolution** for development  
✅ **Asset versioning** with hash or timestamp strategies  
✅ **Spring Boot auto-configuration**  
✅ **Framework-agnostic core** (can be used without Spring)  
🔒 **Security-first design** with path traversal protection  
🔒 **Input validation** and sanitization  
🔒 **Configurable security policies**  
📊 **Security monitoring** and logging

## HTML Example

```html
<!-- Basic usage -->
<img src="/images/logo.png" asset:src/>
<!-- Becomes: <img src="https://assets.example.com/images/logo.123abc.png"/> -->

<!-- With specific CDN -->
<img src="/images/logo.png" asset:src asset:cdn="images"/>
<!-- Becomes: <img src="https://img.example.com/images/logo.123abc.png"/> -->

<!-- Force local -->
<img src="/images/logo.png" asset:src asset:local="true"/>
<!-- Becomes: <img src="/static/images/logo.png"/> -->
```

## Configuration

### Spring Boot Configuration

Configure via `application.properties`:

```properties
# Enable/disable the dialect
tad.enabled=true

# CDN configuration
tad.default-cdn=https://cdn.example.com
tad.cdns.images=https://img.example.com
tad.cdns.js=https://js.example.com
tad.cdns.css=https://css.example.com

# Local development
tad.local-path=/static
tad.use-local-in-dev=true

# Asset versioning
tad.version-assets=true
tad.version-strategy=hash

# Security configuration
tad.asset-base-path=src/main/resources/static
```

### Framework-Agnostic Configuration

```java
DefaultAssetDialectConfiguration config = DefaultAssetDialectConfiguration.builder()
    .enabled(true)
    .defaultCdn("https://cdn.example.com")
    .addCdn("images", "https://images.example.com")
    .addCdn("js", "https://js.example.com")
    .localPath("/static")
    .useLocalInDev(true)
    .versionAssets(true)
    .versionStrategy("hash")
    .assetBasePath("src/main/resources/static")
    .build();
```

## Security Features

🔒 **Path Traversal Protection**: Automatically detects and blocks path traversal attempts like `../../../etc/passwd`

🔒 **Input Validation**: Validates all asset paths against:
- Path traversal sequences (including URL-encoded variants)
- Invalid characters and control characters
- Dangerous file extensions
- Absolute path attempts

🔒 **File Extension Whitelist**: Only allows approved web asset extensions:
- **Images**: jpg, jpeg, png, gif, svg, webp, ico, bmp, tiff
- **Stylesheets**: css, scss, sass, less  
- **Scripts**: js, ts, jsx, tsx, mjs
- **Fonts**: woff, woff2, ttf, otf, eot
- **Documents**: pdf, txt, md, json, xml
- **Media**: mp3, mp4, wav, ogg, webm, avi, mov
- **Archives**: zip, gz, tar

🔒 **Path Containment**: Ensures resolved paths stay within configured base directories

📊 **Security Monitoring**: Comprehensive logging of security violations for monitoring and auditing

## Running the sample app:
```bash
./gradlew :sample:bootRun
```

## Building and Testing

```bash
# Build the project
./gradlew build

# Run tests (including security tests)
./gradlew test

# Run security-specific tests
./gradlew :core:test --tests "*SecurityTest"
```
