package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupInputTextBinding

class PopupInputText(private val context: Context) {

    private val mBinding = PopupInputTextBinding.inflate(LayoutInflater.from(context))

    var isShowing = false
    var popupTitle = ""
    var positiveText = "Lanjut"

    var hint = ""
    fun hint(hint: String): PopupInputText {
        this.hint = hint
        return this
    }

    var value = ""
    fun value(value: String): PopupInputText {
        this.value = value
        return this
    }

    fun popupTitle(popupTitle: String): PopupInputText {
        this.popupTitle = popupTitle
        return this
    }

    var onClickPositive: ((String) -> Unit)? = null
    fun onClickPositive(onClickPositive: (String) -> Unit): PopupInputText {
        this.onClickPositive = onClickPositive
        return this
    }

    fun onClickPositive(positiveText: String, onClickPositive: (String) -> Unit): PopupInputText {
        this.positiveText = positiveText
        this.onClickPositive = onClickPositive
        this.mBinding.btnPositive.text = positiveText
        return this
    }
    
    var onClickNegative: (() -> Unit)? = null
    fun onClickNegative(onClickNegative: () -> Unit): PopupInputText {
        this.onClickNegative = onClickNegative
        return this
    }

    var alertDialogBuilder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    init {
        alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder?.setView(mBinding.root)
        alertDialog = alertDialogBuilder?.create()

        mBinding.apply {

            btnPositive.text = positiveText
            btnPositive.setOnClickListener {
                onClickPositive?.let { it1 -> it1(mBinding.etQuantity.text.toString().trim()) }
                isShowing = false
                alertDialogBuilder?.setView(null)
                alertDialog?.dismiss()
            }

            btnNegative.setOnClickListener {
                onClickNegative?.let { it1 -> it1() }
                isShowing = false
                alertDialogBuilder?.setView(null)
                alertDialog?.dismiss()
            }
        }

        alertDialog?.setCanceledOnTouchOutside(false)
        alertDialog?.setCancelable(false)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        mBinding.etQuantity.hint = hint
        mBinding.etQuantity.setText(value)
        mBinding.tvTitle.text = popupTitle
        alertDialog?.show()
        isShowing = true
    }

}