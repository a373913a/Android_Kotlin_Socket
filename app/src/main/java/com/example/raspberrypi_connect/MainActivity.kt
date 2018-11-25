package com.example.raspberrypi_connect

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.Socket

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val packageManager = packageManager

        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            Log.e("PackageManager","This device doesn't have a mic")
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            Toast.makeText(this,"RECORD_AUDIO錯誤",Toast.LENGTH_SHORT).show()
            return
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
            Toast.makeText(this,"WRITE_EXTERNAL_STORAGE錯誤",Toast.LENGTH_SHORT).show()
            return
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),0)
            Toast.makeText(this,"INTERNET錯誤",Toast.LENGTH_SHORT).show()
            return
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),0)
            Toast.makeText(this,"READ_EXTERNAL_STORAGE錯誤",Toast.LENGTH_SHORT).show()
            return
        }

    }
    lateinit var soundFile: File
    lateinit var mediaRecorder: MediaRecorder

    lateinit var file: File
//    val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.RECORD_AUDIO)
    var recordBtnStatus = "RECORD"
    var recordingStatus = true

    private fun createFile() {
        file = File(Environment.getExternalStorageDirectory(),"Record")
        if (!file.exists()) {
            file.mkdirs()
            Toast.makeText(this, "folder is create", Toast.LENGTH_SHORT).show()
        }
        soundFile = File.createTempFile ("birdRecording", ".3gp", file)
    }

    private fun startRecording() {
        createFile()
        sendvoice_btn.isEnabled = false
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setOutputFile(soundFile.absolutePath)

        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder.prepare()

        mediaRecorder.start()
        Toast.makeText(this,"開始錄音" + soundFile.absolutePath.toString(),Toast.LENGTH_SHORT).show()

    }
    private fun stopRecording() {
        if (recordingStatus) {
            mediaRecorder.stop()
            mediaRecorder.release()

            sendvoice_btn.isEnabled = true
            Toast.makeText(this,"結束錄音",Toast.LENGTH_SHORT).show()
        }
    }
    fun recordOrStopCheck(v:View) {
        when (recordBtnStatus) {
            "RECORD" -> {
                startRecording()
                recordingStatus = true
                recordStopBtn.text = "STOP"
                recordBtnStatus = "STOP"
            }

            "STOP" -> {
                stopRecording()
                recordingStatus = false
                recordStopBtn.text = "RECORD"
                recordBtnStatus = "RECORD"
            }
        }
    }
    fun connect(){
        Thread{
            val soc = Socket("192.168.0.16", 9999)
            val dout = DataOutputStream(soc.getOutputStream())
            dout.writeBytes("this is test word")
            dout.flush()
            dout.close()
            soc.close()
        }.start()
    }

    fun connect_withToast(v:View){

        try{
            connect()

            Toast.makeText(this,"已傳送",Toast.LENGTH_SHORT).show()
        }
        catch (e:Exception){

            Toast.makeText(this,"err",Toast.LENGTH_SHORT).show()
        }

    }
    fun sendvoice(v:View){
        Thread{
            val soc = Socket("192.168.0.16", 9999)
            val dout = DataOutputStream(soc.getOutputStream())
            var myrecord = BufferedInputStream(FileInputStream(soundFile.absolutePath)).readBytes()

            dout.write(myrecord)
            dout.flush()
            dout.close()
            soc.close()
        }.start()

    }


}
