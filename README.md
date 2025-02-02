# Thymeleaf Asset Dialect (TAD)

A Thymeleaf dialect that simplifies asset management in web applications, providing CDN support, local development paths, and asset versioning.

## Features

CDN URL resolution with support for multiple CDNs
Automatic local path resolution for development
Asset versioning with hash or timestamp strategies
Spring Boot auto-configuration  Framework-agnostic core (can be used without Spring)

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

## Non Spring Config example

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
    .build();
```

## Running the sample app:
```
./gradlew :sample:bootRun
```
