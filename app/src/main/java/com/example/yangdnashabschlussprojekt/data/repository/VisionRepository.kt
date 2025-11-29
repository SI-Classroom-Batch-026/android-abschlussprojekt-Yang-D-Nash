package com.example.yangdnashabschlussprojekt.data.repository

import android.graphics.Rect
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class VisionRepository(
    private val apiKey: String,
    private val api: VisionApiService
) {

    fun detectText(
        base64Image: String,
        onResult: (String, List<Rect>) -> Unit
    ) {
        val url = "https://vision.googleapis.com/v1/images:annotate?key=$apiKey"

        val feature = JSONObject().apply { put("type", "TEXT_DETECTION") }
        val image = JSONObject().apply { put("content", base64Image) }
        val requestObj = JSONObject().apply {
            put("image", image)
            put("features", JSONArray().put(feature))
        }
        val rootObj = JSONObject().apply {
            put("requests", JSONArray().put(requestObj))
        }

        val body = rootObj.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder().url(url).post(body).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("VisionRepository", "Request failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body.string().let { jsonResponse ->
                    try {
                        val json = JSONObject(jsonResponse)
                        val responses = json.getJSONArray("responses")
                        if (responses.length() > 0) {
                            val fullTextAnnotation = responses.getJSONObject(0)
                                .optJSONObject("fullTextAnnotation")
                            val text = fullTextAnnotation?.optString("text") ?: ""

                            val boxes = mutableListOf<Rect>()
                            fullTextAnnotation?.optJSONArray("pages")?.let { pages ->
                                for (i in 0 until pages.length()) {
                                    val blocks = pages.getJSONObject(i).getJSONArray("blocks")
                                    for (j in 0 until blocks.length()) {
                                        val block = blocks.getJSONObject(j)
                                        val paragraphs = block.getJSONArray("paragraphs")
                                        for (k in 0 until paragraphs.length()) {
                                            val words = paragraphs.getJSONObject(k).getJSONArray("words")
                                            for (l in 0 until words.length()) {
                                                val boundingPoly = words.getJSONObject(l)
                                                    .getJSONObject("boundingBox")
                                                    .getJSONArray("vertices")
                                                val left = boundingPoly.getJSONObject(0).optInt("x", 0)
                                                val top = boundingPoly.getJSONObject(0).optInt("y", 0)
                                                val right = boundingPoly.getJSONObject(2).optInt("x", 0)
                                                val bottom = boundingPoly.getJSONObject(2).optInt("y", 0)
                                                boxes.add(Rect(left, top, right, bottom))
                                            }
                                        }
                                    }
                                }
                            }

                            onResult(text, boxes)
                        }
                    } catch (e: Exception) {
                        Log.e("VisionRepository", "Parsing failed", e)
                    }
                }
            }
        })
    }
}
