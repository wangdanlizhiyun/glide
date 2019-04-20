package com.glide

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this).load(
            "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2311096300,1092192741&fm=173&app=49&f=JPEG?w=218&h=146&s=DFACAE4576DF086C7EBC718303007082"
        )
            .into(iv1)
    }

}
