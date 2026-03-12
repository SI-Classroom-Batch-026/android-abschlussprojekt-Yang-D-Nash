📱 SmartVision
Die Welt erkennen, verstehen und hören – für jeden, jederzeit.

SmartVision ist ein intelligenter Assistent, der das Smartphone in ein leistungsfähiges „schlaues Auge“ (Head-Up Display) verwandelt. Die App hilft dabei, die Umgebung besser zu verstehen, indem sie Informationen direkt im Kamerabild einblendet, Texte vorliest oder durch Vibration Feedback gibt.

Das Projekt zeigt heute schon, wie wir in Zukunft mit Datenbrillen, Drohnen oder intelligenten Kameras interagieren werden.

🌟 Hauptfunktionen (Was die App kann)
🔍 Intelligente Objekterkennung: Erkennt Gegenstände sofort und blendet Infos direkt im Live-Bild ein.

📝 Texte lesen & übersetzen: Erkennt Schilder oder Dokumente und übersetzt sie in Echtzeit.

🔊 Vorlesefunktion (Text-to-Speech): Liest erkannte Texte laut vor – ideal für Barrierefreiheit.

📳 Spürbare Rückmeldung: Vibration bei erfolgreicher Erkennung von Objekten.

📸 Snapshot-Modus: Fixiert das Bild für wackelfreies Lesen und Analysieren.

🚀 Zukunftsvision: Mehr als nur eine App
SmartVision ist so entwickelt, dass die Technologie flexibel eingesetzt werden kann:

Augmented Reality (AR): Fokus auf Smart Glasses und freihändige Bedienung.

Drohnen-Technologie: Intelligente Bildanalyse aus der Luft (Schilder lesen, Objekte finden).

Spezial-Kameras: Einsatz in Industrie oder Smart Homes für echtes Umgebungsverständnis.

⚙️ Setup & API-Sicherheit
Um modernen Sicherheitsstandards gerecht zu werden, werden API-Keys und Firebase-Konfiguration lokal gehalten und nicht im Repository versioniert.

Secrets Management: Sensitive Daten liegen lokal in der `local.properties` und in `app/google-services.json`.

Automated Build-Injection: Die Cloud-Keys werden während des Build-Vorgangs per Gradle in `BuildConfig` injiziert.

Eigenes Setup hinterlegen:

Google Cloud: Projekt erstellen und die benötigten APIs aktivieren.

`local.properties`: Im Hauptverzeichnis die benötigten Einträge ergänzen:

`CLOUD_VISION_API_KEY=dein_tatsächlicher_api_key_hier`

`CLOUD_TRANSLATE_API_KEY=dein_tatsächlicher_api_key_hier`

Firebase: Die eigene Datei `google-services.json` nach `app/google-services.json` legen.

Build: Projekt synchronisieren. Die Keys werden automatisch via `BuildConfig.CLOUD_VISION_API_KEY` und `BuildConfig.CLOUD_TRANSLATE_API_KEY` eingebunden.

🏗 Projektstatus
[x] Texte & Objekte erkennen: Voll funktionsfähig.

[x] Audio & Vibration: Vorlesefunktion und haptisches Feedback integriert.

[x] Snapshot-Scanning: Stabiles Lesen ohne Wackeln möglich.

[ ] Nächster Schritt: Steuerung per Sprachbefehl (Hands-Free).

[ ] Nächster Schritt: Anpassung für Drohnen-Kameras und AR-Brillen.

🛠 Technologie-Stack
Sprache: Kotlin (Android)

Oberfläche: Jetpack Compose (Modernes HUD-Design)

KI (Lokal): Google ML Kit

KI (Cloud): Google Cloud Vision API

Architektur: MVVM & Koin

👤 Kontakt & Konzept
Entwickler: Nguyen Phuong Ngoc Anh

E-Mail: nash.lioncorna@gmail.com

Konzept-Link: https://www.figma.com/design/FIdhzq6VAZFTA7ljYtjmMB/Abschluss-Android?node-id=5-24&t=JVHdq4fn9pGgHNqH-1

📚 Entwickler-Ressourcen (Dokumentation)
Google ML Kit (On-Device AI)

Hauptdokumentation: https://developers.google.com/ml-kit?hl=de

Objekterkennung & Tracking: https://developers.google.com/ml-kit/vision/object-detection/android?hl=de

Texterkennung (OCR): https://developers.google.com/ml-kit/vision/text-recognition/v2/android?hl=de

Google Cloud Vision (Cloud AI)

Dokumentation Übersicht: https://cloud.google.com/vision/docs?hl=de

Cloud OCR: https://cloud.google.com/vision/docs/fulltext-annotations?hl=de

Setup & Authentifizierung: https://cloud.google.com/vision/docs/setup?hl=de

⚡ Der Elevator Pitch

"SmartVision macht das Unsichtbare sichtbar und das Geschriebene hörbar. Wir verwandeln das Smartphone in ein intelligentes Head-Up Display, das Menschen im Alltag unterstützt. Unsere Technologie ist die Basis für die intelligente Steuerung von AR-Brillen und Drohnen der nächsten Generation."
