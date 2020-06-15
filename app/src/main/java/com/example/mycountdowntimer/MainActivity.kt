package com.example.mycountdowntimer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()  {

    //SoundPoolクラスのインスタンスとサウンドファイルのリソースIDを保持するプロパティを宣言している。
    //soundPoolは後で初期化するので、lateinit修飾子をつける必要がある。
    private lateinit var soundPool: SoundPool
    private var soundResId = 0

    //MainActicityの中に、CountDownTimerを継承したMyCountDownTimerクラスを作っている。
    //インナークラスになっており、外部の変数、ここではレイアウトに配置されたビューのID名を参照できる。
    //元々、CountDownTimerクラスは2つのLong型を引数に持つコンストラクタを持っている。
    //millisInFutureはタイマーの残り時間をミリ秒で指定する
    //countDownInterval onTickメソッドを実行する間隔をミリ秒で指定する
    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        //現在カウントダウン中か停止かを表すフラグ
        //このアプリではカウントダウンのスタート/ストップを1つのフローティングアクションボタンで行うので、このプロパティが必要になる
        var isRunning = false


        override fun onTick(millisUntilFinished: Long) {
            //ミリ秒単位のタイマーの残り時間から分と秒を取り出して、テキストビューに表示している。
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            //ここで使用している.formatは、Kotlinに用意されている拡張関数で、フォーマット文字列.format(値,値...)という風に
            //使って、値をフォーマットされた文字列に変換することができる。
            //ここでは数値を「分:秒」の形式に変換している。「%1d」の「%1」は引数リストの1番目(minute)、
            //「d」は整数で表示を意味している。また、「%2$02d」の「%2」は引数リストの2番目(second)、「02d」は2桁の整数で表示を意味する
            timerText.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish() {
            //TextViewに0:00を表示
            timerText.text = "0:00"
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TextViewに「3:00」を表示
        timerText.text = "3:00"
        //CountDownTimerを継承したクラスのインスタンスを生成
        //ここではタイマーの継続時間として3分、onTickメソッドが呼ばれる感覚として0.1秒を想定している。
        //
        val timer = MyCountDownTimer(3 * 60 * 1000, 100)
        //フローティングアクションボタンがタップされた時の処理
        playStop.setOnClickListener{
            timer.isRunning = when (timer.isRunning) {
                //カウントダウン中の場合は、CountDownTimerクラスのcancelメソッドでカウントダウンを停止し、
                //フローティングアクションボタンに表示する画像をプレイマークに設定している。
                true -> {
                    timer.cancel()
                    playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    false
                }
                //停止中だった場合は、startメソッドでカウントダウンを開始し、フローティングアクションボタンの画像を
                //ストップマークに変更している。
                false -> {
                    timer.start()
                     playStop.setImageResource(
                            R.drawable.ic_baseline_stop_24
                        )
                        true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //アクティビティが画面に表示された時に実行されるonResumeメソッド内で、SoundPoolのインスタンスを作成する。
        //SoundPoolクラスの今コンストラクタでmaxStreamsは同時に再生することのできる音源数,
        //streamTypeはオーディオのストリームタイプを指定、srcQualityhは品質を指定します。
        //第2引数のストリームタイプはAudioManagerクラスに定義された定数で指定。STREAM_ALARMはアラーム音のためのストリーム
        soundPool = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //@Suppress("DEPRECATION")非推奨のメソッドを使っているが、対応済みなので検査不要ということを明示している。
            @Suppress("DEPRECATION")
            SoundPool(2, AudioManager.STREAM_ALARM, 0)
        } else {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        }
        //loadメソッドで、リソースからサウンドファイルを読み込む
        //第1引数でアクティビティを指定、第2引数でサウンドファイルのリソースId,proorityは音の優先順位だが、気にしなくていい。
        soundResId = soundPool.load(this, R.raw.bellsound, 1)
    }

    override fun onPause() {
        super.onPause()
        //アクティビティが非表示になった時に呼ばれるonPause内で、releaseメソッドを使ってメモリの解放をしている。
        soundPool.release()
    }


}
