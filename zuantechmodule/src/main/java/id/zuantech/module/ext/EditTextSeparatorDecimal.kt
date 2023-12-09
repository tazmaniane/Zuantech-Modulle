package id.zuantech.module.ext

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class EditTextSeparatorDecimal(
    private val editText: EditText
) : TextWatcher {

    companion object {
        val TAG = EditTextSeparatorDecimal::class.java.simpleName
    }

    interface EditTextSeparatorDecimalListener {
        fun afterTextChanged(double: Double)
    }

    private var listener: EditTextSeparatorDecimalListener? = null
    fun setListener(listener: EditTextSeparatorDecimalListener) : EditTextSeparatorDecimal {
        this.listener = listener
        return this
    }

    private var maxValue: Double = 0.0
    fun maxValue(maxValue: Double) : EditTextSeparatorDecimal {
        this.maxValue = maxValue
        return this
    }

    private var formatter: DecimalFormat

    init {
        formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")) as DecimalFormat
        with(formatter) {
            decimalFormatSymbols = decimalFormatSymbols.apply {
                currencySymbol = ""
            }
            maximumFractionDigits = 0
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        editText.removeTextChangedListener(this)

        try {
            val inilen: Int = editText.text.length
            var n = s.toString().toDoubleNumber()

            if(maxValue > 0.0 && n > maxValue){
                n = maxValue
            }

            val cp: Int = editText.selectionStart
            editText.setText(formatter.format(n))
            val endlen: Int = editText.text.length
            val sel = cp + (endlen - inilen)
//            if (sel > 0 && sel <= editText.text.length) {
//                editText.setSelection(sel)
//            } else {
//                // place cursor at the end?
//                editText.setSelection(editText.text.length - 1)
//            }
            editText.setSelection(editText.text.toString().length)
            Log.d(TAG, "formatter: ${editText.text.toString()}")

        } catch (nfe: NumberFormatException) {
            Log.d(TAG, "NumberFormatException: ${nfe.localizedMessage}")
        } catch (e: ParseException) {
            Log.d(TAG, "ParseException: ${e.localizedMessage}")
        }

        listener?.afterTextChanged(editText.text.toString().trim().toDoubleNumber())
        editText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {

    }
}
