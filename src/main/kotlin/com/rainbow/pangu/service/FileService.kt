package com.rainbow.pangu.service

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.PutObjectRequest
import com.rainbow.pangu.config.OssConfig
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.util.HexUtil
import com.rainbow.pangu.util.BeanUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FileService {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val timePathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss-")
    private val ossConfig: OssConfig by lazy { BeanUtil.getBean(OssConfig::class) }
    private val ossClient: OSS by lazy {
        OSSClientBuilder().build(ossConfig.endpoint, ossConfig.accessKeyId, ossConfig.accessKeySecret)
    }

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
        val objectName = handleFileName(file.originalFilename!!)
        val request = PutObjectRequest(ossConfig.bucketName, objectName, file.inputStream)
        try {
            ossClient.putObject(request)
        } catch (e: Exception) {
            log.error("上传文件出现异常{}", e.message)
            throw BizException("上传失败")
        }
        return "/$objectName"
    }
}
