package pusan.university.plato_calendar.data.util.crawler

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import pusan.university.plato_calendar.domain.entity.Dormitory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DormMealPlanCrawler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    suspend fun fetchMealPlan(dormitory: Dormitory): String = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                settings.loadsImagesAutomatically = false
                settings.blockNetworkImage = true
            }

            var destroyed = false

            fun destroyWebView() {
                if (!destroyed) {
                    destroyed = true
                    webView.stopLoading()
                    webView.clearHistory()
                    webView.removeAllViews()
                    webView.destroy()
                }
            }

            val timeoutRunnable = Runnable {
                if (continuation.isActive) {
                    destroyWebView()
                    continuation.resumeWith(Result.failure(Exception("크롤링 타임아웃 (30초)")))
                }
            }
            mainHandler.postDelayed(timeoutRunnable, 30_000)

            fun pollForContent(onReady: (String) -> Unit) {
                if (!continuation.isActive) return
                webView.evaluateJavascript(
                    "(function(){ var t=document.querySelector('tbody#mealPlanTable'); return (t&&t.children.length>0)?t.outerHTML:''; })()"
                ) { value ->
                    val html = try {
                        JSONArray("[$value]").getString(0)
                    } catch (e: Exception) {
                        ""
                    }
                    if (html.isNotEmpty()) {
                        onReady(html)
                    } else {
                        mainHandler.postDelayed({ pollForContent(onReady) }, 500)
                    }
                }
            }

            var pollStarted = false
            webView.webViewClient = object : WebViewClient() {

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true && continuation.isActive) {
                        mainHandler.removeCallbacks(timeoutRunnable)
                        destroyWebView()
                        continuation.resumeWith(Result.failure(Exception("웹페이지 로드 실패: ${error?.description}")))
                    }
                }

                override fun onPageFinished(view: WebView, url: String) {
                    if (pollStarted) return
                    pollStarted = true
                    mainHandler.postDelayed({
                        if (dormitory.tabId != null) {
                            webView.evaluateJavascript(
                                """
                                (function(){
                                    var t = document.querySelector('tbody#mealPlanTable');
                                    if(t) t.innerHTML = '';
                                    fn_getMealPlanPdormViewTab('${dormitory.tabId}');
                                })();
                                """.trimIndent(),
                                null,
                            )
                        }
                        pollForContent { html ->
                            mainHandler.removeCallbacks(timeoutRunnable)
                            destroyWebView()
                            if (continuation.isActive) continuation.resumeWith(Result.success(html))
                        }
                    }, 1_000)
                }
            }

            webView.loadUrl("https://dorm.pusan.ac.kr/${dormitory.siteId}/page?menuCD=${dormitory.campusId}")

            continuation.invokeOnCancellation {
                mainHandler.post {
                    mainHandler.removeCallbacks(timeoutRunnable)
                    destroyWebView()
                }
            }
        }
    }
}