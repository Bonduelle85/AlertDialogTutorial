package com.example.alertdialog

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alertdialog.databinding.ActivityMainBinding
import com.example.alertdialog.databinding.PartVolumeInputBinding
import com.example.alertdialog.databinding.SeekBarLayoutBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var selectedValue by Delegates.notNull<Int>()
    private var color by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSimpleDialog.setOnClickListener { simpleDialog() }
        binding.buttonSingleChoiceItems.setOnClickListener { singleChoiceDialog() }
        binding.buttonSingleChoiceItemsConfirm.setOnClickListener { singleChoiceDialogConfirm() }
        binding.buttonMultiChoiceItems.setOnClickListener { multiChoiceDialog() }
        binding.buttonMultiChoiceItemsConfirm.setOnClickListener { multiChoiceDialogConfirm() }
        binding.buttonCustomDialogInputValidation.setOnClickListener { customDialogInputValidation() }
        binding.buttonCustomSingleChoiceAlertDialog.setOnClickListener { customSingleChoiceAlertDialog() }
        binding.buttonCustomSeekBarAlertDialog.setOnClickListener { customSeekBarAlertDialog() }

        // getting state from Bundle
        selectedValue = savedInstanceState?.getInt(KEY_SELECTED_ITEM) ?: 5
        color = savedInstanceState?.getInt(KEY_COLOR) ?: Color.RED
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_ITEM, selectedValue)
        outState.putInt(KEY_COLOR, color)
    }

    private fun simpleDialog() {
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> showToast(getString(R.string.show_toast_ok))
                DialogInterface.BUTTON_NEGATIVE -> showToast(getString(R.string.show_toast_cancel))
                DialogInterface.BUTTON_NEUTRAL -> showToast(getString(R.string.show_toast_ignore))
            }
        }
        val dialog = AlertDialog.Builder(this).apply {
            this.setCancelable(false)
                .setIcon(R.drawable.ic_launcher_my_round_color_blue_foreground)
                .setTitle(getString(R.string.simple_dialog))
                .setMessage(getString(R.string.simple_dialog_msg))
                .setPositiveButton(getString(R.string.ok_positive_button), listener)
                .setNegativeButton(getString(R.string.cancel_negative_button), listener)
                .setNeutralButton(getString(R.string.ignore_neutral_button), listener)
                .setOnCancelListener { showToast(getString(R.string.show_toast_was_canceled)) }
                .setOnDismissListener { Log.d(TAG, "!!!Dialog dismissed!!!") }
                .create()
        }
        dialog.show()
    }

    private fun singleChoiceDialog() {
        // creating items using class ItemsSingleChoice
        val items = ItemsSingleChoice.createItems(selectedValue)
        // creating array of strings using .map
        val textItems = items.values
            .map { getString(R.string.volume_description, it) }
            .toTypedArray()
        // building dialog
        val dialog = AlertDialog.Builder(this).apply {
            this.setTitle("Choose you item!")
                .setSingleChoiceItems(textItems, items.currentIndex) { dialog, which ->
                    selectedValue = items.values[which]
                    binding.numberView.text = selectedValue.toString()
                    dialog.dismiss()
                }
                .create()
        }
        dialog.show()
    }

    private fun singleChoiceDialogConfirm() {
        val items = ItemsSingleChoice.createItems(selectedValue)
        val textItems = items.values
            .map { getString(R.string.volume_description, it) }
            .toTypedArray()

        val dialog = AlertDialog.Builder(this).apply {
            this.setTitle("Choose you item!")
                .setSingleChoiceItems(textItems, items.currentIndex, null)
                .setPositiveButton("Confirm"){dialog, _ ->
                    val index = (dialog as AlertDialog).listView.checkedItemPosition
                    selectedValue = items.values[index]
                    binding.numberView.text = selectedValue.toString()
                }
                .create()
        }
        dialog.show()
    }

    private fun multiChoiceDialog() {
        val colorItems = resources.getStringArray(R.array.colors)
        val colorComponents = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )
        val checkboxes = colorComponents
            .map { it > 0 }
            .toBooleanArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.set_your_color))
            .setMultiChoiceItems(colorItems, checkboxes) { _, which, isChecked ->
                colorComponents[which] = if (isChecked) 255 else 0
                this.color = Color.rgb(
                    colorComponents[0],
                    colorComponents[1],
                    colorComponents[2]
                )
                binding.colorView.setBackgroundColor(color)
            }
            .setPositiveButton(getString(R.string.set_color), null)
            .create()
        dialog.show()
    }

    private fun multiChoiceDialogConfirm() {
        val colorItems = resources.getStringArray(R.array.colors)
        val colorComponents = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )
        val checkboxes = colorComponents
            .map { it > 0 }
            .toBooleanArray()

        var color: Int = this.color
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.set_your_color))
            .setMultiChoiceItems(colorItems, checkboxes) { _, which, isChecked ->
                colorComponents[which] = if (isChecked) 255 else 0
                color = Color.rgb(
                    colorComponents[0],
                    colorComponents[1],
                    colorComponents[2]
                )
            }
            .setPositiveButton(R.string.set_color) { _, _ ->
                this.color = color
                binding.colorView.setBackgroundColor(color)
            }
            .create()
        dialog.show()
    }

    private fun customDialogInputValidation() {
        val dialogBinding = PartVolumeInputBinding.inflate(layoutInflater)
        dialogBinding.volumeInputEditText.setText(selectedValue.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.set_value))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.confirm), null)
            .create()
        dialog.setOnShowListener {
            dialogBinding.volumeInputEditText.requestFocus()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val enteredText = dialogBinding.volumeInputEditText.text.toString()
                if (enteredText.isBlank()) {
                    dialogBinding.volumeInputEditText.error = getString(R.string.empty_value)
                    return@setOnClickListener
                }
                val value = enteredText.toIntOrNull()
                if (value == null || value > 100) {
                    dialogBinding.volumeInputEditText.error = getString(R.string.invalid_value)
                    return@setOnClickListener
                }
                this.selectedValue = value
                binding.numberView.text = value.toString()
                dialog.dismiss()
            }
        }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun customSingleChoiceAlertDialog() {
        val volumeItems = ItemsSingleChoice.createItems(selectedValue)
        val adapter = ValueAdapter(volumeItems.values)

        var value = this.selectedValue
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.setup_value))
            .setSingleChoiceItems(adapter, volumeItems.currentIndex) { _, which ->
                value = adapter.getItem(which)
            }
            .setPositiveButton(R.string.confirm) { _, _ ->
                this.selectedValue = value
                binding.numberView.text = value.toString()
            }
            .create()
        dialog.show()
    }

    private fun customSeekBarAlertDialog() {
        val dialogBinding = SeekBarLayoutBinding.inflate(layoutInflater)
        dialogBinding.volumeSeekBar.progress = selectedValue
        val dialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(R.string.set_value)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.confirm) { _, _ ->
                selectedValue = dialogBinding.volumeSeekBar.progress
                binding.numberView.text = selectedValue.toString()
            }
            .create()
        dialog.show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val KEY_SELECTED_ITEM = "selected item"
        val KEY_COLOR = "color"
    }
}