package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupSuccessBinding

class PopupSuccess(private val context: Context) {

    private val mBinding = PopupSuccessBinding.inflate(LayoutInflater.from(context))

    var popupTitle = "Berhasil"
    var popupMessage = ""

    fun popupTitle(popupTitle: String): PopupSuccess {
        this.popupTitle = popupTitle
        return this
    }

    fun popupMessage(popupMessage: String): PopupSuccess {
        this.popupMessage = popupMessage
        return this
    }

    var onClickPositive: (() -> Unit)? = null
    fun onClickPositive(onClickPositive: () -> Unit): PopupSuccess {
        this.onClickPositive = onClickPositive
        return this
    }

    fun show() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(mBinding.root)
        val alertDialog = alertDialogBuilder.create()

        mBinding.apply {
            tvTitle.text = popupTitle
            tvMessage.text = popupMessage

            btnPositive.setOnClickListener {
                onClickPositive?.let { it1 -> it1() }
                alertDialog.dismiss()
            }
        }

        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}