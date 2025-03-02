package com.visioncameraobjectdetection

import android.graphics.Rect
import android.media.Image
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.mrousavy.camera.frameprocessors.Frame
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin
import com.mrousavy.camera.frameprocessors.VisionCameraProxy
import java.util.ArrayList

class ObjectDetectionFrameProcessorPlugin(proxy: VisionCameraProxy, options: Map<String, Any>?) :
        FrameProcessorPlugin() {
    private var objectDetector: ObjectDetector

    init {
        val multipleObjects = (options?.get("detectionType") as? String) == "multiple"
        val classifyObjects = (options?.get("classifyObjects") as? Boolean) ?: false
        val mode =
                when (options?.get("mode")) {
                    "image" -> ObjectDetectorOptions.SINGLE_IMAGE_MODE
                    "stream" -> ObjectDetectorOptions.STREAM_MODE
                    else -> ObjectDetectorOptions.SINGLE_IMAGE_MODE
                }

        val objectDetectorOptionsBuilder = ObjectDetectorOptions.Builder().setDetectorMode(mode)

        if (multipleObjects) {
            objectDetectorOptionsBuilder.enableMultipleObjects()
        }

        if (classifyObjects) {
            objectDetectorOptionsBuilder.enableClassification()
        }

        this.objectDetector = ObjectDetection.getClient(objectDetectorOptionsBuilder.build())
    }

    override fun callback(frame: Frame, arguments: Map<String, Any>?): ArrayList<Any> {

        val mediaImage: Image = frame.image
        val image =
                InputImage.fromMediaImage(mediaImage, frame.imageProxy.imageInfo.rotationDegrees)

        val task: Task<List<DetectedObject>> = this.objectDetector.process(image)

        try {
            val detectedObjects: List<DetectedObject> = Tasks.await(task)
            val array = createDetectedObjectsArray(detectedObjects)
            return array.toArrayList()
        } catch (ex: Exception) {}

        return arrayListOf()
    }

    private fun createLabelsArray(labels: List<DetectedObject.Label>): WritableNativeArray {
        return WritableNativeArray().apply {
            labels
                    .map { label ->
                        WritableNativeMap().apply {
                            putString("text", label.text)
                            putDouble("confidence", label.confidence.toDouble())
                            putInt("index", label.index)
                        }
                    }
                    .forEach { pushMap(it) }
        }
    }

    private fun createDetectedObjectsArray(
            detectedObjects: List<DetectedObject>
    ): WritableNativeArray {
        return WritableNativeArray().apply {
            detectedObjects
                    .map { detectedObject ->
                        WritableNativeMap().apply {
                            detectedObject.trackingId?.let { trackingId ->
                                putInt("trackingId", trackingId)
                            }

                            putMap("bounds", createBoundsMap(detectedObject.boundingBox))
                            putArray("labels", createLabelsArray(detectedObject.labels))
                        }
                    }
                    .forEach { pushMap(it) }
        }
    }

    fun createBoundsMap(bounds: Rect): WritableNativeMap {
        return WritableNativeMap().apply {
            putDouble("x", bounds.exactCenterX().toDouble())
            putDouble("y", bounds.exactCenterY().toDouble())
            putInt("centerX", bounds.centerX())
            putInt("centerY", bounds.centerY())
            putInt("width", bounds.width())
            putInt("height", bounds.height())
            putInt("top", bounds.top)
            putInt("left", bounds.left)
            putInt("bottom", bounds.bottom)
            putInt("right", bounds.right)
        }
    }
}
