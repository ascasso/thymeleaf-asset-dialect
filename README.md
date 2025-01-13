#



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
