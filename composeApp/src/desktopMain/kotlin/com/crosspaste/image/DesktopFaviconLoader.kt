package com.crosspaste.image

import com.crosspaste.app.AppFileType
import com.crosspaste.net.DesktopProxy
import com.crosspaste.path.UserDataPathProvider
import com.crosspaste.utils.PlatformLock
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.util.collections.*
import okio.Path
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.net.ProxySelector
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class DesktopFaviconLoader(
    private val userDataPathProvider: UserDataPathProvider,
) : ConcurrentLoader<String, Path>, FaviconLoader {

    private val logger = KotlinLogging.logger {}

    override val lockMap: ConcurrentMap<String, PlatformLock> = ConcurrentMap()

    private fun getGoogleIconUrl(host: String): String {
        return "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://$host&size=32"
    }

    private fun getDefaultIcoUrl(host: String): String {
        return "https://$host/favicon.ico"
    }

    private fun saveIco(
        url: String,
        path: Path,
    ): Path? {
        val httpsUrl = URL(url)

        val uri = httpsUrl.toURI()

        val proxy = DesktopProxy.getProxy(uri)

        try {
            val builder = HttpClient.newBuilder()

            (proxy.address() as InetSocketAddress?)?.let { address ->
                builder.proxy(ProxySelector.of(address))
            }

            val client = builder.build()

            val request =
                HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(5))
                    .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())

            if (response.statusCode() == 200) {
                FileOutputStream(path.toFile()).use { output ->
                    response.body().use { input ->
                        input.copyTo(output)
                    }
                }
                return path
            }
        } catch (e: Exception) {
            logger.warn(e) { "Failed to save favicon for $url" }
        }
        return null
    }

    override fun resolve(
        key: String,
        value: String,
    ): Path {
        return userDataPathProvider.resolve("$key.ico", AppFileType.FAVICON)
    }

    override fun exist(result: Path): Boolean {
        return result.toFile().exists()
    }

    override fun loggerWarning(
        value: String,
        e: Exception,
    ) {
        logger.warn(e) { "Failed to get favicon for $value" }
    }

    override fun save(
        key: String,
        value: String,
        result: Path,
    ) {
        saveIco(getDefaultIcoUrl(key), result)?.let {
            return
        } ?: run {
            saveIco(getGoogleIconUrl(key), result)
        }
    }

    override fun convertToKey(value: String): String {
        return Url(value).host
    }
}
