package com.raillylinker.module_common.util_components.impls

import com.raillylinker.module_common.util_components.GifUtil
import org.springframework.stereotype.Component
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.math.roundToInt

// [Gif 관련 유틸 오브젝트]
@Component
class GifUtilImpl : GifUtil {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (Gif input 을 프레임 리스트로 분리)
    override fun decodeGif(inputStream: InputStream): ArrayList<GifUtil.GifFrame> {
        var frameCount = 0
        val frameWidth: Int
        val frameHeight: Int
        val frames = ArrayList<GifUtil.GifFrame>()
        val maxStackSize = 4096
        val bufferedInputStream: BufferedInputStream?
        var gct: IntArray? = null
        var lct: IntArray?
        var act: IntArray?
        val bgIndex: Int
        var bgColor = 0
        var lastBgColor = 0
        var ix: Int
        var iy: Int
        var iw: Int
        var ih: Int
        var lastRect: Rectangle? = null
        var lastImage: BufferedImage? = null
        val block = ByteArray(256)
        var blockSize: Int
        var dispose = 0
        var lastDispose = 0
        var transparency = false
        var delay = 0
        var transIndex = 0
        var prefix: ShortArray? = null
        var suffix: ByteArray? = null
        var pixelStack: ByteArray? = null
        var pixels: ByteArray? = null
        val statusOk = 0
        val statusFormatError = 1
        var status = statusOk
        var newInputStream = inputStream
        if (newInputStream !is BufferedInputStream) newInputStream = BufferedInputStream(newInputStream)
        bufferedInputStream = newInputStream

        var id = ""
        for (i in 0..5) {
            id += kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            }.toChar()
        }
        if (!id.startsWith("GIF")) {
            throw RuntimeException("Gif 가 아닙니다.")
        } else {
            frameWidth = (kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            } or (kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            } shl 8))
            frameHeight = (kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            } or (kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            } shl 8))

            val packed = kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            }
            val gctFlag = packed and 0x80 != 0
            val gctSize = 2 shl (packed and 7)
            bgIndex = kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            }
            kotlin.run {
                var curByte = 0
                try {
                    curByte = bufferedInputStream.read()
                } catch (e: IOException) {
                    status = statusFormatError
                }
                curByte
            }
            if (gctFlag && status == statusOk) {
                gct = kotlin.run {
                    val nbytes = 3 * gctSize
                    var tab: IntArray? = null
                    val c = ByteArray(nbytes)
                    var n = 0
                    try {
                        n = bufferedInputStream.read(c)
                    } catch (_: IOException) {
                    }
                    if (n < nbytes) {
                        status = statusFormatError
                    } else {
                        tab = IntArray(256)
                        var i = 0
                        var j = 0
                        while (i < gctSize) {
                            val r = c[j++].toInt() and 0xff
                            val g = c[j++].toInt() and 0xff
                            val b = c[j++].toInt() and 0xff
                            tab[i++] = -0x1000000 or (r shl 16) or (g shl 8) or b
                        }
                    }
                    tab
                }
                bgColor = gct!![bgIndex]
            }
        }

        if (status == statusOk) {
            kotlin.run {
                // read GIF file content blocks
                var done = false
                while (!(done || (status != statusOk))) {
                    var code = kotlin.run {
                        var curByte = 0
                        try {
                            curByte = bufferedInputStream.read()
                        } catch (e: IOException) {
                            status = statusFormatError
                        }
                        curByte
                    }
                    when (code) {
                        0x2C -> {
                            ix = (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } or (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } shl 8))
                            iy = (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } or (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } shl 8))
                            iw = (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } or (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } shl 8))
                            ih = (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } or (kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            } shl 8))
                            val packed = kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            }
                            val lctFlag = packed and 0x80 != 0
                            val interlace = packed and 0x40 != 0
                            val lctSize = 2 shl (packed and 7)
                            if (lctFlag) {
                                lct = kotlin.run {
                                    val nbytes = 3 * lctSize
                                    var tab1: IntArray? = null
                                    val c = ByteArray(nbytes)
                                    var n = 0
                                    try {
                                        n = bufferedInputStream.read(c)
                                    } catch (_: IOException) {
                                    }
                                    if (n < nbytes) {
                                        status = statusFormatError
                                    } else {
                                        tab1 = IntArray(256)
                                        var i = 0
                                        var j = 0
                                        while (i < lctSize) {
                                            val r = c[j++].toInt() and 0xff
                                            val g = c[j++].toInt() and 0xff
                                            val b = c[j++].toInt() and 0xff
                                            tab1[i++] = -0x1000000 or (r shl 16) or (g shl 8) or b
                                        }
                                    }
                                    tab1
                                }
                                act = lct
                            } else {
                                act = gct
                                if (bgIndex == transIndex) bgColor = 0
                            }
                            var save = 0
                            if (transparency) {
                                save = act!![transIndex]
                                act!![transIndex] = 0
                            }
                            if (act == null) {
                                status = statusFormatError
                            }
                            if ((status != statusOk)) {
                                break
                            }
                            val nullCode = -1
                            val npix = iw * ih
                            var available: Int
                            val clear: Int
                            var codeMask: Int
                            var codeSize: Int
                            var inCode: Int
                            var oldCode: Int
                            var code1: Int
                            var count: Int
                            var datum: Int
                            var first: Int
                            var bi: Int
                            if (pixels == null || pixels!!.size < npix) {
                                pixels = ByteArray(npix)
                            }
                            if (prefix == null) prefix = ShortArray(maxStackSize)
                            if (suffix == null) suffix = ByteArray(maxStackSize)
                            if (pixelStack == null) pixelStack = ByteArray(maxStackSize + 1)

                            val dataSize: Int = kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            }
                            clear = 1 shl dataSize
                            val endOfInformation: Int = clear + 1
                            available = clear + 2
                            oldCode = nullCode
                            codeSize = dataSize + 1
                            codeMask = (1 shl codeSize) - 1
                            code1 = 0
                            while (code1 < clear) {
                                prefix!![code1] = 0
                                suffix!![code1] = code1.toByte()
                                code1++
                            }

                            bi = 0
                            var pi = 0
                            var top = 0
                            first = 0
                            count = 0
                            var bits = 0
                            datum = 0
                            var i = 0
                            while (i < npix) {
                                if (top == 0) {
                                    if (bits < codeSize) {
                                        if (count == 0) {
                                            count = kotlin.run {
                                                blockSize = kotlin.run {
                                                    var curByte = 0
                                                    try {
                                                        curByte = bufferedInputStream.read()
                                                    } catch (e: IOException) {
                                                        status = statusFormatError
                                                    }
                                                    curByte
                                                }
                                                var n = 0
                                                if (blockSize > 0) {
                                                    try {
                                                        var count1: Int
                                                        while (n < blockSize) {
                                                            count1 = bufferedInputStream.read(block, n, blockSize - n)
                                                            if (count1 == -1) break
                                                            n += count1
                                                        }
                                                    } catch (_: IOException) {
                                                    }
                                                    if (n < blockSize) {
                                                        status = statusFormatError
                                                    }
                                                }
                                                n
                                            }
                                            if (count <= 0) break
                                            bi = 0
                                        }
                                        datum += block[bi].toInt() and 0xff shl bits
                                        bits += 8
                                        bi++
                                        count--
                                        continue
                                    }

                                    code1 = datum and codeMask
                                    datum = datum shr codeSize
                                    bits -= codeSize

                                    if (code1 > available || code1 == endOfInformation) break
                                    if (code1 == clear) {
                                        codeSize = dataSize + 1
                                        codeMask = (1 shl codeSize) - 1
                                        available = clear + 2
                                        oldCode = nullCode
                                        continue
                                    }
                                    if (oldCode == nullCode) {
                                        pixelStack!![top++] = suffix!![code1]
                                        oldCode = code1
                                        first = code1
                                        continue
                                    }
                                    inCode = code1
                                    if (code1 == available) {
                                        pixelStack!![top++] = first.toByte()
                                        code1 = oldCode
                                    }
                                    while (code1 > clear) {
                                        pixelStack!![top++] = suffix!![code1]
                                        code1 = prefix!![code1].toInt()
                                    }
                                    first = suffix!![code1].toInt() and 0xff

                                    if (available >= maxStackSize) break
                                    pixelStack!![top++] = first.toByte()
                                    prefix!![available] = oldCode.toShort()
                                    suffix!![available] = first.toByte()
                                    available++
                                    if (available and codeMask == 0 && available < maxStackSize) {
                                        codeSize++
                                        codeMask += available
                                    }
                                    oldCode = inCode
                                }

                                top--
                                pixels!![pi++] = pixelStack!![top]
                                i++
                            }
                            i = pi
                            while (i < npix) {
                                pixels!![i] = 0
                                i++
                            }
                            do {
                                kotlin.run {
                                    blockSize = kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    }
                                    var n = 0
                                    if (blockSize > 0) {
                                        try {
                                            var count1: Int
                                            while (n < blockSize) {
                                                count1 = bufferedInputStream.read(block, n, blockSize - n)
                                                if (count1 == -1) break
                                                n += count1
                                            }
                                        } catch (_: IOException) {
                                        }
                                        if (n < blockSize) {
                                            status = statusFormatError
                                        }
                                    }
                                    n
                                }
                            } while (blockSize > 0 && status == statusOk)
                            if ((status != statusOk)) break
                            frameCount++

                            val image = BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB_PRE)
                            val dest = (image.raster.dataBuffer as DataBufferInt).data

                            if (lastDispose > 0) {
                                if (lastDispose == 3) {
                                    val n = frameCount - 2
                                    lastImage = if (n > 0) {
                                        kotlin.run {
                                            var im: BufferedImage? = null
                                            if (n - 1 in 0 until frameCount) {
                                                im = frames[n - 1].frameBufferedImage
                                            }
                                            im
                                        }
                                    } else {
                                        null
                                    }
                                }
                                if (lastImage != null) {
                                    val prev = (lastImage!!.raster.dataBuffer as DataBufferInt).data
                                    System.arraycopy(prev, 0, dest, 0, frameWidth * frameHeight)
                                    if (lastDispose == 2) {
                                        val g = image.createGraphics()
                                        val c: Color = if (transparency) {
                                            Color(0, 0, 0, 0)
                                        } else {
                                            Color(lastBgColor)
                                        }
                                        g.color = c
                                        g.composite = AlphaComposite.Src
                                        g.fill(lastRect)
                                        g.dispose()
                                    }
                                }
                            }

                            var pass = 1
                            var inc = 8
                            var iline = 0
                            for (i1 in 0 until ih) {
                                var line = i1
                                if (interlace) {
                                    if (iline >= ih) {
                                        pass++
                                        when (pass) {
                                            2 -> iline = 4
                                            3 -> {
                                                iline = 2
                                                inc = 4
                                            }

                                            4 -> {
                                                iline = 1
                                                inc = 2
                                            }
                                        }
                                    }
                                    line = iline
                                    iline += inc
                                }
                                line += iy
                                if (line < frameHeight) {
                                    val k = line * frameWidth
                                    var dx = k + ix
                                    var dlim = dx + iw
                                    if (k + frameWidth < dlim) {
                                        dlim = k + frameWidth
                                    }
                                    var sx = i1 * iw
                                    while (dx < dlim) {
                                        val index = pixels!![sx++].toInt() and 0xff
                                        val c = act!![index]
                                        if (c != 0) {
                                            dest[dx] = c
                                        }
                                        dx++
                                    }
                                }
                            }
                            frames.add(GifUtil.GifFrame(image, delay))
                            if (transparency) {
                                act!![transIndex] = save
                            }

                            lastDispose = dispose
                            lastRect = Rectangle(ix, iy, iw, ih)
                            lastImage = image
                            lastBgColor = bgColor
                            dispose = 0
                            transparency = false
                            delay = 0
                            lct = null
                        }

                        0x21 -> {
                            code = kotlin.run {
                                var curByte = 0
                                try {
                                    curByte = bufferedInputStream.read()
                                } catch (e: IOException) {
                                    status = statusFormatError
                                }
                                curByte
                            }
                            when (code) {
                                0xf9 -> {
                                    kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    }
                                    val packed = kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    }
                                    dispose = packed and 0x1c shr 2
                                    if (dispose == 0) {
                                        dispose = 1
                                    }
                                    transparency = packed and 1 != 0
                                    delay = (kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    } or (kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    } shl 8)) * 10
                                    transIndex = kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    }
                                    kotlin.run {
                                        var curByte = 0
                                        try {
                                            curByte = bufferedInputStream.read()
                                        } catch (e: IOException) {
                                            status = statusFormatError
                                        }
                                        curByte
                                    }
                                }

                                0xff -> {
                                    kotlin.run {
                                        blockSize = kotlin.run {
                                            var curByte = 0
                                            try {
                                                curByte = bufferedInputStream.read()
                                            } catch (e: IOException) {
                                                status = statusFormatError
                                            }
                                            curByte
                                        }
                                        var n = 0
                                        if (blockSize > 0) {
                                            try {
                                                var count: Int
                                                while (n < blockSize) {
                                                    count = bufferedInputStream.read(block, n, blockSize - n)
                                                    if (count == -1) break
                                                    n += count
                                                }
                                            } catch (_: IOException) {
                                            }
                                            if (n < blockSize) {
                                                status = statusFormatError
                                            }
                                        }
                                        n
                                    }
                                    var app = ""
                                    var i = 0
                                    while (i < 11) {
                                        app += Char(block[i].toUShort())
                                        i++
                                    }
                                    if (app == "NETSCAPE2.0") {
                                        do {
                                            kotlin.run {
                                                blockSize = kotlin.run {
                                                    var curByte = 0
                                                    try {
                                                        curByte = bufferedInputStream.read()
                                                    } catch (e: IOException) {
                                                        status = statusFormatError
                                                    }
                                                    curByte
                                                }
                                                var n = 0
                                                if (blockSize > 0) {
                                                    try {
                                                        var count: Int
                                                        while (n < blockSize) {
                                                            count = bufferedInputStream.read(block, n, blockSize - n)
                                                            if (count == -1) break
                                                            n += count
                                                        }
                                                    } catch (_: IOException) {
                                                    }
                                                    if (n < blockSize) {
                                                        status = statusFormatError
                                                    }
                                                }
                                                n
                                            }
                                            if (block[0].toInt() == 1) {
                                                block[1].toInt() and 0xff
                                                block[2].toInt() and 0xff
                                            }
                                        } while (blockSize > 0 && status == statusOk)
                                    } else
                                        do {
                                            kotlin.run {
                                                blockSize = kotlin.run {
                                                    var curByte = 0
                                                    try {
                                                        curByte = bufferedInputStream.read()
                                                    } catch (e: IOException) {
                                                        status = statusFormatError
                                                    }
                                                    curByte
                                                }
                                                var n = 0
                                                if (blockSize > 0) {
                                                    try {
                                                        var count: Int
                                                        while (n < blockSize) {
                                                            count = bufferedInputStream.read(block, n, blockSize - n)
                                                            if (count == -1) break
                                                            n += count
                                                        }
                                                    } catch (_: IOException) {
                                                    }
                                                    if (n < blockSize) {
                                                        status = statusFormatError
                                                    }
                                                }
                                                n
                                            }
                                        } while (blockSize > 0 && status == statusOk)
                                }

                                else ->
                                    do {
                                        kotlin.run {
                                            blockSize = kotlin.run {
                                                var curByte = 0
                                                try {
                                                    curByte = bufferedInputStream.read()
                                                } catch (e: IOException) {
                                                    status = statusFormatError
                                                }
                                                curByte
                                            }
                                            var n = 0
                                            if (blockSize > 0) {
                                                try {
                                                    var count: Int
                                                    while (n < blockSize) {
                                                        count = bufferedInputStream.read(block, n, blockSize - n)
                                                        if (count == -1) break
                                                        n += count
                                                    }
                                                } catch (_: IOException) {
                                                }
                                                if (n < blockSize) {
                                                    status = statusFormatError
                                                }
                                            }
                                            n
                                        }
                                    } while (blockSize > 0 && status == statusOk)
                            }
                        }

                        0x3b -> done = true
                        0x00 -> {}
                        else -> status = statusFormatError
                    }
                }
            }
            if (frameCount < 0) {
                throw RuntimeException("올바른 입력값이 아닙니다.")
            }
        }
        return frames
    }

    // (Frame 리스트를 Gif Output 으로 합치기)
    override fun encodeGif(
        gifFrameList: ArrayList<GifUtil.GifFrame>,
        outputStream: OutputStream,
        repeatCount: Int,
        applyDither: Boolean
    ) {
        val bitsReserved = 5
        val bitsDiscarded: Int = 8 - bitsReserved
        val maxColorVal: Int = 1 shl bitsReserved
        val invMapLen: Int = maxColorVal * maxColorVal * maxColorVal
        val invColorMap = ByteArray(invMapLen)
        val mask = intArrayOf(0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff)
        var codeLenMbr: Int
        var codeIndexMbr: Int
        var clearCodeMbr: Int
        var endOfImageMbr: Int
        var bufIndexMbr = 0
        var emptyBitsMbr: Int
        var colorDepth: Int
        val bytesBufMbr = ByteArray(256)
        lateinit var colorPalette: IntArray
        val childMbr = IntArray(4097)
        val siblingsMbr = IntArray(4097)
        val suffixMbr = IntArray(4097)
        val logicalScreenWidthMbr: Int
        val logicalScreenHeightMbr: Int
        var firstFrameMbr = true
        val loopCountMbr: Int = repeatCount

        outputStream.write("GIF89a".toByteArray())
        var logicalScreenWidth = 0
        var logicalScreenHeight = 0
        for (image in gifFrameList) {
            if (image.frameBufferedImage.width > logicalScreenWidth) logicalScreenWidth =
                image.frameBufferedImage.width
            if (image.frameBufferedImage.height > logicalScreenHeight) logicalScreenHeight =
                image.frameBufferedImage.height
        }
        val logicalScreenSize = Dimension(logicalScreenWidth, logicalScreenHeight)
        logicalScreenWidthMbr = logicalScreenSize.width
        logicalScreenHeightMbr = logicalScreenSize.height
        for (i in gifFrameList.indices) {
            val imageWidth = gifFrameList[i].frameBufferedImage.width
            val imageHeight = gifFrameList[i].frameBufferedImage.height
            val image = gifFrameList[i].frameBufferedImage
            val type = image.type
            val raster: Raster = image.raster
            val `object` = raster.getDataElements(0, 0, image.width, image.height, null)
            val transferType = raster.transferType
            val imageWidth1 = image.width
            val imageHeight1 = image.height
            val imageSize = imageWidth1 * imageHeight1
            var rgbs = IntArray(imageSize)

            val resultRgb: IntArray = when (transferType) {
                DataBuffer.TYPE_INT -> {
                    rgbs = `object` as IntArray
                    when (type) {
                        BufferedImage.TYPE_INT_ARGB_PRE -> {
                            var i1 = 0
                            while (i1 < imageSize) {
                                val alpha = 255.0f / (rgbs[i1] shr 24 and 0xff)
                                val red = ((rgbs[i1] shr 16 and 0xff) * alpha).toInt().toByte()
                                val green = ((rgbs[i1] shr 8 and 0xff) * alpha).toInt().toByte()
                                val blue = ((rgbs[i1] and 0xff) * alpha).toInt().toByte()
                                rgbs[i1] =
                                    rgbs[i1] and -0x1000000 or (red.toInt() and 0xff shl 16) or (green.toInt() and 0xff shl 8) or (blue.toInt() and 0xff)
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_INT_BGR -> { // Convert BGR to RGB
                            var i1 = 0
                            while (i1 < rgbs.size) {
                                val blue = rgbs[i1] shr 16 and 0xff
                                val green = rgbs[i1] shr 8 and 0xff
                                val red = rgbs[i1] and 0xff
                                rgbs[i1] = -0x1000000 or (red shl 16) or (green shl 8) or blue
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_INT_RGB -> {
                            var i1 = 0
                            while (i1 < rgbs.size) {
                                rgbs[i1] = -0x1000000 or rgbs[i1]
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_INT_ARGB -> {
                            rgbs
                        }

                        else -> {
                            image.getRGB(0, 0, imageWidth1, imageHeight1, rgbs, 0, imageWidth1)
                        }
                    }
                }

                DataBuffer.TYPE_BYTE -> {
                    val bpixels = `object` as ByteArray
                    // BufferedImage.getRGB() seems a bit faster in this case for small images.
                    when (type) {
                        BufferedImage.TYPE_BYTE_INDEXED, BufferedImage.TYPE_BYTE_BINARY -> {
                            val indexModel = image.colorModel as IndexColorModel
                            val mapSize = indexModel.mapSize
                            val reds = ByteArray(mapSize)
                            val greens = ByteArray(mapSize)
                            val blues = ByteArray(mapSize)
                            val alphas = ByteArray(mapSize)
                            val palette = IntArray(mapSize)
                            indexModel.getReds(reds)
                            indexModel.getGreens(greens)
                            indexModel.getBlues(blues)
                            indexModel.getAlphas(alphas)
                            run {
                                var i1 = 0
                                while (i1 < mapSize) {
                                    palette[i1] = alphas[i1].toInt() and 0xff shl 24 or (reds[i1]
                                        .toInt() and 0xff shl 16) or (greens[i1].toInt() and 0xff shl 8) or (blues[i1]
                                        .toInt() and 0xff)
                                    i1++
                                }
                            }
                            var i1 = 0
                            while (i1 < imageSize) {
                                rgbs[i1] = palette[bpixels[i1].toInt() and 0xff]
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_4BYTE_ABGR -> {
                            var i1 = 0
                            var index = 0
                            while (i1 < imageSize) {
                                rgbs[i1] =
                                    bpixels[index++].toInt() and 0xff shl 16 or (bpixels[index++].toInt() and 0xff shl 8) or (bpixels[index++].toInt() and 0xff) or (bpixels[index++].toInt() and 0xff shl 24)
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_3BYTE_BGR -> {
                            var i1 = 0
                            var index = 0
                            while (i1 < imageSize) {
                                rgbs[i1] =
                                    -0x1000000 or (bpixels[index++].toInt() and 0xff shl 16) or (bpixels[index++].toInt() and 0xff shl 8) or (bpixels[index++].toInt() and 0xff)
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_4BYTE_ABGR_PRE -> {
                            var i1 = 0
                            var index = 0
                            while (i1 < imageSize) {
                                val alpha = 255.0f * (bpixels[index + 3].toInt() and 0xff)
                                val blue = ((bpixels[index + 2].toInt() and 0xff) * alpha).toInt().toByte()
                                val green = ((bpixels[index + 1].toInt() and 0xff) * alpha).toInt().toByte()
                                val red = ((bpixels[index].toInt() and 0xff) * alpha).toInt().toByte()
                                rgbs[i1] =
                                    bpixels[index + 3].toInt() and -0x1000000 or (red.toInt() and 0xff shl 16) or (green.toInt() and 0xff shl 8) or (blue.toInt() and 0xff)
                                i1++
                                index += 4
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_BYTE_GRAY -> {
                            var i1 = 0
                            while (i1 < imageSize) {
                                rgbs[i1] =
                                    -0x1000000 or (bpixels[i1].toInt() and 0xff shl 16) or (bpixels[i1].toInt() and 0xff shl 8) or (bpixels[i1].toInt() and 0xff)
                                i1++
                            }
                            rgbs
                        }

                        else -> {
                            image.getRGB(0, 0, imageWidth1, imageHeight1, rgbs, 0, imageWidth1)
                        }
                    }
                }

                DataBuffer.TYPE_USHORT -> {
                    val spixels = `object` as ShortArray
                    when (type) {
                        BufferedImage.TYPE_USHORT_GRAY -> {
                            var i1 = 0
                            while (i1 < imageSize) {
                                val gray = spixels[i1].toInt() shr 8 and 0xff
                                rgbs[i1] = -0x1000000 or (gray shl 16) or (gray shl 8) or gray
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_USHORT_565_RGB -> {
                            var i1 = 0
                            while (i1 < imageSize) {
                                val red = spixels[i1].toInt() shr 11 and 0x1f
                                val green = spixels[i1].toInt() shr 5 and 0x3f
                                val blue = spixels[i1].toInt() and 0x1f
                                rgbs[i1] = -0x1000000 or (red shl 19) or (green shl 10) or (blue shl 3)
                                i1++
                            }
                            rgbs
                        }

                        BufferedImage.TYPE_USHORT_555_RGB -> {
                            var i1 = 0
                            while (i1 < imageSize) {
                                val red = spixels[i1].toInt() ushr 10 and 0x1f
                                val green = spixels[i1].toInt() ushr 5 and 0x1f
                                val blue = spixels[i1].toInt() and 0x1f
                                rgbs[i1] = -0x1000000 or (red shl 19) or (green shl 11) or (blue shl 3)
                                i1++
                            }
                            rgbs
                        }

                        else -> {
                            image.getRGB(0, 0, imageWidth1, imageHeight1, rgbs, 0, imageWidth1)
                        }
                    }
                }

                else -> image.getRGB(0, 0, imageWidth1, imageHeight1, rgbs, 0, imageWidth1)
            }

            val delay = gifFrameList[i].frameDelay
            emptyBitsMbr = 0x08
            val transparentColor: Int
            var colorInfo: IntArray
            val newPixels = ByteArray(imageWidth * imageHeight)
            colorPalette = IntArray(256)
            var index1 = 0
            var temp3: Int
            var bitsPerPixel = 1
            var transparentIndex1 = -1 // Transparent color index
            var transparentColor1 = -1 // Transparent color
            val colorInfo1 = IntArray(2) // Return value
            val rgbHash = IntHashtable()
            var resultColorDepth: IntArray? = null
            for (i1 in resultRgb.indices) {
                temp3 = resultRgb[i1] and 0x00ffffff
                if (resultRgb[i1] ushr 24 < 0x80) { // Transparent
                    if (transparentIndex1 < 0) {
                        transparentIndex1 = index1
                        transparentColor1 = temp3 // Remember transparent color
                    }
                    temp3 = Int.MAX_VALUE
                }
                val entry = rgbHash.get(temp3)
                if (entry != null) {
                    newPixels[i1] = entry.toByte()
                } else {
                    if (index1 > 0xff) { // More than 256 colors, have to reduce
                        // Colors before saving as an indexed color image
                        colorInfo1[0] = 24
                        resultColorDepth = colorInfo1
                        break
                    }
                    rgbHash.put(temp3, index1)
                    newPixels[i1] = index1.toByte()
                    colorPalette[index1++] = 0xff shl 24 or temp3
                }
            }
            if (resultColorDepth == null) {
                if (transparentIndex1 >= 0) // This line could be used to set a different background color
                    colorPalette[transparentIndex1] = transparentColor1
                // Return the actual bits per pixel and the transparent color index if any
                while (1 shl bitsPerPixel < index1) bitsPerPixel++
                colorInfo1[0] = bitsPerPixel
                colorInfo1[1] = transparentIndex1
                resultColorDepth = colorInfo1
            }

            colorInfo = resultColorDepth
            if (colorInfo[0] > 0x08) {
                colorDepth = 8
                colorInfo = if (applyDither) {
                    require(!(colorDepth > 8 || colorDepth < 1)) { "Invalid color depth $colorDepth" }
                    val colorInfo2 = IntArray(2)
                    val quantizeInput2 = 1 shl colorDepth
                    val quantizeInput3 = colorPalette
                    val colors: Int = kotlin.run {
                        var lutSize: Int = quantizeInput2
                        val maxColour = 256
                        val red = 2
                        val green = 1
                        val blue = 0
                        val quantSize = 33 // quant size
                        val size: Int = resultRgb.size
                        var transparentColor2 = -1
                        val m2 = Array(quantSize) { Array(quantSize) { FloatArray(quantSize) } }
                        val wt = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }
                        val mr = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }
                        val mg = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }
                        val mb = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }

                        val cube = arrayOfNulls<RgbBox>(maxColour)
                        var lutR: Int
                        var lutG: Int
                        var lutB: Int
                        var next: Int
                        var k: Int
                        var weight: Long
                        val vv = FloatArray(maxColour)
                        var temp: Float
                        /* build 3-D color histogram of counts, r/g/b, c^2 */
                        var r: Int
                        var g: Int
                        var b: Int
                        var inr: Int
                        var ing: Int
                        var inb: Int
                        val table = IntArray(256)
                        var i1 = 0
                        while (i1 < 256) {
                            table[i1] = i1 * i1
                            ++i1
                        }
                        val qadd = IntArray(size)
                        i1 = 0
                        while (i1 < size) {
                            val rgb = resultRgb[i1]
                            if (rgb ushr 24 < 0x80) { // Transparent
                                if (transparentColor2 < 0) // Find the transparent color
                                    transparentColor2 = rgb
                            }
                            r = rgb shr 16 and 0xff
                            g = rgb shr 8 and 0xff
                            b = rgb and 0xff
                            inr = (r shr 3) + 1
                            ing = (g shr 3) + 1
                            inb = (b shr 3) + 1
                            qadd[i1] = (inr shl 10) + (inr shl 6) + inr + (ing shl 5) + ing + inb
                            /*[inr][ing][inb]*/++wt[inr][ing][inb]
                            mr[inr][ing][inb] += r.toLong()
                            mg[inr][ing][inb] += g.toLong()
                            mb[inr][ing][inb] += b.toLong()
                            m2[inr][ing][inb] += (table[r] + table[g] + table[b]).toFloat()
                            ++i1
                        }
                        /* compute cumulative moments. */
                        var i2: Int
                        var g2: Int
                        var b2: Int
                        var line: Int
                        var lineR: Int
                        var lineG: Int
                        var lineB: Int
                        val area = IntArray(quantSize)
                        val areaR = IntArray(quantSize)
                        val areaG = IntArray(quantSize)
                        val areaB = IntArray(quantSize)
                        var line2: Float
                        val area2 = FloatArray(quantSize)
                        var r2 = 1
                        while (r2 < quantSize) {
                            i2 = 0
                            while (i2 < quantSize) {
                                areaB[i2] = 0
                                areaG[i2] = areaB[i2]
                                areaR[i2] = areaG[i2]
                                area[i2] = areaR[i2]
                                area2[i2] = area[i2].toFloat()
                                ++i2
                            }
                            g2 = 1
                            while (g2 < quantSize) {
                                lineB = 0
                                lineG = 0
                                lineR = 0
                                line = 0
                                line2 = 0.toFloat()
                                b2 = 1
                                while (b2 < quantSize) {
                                    line += wt[r2][g2][b2].toInt()
                                    lineR += mr[r2][g2][b2].toInt()
                                    lineG += mg[r2][g2][b2].toInt()
                                    lineB += mb[r2][g2][b2].toInt()
                                    line2 += m2[r2][g2][b2]
                                    area[b2] += line
                                    areaR[b2] += lineR
                                    areaG[b2] += lineG
                                    areaB[b2] += lineB
                                    area2[b2] += line2
                                    wt[r2][g2][b2] = wt[r2 - 1][g2][b2] + area[b2]
                                    mr[r2][g2][b2] = mr[r2 - 1][g2][b2] + areaR[b2]
                                    mg[r2][g2][b2] = mg[r2 - 1][g2][b2] + areaG[b2]
                                    mb[r2][g2][b2] = mb[r2 - 1][g2][b2] + areaB[b2]
                                    m2[r2][g2][b2] = m2[r2 - 1][g2][b2] + area2[b2]
                                    ++b2
                                }
                                ++g2
                            }
                            ++r2
                        }
                        var i3 = 0
                        while (i3 < maxColour) {
                            cube[i3] = RgbBox()
                            i3++
                        }
                        cube[0]!!.b0 = 0
                        cube[0]!!.g0 = cube[0]!!.b0
                        cube[0]!!.r0 = cube[0]!!.g0
                        cube[0]!!.b1 = quantSize - 1
                        cube[0]!!.g1 = cube[0]!!.b1
                        cube[0]!!.r1 = cube[0]!!.g1
                        next = 0
                        if (transparentColor2 >= 0) lutSize--
                        i3 = 1
                        while (i3 < lutSize) {
                            if (kotlin.run {
                                    val cutInput1 = cube[next]
                                    val cutInput2 = cube[i3]

                                    val dir: Int
                                    val cutr = IntArray(1)
                                    val cutg = IntArray(1)
                                    val cutb = IntArray(1)
                                    val maxr: Float
                                    val maxg: Float
                                    val maxb: Float
                                    val wholeR: Long = ((((mr[cutInput1!!.r1][cutInput1.g1][cutInput1.b1]
                                            - mr[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - mr[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + mr[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - mr[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + mr[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + mr[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                    val wholeG: Long = ((((mg[cutInput1.r1][cutInput1.g1][cutInput1.b1]
                                            - mg[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - mg[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + mg[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - mg[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + mg[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + mg[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                    val wholeB: Long = ((((mb[cutInput1.r1][cutInput1.g1][cutInput1.b1]
                                            - mb[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - mb[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + mb[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - mb[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + mb[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + mb[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                    val wholeW: Long = ((((wt[cutInput1.r1][cutInput1.g1][cutInput1.b1]
                                            - wt[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - wt[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + wt[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - wt[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + wt[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + wt[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])

                                    maxr = kotlin.run {
                                        var halfR: Long
                                        var halfG: Long
                                        var halfB: Long
                                        var halfW: Long
                                        var temp1: Float
                                        var max: Float
                                        val baseR: Long = ((-mr[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + mr[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + mr[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseG: Long = ((-mg[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + mg[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + mg[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseB: Long = ((-mb[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + mb[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + mb[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseW: Long = ((-wt[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + wt[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + wt[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        max = 0.0f
                                        cutr[0] = -1
                                        var i4: Int = cutInput1.r0 + 1
                                        while (i4 < cutInput1.r1) {
                                            halfR = baseR + ((mr[i4][cutInput1.g1][cutInput1.b1]
                                                    - mr[i4][cutInput1.g1][cutInput1.b0]
                                                    - mr[i4][cutInput1.g0][cutInput1.b1])
                                                    + mr[i4][cutInput1.g0][cutInput1.b0])
                                            halfG = baseG + ((mg[i4][cutInput1.g1][cutInput1.b1]
                                                    - mg[i4][cutInput1.g1][cutInput1.b0]
                                                    - mg[i4][cutInput1.g0][cutInput1.b1])
                                                    + mg[i4][cutInput1.g0][cutInput1.b0])
                                            halfB = baseB + ((mb[i4][cutInput1.g1][cutInput1.b1]
                                                    - mb[i4][cutInput1.g1][cutInput1.b0]
                                                    - mb[i4][cutInput1.g0][cutInput1.b1])
                                                    + mb[i4][cutInput1.g0][cutInput1.b0])
                                            halfW = baseW + ((wt[i4][cutInput1.g1][cutInput1.b1]
                                                    - wt[i4][cutInput1.g1][cutInput1.b0]
                                                    - wt[i4][cutInput1.g0][cutInput1.b1])
                                                    + wt[i4][cutInput1.g0][cutInput1.b0])
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 = (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            halfR = wholeR - halfR
                                            halfG = wholeG - halfG
                                            halfB = wholeB - halfB
                                            halfW = wholeW - halfW
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 += (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            if (temp1 > max) {
                                                max = temp1
                                                cutr[0] = i4
                                            }
                                            ++i4
                                        }
                                        max
                                    }
                                    maxg = kotlin.run {
                                        var halfR: Long
                                        var halfG: Long
                                        var halfB: Long
                                        var halfW: Long
                                        var temp1: Float
                                        var max: Float
                                        val baseR: Long = ((-mr[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + mr[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mr[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseG: Long = ((-mg[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + mg[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mg[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseB: Long = ((-mb[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + mb[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mb[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseW: Long = ((-wt[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + wt[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + wt[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        max = 0.0f
                                        cutg[0] = -1
                                        var i4: Int = cutInput1.g0 + 1
                                        while (i4 < cutInput1.g1) {
                                            halfR = baseR + ((mr[cutInput1.r1][i4][cutInput1.b1]
                                                    - mr[cutInput1.r1][i4][cutInput1.b0]
                                                    - mr[cutInput1.r0][i4][cutInput1.b1])
                                                    + mr[cutInput1.r0][i4][cutInput1.b0])
                                            halfG = baseG + ((mg[cutInput1.r1][i4][cutInput1.b1]
                                                    - mg[cutInput1.r1][i4][cutInput1.b0]
                                                    - mg[cutInput1.r0][i4][cutInput1.b1])
                                                    + mg[cutInput1.r0][i4][cutInput1.b0])
                                            halfB = baseB + ((mb[cutInput1.r1][i4][cutInput1.b1]
                                                    - mb[cutInput1.r1][i4][cutInput1.b0]
                                                    - mb[cutInput1.r0][i4][cutInput1.b1])
                                                    + mb[cutInput1.r0][i4][cutInput1.b0])
                                            halfW = baseW + ((wt[cutInput1.r1][i4][cutInput1.b1]
                                                    - wt[cutInput1.r1][i4][cutInput1.b0]
                                                    - wt[cutInput1.r0][i4][cutInput1.b1])
                                                    + wt[cutInput1.r0][i4][cutInput1.b0])
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 = (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            halfR = wholeR - halfR
                                            halfG = wholeG - halfG
                                            halfB = wholeB - halfB
                                            halfW = wholeW - halfW
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 += (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            if (temp1 > max) {
                                                max = temp1
                                                cutg[0] = i4
                                            }
                                            ++i4
                                        }
                                        max
                                    }
                                    maxb = kotlin.run {
                                        var halfR: Long
                                        var halfG: Long
                                        var halfB: Long
                                        var halfW: Long
                                        var temp1: Float
                                        var max: Float
                                        val baseR: Long = ((-mr[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + mr[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mr[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseG: Long = ((-mg[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + mg[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mg[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseB: Long = ((-mb[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + mb[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mb[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseW: Long = ((-wt[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + wt[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + wt[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        max = 0.0f
                                        cutb[0] = -1
                                        var i4: Int = cutInput1.b0 + 1
                                        while (i4 < cutInput1.b1) {
                                            halfR = baseR + ((mr[cutInput1.r1][cutInput1.g1][i4]
                                                    - mr[cutInput1.r1][cutInput1.g0][i4]
                                                    - mr[cutInput1.r0][cutInput1.g1][i4])
                                                    + mr[cutInput1.r0][cutInput1.g0][i4])
                                            halfG = baseG + ((mg[cutInput1.r1][cutInput1.g1][i4]
                                                    - mg[cutInput1.r1][cutInput1.g0][i4]
                                                    - mg[cutInput1.r0][cutInput1.g1][i4])
                                                    + mg[cutInput1.r0][cutInput1.g0][i4])
                                            halfB = baseB + ((mb[cutInput1.r1][cutInput1.g1][i4]
                                                    - mb[cutInput1.r1][cutInput1.g0][i4]
                                                    - mb[cutInput1.r0][cutInput1.g1][i4])
                                                    + mb[cutInput1.r0][cutInput1.g0][i4])
                                            halfW = baseW + ((wt[cutInput1.r1][cutInput1.g1][i4]
                                                    - wt[cutInput1.r1][cutInput1.g0][i4]
                                                    - wt[cutInput1.r0][cutInput1.g1][i4])
                                                    + wt[cutInput1.r0][cutInput1.g0][i4])
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 = (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            halfR = wholeR - halfR
                                            halfG = wholeG - halfG
                                            halfB = wholeB - halfB
                                            halfW = wholeW - halfW
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 += (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            if (temp1 > max) {
                                                max = temp1
                                                cutb[0] = i4
                                            }
                                            ++i4
                                        }
                                        max
                                    }
                                    if (maxr >= maxg && maxr >= maxb) {
                                        dir = red
                                        if (cutr[0] < 0) {
                                            false
                                        } else {
                                            cutInput2!!.r1 = cutInput1.r1
                                            cutInput2.g1 = cutInput1.g1
                                            cutInput2.b1 = cutInput1.b1
                                            when (dir) {
                                                red -> {
                                                    run {
                                                        cutInput1.r1 = cutr[0]
                                                        cutInput2.r0 = cutInput1.r1
                                                    }
                                                    cutInput2.g0 = cutInput1.g0
                                                    cutInput2.b0 = cutInput1.b0
                                                }
                                            }
                                            cutInput1.vol =
                                                (cutInput1.r1 - cutInput1.r0) * (cutInput1.g1 - cutInput1.g0) * (cutInput1.b1 - cutInput1.b0)
                                            cutInput2.vol =
                                                (cutInput2.r1 - cutInput2.r0) * (cutInput2.g1 - cutInput2.g0) * (cutInput2.b1 - cutInput2.b0)
                                            true
                                        }
                                    } else {
                                        dir = if (maxg >= maxr && maxg >= maxb) green else blue
                                        cutInput2!!.r1 = cutInput1.r1
                                        cutInput2.g1 = cutInput1.g1
                                        cutInput2.b1 = cutInput1.b1
                                        when (dir) {
                                            red -> {
                                                run {
                                                    cutInput1.r1 = cutr[0]
                                                    cutInput2.r0 = cutInput1.r1
                                                }
                                                cutInput2.g0 = cutInput1.g0
                                                cutInput2.b0 = cutInput1.b0
                                            }

                                            green -> {
                                                run {
                                                    cutInput1.g1 = cutg[0]
                                                    cutInput2.g0 = cutInput1.g1
                                                }
                                                cutInput2.r0 = cutInput1.r0
                                                cutInput2.b0 = cutInput1.b0
                                            }

                                            blue -> {
                                                run {
                                                    cutInput1.b1 = cutb[0]
                                                    cutInput2.b0 = cutInput1.b1
                                                }
                                                cutInput2.r0 = cutInput1.r0
                                                cutInput2.g0 = cutInput1.g0
                                            }
                                        }
                                        cutInput1.vol =
                                            (cutInput1.r1 - cutInput1.r0) * (cutInput1.g1 - cutInput1.g0) * (cutInput1.b1 - cutInput1.b0)
                                        cutInput2.vol =
                                            (cutInput2.r1 - cutInput2.r0) * (cutInput2.g1 - cutInput2.g0) * (cutInput2.b1 - cutInput2.b0)
                                        true
                                    }
                                }) {
                                /* volume test ensures we won't try to cut one-cell box */
                                vv[next] = if (cube[next]!!.vol > 1) kotlin.run {
                                    /* Compute the weighted variance of a box */
                                    /* NB: as with the raw statistics, this is really the variance * size */
                                    val dr: Float = ((((mr[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - mr[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - mr[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + mr[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - mr[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + mr[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + mr[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - mr[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]).toFloat()
                                    val dg: Float = ((((mg[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - mg[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - mg[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + mg[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - mg[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + mg[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + mg[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - mg[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]).toFloat()
                                    val db: Float = ((((mb[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - mb[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - mb[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + mb[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - mb[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + mb[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + mb[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - mb[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]).toFloat()
                                    ((((m2[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - m2[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - m2[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + m2[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - m2[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + m2[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + m2[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - m2[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]) - (dr * dr + dg * dg + db * db) / ((((wt[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - wt[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - wt[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + wt[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - wt[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + wt[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + wt[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - wt[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0])
                                } else 0.0f
                                vv[i3] = if (cube[i3]!!.vol > 1) kotlin.run {
                                    /* Compute the weighted variance of a box */
                                    /* NB: as with the raw statistics, this is really the variance * size */
                                    val dr: Float = ((((mr[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - mr[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - mr[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + mr[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - mr[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + mr[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + mr[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - mr[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]).toFloat()
                                    val dg: Float = ((((mg[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - mg[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - mg[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + mg[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - mg[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + mg[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + mg[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - mg[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]).toFloat()
                                    val db: Float = ((((mb[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - mb[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - mb[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + mb[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - mb[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + mb[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + mb[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - mb[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]).toFloat()
                                    ((((m2[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - m2[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - m2[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + m2[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - m2[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + m2[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + m2[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - m2[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]) - (dr * dr + dg * dg + db * db) / ((((wt[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - wt[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - wt[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + wt[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - wt[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + wt[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + wt[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - wt[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0])
                                } else 0.0f
                            } else {
                                vv[next] = 0.0f /* don't try to split this box again */
                                i3-- /* didn't create box i */
                            }
                            next = 0
                            temp = vv[0]
                            k = 1
                            while (k <= i3) {
                                if (vv[k] > temp) {
                                    temp = vv[k]
                                    next = k
                                }
                                ++k
                            }
                            if (temp <= 0.0f) {
                                break
                            }
                            ++i3
                        }
                        k = 0
                        while (k < lutSize) {
                            weight = ((((wt[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                    - wt[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                    - wt[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                    + wt[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                    - wt[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                    ) + wt[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                    + wt[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                    - wt[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0])
                            if (weight > 0) {
                                lutR = (((((mr[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                        - mr[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                        - mr[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                        + mr[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                        - mr[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                        ) + mr[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                        + mr[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                        - mr[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0]) / weight).toInt()
                                lutG = (((((mg[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                        - mg[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                        - mg[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                        + mg[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                        - mg[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                        ) + mg[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                        + mg[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                        - mg[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0]) / weight).toInt()
                                lutB = (((((mb[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                        - mb[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                        - mb[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                        + mb[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                        - mb[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                        ) + mb[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                        + mb[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                        - mb[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0]) / weight).toInt()
                                quantizeInput3[k] = 255 shl 24 or (lutR shl 16) or (lutG shl 8) or lutB
                            } else {
                                quantizeInput3[k] = 0
                            }
                            ++k
                        }
                        var bitsPerPixel1 = 0
                        while (1 shl bitsPerPixel1 < lutSize) bitsPerPixel1++
                        colorInfo2[0] = bitsPerPixel1
                        colorInfo2[1] = -1
                        if (transparentColor2 >= 0) {
                            quantizeInput3[lutSize] = transparentColor2 // Set the transparent color
                            colorInfo2[1] = lutSize
                        }
                        lutSize
                    }
                    // Call Floyd-Steinberg dither
                    val transparentIndex = colorInfo2[1]
                    var index: Int
                    var index2 = 0
                    var err1: Int
                    var err2: Int
                    var err3: Int
                    var red: Int
                    var green: Int
                    var blue: Int
                    // Define error arrays
                    // Errors for the current line
                    var tempErr: IntArray
                    var thisErrR = IntArray(imageWidth + 2)
                    var thisErrG = IntArray(imageWidth + 2)
                    var thisErrB = IntArray(imageWidth + 2)
                    // Errors for the following line
                    var nextErrR = IntArray(imageWidth + 2)
                    var nextErrG = IntArray(imageWidth + 2)
                    var nextErrB = IntArray(imageWidth + 2)
                    kotlin.run {
                        var red1: Int
                        var green1: Int
                        var blue1: Int
                        var r: Int
                        var g: Int
                        var b: Int
                        var rdist: Int
                        var gdist: Int
                        var bdist: Int
                        var dist: Int
                        var rinc: Int
                        var ginc: Int
                        var binc: Int
                        val x = 1 shl bitsDiscarded // Step size for each color
                        val xsqr = 1 shl bitsDiscarded + bitsDiscarded
                        val txsqr = xsqr + xsqr
                        var bufIndex: Int
                        val distBuf = IntArray(invMapLen)

                        // Initialize the distance buffer array with the largest integer value
                        run {
                            var i1 = invMapLen
                            while (--i1 >= 0) {
                                distBuf[i1] = 0x7FFFFFFF
                            }
                        }
                        // Now loop through all the colors in the color map
                        for (i1 in 0 until colors) {
                            red1 = colorPalette[i1] shr 16 and 0xff
                            green1 = colorPalette[i1] shr 8 and 0xff
                            blue1 = colorPalette[i1] and 0xff
                            rdist = (x shr 1) - red1 // Red distance
                            gdist = (x shr 1) - green1 // Green distance
                            bdist = (x shr 1) - blue1 // Blue distance
                            dist = rdist * rdist + gdist * gdist + bdist * bdist //The modular
                            // The distance increment with each step value x
                            rinc = txsqr - (red1 shl bitsDiscarded + 1)
                            ginc = txsqr - (green1 shl bitsDiscarded + 1)
                            binc = txsqr - (blue1 shl bitsDiscarded + 1)
                            bufIndex = 0
                            // Loop through quantized RGB space
                            r = 0
                            rdist = dist
                            while (r < maxColorVal) {
                                g = 0
                                gdist = rdist
                                while (g < maxColorVal) {
                                    b = 0
                                    bdist = gdist
                                    while (b < maxColorVal) {
                                        if (bdist < distBuf[bufIndex]) {
                                            distBuf[bufIndex] = bdist
                                            invColorMap[bufIndex] = i1.toByte()
                                        }
                                        bdist += binc
                                        binc += txsqr
                                        bufIndex++
                                        b++
                                    }
                                    gdist += ginc
                                    ginc += txsqr
                                    g++
                                }
                                rdist += rinc
                                rinc += txsqr
                                r++
                            }
                        }
                    }
                    for (row in 0 until imageHeight) {
                        var col = 0
                        while (col < imageWidth) {

                            // Transparent, no dither
                            if (resultRgb[index2] ushr 24 < 0x80) {
                                newPixels[index2] = transparentIndex.toByte()
                                index2++
                                col++
                                continue
                            }
                            red = (resultRgb[index2] and 0xff0000 ushr 16) + thisErrR[col + 1]
                            if (red > 255) red = 255 else if (red < 0) red = 0
                            green = (resultRgb[index2] and 0x00ff00 ushr 8) + thisErrG[col + 1]
                            if (green > 255) green = 255 else if (green < 0) green = 0
                            blue = (resultRgb[index2] and 0x0000ff) + thisErrB[col + 1]
                            if (blue > 255) blue = 255 else if (blue < 0) blue = 0

                            // Find the nearest color index
                            index = invColorMap[red shr bitsDiscarded shl (bitsReserved shl 1) or
                                    (green shr bitsDiscarded shl bitsReserved) or
                                    (blue shr bitsDiscarded)].toInt() and 0xff
                            newPixels[index2] = index.toByte() // The colorPalette index for this pixel

                            // Find errors for different channels
                            err1 = red - (colorPalette[index] shr 16 and 0xff) // Red channel
                            err2 = green - (colorPalette[index] shr 8 and 0xff) // Green channel
                            err3 = blue - (colorPalette[index] and 0xff) // Blue channel
                            // Diffuse error
                            // Red
                            thisErrR[col + 2] += err1 * 7 / 16
                            nextErrR[col] += err1 * 3 / 16
                            nextErrR[col + 1] += err1 * 5 / 16
                            nextErrR[col + 2] += err1 / 16
                            // Green
                            thisErrG[col + 2] += err2 * 7 / 16
                            nextErrG[col] += err2 * 3 / 16
                            nextErrG[col + 1] += err2 * 5 / 16
                            nextErrG[col + 2] += err2 / 16
                            // Blue
                            thisErrB[col + 2] += err3 * 7 / 16
                            nextErrB[col] += err3 * 3 / 16
                            nextErrB[col + 1] += err3 * 5 / 16
                            nextErrB[col + 2] += err3 / 16
                            index2++
                            col++
                        }
                        // We have finished one row, switch the error arrays
                        tempErr = thisErrR
                        thisErrR = nextErrR
                        nextErrR = tempErr
                        tempErr = thisErrG
                        thisErrG = nextErrG
                        nextErrG = tempErr
                        tempErr = thisErrB
                        thisErrB = nextErrB
                        nextErrB = tempErr

                        // Clear the error arrays
                        Arrays.fill(nextErrR, 0)
                        Arrays.fill(nextErrG, 0)
                        Arrays.fill(nextErrB, 0)
                    }
                    // Return the actual bits per pixel and the transparent color index if any
                    colorInfo2
                } else {
                    val colorInfo2 = IntArray(2)
                    kotlin.run {
                        val quantizeInput2 = 1 shl colorDepth
                        val quantizeInput4 = colorPalette

                        var lutSize: Int = quantizeInput2
                        val maxColour = 256
                        val red = 2
                        val green = 1
                        val blue = 0
                        val quantSize = 33 // quant size
                        val size: Int = resultRgb.size
                        var transparentColor2 = -1
                        val m2 = Array(quantSize) { Array(quantSize) { FloatArray(quantSize) } }
                        val wt = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }
                        val mr = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }
                        val mg = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }
                        val mb = Array(quantSize) { Array(quantSize) { LongArray(quantSize) } }

                        val cube = arrayOfNulls<RgbBox>(maxColour)
                        var lutR: Int
                        var lutG: Int
                        var lutB: Int
                        val tag = IntArray(quantSize * quantSize * quantSize)
                        var next: Int
                        var k: Int
                        var weight: Long
                        val vv = FloatArray(maxColour)
                        var temp: Float
                        var r: Int
                        var g: Int
                        var b: Int
                        var inr: Int
                        var ing: Int
                        var inb: Int
                        val table = IntArray(256)
                        var i1 = 0
                        while (i1 < 256) {
                            table[i1] = i1 * i1
                            ++i1
                        }
                        val qadd = IntArray(size)
                        i1 = 0
                        while (i1 < size) {
                            val rgb = resultRgb[i1]
                            if (rgb ushr 24 < 0x80) { // Transparent
                                if (transparentColor2 < 0) // Find the transparent color
                                    transparentColor2 = rgb
                            }
                            r = rgb shr 16 and 0xff
                            g = rgb shr 8 and 0xff
                            b = rgb and 0xff
                            inr = (r shr 3) + 1
                            ing = (g shr 3) + 1
                            inb = (b shr 3) + 1
                            qadd[i1] = (inr shl 10) + (inr shl 6) + inr + (ing shl 5) + ing + inb
                            /*[inr][ing][inb]*/++wt[inr][ing][inb]
                            mr[inr][ing][inb] += r.toLong()
                            mg[inr][ing][inb] += g.toLong()
                            mb[inr][ing][inb] += b.toLong()
                            m2[inr][ing][inb] += (table[r] + table[g] + table[b]).toFloat()
                            ++i1
                        }
                        /* compute cumulative moments. */
                        var i2: Int
                        var g2: Int
                        var b2: Int
                        var line: Int
                        var lineR: Int
                        var lineG: Int
                        var lineB: Int
                        val area = IntArray(quantSize)
                        val areaR = IntArray(quantSize)
                        val areaG = IntArray(quantSize)
                        val areaB = IntArray(quantSize)
                        var line2: Float
                        val area2 = FloatArray(quantSize)
                        var r2 = 1
                        while (r2 < quantSize) {
                            i2 = 0
                            while (i2 < quantSize) {
                                areaB[i2] = 0
                                areaG[i2] = areaB[i2]
                                areaR[i2] = areaG[i2]
                                area[i2] = areaR[i2]
                                area2[i2] = area[i2].toFloat()
                                ++i2
                            }
                            g2 = 1
                            while (g2 < quantSize) {
                                lineB = 0
                                lineG = 0
                                lineR = 0
                                line = 0
                                line2 = 0.toFloat()
                                b2 = 1
                                while (b2 < quantSize) {
                                    line += wt[r2][g2][b2].toInt()
                                    lineR += mr[r2][g2][b2].toInt()
                                    lineG += mg[r2][g2][b2].toInt()
                                    lineB += mb[r2][g2][b2].toInt()
                                    line2 += m2[r2][g2][b2]
                                    area[b2] += line
                                    areaR[b2] += lineR
                                    areaG[b2] += lineG
                                    areaB[b2] += lineB
                                    area2[b2] += line2
                                    wt[r2][g2][b2] = wt[r2 - 1][g2][b2] + area[b2]
                                    mr[r2][g2][b2] = mr[r2 - 1][g2][b2] + areaR[b2]
                                    mg[r2][g2][b2] = mg[r2 - 1][g2][b2] + areaG[b2]
                                    mb[r2][g2][b2] = mb[r2 - 1][g2][b2] + areaB[b2]
                                    m2[r2][g2][b2] = m2[r2 - 1][g2][b2] + area2[b2]
                                    ++b2
                                }
                                ++g2
                            }
                            ++r2
                        }
                        var i3 = 0
                        while (i3 < maxColour) {
                            cube[i3] = RgbBox()
                            i3++
                        }
                        cube[0]!!.b0 = 0
                        cube[0]!!.g0 = cube[0]!!.b0
                        cube[0]!!.r0 = cube[0]!!.g0
                        cube[0]!!.b1 = quantSize - 1
                        cube[0]!!.g1 = cube[0]!!.b1
                        cube[0]!!.r1 = cube[0]!!.g1
                        next = 0
                        if (transparentColor2 >= 0) lutSize--
                        i3 = 1
                        while (i3 < lutSize) {
                            if (kotlin.run {
                                    val cutInput1 = cube[next]
                                    val cutInput2 = cube[i3]

                                    val dir: Int
                                    val cutr = IntArray(1)
                                    val cutg = IntArray(1)
                                    val cutb = IntArray(1)
                                    val maxr: Float
                                    val maxg: Float
                                    val maxb: Float
                                    val wholeR: Long = ((((mr[cutInput1!!.r1][cutInput1.g1][cutInput1.b1]
                                            - mr[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - mr[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + mr[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - mr[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + mr[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + mr[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                    val wholeG: Long = ((((mg[cutInput1.r1][cutInput1.g1][cutInput1.b1]
                                            - mg[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - mg[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + mg[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - mg[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + mg[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + mg[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                    val wholeB: Long = ((((mb[cutInput1.r1][cutInput1.g1][cutInput1.b1]
                                            - mb[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - mb[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + mb[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - mb[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + mb[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + mb[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                    val wholeW: Long = ((((wt[cutInput1.r1][cutInput1.g1][cutInput1.b1]
                                            - wt[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                            - wt[cutInput1.r1][cutInput1.g0][cutInput1.b1])
                                            + wt[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                            - wt[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                            ) + wt[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                            + wt[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                            - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])

                                    maxr = kotlin.run {
                                        var halfR: Long
                                        var halfG: Long
                                        var halfB: Long
                                        var halfW: Long
                                        var temp1: Float
                                        var max: Float
                                        val baseR: Long = ((-mr[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + mr[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + mr[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseG: Long = ((-mg[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + mg[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + mg[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseB: Long = ((-mb[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + mb[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + mb[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseW: Long = ((-wt[cutInput1.r0][cutInput1.g1][cutInput1.b1]
                                                + wt[cutInput1.r0][cutInput1.g1][cutInput1.b0]
                                                + wt[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        max = 0.0f
                                        cutr[0] = -1
                                        var i4: Int = cutInput1.r0 + 1
                                        while (i4 < cutInput1.r1) {
                                            halfR = baseR + ((mr[i4][cutInput1.g1][cutInput1.b1]
                                                    - mr[i4][cutInput1.g1][cutInput1.b0]
                                                    - mr[i4][cutInput1.g0][cutInput1.b1])
                                                    + mr[i4][cutInput1.g0][cutInput1.b0])
                                            halfG = baseG + ((mg[i4][cutInput1.g1][cutInput1.b1]
                                                    - mg[i4][cutInput1.g1][cutInput1.b0]
                                                    - mg[i4][cutInput1.g0][cutInput1.b1])
                                                    + mg[i4][cutInput1.g0][cutInput1.b0])
                                            halfB = baseB + ((mb[i4][cutInput1.g1][cutInput1.b1]
                                                    - mb[i4][cutInput1.g1][cutInput1.b0]
                                                    - mb[i4][cutInput1.g0][cutInput1.b1])
                                                    + mb[i4][cutInput1.g0][cutInput1.b0])
                                            halfW = baseW + ((wt[i4][cutInput1.g1][cutInput1.b1]
                                                    - wt[i4][cutInput1.g1][cutInput1.b0]
                                                    - wt[i4][cutInput1.g0][cutInput1.b1])
                                                    + wt[i4][cutInput1.g0][cutInput1.b0])
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 = (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            halfR = wholeR - halfR
                                            halfG = wholeG - halfG
                                            halfB = wholeB - halfB
                                            halfW = wholeW - halfW
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 += (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            if (temp1 > max) {
                                                max = temp1
                                                cutr[0] = i4
                                            }
                                            ++i4
                                        }
                                        max
                                    }
                                    maxg = kotlin.run {
                                        var halfR: Long
                                        var halfG: Long
                                        var halfB: Long
                                        var halfW: Long
                                        var temp1: Float
                                        var max: Float
                                        val baseR: Long = ((-mr[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + mr[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mr[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseG: Long = ((-mg[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + mg[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mg[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseB: Long = ((-mb[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + mb[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mb[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseW: Long = ((-wt[cutInput1.r1][cutInput1.g0][cutInput1.b1]
                                                + wt[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + wt[cutInput1.r0][cutInput1.g0][cutInput1.b1])
                                                - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        max = 0.0f
                                        cutg[0] = -1
                                        var i4: Int = cutInput1.g0 + 1
                                        while (i4 < cutInput1.g1) {
                                            halfR = baseR + ((mr[cutInput1.r1][i4][cutInput1.b1]
                                                    - mr[cutInput1.r1][i4][cutInput1.b0]
                                                    - mr[cutInput1.r0][i4][cutInput1.b1])
                                                    + mr[cutInput1.r0][i4][cutInput1.b0])
                                            halfG = baseG + ((mg[cutInput1.r1][i4][cutInput1.b1]
                                                    - mg[cutInput1.r1][i4][cutInput1.b0]
                                                    - mg[cutInput1.r0][i4][cutInput1.b1])
                                                    + mg[cutInput1.r0][i4][cutInput1.b0])
                                            halfB = baseB + ((mb[cutInput1.r1][i4][cutInput1.b1]
                                                    - mb[cutInput1.r1][i4][cutInput1.b0]
                                                    - mb[cutInput1.r0][i4][cutInput1.b1])
                                                    + mb[cutInput1.r0][i4][cutInput1.b0])
                                            halfW = baseW + ((wt[cutInput1.r1][i4][cutInput1.b1]
                                                    - wt[cutInput1.r1][i4][cutInput1.b0]
                                                    - wt[cutInput1.r0][i4][cutInput1.b1])
                                                    + wt[cutInput1.r0][i4][cutInput1.b0])
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 = (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            halfR = wholeR - halfR
                                            halfG = wholeG - halfG
                                            halfB = wholeB - halfB
                                            halfW = wholeW - halfW
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 += (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            if (temp1 > max) {
                                                max = temp1
                                                cutg[0] = i4
                                            }
                                            ++i4
                                        }
                                        max
                                    }
                                    maxb = kotlin.run {
                                        var halfR: Long
                                        var halfG: Long
                                        var halfB: Long
                                        var halfW: Long
                                        var temp1: Float
                                        var max: Float
                                        val baseR: Long = ((-mr[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + mr[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mr[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - mr[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseG: Long = ((-mg[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + mg[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mg[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - mg[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseB: Long = ((-mb[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + mb[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + mb[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - mb[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        val baseW: Long = ((-wt[cutInput1.r1][cutInput1.g1][cutInput1.b0]
                                                + wt[cutInput1.r1][cutInput1.g0][cutInput1.b0]
                                                + wt[cutInput1.r0][cutInput1.g1][cutInput1.b0])
                                                - wt[cutInput1.r0][cutInput1.g0][cutInput1.b0])
                                        max = 0.0f
                                        cutb[0] = -1
                                        var i4: Int = cutInput1.b0 + 1
                                        while (i4 < cutInput1.b1) {
                                            halfR = baseR + ((mr[cutInput1.r1][cutInput1.g1][i4]
                                                    - mr[cutInput1.r1][cutInput1.g0][i4]
                                                    - mr[cutInput1.r0][cutInput1.g1][i4])
                                                    + mr[cutInput1.r0][cutInput1.g0][i4])
                                            halfG = baseG + ((mg[cutInput1.r1][cutInput1.g1][i4]
                                                    - mg[cutInput1.r1][cutInput1.g0][i4]
                                                    - mg[cutInput1.r0][cutInput1.g1][i4])
                                                    + mg[cutInput1.r0][cutInput1.g0][i4])
                                            halfB = baseB + ((mb[cutInput1.r1][cutInput1.g1][i4]
                                                    - mb[cutInput1.r1][cutInput1.g0][i4]
                                                    - mb[cutInput1.r0][cutInput1.g1][i4])
                                                    + mb[cutInput1.r0][cutInput1.g0][i4])
                                            halfW = baseW + ((wt[cutInput1.r1][cutInput1.g1][i4]
                                                    - wt[cutInput1.r1][cutInput1.g0][i4]
                                                    - wt[cutInput1.r0][cutInput1.g1][i4])
                                                    + wt[cutInput1.r0][cutInput1.g0][i4])
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 = (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            halfR = wholeR - halfR
                                            halfG = wholeG - halfG
                                            halfB = wholeB - halfB
                                            halfW = wholeW - halfW
                                            if (halfW == 0L) {
                                                ++i4
                                                continue
                                            }
                                            temp1 += (halfR * halfR + halfG * halfG + halfB * halfB) / halfW.toFloat()
                                            if (temp1 > max) {
                                                max = temp1
                                                cutb[0] = i4
                                            }
                                            ++i4
                                        }
                                        max
                                    }
                                    if (maxr >= maxg && maxr >= maxb) {
                                        dir = red
                                        if (cutr[0] < 0) {
                                            false
                                        } else {
                                            cutInput2!!.r1 = cutInput1.r1
                                            cutInput2.g1 = cutInput1.g1
                                            cutInput2.b1 = cutInput1.b1
                                            when (dir) {
                                                red -> {
                                                    run {
                                                        cutInput1.r1 = cutr[0]
                                                        cutInput2.r0 = cutInput1.r1
                                                    }
                                                    cutInput2.g0 = cutInput1.g0
                                                    cutInput2.b0 = cutInput1.b0
                                                }
                                            }
                                            cutInput1.vol =
                                                (cutInput1.r1 - cutInput1.r0) * (cutInput1.g1 - cutInput1.g0) * (cutInput1.b1 - cutInput1.b0)
                                            cutInput2.vol =
                                                (cutInput2.r1 - cutInput2.r0) * (cutInput2.g1 - cutInput2.g0) * (cutInput2.b1 - cutInput2.b0)
                                            true
                                        }
                                    } else {
                                        dir = if (maxg >= maxr && maxg >= maxb) green else blue
                                        cutInput2!!.r1 = cutInput1.r1
                                        cutInput2.g1 = cutInput1.g1
                                        cutInput2.b1 = cutInput1.b1
                                        when (dir) {
                                            red -> {
                                                run {
                                                    cutInput1.r1 = cutr[0]
                                                    cutInput2.r0 = cutInput1.r1
                                                }
                                                cutInput2.g0 = cutInput1.g0
                                                cutInput2.b0 = cutInput1.b0
                                            }

                                            green -> {
                                                run {
                                                    cutInput1.g1 = cutg[0]
                                                    cutInput2.g0 = cutInput1.g1
                                                }
                                                cutInput2.r0 = cutInput1.r0
                                                cutInput2.b0 = cutInput1.b0
                                            }

                                            blue -> {
                                                run {
                                                    cutInput1.b1 = cutb[0]
                                                    cutInput2.b0 = cutInput1.b1
                                                }
                                                cutInput2.r0 = cutInput1.r0
                                                cutInput2.g0 = cutInput1.g0
                                            }
                                        }
                                        cutInput1.vol =
                                            (cutInput1.r1 - cutInput1.r0) * (cutInput1.g1 - cutInput1.g0) * (cutInput1.b1 - cutInput1.b0)
                                        cutInput2.vol =
                                            (cutInput2.r1 - cutInput2.r0) * (cutInput2.g1 - cutInput2.g0) * (cutInput2.b1 - cutInput2.b0)
                                        true
                                    }
                                }) {
                                /* volume test ensures we won't try to cut one-cell box */
                                vv[next] = if (cube[next]!!.vol > 1) kotlin.run {
                                    /* Compute the weighted variance of a box */
                                    /* NB: as with the raw statistics, this is really the variance * size */
                                    val dr: Float = ((((mr[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - mr[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - mr[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + mr[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - mr[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + mr[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + mr[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - mr[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]).toFloat()
                                    val dg: Float = ((((mg[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - mg[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - mg[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + mg[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - mg[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + mg[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + mg[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - mg[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]).toFloat()
                                    val db: Float = ((((mb[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - mb[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - mb[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + mb[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - mb[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + mb[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + mb[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - mb[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]).toFloat()
                                    ((((m2[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - m2[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - m2[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + m2[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - m2[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + m2[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + m2[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - m2[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0]) - (dr * dr + dg * dg + db * db) / ((((wt[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b1]
                                            - wt[cube[next]!!.r1][cube[next]!!.g1][cube[next]!!.b0]
                                            - wt[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b1])
                                            + wt[cube[next]!!.r1][cube[next]!!.g0][cube[next]!!.b0]
                                            - wt[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b1]
                                            ) + wt[cube[next]!!.r0][cube[next]!!.g1][cube[next]!!.b0]
                                            + wt[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b1])
                                            - wt[cube[next]!!.r0][cube[next]!!.g0][cube[next]!!.b0])
                                } else 0.0f
                                vv[i3] = if (cube[i3]!!.vol > 1) kotlin.run {

                                    /* Compute the weighted variance of a box */
                                    /* NB: as with the raw statistics, this is really the variance * size */
                                    val dr: Float = ((((mr[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - mr[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - mr[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + mr[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - mr[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + mr[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + mr[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - mr[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]).toFloat()
                                    val dg: Float = ((((mg[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - mg[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - mg[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + mg[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - mg[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + mg[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + mg[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - mg[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]).toFloat()
                                    val db: Float = ((((mb[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - mb[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - mb[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + mb[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - mb[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + mb[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + mb[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - mb[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]).toFloat()
                                    ((((m2[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - m2[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - m2[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + m2[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - m2[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + m2[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + m2[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - m2[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0]) - (dr * dr + dg * dg + db * db) / ((((wt[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b1]
                                            - wt[cube[i3]!!.r1][cube[i3]!!.g1][cube[i3]!!.b0]
                                            - wt[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b1])
                                            + wt[cube[i3]!!.r1][cube[i3]!!.g0][cube[i3]!!.b0]
                                            - wt[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b1]
                                            ) + wt[cube[i3]!!.r0][cube[i3]!!.g1][cube[i3]!!.b0]
                                            + wt[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b1])
                                            - wt[cube[i3]!!.r0][cube[i3]!!.g0][cube[i3]!!.b0])
                                } else 0.0f
                            } else {
                                vv[next] = 0.0f /* don't try to split this box again */
                                i3-- /* didn't create box i */
                            }
                            next = 0
                            temp = vv[0]
                            k = 1
                            while (k <= i3) {
                                if (vv[k] > temp) {
                                    temp = vv[k]
                                    next = k
                                }
                                ++k
                            }
                            if (temp <= 0.0f) {
                                break
                            }
                            ++i3
                        }
                        k = 0
                        while (k < lutSize) {
                            var g1: Int
                            var b1: Int
                            var r1: Int = cube[k]!!.r0 + 1
                            while (r1 <= cube[k]!!.r1) {
                                g1 = cube[k]!!.g0 + 1
                                while (g1 <= cube[k]!!.g1) {
                                    b1 = cube[k]!!.b0 + 1
                                    while (b1 <= cube[k]!!.b1) {
                                        tag[(r1 shl 10) + (r1 shl 6) + r1 + (g1 shl 5) + g1 + b1] = k
                                        ++b1
                                    }
                                    ++g1
                                }
                                ++r1
                            }
                            /* Compute sum over a box of any given statistic */
                            weight = ((((wt[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                    - wt[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                    - wt[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                    + wt[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                    - wt[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                    ) + wt[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                    + wt[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                    - wt[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0])
                            if (weight > 0) {
                                lutR = (((((mr[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                        - mr[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                        - mr[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                        + mr[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                        - mr[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                        ) + mr[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                        + mr[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                        - mr[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0]) / weight).toInt()
                                lutG = (((((mg[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                        - mg[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                        - mg[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                        + mg[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                        - mg[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                        ) + mg[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                        + mg[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                        - mg[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0]) / weight).toInt()
                                lutB = (((((mb[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b1]
                                        - mb[cube[k]!!.r1][cube[k]!!.g1][cube[k]!!.b0]
                                        - mb[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b1])
                                        + mb[cube[k]!!.r1][cube[k]!!.g0][cube[k]!!.b0]
                                        - mb[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b1]
                                        ) + mb[cube[k]!!.r0][cube[k]!!.g1][cube[k]!!.b0]
                                        + mb[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b1])
                                        - mb[cube[k]!!.r0][cube[k]!!.g0][cube[k]!!.b0]) / weight).toInt()
                                quantizeInput4[k] = 255 shl 24 or (lutR shl 16) or (lutG shl 8) or lutB
                            } else {
                                quantizeInput4[k] = 0
                            }
                            ++k
                        }
                        i3 = 0
                        while (i3 < size) {
                            if (resultRgb[i3] ushr 24 < 0x80) newPixels[i3] =
                                lutSize.toByte() else newPixels[i3] = tag[qadd[i3]].toByte()
                            ++i3
                        }
                        var bitsPerPixel1 = 0
                        while (1 shl bitsPerPixel1 < lutSize) bitsPerPixel1++
                        colorInfo2[0] = bitsPerPixel1
                        colorInfo2[1] = -1
                        if (transparentColor2 >= 0) {
                            quantizeInput4[lutSize] = transparentColor2 // Set the transparent color
                            colorInfo2[1] = lutSize
                        }
                        lutSize
                    }
                    colorInfo2
                }
            }
            colorDepth = colorInfo[0]
            transparentColor = colorInfo[1]
            val numOfColor = 1 shl colorDepth
            if (firstFrameMbr) {
                var flags = 0x88.toByte()
                var bgcolor: Byte = 0x00
                val aspectRatio: Byte = 0x00
                val colorResolution = 0x07
                flags = (flags.toInt() or (colorResolution shl 4 or colorDepth - 1)).toByte()
                if (transparentColor >= 0) bgcolor = transparentColor.toByte()
                val screenWidth = logicalScreenWidthMbr.toShort()
                val screenHeight = logicalScreenHeightMbr.toShort()
                val flagsShort = flags.toShort()
                val descriptor = ByteArray(7)
                descriptor[0] = (screenWidth.toInt() and 0xff).toByte()
                descriptor[1] = (screenWidth.toInt() shr 8 and 0xff).toByte()
                descriptor[2] = (screenHeight.toInt() and 0xff).toByte()
                descriptor[3] = (screenHeight.toInt() shr 8 and 0xff).toByte()
                descriptor[4] = (flagsShort.toInt() and 0xff).toByte()
                descriptor[5] = bgcolor
                descriptor[6] = aspectRatio
                outputStream.write(descriptor)
                var index = 0
                val colors = ByteArray(numOfColor * 3)
                for (i1 in 0 until numOfColor) {
                    colors[index++] = (colorPalette[i1] shr 16 and 0xff).toByte()
                    colors[index++] = (colorPalette[i1] shr 8 and 0xff).toByte()
                    colors[index++] = (colorPalette[i1] and 0xff).toByte()
                }
                outputStream.write(colors, 0, numOfColor * 3)
                outputStream.write(0x21) // "!"
                outputStream.write(0xfe.toByte().toInt())
                val commentBytes = "Created by Prowdloner".toByteArray()
                val numBlocks = commentBytes.size / 0xff
                val leftOver = commentBytes.size % 0xff
                var offset = 0
                if (numBlocks > 0) {
                    for (i1 in 0 until numBlocks) {
                        outputStream.write(0xff)
                        outputStream.write(commentBytes, offset, 0xff)
                        offset += 0xff
                    }
                }
                if (leftOver > 0) {
                    outputStream.write(leftOver)
                    outputStream.write(commentBytes, offset, leftOver)
                }
                outputStream.write(0)
                val buf = ByteArray(19)
                buf[0] = 0x21 // "!"
                buf[1] = 0xff.toByte()
                buf[2] = 0x0b // Block size
                buf[3] = 'N'.code.toByte()
                buf[4] = 'E'.code.toByte()
                buf[5] = 'T'.code.toByte()
                buf[6] = 'S'.code.toByte()
                buf[7] = 'C'.code.toByte()
                buf[8] = 'A'.code.toByte()
                buf[9] = 'P'.code.toByte()
                buf[10] = 'E'.code.toByte()
                buf[11] = '2'.code.toByte()
                buf[12] = '.'.code.toByte()
                buf[13] = '0'.code.toByte()
                buf[14] = 0x03
                buf[15] = 0x01
                buf[16] = (loopCountMbr and 0xff).toByte()
                buf[17] = (loopCountMbr shr 8 and 0xff).toByte()
                buf[18] = 0x00
                outputStream.write(buf)
            }

            var delay1 = delay
            delay1 = (delay1 / 10.0f).roundToInt()
            val buf = ByteArray(8)
            buf[0] = 0x21 // "!"
            buf[1] = 0xf9.toByte()
            buf[2] = 0x04
            buf[3] = (buf[3].toInt() or (2 and 0x07 shl 2 or (0 and 0x01 shl 1))).toByte()
            buf[4] = (delay1 and 0xff).toByte()
            buf[5] = (delay1 shr 8 and 0xff).toByte()
            buf[6] = transparentColor.toByte()
            buf[7] = 0x00
            if (transparentColor >= 0)
                buf[3] = (buf[3].toInt() or 0x01).toByte()
            outputStream.write(buf, 0, 8)
            if (firstFrameMbr) {
                val imageDescriptor = ByteArray(10)
                imageDescriptor[0] = 0x2c // ","
                imageDescriptor[1] = (0 and 0xff).toByte()
                imageDescriptor[2] = (0 shr 8 and 0xff).toByte()
                imageDescriptor[3] = (0 and 0xff).toByte()
                imageDescriptor[4] = (0 shr 8 and 0xff).toByte()
                imageDescriptor[5] = (imageWidth and 0xff).toByte()
                imageDescriptor[6] = (imageWidth shr 8 and 0xff).toByte()
                imageDescriptor[7] = (imageHeight and 0xff).toByte()
                imageDescriptor[8] = (imageHeight shr 8 and 0xff).toByte()
                imageDescriptor[9] = 0x20.toByte()
                outputStream.write(imageDescriptor, 0, 10)
                firstFrameMbr = false
            } else {
                val colorTableSize = colorDepth - 1
                val imageDescriptor = ByteArray(10)
                imageDescriptor[0] = 0x2c // ","
                imageDescriptor[1] = (0 and 0xff).toByte()
                imageDescriptor[2] = (0 shr 8 and 0xff).toByte()
                imageDescriptor[3] = (0 and 0xff).toByte()
                imageDescriptor[4] = (0 shr 8 and 0xff).toByte()
                imageDescriptor[5] = (imageWidth and 0xff).toByte()
                imageDescriptor[6] = (imageWidth shr 8 and 0xff).toByte()
                imageDescriptor[7] = (imageHeight and 0xff).toByte()
                imageDescriptor[8] = (imageHeight shr 8 and 0xff).toByte()
                imageDescriptor[9] = 0x20.toByte()
                if (colorTableSize >= 0)
                    imageDescriptor[9] = (imageDescriptor[9].toInt() or (1 shl 7 or colorTableSize)).toByte()
                outputStream.write(imageDescriptor, 0, 10)
                var index = 0
                val colors = ByteArray(numOfColor * 3)
                for (i1 in 0 until numOfColor) {
                    colors[index++] = (colorPalette[i1] shr 16 and 0xff).toByte()
                    colors[index++] = (colorPalette[i1] shr 8 and 0xff).toByte()
                    colors[index++] = (colorPalette[i1] and 0xff).toByte()
                }
                outputStream.write(colors, 0, numOfColor * 3)
            }
            var parent: Int
            var son: Int
            var brother: Int
            var color: Int
            var index = 0
            val dimension = newPixels.size

            outputStream.write(if (colorDepth == 1) 2 else colorDepth.also { colorDepth = it })

            clearCodeMbr = 1 shl colorDepth
            endOfImageMbr = clearCodeMbr + 1
            codeLenMbr = colorDepth + 1
            codeIndexMbr = endOfImageMbr + 1
            Arrays.fill(childMbr, 0)
            Arrays.fill(siblingsMbr, 0)
            Arrays.fill(suffixMbr, 0)

            var code = clearCodeMbr
            var temp = codeLenMbr
            bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                .toInt() or (code and mask[emptyBitsMbr] shl 0)).toByte()
            code = code shr emptyBitsMbr
            temp -= emptyBitsMbr
            while (temp > 0) {
                if (++bufIndexMbr >= 0xff) {
                    outputStream.write(0xff)
                    outputStream.write(bytesBufMbr, 0, 0xff)
                    bufIndexMbr = 0
                    Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                }
                bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr].toInt() or (code and 0xff)).toByte()
                code = code shr 8
                temp -= 8
            }
            emptyBitsMbr = -temp

            parent = newPixels[index++].toInt() and 0xff
            while (index < dimension) {
                color = newPixels[index++].toInt() and 0xff
                son = childMbr[parent]
                if (son > 0) {
                    if (suffixMbr[son] == color) {
                        parent = son
                    } else {
                        brother = son
                        while (true) {
                            if (siblingsMbr[brother] > 0) {
                                brother = siblingsMbr[brother]
                                if (suffixMbr[brother] == color) {
                                    parent = brother
                                    break
                                }
                            } else {
                                siblingsMbr[brother] = codeIndexMbr
                                suffixMbr[codeIndexMbr] = color
                                var code1 = parent
                                var temp1 = codeLenMbr
                                bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                                    .toInt() or (code1 and mask[emptyBitsMbr] shl 8 - emptyBitsMbr)).toByte()
                                code1 = code1 shr emptyBitsMbr
                                temp1 -= emptyBitsMbr
                                while (temp1 > 0) {
                                    if (++bufIndexMbr >= 0xff) {
                                        outputStream.write(0xff)
                                        outputStream.write(bytesBufMbr, 0, 0xff)
                                        bufIndexMbr = 0
                                        Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                                    }
                                    bytesBufMbr[bufIndexMbr] =
                                        (bytesBufMbr[bufIndexMbr].toInt() or (code1 and 0xff)).toByte()
                                    code1 = code1 shr 8
                                    temp1 -= 8
                                }
                                emptyBitsMbr = -temp1
                                parent = color
                                codeIndexMbr++
                                if (codeIndexMbr > 1 shl codeLenMbr) {
                                    if (codeLenMbr == 12) {
                                        var code2 = clearCodeMbr
                                        var temp2 = codeLenMbr
                                        bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                                            .toInt() or (code2 and mask[emptyBitsMbr] shl 8 - emptyBitsMbr)).toByte()
                                        code2 = code2 shr emptyBitsMbr
                                        temp2 -= emptyBitsMbr
                                        while (temp2 > 0) {
                                            if (++bufIndexMbr >= 0xff) {
                                                outputStream.write(0xff)
                                                outputStream.write(bytesBufMbr, 0, 0xff)
                                                bufIndexMbr = 0
                                                Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                                            }
                                            bytesBufMbr[bufIndexMbr] =
                                                (bytesBufMbr[bufIndexMbr].toInt() or (code2 and 0xff)).toByte()
                                            code2 = code2 shr 8
                                            temp2 -= 8
                                        }
                                        emptyBitsMbr = -temp2

                                        clearCodeMbr = 1 shl colorDepth
                                        endOfImageMbr = clearCodeMbr + 1
                                        codeLenMbr = colorDepth + 1
                                        codeIndexMbr = endOfImageMbr + 1
                                        Arrays.fill(childMbr, 0)
                                        Arrays.fill(siblingsMbr, 0)
                                        Arrays.fill(suffixMbr, 0)

                                    } else codeLenMbr++
                                }
                                break
                            }
                        }
                    }
                } else {
                    childMbr[parent] = codeIndexMbr
                    suffixMbr[codeIndexMbr] = color
                    var code1 = parent
                    var temp1 = codeLenMbr
                    bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                        .toInt() or (code1 and mask[emptyBitsMbr] shl 8 - emptyBitsMbr)).toByte()
                    code1 = code1 shr emptyBitsMbr
                    temp1 -= emptyBitsMbr
                    while (temp1 > 0) {
                        if (++bufIndexMbr >= 0xff) {
                            outputStream.write(0xff)
                            outputStream.write(bytesBufMbr, 0, 0xff)
                            bufIndexMbr = 0
                            Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                        }
                        bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr].toInt() or (code1 and 0xff)).toByte()
                        code1 = code1 shr 8
                        temp1 -= 8
                    }
                    emptyBitsMbr = -temp1
                    parent = color
                    codeIndexMbr++
                    if (codeIndexMbr > 1 shl codeLenMbr) {
                        if (codeLenMbr == 12) {
                            var code2 = clearCodeMbr
                            var temp2 = codeLenMbr
                            bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                                .toInt() or (code2 and mask[emptyBitsMbr] shl 8 - emptyBitsMbr)).toByte()
                            code2 = code2 shr emptyBitsMbr
                            temp2 -= emptyBitsMbr
                            while (temp2 > 0) {
                                if (++bufIndexMbr >= 0xff) {
                                    outputStream.write(0xff)
                                    outputStream.write(bytesBufMbr, 0, 0xff)
                                    bufIndexMbr = 0
                                    Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                                }
                                bytesBufMbr[bufIndexMbr] =
                                    (bytesBufMbr[bufIndexMbr].toInt() or (code2 and 0xff)).toByte()
                                code2 = code2 shr 8
                                temp2 -= 8
                            }
                            emptyBitsMbr = -temp2
                            clearCodeMbr = 1 shl colorDepth
                            endOfImageMbr = clearCodeMbr + 1
                            codeLenMbr = colorDepth + 1
                            codeIndexMbr = endOfImageMbr + 1
                            Arrays.fill(childMbr, 0)
                            Arrays.fill(siblingsMbr, 0)
                            Arrays.fill(suffixMbr, 0)
                        } else codeLenMbr++
                    }
                }
            }
            var code1 = parent
            var temp1 = codeLenMbr
            bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                .toInt() or (code1 and mask[emptyBitsMbr] shl 8 - emptyBitsMbr)).toByte()
            code1 = code1 shr emptyBitsMbr
            temp1 -= emptyBitsMbr
            while (temp1 > 0) {
                if (++bufIndexMbr >= 0xff) {
                    outputStream.write(0xff)
                    outputStream.write(bytesBufMbr, 0, 0xff)
                    bufIndexMbr = 0
                    Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                }
                bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr].toInt() or (code1 and 0xff)).toByte()
                code1 = code1 shr 8
                temp1 -= 8
            }
            emptyBitsMbr = -temp1
            var code2 = endOfImageMbr
            var temp2 = codeLenMbr
            bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr]
                .toInt() or (code2 and mask[emptyBitsMbr] shl 8 - emptyBitsMbr)).toByte()
            code2 = code2 shr emptyBitsMbr
            temp2 -= emptyBitsMbr
            while (temp2 > 0) {
                if (++bufIndexMbr >= 0xff) {
                    outputStream.write(0xff)
                    outputStream.write(bytesBufMbr, 0, 0xff)
                    bufIndexMbr = 0
                    Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
                }
                bytesBufMbr[bufIndexMbr] = (bytesBufMbr[bufIndexMbr].toInt() or (code2 and 0xff)).toByte()
                code2 = code2 shr 8
                temp2 -= 8
            }
            val len: Int = bufIndexMbr + 1
            outputStream.write(len)
            outputStream.write(bytesBufMbr, 0, len)
            bufIndexMbr = 0
            Arrays.fill(bytesBufMbr, 0, 0xff, 0x00.toByte())
            outputStream.write(0x00)
        }
        outputStream.write(0x3b) // ";"
    }


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    private data class RgbBox(
        var r0: Int = 0,
        var r1: Int = 0,
        var g0: Int = 0,
        var g1: Int = 0,
        var b0: Int = 0,
        var b1: Int = 0,
        var vol: Int = 0
    )

    private class IntHashtable {
        private var array: Array<HashEntry?>
        private var currentSize = 0

        init {
            array = arrayOfNulls<HashEntry?>(1023)
            currentSize = 0
            for (i in array.indices) array[i] = null
        }

        fun get(key: Int): Int? {
            var collisionNum = 0

            // And with the largest positive integer
            var currentPos = (key and 0x7FFFFFFF) % array.size
            while (array[currentPos] != null &&
                array[currentPos]!!.key != key
            ) {
                currentPos += 2 * ++collisionNum - 1 // Compute ith probe
                if (currentPos >= array.size) // Implement the mod
                    currentPos -= array.size
            }
            return if (array[currentPos] != null && array[currentPos]!!.isActive) array[currentPos]!!.value else null
        }

        fun put(key: Int, value: Int) {
            // Insert key as active
            var collisionNum = 0

            // And with the largest positive integer
            var currentPos = (key and 0x7FFFFFFF) % array.size
            while (array[currentPos] != null &&
                array[currentPos]!!.key != key
            ) {
                currentPos += 2 * ++collisionNum - 1 // Compute ith probe
                if (currentPos >= array.size) // Implement the mod
                    currentPos -= array.size
            }
            if (array[currentPos] != null && array[currentPos]!!.isActive) return
            array[currentPos] = HashEntry(key, value, true)

            // Rehash
            if (++currentSize > array.size / 2) {
                val oldArray = array

                // Create a new double-sized, empty table
                array = arrayOfNulls(
                    kotlin.run {
                        val n = 2 * oldArray.size
                        var n1 = n
                        n1++
                        while (!kotlin.run {
                                var resultBool = true
                                if (n1 == 2 || n1 == 3) resultBool = true
                                if (n1 == 1 || n1 % 2 == 0) resultBool = false
                                var i = 3
                                while (i * i <= n1) {
                                    if (n1 % i == 0) {
                                        resultBool = false
                                        break
                                    }
                                    i += 2
                                }
                                resultBool
                            }) {
                            n1 += 2
                        }
                        n1
                    })
                currentSize = 0

                // Copy table over
                for (i in oldArray.indices) if (oldArray[i] != null && oldArray[i]!!.isActive) put(
                    oldArray[i]!!.key,
                    oldArray[i]!!.value
                )
            }
        }

        private class HashEntry(// the key
            var key: Int, // the value
            var value: Int, // false if deleted
            var isActive: Boolean
        )
    }
}