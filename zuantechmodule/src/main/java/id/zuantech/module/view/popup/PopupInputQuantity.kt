package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupInputQuantityBinding
import id.zuantech.module.ext.EditTextSeparatorDecimal
import id.zuantech.module.ext.toStringNumber

class PopupInputQuantity(private val context: Context) {

    private val mBinding = PopupInputQuantityBinding.inflate(LayoutInflater.from(context))

    var isShowing = false
    var popupTitle = "Masukkan Jumlah"
    var positiveText = "Lanjut"

    var value = 0.0
    fun value(value: Double): PopupInputQuantity {
        this.value = value
        return this
    }

    var maxValue = 0.0
    fun maxValue(maxValue: Double): PopupInputQuantity {
        this.maxValue = maxValue
        return this
    }

    fun popupTitle(popupTitle: String): PopupInputQuantity {
        this.popupTitle = popupTitle
        return this
    }

    var onClickPositive: ((qty: Double) -> Unit)? = null
    fun onClickPositive(onClickPositive: (qty: Double) -> Unit): PopupInputQuantity {
        this.onClickPositive = onClickPositive
        return this
    }

    fun onClickPositive(positiveText: String, onClickPositive: (qty: Double) -> Unit): PopupInputQuantity {
        this.positiveText = positiveText
        this.onClickPositive = onClickPositive
        this.mBinding.btnPositive.text = positiveText
        return this
    }
    
    var onClickNegative: (() -> Unit)? = null
    fun onClickNegative(onClickNegative: () -> Unit): PopupInputQuantity {
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

            btnPlus.setOnClickListener {
                value +=1
                mBinding.etQuantity.setText(value.toLong().toString())
            }

            btnMinus.setOnClickListener {
                if(value >= 1.0){
                    value -=1
                    mBinding.etQuantity.setText(value.toLong().toString())
                }
            }

            btnPositive.text = positiveText
            btnPositive.setOnClickListener {
                val qty = mBinding.etQuantity.text.toString().trim().toStringNumber().toDouble()
                onClickPositive?.let { it1 -> it1(qty) }
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
        mBinding.etQuantity.addTextChangedListener(
            EditTextSeparatorDecimal(mBinding.etQuantity)
                .maxValue(maxValue)
                .setListener(object : EditTextSeparatorDecimal.EditTextSeparatorDecimalListener {
                    override fun afterTextChanged(double: Double) {
                        value = double
                    }
                })
        )
        mBinding.etQuantity.setText(value.toStringNumber())
        mBinding.tvTitle.text = popupTitle
        alertDialog?.show()
        isShowing = true
    }

}