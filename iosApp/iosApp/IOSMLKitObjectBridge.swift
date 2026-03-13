import Foundation
import UIKit
import MLKitObjectDetection
import MLKitObjectDetectionCommon
import MLKitVision

@objc(IOSMLKitObjectPayload)
@objcMembers
final class IOSMLKitObjectPayload: NSObject {
    let label: String
    let confidence: Float
    let trackingId: NSNumber?
    let left: CGFloat
    let top: CGFloat
    let right: CGFloat
    let bottom: CGFloat

    init(
        label: String,
        confidence: Float,
        trackingId: NSNumber?,
        frame: CGRect
    ) {
        self.label = label
        self.confidence = confidence
        self.trackingId = trackingId
        self.left = frame.minX
        self.top = frame.minY
        self.right = frame.maxX
        self.bottom = frame.maxY
    }
}

@objc(IOSMLKitObjectDetectionResult)
@objcMembers
final class IOSMLKitObjectDetectionResult: NSObject {
    let primaryLabel: String
    let objects: [IOSMLKitObjectPayload]

    init(primaryLabel: String, objects: [IOSMLKitObjectPayload]) {
        self.primaryLabel = primaryLabel
        self.objects = objects
    }
}

@objc(IOSMLKitObjectBridge)
@objcMembers
final class IOSMLKitObjectBridge: NSObject {
    static let shared = IOSMLKitObjectBridge()

    private lazy var objectDetector: ObjectDetector = {
        let options = ObjectDetectorOptions()
        options.detectorMode = .singleImage
        options.shouldEnableClassification = true
        options.shouldEnableMultipleObjects = true
        return ObjectDetector.objectDetector(options: options)
    }()

    @objc(sharedBridge)
    class func sharedBridge() -> IOSMLKitObjectBridge {
        shared
    }

    @objc(detectObjectsInImage:)
    func detectObjects(in image: UIImage) -> IOSMLKitObjectDetectionResult? {
        let visionImage = VisionImage(image: image)

        do {
            let objects = try objectDetector.results(in: visionImage)
            let payloads = objects.compactMap { object -> IOSMLKitObjectPayload? in
                let label = object.labels
                    .max(by: { lhs, rhs in lhs.confidence < rhs.confidence })?
                    .text
                    .trimmingCharacters(in: .whitespacesAndNewlines)
                    ?? ""

                return IOSMLKitObjectPayload(
                    label: label,
                    confidence: object.labels.first?.confidence ?? 0,
                    trackingId: object.trackingID,
                    frame: object.frame
                )
            }

            let primaryLabel = payloads.first(where: { !$0.label.isEmpty })?.label ?? ""
            return IOSMLKitObjectDetectionResult(
                primaryLabel: primaryLabel,
                objects: payloads
            )
        } catch {
            return nil
        }
    }
}
