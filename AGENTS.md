# Repository instructions

## Project layout

- `core/` contains the published Thymeleaf asset-dialect library.
- `sample/` is the Spring Boot 4.1.0 demonstration application.
- The build targets Java 25 and uses the included Gradle 9.7.0 wrapper.

## Architecture and security

- The default dialect prefix is `tad`; support `tad:src` and `tad:href`, plus the optional `tad:cdn` and `tad:local` attributes.
- Keep URL resolution, CDN selection, local-path behavior, and versioning in `AssetResolver` implementations; processors should only translate template attributes.
- Treat `DefaultAssetResolver` and its security tests as security-sensitive. Preserve path-traversal, invalid-character, extension-whitelist, and path-containment protections.

## Common commands

```bash
./gradlew :core:test
./gradlew :core:test --tests "*SecurityTest"
./gradlew :sample:test
./gradlew clean build --no-daemon
./gradlew :sample:bootRun
```

## Change tracking

- Every repository change must be recorded in a dated entry under `docs/logs/`.
- All changes must be committed in one or more focused Git commits containing only files related to the requested work.
- Do not leave requested changes uncommitted, and do not push or release unless explicitly requested.

## Verification

- Run the narrowest relevant Gradle checks for the change.
- Run `git diff --check` before committing.
- Report any checks that could not run and why.
