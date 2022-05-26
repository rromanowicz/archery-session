package ex.rr.archerysession

import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

class VersionChecker {

    private val client = OkHttpClient()
    private val url = "https://api.github.com/repos/rromanowicz/archery-session/releases/latest"

    fun exec(): String {
        return runBlocking { run() }
    }

    private suspend fun run(): String {
        var resp = ""
        var byteArr = ByteArray(0)
        val job = CoroutineScope(Dispatchers.IO).launch {
            var request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    resp = "Error: & $e"
                }

                override fun onResponse(call: Call, response: Response) {
                        resp = response.body?.string().toString()
                }
            })
            withTimeout(1000) {
                while (resp == "" && byteArr.isEmpty()) {
                    delay(100)
                }
            }
        }
        job.join()
        return resp
    }


}