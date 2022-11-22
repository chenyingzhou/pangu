package com.rainbow.pangu.service

import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.util.RedisUtil
import org.springframework.stereotype.Service

@Service
class UnsoldService {
    /**
     * 剩余数量
     */
    fun stockNum(goodsId: Int): Int {
        val key = KeyTemplate.UNSOLD_GOODS_ITEM_IDS.fill(goodsId)
        return RedisUtil.sCard(key)
    }

    /**
     * 获取goodsItemIds
     */
    fun pop(goodsId: Int, count: Int): List<Int> {
        val key = KeyTemplate.UNSOLD_GOODS_ITEM_IDS.fill(goodsId)
        val goodsItemIds = RedisUtil.sPop(key, count, Int::class)
        if (goodsItemIds.size < count) {
            add(goodsId, goodsItemIds)
            return emptyList()
        }
        return goodsItemIds
    }

    /**
     * 添加goodsItemIds
     */
    fun add(goodsId: Int, goodsItemIds: Iterable<Int>) {
        val key = KeyTemplate.UNSOLD_GOODS_ITEM_IDS.fill(goodsId)
        RedisUtil.sAdd(key, goodsItemIds)
    }
}