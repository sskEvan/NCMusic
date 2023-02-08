package com.ssk.ncmusic.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

/**
 * Created by ssk on 2023/2/8.
 */
object QrcodeUtil {

    /**
     * 创建二维码图片
     */
    fun createQrcodeBitmap(
        qrcodeStr: String,
        width: Int = 400,
        height: Int = 400,
    ): Bitmap? {
        // 用于设置QR二维码参数
        val qrParam = Hashtable<EncodeHintType, Any>()
        // 设置QR二维码的纠错级别——这里选择最高H级别
        qrParam[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        // 设置编码方式
        qrParam[EncodeHintType.CHARACTER_SET] = "UTF-8"

        try {
            val bitMatrix = MultiFormatWriter().encode(
                qrcodeStr,
                BarcodeFormat.QR_CODE, width, height, qrParam
            )
            // 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
            val w = bitMatrix.width
            val h = bitMatrix.height
            val data = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (bitMatrix[x, y]) data[y * w + x] = -0x1000000 // 黑色
                    else data[y * w + x] = 0x00ffffff // -1 相当于0xffffffff 白色
                }
            }

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            // 将上面的二维码颜色数组传入，生成图片颜色
            bitmap.setPixels(data, 0, w, 0, 0, w, h)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}