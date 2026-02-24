package pusan.university.plato_calendar.presentation.util.component

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    url: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }

                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                webViewClient =
                    object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: android.webkit.WebResourceRequest?,
                        ): Boolean {
                            val requestUrl = request?.url.toString()

                            return when {
                                requestUrl.startsWith("intent://") -> {
                                    try {
                                        val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        val packageName =Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME).`package`

                                        if (!packageName.isNullOrBlank()) {
                                            val marketIntent =
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    "market://details?id=$packageName".toUri(),
                                                )
                                            context.startActivity(marketIntent)
                                        }
                                    }
                                    true
                                }

                                requestUrl.startsWith("market://") -> {
                                    val marketIntent = Intent(Intent.ACTION_VIEW, requestUrl.toUri())
                                    context.startActivity(marketIntent)
                                    true
                                }

                                else -> false
                            }
                        }
                    }

                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
    )
}
