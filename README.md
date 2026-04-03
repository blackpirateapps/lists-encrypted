# FlowyE2EE

WorkFlowy-inspired Android app with rich text and end-to-end encryption.

## Build

Use GitHub Actions to build the APK.

Local dev uses Gradle:

```bash
./gradlew assembleDebug
```

## Notes

- Everything is a list node.
- Rich text is inline and serializable.
- All content is encrypted at rest.
