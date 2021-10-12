package com.stickers

import android.view.MotionEvent

class DeleteIconEvent : StickerIconEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {
    }

    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {
    }

    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        stickerView?.removeCurrentSticker()
    }
}
