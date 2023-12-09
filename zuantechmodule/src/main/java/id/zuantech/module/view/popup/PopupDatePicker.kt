package id.zuantech.module.view.popup

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class PopupDatePicker(private val context: Context) {

    var onDateSelected: ((date: Date?) -> Unit)? = null
    fun onDateSelected(onDateSelected: (date: Date?) -> Unit): PopupDatePicker {
        this.onDateSelected = onDateSelected
        return this
    }

    fun showDatePicker(dateInput: Date?) {
        val c = Calendar.getInstance()
        if (dateInput == null) {
            c.time = Date()
        } else {
            c.time = dateInput
        }
        val dialog = DatePickerDialog(
            context, { datePicker, year, month, dayOfMonth ->
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, month)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected?.let { it(c.time) }
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun showDatePicker(
        dateInput: Date?,
        minDate: Date,
        maxDate: Date
    ) {
        val c = Calendar.getInstance()
        if (dateInput == null) {
            c.time = Date()
        } else {
            c.time = dateInput
        }
        val dialog = DatePickerDialog(
            context, { datePicker, year, month, dayOfMonth ->
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, month)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected?.let { it(c.time) }
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]
        )
        dialog.datePicker.minDate = minDate.time
        dialog.datePicker.minDate = maxDate.time
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
}