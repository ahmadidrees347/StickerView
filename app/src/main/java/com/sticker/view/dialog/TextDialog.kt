package com.sticker.view.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import com.sticker.view.databinding.TextDialogBinding

class TextDialog(
    context: Activity,
    private val listener: (String) -> Unit
) : Dialog(context) {
    private lateinit var binding: TextDialogBinding
    private val limit = 150

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TextDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        setLimitText()

        with(binding) {
            imgClose.setOnClickListener {
                dismiss()
            }
            edtText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    setLimitText()
                }

            })
            imgDone.setOnClickListener {
                if (edtText.text.toString().isNotEmpty()) {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    listener.invoke(edtText.text.toString().trim())
                }
                dismiss()
            }
        }
    }

    private fun setLimitText() {
        val textLimit = binding.edtText.text.toString().length
        val text = "$textLimit / $limit"
        binding.txtLimit.text = text
    }
}