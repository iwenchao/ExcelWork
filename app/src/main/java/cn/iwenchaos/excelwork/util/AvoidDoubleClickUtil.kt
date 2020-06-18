package cn.iwenchaos.excelwork.util

/**
 * Created by:  awen on 2020/6/18  11:26 AM
 * email     :  liuwenchao@mockuai.com
 * Describe  :
 */
object AvoidDoubleClickUtil {
    private const val MIN_DELAY_TIME = 500L
    private var lastClickTime: Long? = null

    /**
     * 避免多次点击造成异常
     * return true: 可以正常操作
     * return false : 是快速点击 不能正常操作
     */
    fun avoidFastClick(minDelayTime : Long = MIN_DELAY_TIME): Boolean {
        val currentTime = System.currentTimeMillis()
        var isFastClick = false
        if (currentTime - (lastClickTime ?: 0) >= minDelayTime) {
            isFastClick = true
        }
        lastClickTime = currentTime
        return isFastClick

    }

}