/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 2 May 2025
 */

package ru.urbanmedic.testapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogHelper {

    fun showDialog(
        context: Context,
        title: String?,
        message: String?,
        positiveBtnStringId: Int?,
        negativeBtnStringId: Int?,
        positiveCallback: () -> Unit = {},
        negativeCallback: () -> Unit = {}
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        if (positiveBtnStringId != null) {
            builder.setPositiveButton(positiveBtnStringId){ _, _ ->
                positiveCallback()
            }
        }
        if (negativeBtnStringId != null) {
            builder.setNegativeButton(negativeBtnStringId){ _, _ ->
                negativeCallback()
            }
        }
        builder.show()
    }

    fun showDialog(
        context: Context,
        title: Int,
        message: String,
        positiveBtnStringId: Int,
        negativeBtnStringId: Int?,
        positiveCallback: () -> Unit = {},
        negativeCallback: () -> Unit = {}
    ) {
        with(context) {
            showDialog(
                this,
                resources.getString(title),
                message,
                positiveBtnStringId,
                negativeBtnStringId,
                positiveCallback,
                negativeCallback
            )
        }
    }

    fun showDialog(context: Context, titleId: Int, messageId: Int, positiveBtnStrId: Int) {
        with(context){
            showDialog(
                this,
                resources.getString(titleId),
                resources.getString(messageId),
                positiveBtnStrId,
                null
            )
        }
    }

}