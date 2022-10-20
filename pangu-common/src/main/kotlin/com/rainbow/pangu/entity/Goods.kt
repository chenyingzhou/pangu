package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import com.rainbow.pangu.constant.DefaultValue.END_TIME
import com.rainbow.pangu.constant.DefaultValue.START_TIME
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `goods` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "goods", comment = "商品")
@javax.persistence.Table(name = "goods")
class Goods : BaseEntity() {
    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '创作者ID'")
    var creatorId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '系列ID(预留)'")
    var categoryId = 0

    @Column(name = "`name`", nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '名称'")
    var name = ""

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '发行价(分)'")
    var initPrice = 0L

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '发行数量'")
    var initCount = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '真实数量'")
    var realCount = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '藏品(展示)数量'")
    var nftCount = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '卖方手续费率(万分比)'")
    var sellerFeeRate = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '买方手续费率(万分比)'")
    var buyerFeeRate = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '图片地址'")
    var imageUrl = ""

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '媒体类型(IMAGE/MUSIC/VIDEO)'"
    )
    var mediaType = Type.IMAGE

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '媒体地址'")
    var mediaUrl = ""

    @Column(nullable = false, columnDefinition = "text COMMENT '描述'")
    var description = ""

    @Column(nullable = false, columnDefinition = "text COMMENT '信息'")
    var information = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '认证网络'")
    var blockchainNet = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '认证协议'")
    var blockchainContract = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '合约地址'")
    var blockchainAddress = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT 'IPFS'")
    var ipfs = ""

    @Column(columnDefinition = "timestamp COMMENT '二级市场开放时间'")
    var secondaryTime = END_TIME

    @Column(columnDefinition = "timestamp COMMENT '开始时间'")
    var startTime: LocalDateTime = START_TIME

    @Column(columnDefinition = "timestamp COMMENT '结束时间'")
    var endTime: LocalDateTime = END_TIME

    override fun toString(): String {
        return "Goods(id=$id, creatorId=$creatorId, categoryId=$categoryId, name='$name', initPrice=$initPrice, initCount=$initCount, realCount=$realCount, nftCount=$nftCount, sellerFeeRate=$sellerFeeRate, buyerFeeRate=$buyerFeeRate, imageUrl='$imageUrl', mediaType=$mediaType, mediaUrl='$mediaUrl', description='$description', information='$information', blockchainNet='$blockchainNet', blockchainContract='$blockchainContract', blockchainAddress='$blockchainAddress', ipfs='$ipfs', secondaryTime=$secondaryTime, startTime=$startTime, endTime=$endTime)"
    }

    enum class Type {
        IMAGE, MUSIC, VIDEO
    }
}