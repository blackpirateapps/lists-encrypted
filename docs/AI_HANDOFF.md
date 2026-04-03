# FlowyE2EE AI Handoff

This document captures the core architectural decisions, data models, and constraints for building the WorkFlowy-inspired Android app with rich text and E2EE. It is tailored for the current codebase scaffold and should be kept in sync as features land.

## Project Identity
- App name: FlowyE2EE
- Package: com.blackpiratex.flowye2ee
- UI: Jetpack Compose + Material 3
- Architecture: MVVM + Clean + Repository
- Storage: Room
- Encryption: AES-GCM (Android Keystore backed)

## Non-Negotiable Constraints
- Everything is a list item (node). There are no block types.
- Rich text is inline and data-driven (serializable, encryptable).
- Node styles are visual/behavioral overlays only; they never change the tree.
- E2EE for all content (text + spans + future metadata).

## Core Data Models
Located in `app/src/main/java/com/blackpiratex/flowye2ee/domain/model/`.

```kotlin
data class RichText(
    val text: String,
    val spans: List<SpanRange>
)

data class SpanRange(
    val start: Int,
    val end: Int,
    val style: SpanStyleType
)

enum class SpanStyleType {
    BOLD, ITALIC, UNDERLINE, STRIKETHROUGH, INLINE_CODE, LINK
}

enum class NodeStyle {
    BULLET, TODO, NUMBERED, HEADING_1, HEADING_2, HEADING_3, PARAGRAPH, QUOTE, DIVIDER
}
```

Room entity is in `app/src/main/java/com/blackpiratex/flowye2ee/data/local/entity/NodeEntity.kt`:

```kotlin
@Entity
data class NodeEntity(
    @PrimaryKey val id: String,
    val parentId: String?,
    val encryptedContent: ByteArray,
    val style: NodeStyle,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: Long,
    val updatedAt: Long
)
```

## Encryption Layer
Implementation in `app/src/main/java/com/blackpiratex/flowye2ee/data/crypto/`.

### Flow
1. Serialize `RichText` to JSON.
2. Encrypt with AES-GCM.
3. Store bytes in Room.
4. Decrypt only for rendering or search.

### CryptoManager
`CryptoManager` uses Android Keystore with AES/GCM. The IV is stored alongside ciphertext in a single byte array payload.

### Password and Key Strategy (Current + Future)
- Current scaffold is device-managed; the next step is to add password-derived keys (PBKDF2) and store encrypted key material in Keystore.
- Must support re-encryption on password change: decrypt all nodes with old key, re-encrypt with new key, update in DB.
- Design should allow future cloud sync without data loss: derive encryption from email + password, with local migration support.

## Repository Pattern
`NodeRepository` in `app/src/main/java/com/blackpiratex/flowye2ee/data/repository/NodeRepository.kt` is responsible for:
- Creating nodes with encrypted content
- Decrypting content for rendering
- Updating content with encryption

Future repository work:
- CRUD for tree operations (indent, un-indent, reorder)
- Style transforms (preserve id, content, children, position)
- Search that decrypts in-memory only

## UI and Editor Behavior
UI is Compose. Target behaviors:
- Inline editing (no separate editor screens)
- Enter: create new node
- Tab: indent (make child)
- Shift + Tab: un-indent
- Backspace on empty: delete or merge node
- Tap node: zoom into it
- Breadcrumbs at top
- Collapse/expand children

### Node Styles (Visual Only)
All node styles are overlays. They must look distinct without changing the data model.
- BULLET: dot bullet
- TODO: checkbox + `isCompleted`
- NUMBERED: auto-numbered per sibling
- HEADING 1/2/3: distinct sizes, bold
- PARAGRAPH: no bullet, full-width text
- QUOTE: left border + italic
- DIVIDER: horizontal line, no editable text

### Slash Commands
Typing `/` opens palette:
`/todo`, `/h1`, `/h2`, `/h3`, `/quote`, `/divider`, `/numbered`
Converts current node style only; content/children preserved.

## Export
### JSON Export
- Full node tree with IDs, parents, styles, RichText.
- No UI state.

### Markdown Export (export only)
- TODO -> `- [ ]`
- BULLET -> `-`
- NUMBERED -> `1.`
- H1 -> `#`
- H2 -> `##`
- H3 -> `###`
- QUOTE -> `>`
- DIVIDER -> `---`

## Performance Targets
- 10,000+ nodes.
- LazyColumn for rendering.
- Efficient tree traversal.
- Minimize recomposition.
- Fast keyboard input.

## Tests Required
- Node style transformations
- RichText serialization/deserialization
- Encryption/decryption
- Re-encryption on password change
- Export correctness

## GitHub Actions
Use the existing APK build workflow unchanged.
If new workflow is needed, place under `.github/workflows/` and align with CI expectations.
