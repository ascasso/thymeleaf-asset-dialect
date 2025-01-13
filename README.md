#

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
