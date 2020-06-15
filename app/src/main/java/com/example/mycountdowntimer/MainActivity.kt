package com.example.mycountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
 }
