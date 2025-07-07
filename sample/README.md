# Thymeleaf Asset Dialect Sample Application

This sample application demonstrates the features of the Thymeleaf Asset Dialect. It's a simple Spring Boot application that uses the dialect to manage static assets.

## Running the Application

You can run the application using the following Gradle command:

```bash
../gradlew bootRun
```

The application will be available at [http://localhost:8080](http://localhost:8080).

## Features Demonstrated

The `index.html` file in `src/main/resources/templates` showcases the following features:

*   **Local Asset Resolution:** The dialect resolves local assets and adds a version hash to the URL to prevent caching issues.
*   **CDN Asset Resolution:** The dialect can be configured to serve assets from a CDN in a production environment.
*   **Automatic Protocol:** The dialect automatically uses the correct protocol (HTTP or HTTPS) for CDN URLs.
*   **`tad:src` and `tad:href` Attributes:** The `tad:src` attribute is used for `<img>` and `<script>` tags, while the `tad:href` attribute is used for `<link>` tags.

## Configuration

The application is configured in `src/main/resources/application.properties`. The following properties are used to configure the asset dialect:

*   `tad.enabled`: Enables or disables the dialect.
*   `tad.local-path`: The path to the local assets.
*   `tad.cdn-url`: The URL of the CDN.

You can experiment with these properties to see how the dialect behaves in different environments. For example, you can enable the CDN by setting the `tad.cdn-url` property.
