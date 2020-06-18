package cn.iwenchaos.excelwork.entity

import androidx.core.text.isDigitsOnly
import java.io.Serializable

/**
 * Created by:  awen on 2020/6/15  10:58 AM
 * email     :  liuwenchao@mockuai.com
 * Describe  :
 */
class SampleInfo : Serializable {


    var scNumber: String? = null//订单编号
    var name: String? = null//样品名称
    var originQty: String? = null//
    var restQty: String? = null
    var usdUnit: String? = null
    var discount: String? = null//折扣
    var unoutGoodsFee: String? = null
    var ctnQty: String? = null
    var gw: String? = null
    var L: String? = null
    var W: String? = null
    var H: String? = null
    var qtyDivCtn: String? = null
    var CBM: String? = null
    var part1: String? = null
    var part2: String? = null
    var part3: String? = null


    override fun toString(): String {
        return super.toString()
    }

    fun getIntRestQty(): Int {
        if (restQty.isNullOrBlank()) {
            return 0
        }
        return restQty?.trim()?.replace(
            ",",
            ""
        )?.takeIf { it.trim().isNotBlank() && it.isDigitsOnly() }?.toInt() ?: 0
    }

    /**
     * 当前表格列数
     */
    companion object {
        fun size(): Int {
            return 17
        }
    }


}