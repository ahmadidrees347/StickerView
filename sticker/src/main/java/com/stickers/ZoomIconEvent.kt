package com.stickers

import android.view.MotionEvent

class ZoomIconEvent : StickerIconEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {
    }

    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {
        event?.let {
            stickerView?.zoomAndRotateCurrentSticker(it)
        }
    }

    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        stickerView?.currentSticker?.let {
            stickerView.onStickerOperationListener
                ?.onStickerZoomFinished(it)
        }
    }
}