package com.example.bicatakotline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Completable.timer(3, TimeUnit.SECONDS,AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this@SplashScreenActivity,"Splash Screen run done!",Toast.LENGTH_SHORT).show()
            })
    }
}