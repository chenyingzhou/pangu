package com.rainbow.pangu.api.service

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.PutObjectRequest
import com.rainbow.pangu.api.config.OssConfig
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.util.HexUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FileService : BeanFactoryAware {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private lateinit var ossClient: OSS
    private var bucketName = ""
    private val timePathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss-")

    override fun setBeanFactory(beanFactory: BeanFactory) {
        val ossConfig = beanFactory.getBean(OssConfig::class.java)
        bucketName = ossConfig.bucketName
        ossClient = OSSClientBuilder().build(ossConfig.endpoint, ossConfig.accessKeyId, ossConfig.accessKeySecret)
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
        val request = PutObjectRequest(bucketName, objectName, file.inputStream)
        try {
            ossClient.putObject(request)
        } catch (e: Exception) {
            log.error("上传文件出现异常{}", e.message)
            throw BizException("上传失败")
        }
        return objectName
    }
}
