import Foundation
import UIKit

@objc(IOSMLKitRuntimeHost)
final class IOSMLKitRuntimeHost: NSObject {
    static let shared = IOSMLKitRuntimeHost()

    private let notificationCenter = NotificationCenter.default

    private override init() {
        super.init()
        notificationCenter.addObserver(
            self,
            selector: #selector(handleTextRequest(_:)),
            name: Notification.Name("SmartVisionMLKitTextRequest"),
            object: nil
        )
        notificationCenter.addObserver(
            self,
            selector: #selector(handleObjectRequest(_:)),
            name: Notification.Name("SmartVisionMLKitObjectRequest"),
            object: nil
        )
    }

    @objc(handleTextRequest:)
    private func handleTextRequest(_ notification: Notification) {
        let payload = resolveRequestPayload(from: notification)
        guard let requestId = payload.requestId else { return }
        guard let image = payload.image else {
            postResponse(
                name: "SmartVisionMLKitTextResponse",
                requestId: requestId,
                payload: ["error": "Bild konnte fuer ML Kit nicht geladen werden."]
            )
            return
        }

        guard let result = IOSMLKitTextBridge.shared.recognizeText(in: image) else {
            postResponse(
                name: "SmartVisionMLKitTextResponse",
                requestId: requestId,
                payload: ["error": "ML Kit Texterkennung fehlgeschlagen."]
            )
            return
        }

        postResponse(
            name: "SmartVisionMLKitTextResponse",
            requestId: requestId,
            payload: [
                "fullText": result.fullText,
                "blocks": result.blocks.map { block in
                    [
                        "text": block.text,
                        "left": block.left,
                        "top": block.top,
                        "right": block.right,
                        "bottom": block.bottom
                    ]
                }
            ]
        )
    }

    @objc(handleObjectRequest:)
    private func handleObjectRequest(_ notification: Notification) {
        let payload = resolveRequestPayload(from: notification)
        guard let requestId = payload.requestId else { return }
        guard let image = payload.image else {
            postResponse(
                name: "SmartVisionMLKitObjectResponse",
                requestId: requestId,
                payload: ["error": "Bild konnte fuer ML Kit nicht geladen werden."]
            )
            return
        }

        guard let result = IOSMLKitObjectBridge.shared.detectObjects(in: image) else {
            postResponse(
                name: "SmartVisionMLKitObjectResponse",
                requestId: requestId,
                payload: ["error": "ML Kit Objekterkennung fehlgeschlagen."]
            )
            return
        }

        postResponse(
            name: "SmartVisionMLKitObjectResponse",
            requestId: requestId,
            payload: [
                "primaryLabel": result.primaryLabel,
                "objects": result.objects.map { object in
                    [
                        "label": object.label,
                        "confidence": object.confidence,
                        "trackingId": object.trackingId as Any,
                        "left": object.left,
                        "top": object.top,
                        "right": object.right,
                        "bottom": object.bottom
                    ]
                }
            ]
        )
    }

    private func resolveRequestPayload(from notification: Notification) -> (requestId: String?, image: UIImage?) {
        guard
            let userInfo = notification.userInfo,
            let requestId = userInfo["requestId"] as? String,
            let imagePath = userInfo["path"] as? String
        else {
            return (nil, nil)
        }

        return (requestId, UIImage(contentsOfFile: imagePath))
    }

    private func postResponse(name: String, requestId: String, payload: [String: Any]) {
        var userInfo = payload
        userInfo["requestId"] = requestId
        notificationCenter.post(
            name: Notification.Name(name),
            object: nil,
            userInfo: userInfo
        )
    }
}
