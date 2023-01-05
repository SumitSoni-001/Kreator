package com.blog.kreator.utils

import android.view.Gravity
import es.dmoral.toasty.Toasty

class CustomToast {

    companion object{
        fun initialize(){
            Toasty.Config.getInstance()
                .tintIcon(true) // optional (apply textColor also to the icon)
//            .setToastTypeface(@NonNull Typeface typeface) // optional
                .setTextSize(14) // optional
                .allowQueue(true) // optional (prevents several Toastys from queuing)
                .setGravity(Gravity.BOTTOM) // optional (set toast gravity, offsets are optional)
                .supportDarkTheme(false) // optional (whether to support dark theme or not)
                .setRTL(false) // optional (icon is on the right)
                .apply()
        }

        fun reset(){
            Toasty.Config.reset()
        }
    }

}