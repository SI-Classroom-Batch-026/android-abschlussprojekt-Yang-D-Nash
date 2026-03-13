import Foundation
import UIKit
import MLKitTextRecognition
import MLKitVision

@objc(IOSMLKitTextBlockPayload)
@objcMembers
final class IOSMLKitTextBlockPayload: NSObject {
    let text: String
    let left: CGFloat
    let top: CGFloat
    let right: CGFloat
    let bottom: CGFloat

    init(text: String, frame: CGRect) {
        self.text = text
        self.left = frame.minX
        self.top = frame.minY
        self.right = frame.maxX
        self.bottom = frame.maxY
    }
}

@objc(IOSMLKitTextRecognitionResult)
@objcMembers
final class IOSMLKitTextRecognitionResult: NSObject {
    let fullText: String
    let blocks: [IOSMLKitTextBlockPayload]

    init(fullText: String, blocks: [IOSMLKitTextBlockPayload]) {
        self.fullText = fullText
        self.blocks = blocks
    }
}

@objc(IOSMLKitTextBridge)
@objcMembers
final class IOSMLKitTextBridge: NSObject {
    static let shared = IOSMLKitTextBridge()

    private lazy var textRecognizer: TextRecognizer = {
        let options = TextRecognizerOptions()
        return TextRecognizer.textRecognizer(options: options)
    }()

    @objc(sharedBridge)
    class func sharedBridge() -> IOSMLKitTextBridge {
        shared
    }

    @objc(recognizeTextInImage:)
    func recognizeText(in image: UIImage) -> IOSMLKitTextRecognitionResult? {
        let visionImage = VisionImage(image: image)
        do {
            let result = try textRecognizer.results(in: visionImage)
            let fullText = result.text.trimmingCharacters(in: .whitespacesAndNewlines)

            let blocks = result.blocks.compactMap { block -> IOSMLKitTextBlockPayload? in
                let cleanText = block.text.trimmingCharacters(in: .whitespacesAndNewlines)
                guard !cleanText.isEmpty else { return nil }
                return IOSMLKitTextBlockPayload(text: cleanText, frame: block.frame)
            }

            return IOSMLKitTextRecognitionResult(
                fullText: fullText,
                blocks: blocks
            )
        } catch {
            return nil
        }
    }
}
