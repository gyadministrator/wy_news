package com.android.wy.news.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.android.wy.news.common.Logger
import com.android.wy.news.util.ToastUtil
import java.util.Locale


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/5/27 14:01
  * @Version:        1.0
  * @Description:    
 */
object SpeechRecognizerManager {
    private var speechRecognizer: SpeechRecognizer? = null

    fun checkListen(context: Context): Boolean {
        val recognitionAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        if (!recognitionAvailable) {
            ToastUtil.show("当前没有语音识别服务")
        }
        return recognitionAvailable
    }

    fun startListen(context: Context) {
        if (checkListen(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognitionListener)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINESE.toString())
            speechRecognizer?.startListening(intent)
        }
    }

    fun stopListen() {
        //SpeechRecognizer 会自动检测到说话结束，但是用该方法可以手动停止Recognizer
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            Logger.i("onReadyForSpeech---->>>$p0")
        }

        override fun onBeginningOfSpeech() {
            Logger.i("onBeginningOfSpeech: ")
        }

        override fun onRmsChanged(p0: Float) {
            Logger.i("onRmsChanged---->>>$p0")
        }

        override fun onBufferReceived(p0: ByteArray?) {
            Logger.i("onBufferReceived---->>>$p0")
        }

        override fun onEndOfSpeech() {
            Logger.i("onEndOfSpeech: ")
        }

        override fun onError(p0: Int) {
            Logger.i("onError---->>>$p0")
            when (p0) {
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    ToastUtil.show("网络链接超时")
                }

                SpeechRecognizer.ERROR_NETWORK -> {
                    ToastUtil.show("网络错误或者没有权限")
                }

                SpeechRecognizer.ERROR_AUDIO -> {
                    ToastUtil.show("音频发生错误")
                }

                SpeechRecognizer.ERROR_CLIENT -> {
                    ToastUtil.show("连接出错")
                }

                SpeechRecognizer.ERROR_SERVER -> {
                    ToastUtil.show("服务器出错")
                }

                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    ToastUtil.show("什么也没有听到")
                }

                SpeechRecognizer.ERROR_NO_MATCH -> {
                    ToastUtil.show("没有匹配到合适的结果")
                }

                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                    ToastUtil.show("RecognitionService已经启动,请稍后")
                }

                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    ToastUtil.show("请赋予APP权限,另请（Android6.0以上）确认动态申请权限")
                }

                else -> {
                    ToastUtil.show("未知错误")
                }
            }
        }

        override fun onResults(p0: Bundle?) {
            Logger.i("onResults---->>>$p0")
            val key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult: ArrayList<String>? = p0?.getStringArrayList(key)
            var result = arrayOfNulls<String>(0)
            if (mResult != null) {
                result = arrayOfNulls(mResult.size)
            }
            mResult?.toArray(result)
            Logger.i("Recognize Result:$result")
        }

        override fun onPartialResults(p0: Bundle?) {
            Logger.i("onPartialResults---->>>$p0")
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
            Logger.i("onEvent---->>>$p0,$p1")
        }
    }
}