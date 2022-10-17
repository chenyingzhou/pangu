package com.rainbow.pangu.api.service

import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.util.HexUtil
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FileService {
    private val timePathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss-")

    private fun handleFileName(originFileName: String): String {
        val index = originFileName.lastIndexOf(".")
        if (index == -1) {
            throw BizException("该文件没有后缀名，请重命名后再上传")
        }
        val suffix = originFileName.substring(index + 1)
        val fileType = when (suffix) {
            "jpg", "jpeg", "gif", "png" -> "image"
            "xls", "xlsx" -> "excel"
            else -> suffix
        }
        val hex = HexUtil.toHex(originFileName + (0..999999999).random()).substring(0, 8)
        return fileType + "/" + timePathFormatter.format(LocalDateTime.now()) + hex + "." + suffix
    }

    fun upload(file: MultipartFile): String {
        // TODO 上传
        return handleFileName(file.originalFilename!!)
    }
}
