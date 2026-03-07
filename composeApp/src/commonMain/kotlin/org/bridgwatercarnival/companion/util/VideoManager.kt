package org.bridgwatercarnival.companion.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Manages video playback and shuffling for the app
 */
object VideoManager {
    // List of YouTube video IDs
    private val videoIds = listOf(
        "TO7jGuIctzk", //Renegades
        "I-28LGMjCGo", //Wills
        "cKwdKoxI-js", //Vagabonds
        "EhpI5nVkWu0", //Ramblers
        "P5SGLJWqb_4", //Marketeers
        "DvmMDsYEUa0",//Lime Kiln
        "uLArC4HBfRE",//Griffens
        "JxSGL7wy3Gs",//Gremlins
        "COS6zsP56DI",//Centurion
        "edFKVD4rSAw",//Cavaliers
        "myHuU9J9ZLw",//Crusaders
        "seBUZ1rVHtA",//British Flag
        "ZJJq1ehEEY4",//Marina
        "IXUThSsVnQY"//New market
    )
    
    // Current video state
    private val _currentVideoId = MutableStateFlow(videoIds.first())
    val currentVideoId: StateFlow<String> = _currentVideoId.asStateFlow()
    
    // Embedded HTML for YouTube video
    fun getEmbeddedVideoHtml(videoId: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        width: 100%;
                        height: 100%;
                        background-color: #000;
                        overflow: hidden;
                    }
                    .video-container {
                        position: relative;
                        width: 100%;
                        height: 100%;
                        overflow: hidden;
                    }
                    iframe {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        border: 0;
                    }
                </style>
            </head>
            <body>
                <div class="video-container">
                    <iframe 
                        src="https://www.youtube.com/embed/$videoId?autoplay=1&mute=0&controls=1&showinfo=0&rel=0&enablejsapi=1&playsinline=1"
                        frameborder="0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen>
                    </iframe>
                </div>
                <script>
                    // Fix for YouTube embedding
                    document.addEventListener('DOMContentLoaded', function() {
                        var iframe = document.querySelector('iframe');
                        if (iframe) {
                            // Force iframe to take full size
                            iframe.style.width = '100%';
                            iframe.style.height = '100%';
                            iframe.style.position = 'absolute';
                            iframe.style.top = '0';
                            iframe.style.left = '0';
                        }
                        
                        // Enable all elements to be clickable
                        var style = document.createElement('style');
                        style.textContent = '* { pointer-events: auto !important; }';
                        document.head.appendChild(style);
                    });
                </script>
            </body>
            </html>
        """.trimIndent()
    }
    
    // Extracts YouTube video ID from a URL
    fun extractVideoId(url: String): String? {
        val regex = Regex("(?:youtube\\.com/watch\\?v=|youtu\\.be/)([\\w-]+)")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.getOrNull(1)
    }
    
    // Shuffles to a new random video
    fun shuffleVideo() {
        val currentId = _currentVideoId.value
        var nextId = currentId
        
        // Make sure we get a different video
        while (nextId == currentId) {
            nextId = videoIds[Random.nextInt(videoIds.size)]
        }
        
        _currentVideoId.value = nextId
    }
    
    // Goes to the next video in sequence
    fun nextVideo() {
        val currentId = _currentVideoId.value
        val currentIndex = videoIds.indexOf(currentId)
        
        // Calculate next index, wrapping around to the beginning if at the end
        val nextIndex = if (currentIndex < videoIds.size - 1) currentIndex + 1 else 0
        
        _currentVideoId.value = videoIds[nextIndex]
    }
} 