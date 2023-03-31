package com.rainbow.pangu.entity

import com.rainbow.pangu.constant.DefaultValue.MAX_TIME
import com.rainbow.pangu.constant.DefaultValue.MIN_TIME
import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `goods` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "goods", comment = "商品")
@jakarta.persistence.Table(name = "goods")
class Goods : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<Goods>

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("创作者ID")
    var creatorId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("系列ID(预留)")
    var categoryId = 0

    @Column(name = "`name`", nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("名称")
    var name = ""

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("发行价(分)")
    var initPrice = 0L

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("发行数量")
    var initCount = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("真实数量")
    var realCount = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("藏品(展示)数量")
    var nftCount = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("卖方手续费率(万分比)")
    var sellerFeeRate = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("买方手续费率(万分比)")
    var buyerFeeRate = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("图片地址")
    var imageUrl = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("媒体类型(IMAGE/MUSIC/VIDEO)")
    var mediaType = Type.IMAGE

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("媒体地址")
    var mediaUrl = ""

    @Column(nullable = false, columnDefinition = "text")
    @Comment("描述")
    var description = ""

    @Column(nullable = false, columnDefinition = "text")
    @Comment("信息")
    var information = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("认证网络")
    var blockchainNet = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("认证协议")
    var blockchainContract = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("合约地址")
    var blockchainAddress = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("IPFS")
    var ipfs = ""

    @Column(columnDefinition = "timestamp")
    @Comment("一级市场开放时间")
    var primaryTime = MAX_TIME

    @Column(columnDefinition = "timestamp")
    @Comment("二级市场开放时间")
    var secondaryTime = MAX_TIME

    @Column(columnDefinition = "timestamp")
    @Comment("开始时间")
    var startTime: LocalDateTime = MIN_TIME

    @Column(columnDefinition = "timestamp")
    @Comment("结束时间")
    var endTime: LocalDateTime = MAX_TIME

    override fun toString(): String {
        return "Goods(id=$id, creatorId=$creatorId, categoryId=$categoryId, name='$name', initPrice=$initPrice, initCount=$initCount, realCount=$realCount, nftCount=$nftCount, sellerFeeRate=$sellerFeeRate, buyerFeeRate=$buyerFeeRate, imageUrl='$imageUrl', mediaType=$mediaType, mediaUrl='$mediaUrl', description='$description', information='$information', blockchainNet='$blockchainNet', blockchainContract='$blockchainContract', blockchainAddress='$blockchainAddress', ipfs='$ipfs', primaryTime=$primaryTime, secondaryTime=$secondaryTime, startTime=$startTime, endTime=$endTime)"
    }

    enum class Type {
        IMAGE, MUSIC, VIDEO
    }
}