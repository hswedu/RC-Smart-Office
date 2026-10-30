# RC Smart Office — Starter Project (v0.1.0)

This is the first Android Studio starter prototype for the planned offline-first document editor.

## Current prototype
- Android 12+ (minSdk 31)
- Kotlin + Jetpack Compose
- Basic text editing surface
- Document title field
- Font and page-size selection UI
- Open a basic `.docx` file using Android's document picker (text extraction only)\n- Export a basic `.docx` file using Android's document picker\n- Writes selected page size (A4/A3/Letter/Legal) into DOCX section properties

## Important limitations
This is **not yet a professional DOCX editor** and is not a finished APK. DOCX export is an early basic implementation and should be validated in Microsoft Word.
The current prototype does not yet implement:
- DOCX import/export or Word-compatible pagination
- PDF export / print
- Real font file loading or embedding
- SutonnyMJ / Bijoy keyboard mapping
- Unicode-to-Bijoy conversion
- Rich text formatting, tables, images, headers/footers, margins

The `SutonnyMJ*` menu item is only a placeholder and deliberately does not claim to render that font.

## Build
Open this folder in Android Studio (recent version), allow Gradle sync, then choose:
Build > Build Bundle(s) / APK(s) > Build APK(s).

Requires JDK 17 and Android SDK platform 35. If your machine has no Android development setup, install Android Studio first.

## Next engineering milestones
1. Implement a real document model and DOCX import/export.
2. Add page geometry (A4/Letter/Legal, margins, orientation) and pagination.
3. Add rich text toolbar and document elements.
4. Add licensed font assets and font embedding where format/tooling permits.
5. Implement tested Bijoy keyboard mapping and Unicode↔Bijoy conversion.
6. Validate generated DOCX files in Microsoft Word on PC and compare PDF output.
