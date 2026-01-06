📱 SmartVision
Die Welt erkennen, verstehen und hören – jederzeit, überall, für jeden.

SmartVision ist eine intelligente Assistenz-App, die das Smartphone in ein leistungsfähiges HUD (Head-Up Display) verwandelt. Das Projekt dient als Machbarkeitsstudie für die nächste Generation der Augmented Reality (AR). Ziel ist es, Technologien, die später Standard in Smart Glasses sein werden, schon heute auf mobilen Endgeräten intuitiv nutzbar zu machen.

🚀 Kernfunktionen
🔍 Intelligente Objekterkennung (Live-AR)

Hybrid-Analyse: Schnelles On-Device Tracking (ML Kit) kombiniert mit hochpräzisen Cloud-Deep-Scans (Vision API) für komplexe Szenarien.

AR-Overlay: Ein mathematisch optimiertes Koordinaten-Mapping (Scale & Offset) simuliert die Informations-Einblendung einer AR-Brille direkt im Live-Bild.

📝 OCR, Translation & Audio (TTS)

Snapshot-Technologie: Stabiler Scan-on-Demand verhindert das "Zittern" der Boxen – essenziell für die Lesbarkeit in Wearables.

Barrierefreiheit: Sofortige Übersetzung (z.B. EN -> DE) inklusive Text-to-Speech (TTS). Die App erkennt Texte und liest sie auf Knopfdruck vor.

Cloud Document OCR: Spezial-Modus für komplexe Schriftsätze via Google Cloud Vision.

👓 Future-Ready: Wearable Vision

HUD-Interface: Das Design folgt den Prinzipien für Smart Glasses: Fokus auf das Zentrum des Sichtfeldes, minimalistische Steuerung und haptisches Feedback.

Hands-Free Vorbereitung: Die Architektur ist darauf ausgelegt, später ohne klassische Touch-Eingabe (z.B. über Eye-Tracking oder Sprachbefehle) zu funktionieren.

🛠 Technologie-Stack
Bereich	Technologie
Sprache	Kotlin
UI	Jetpack Compose (Modern & HUD-optimiert)
AI (Local)	Google ML Kit (Object, Text, Translation)
AI (Cloud)	Google Cloud Vision API
Audio	Android Text-to-Speech (TTS) Engine
DI / Architektur	Koin & MVVM
⚙️ Setup & API-Sicherheit
Um modernen Sicherheitsstandards gerecht zu werden, wurde der API-Key konsequent vom Quellcode entkoppelt (Separation of Concerns).

Secrets Management: Sensitive Daten werden ausschließlich lokal in der local.properties verwaltet (geschützt durch .gitignore).

Automated Build-Injection: Der Key wird während des Build-Vorgangs via Gradle in die BuildConfig injiziert.

[!IMPORTANT] Hinweis zum API-Key: Ein Test-API-Key ist aktuell für Demonstrationszwecke im Hintergrund hinterlegt, wird jedoch voraussichtlich in den nächsten 14 Tagen deaktiviert. Danach ist ein eigener Key zwingend erforderlich.

Eignen API-Key hinterlegen:

Google Cloud: Projekt erstellen und Cloud Vision API aktivieren.

Local.properties: Fügen Sie im Hauptverzeichnis folgende Zeile hinzu: VISION_API_KEY=dein_tatsächlicher_api_key_hier

Build: Projekt synchronisieren. Der Key wird automatisch via BuildConfig.VISION_API_KEY eingebunden.

🏗 Projektstatus & Roadmap
[x] Snapshot-Scanning: Stabilisierte Texterkennung für Wearable-ähnliche Displays.

[x] Audio-Vision: Voll integrierte Text-to-Speech Unterstützung.

[x] AR-Mapping: Korrektes Koordinaten-Mapping (Scale & Offset).

[ ] Wearable Integration: Prototyping für Head-Worn Displays.

[ ] Cloud-Sync: Optionales Backup der Scan-Historie via Firebase.

👤 Kontakt
Projekt von: Yang D. Nash

E-Mail: nash.lioncorna@gmail.com

Konzept: https://www.figma.com/design/FIdhzq6VAZFTA7ljYtjmMB/Abschluss-Android?node-id=5-24&t=JVHdq4fn9pGgHNqH-1

Das ist eine hervorragende Ergänzung für die technische Dokumentation deines Projekts. Hier sind die wichtigsten offiziellen Ressourcen, strukturiert nach den von dir verwendeten Technologien:

📚 Entwickler-Ressourcen

Google ML Kit (On-Device AI)

Hauptdokumentation: [ML Kit für Entwickler](https://developers.google.com/ml-kit?hl=de)

Objekterkennung & Tracking: [Guide für Android](https://developers.google.com/ml-kit/vision/object-detection/android?hl=de)

Texterkennung (OCR): [Text mit ML Kit erkennen](https://developers.google.com/ml-kit/vision/text-recognition/v2/android?hl=de)

Übersetzung: [On-Device Translation Guide](https://developers.google.com/ml-kit/language/translation?hl=de)

Google Cloud Vision (Cloud AI)

Dokumentation Übersicht: [Cloud Vision API Dokumentation](https://cloud.google.com/vision/docs?hl=de)

Cloud OCR (Dichte Texte): [Texterkennung in Dokumenten](https://docs.cloud.google.com/vision/docs/fulltext-annotations?hl=de)

Setup & Authentifizierung: [Cloud Vision API einrichten](https://cloud.google.com/vision/docs/setup?hl=de)

⭐ Kernbotschaft: SmartVision erkennt die Welt – und macht sie für jeden verständlich.
