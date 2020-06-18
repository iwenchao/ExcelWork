package cn.iwenchaos.excelwork.util

import android.util.Log
import cn.iwenchaos.excelwork.entity.SampleInfo
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.nio.charset.Charset
import java.util.*


/**
 * Created by:  awen on 2020/6/15  10:46 AM
 * email     :  liuwenchao@mockuai.com
 * Describe  :
 */
object ExcelHelper {
    val TAG = javaClass.simpleName
    var sampleTitleInfo: SampleInfo? = null


    var arial14font: WritableFont? = null
    var arial14format: WritableCellFormat? = null
    var arial10font: WritableFont? = null
    var arial10format: WritableCellFormat? = null
    var arial12font: WritableFont? = null
    var arial12format: WritableCellFormat? = null
    const val UTF8_ENCODING = "UTF-8"
    const val GBK_ENCODING = "GBK"
    /** * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...  */
    init {
        try {
            arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            arial14font!!.colour = Colour.LIGHT_BLUE
            arial14format = WritableCellFormat(arial14font)
            arial14format!!.alignment = Alignment.CENTRE
            arial14format!!.setBorder(Border.ALL, BorderLineStyle.THIN)
            arial14format!!.setBackground(Colour.VERY_LIGHT_YELLOW)
            arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
            arial10format = WritableCellFormat(arial10font)
            arial10format!!.alignment = Alignment.CENTRE
            arial10format!!.setBorder(Border.ALL, BorderLineStyle.THIN)
            arial10format!!.setBackground(Colour.GRAY_25)
            arial12font = WritableFont(WritableFont.ARIAL, 10)
            arial12format = WritableCellFormat(arial12font)
            arial10format!!.alignment = Alignment.CENTRE //对齐格式
            arial12format!!.setBorder(Border.ALL, BorderLineStyle.THIN) //设置边框
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    /**
     * 根据指定文件，返回处理结果的输出文件
     * @return
     */
    fun execute(file: File?, sheetIndex: Int = 0): File? {
        if (file == null || !file.exists() || file.isDirectory) {
            return null
        }
        val originMap = readDataFromExcel(file, sheetIndex)
        val resultSampleList = calculateData(originMap)
        return writeDataToFile(file, resultSampleList)
    }


    /**
     * 从文件中读取数据
     * @return 数据集合；集合中第一个item为当前文件的表头
     */
    private fun readDataFromExcel(
        file: File?,
        sheetIndex: Int
    ): MutableMap<String, MutableList<SampleInfo>>? {
        if (file == null || !file.exists() || file.isDirectory) {
            return null
        }
        val readWorkbook = Workbook.getWorkbook(file)
        //获取指定sheet表格
        val sheet = readWorkbook.getSheet(sheetIndex)
        var originList = mutableListOf<SampleInfo>()
        var originMap = mutableMapOf<String, MutableList<SampleInfo>>()
        var sampleInfo: SampleInfo? = null
        //从1行开始遍历
        for (i in 0 until sheet.rows) {//行数据
            sampleInfo = SampleInfo().apply {
                scNumber = sheet.getCell(0, i).contents
                name = sheet.getCell(1, i).contents
                originQty = sheet.getCell(2, i).contents
                restQty = sheet.getCell(3, i).contents
                usdUnit = sheet.getCell(4, i).contents
                discount = sheet.getCell(5, i).contents
                unoutGoodsFee = sheet.getCell(6, i).contents
                ctnQty = sheet.getCell(7, i).contents
                gw = sheet.getCell(8, i).contents
                L = sheet.getCell(9, i).contents
                W = sheet.getCell(10, i).contents
                H = sheet.getCell(11, i).contents
                qtyDivCtn = sheet.getCell(12, i).contents
                CBM = sheet.getCell(13, i).contents
                part1 = sheet.getCell(14, i).contents
                part2 = sheet.getCell(15, i).contents
                part3 = sheet.getCell(16, i).contents
            }
            if (i == 0) {
                sampleTitleInfo = sampleInfo
            }
            var targetList = originMap[sampleInfo.name]
            if (targetList == null) {
                targetList = mutableListOf()
                originMap.put(sampleInfo.name ?: "", targetList)
            }
            targetList.add(sampleInfo)
        }
        Log.i(TAG, "成功读取完成")
        return originMap
    }

    /**
     * 从数据集合中计算相同样品的 rest qty的总和
     */
    private fun calculateData(originMap: MutableMap<String, MutableList<SampleInfo>>?): MutableList<SampleInfo>? {
        if (originMap.isNullOrEmpty()) {
            return null
        }
        val result = mutableListOf<SampleInfo>()
        for (entry in originMap.entries) {
            val sampleInfoList = entry.value
            if (sampleInfoList.isNullOrEmpty()) {
                break
            }
            val sampleInfo = sampleInfoList[0]
            var totalRestQty = 0
            for (i in 0 until sampleInfoList.size) {
                val sample = sampleInfoList[i]
                totalRestQty += sample.getIntRestQty()
            }

            val resultSampleInfo = SampleInfo().apply {
                name = sampleInfo.name
                restQty = totalRestQty.toString()
            }
            result.add(resultSampleInfo)
        }
        return result
    }

    /**
     * 将数据
     */
    private fun writeDataToFile(originFile: File, result: MutableList<SampleInfo>?): File? {
        if (result.isNullOrEmpty()) {
            return null
        }
        val outFile = File(originFile.parent, "${originFile.name.replace(".xls", "")}_result.xls")
        if (outFile.exists()) {
            outFile.delete()
        }
        outFile.createNewFile()
        var writableWorkbook: WritableWorkbook? = null
        try {
            val wbs = WorkbookSettings().apply {
                encoding = UTF8_ENCODING
                locale = Locale.CHINESE
            }
            writableWorkbook = Workbook.createWorkbook(outFile, wbs)


            val writeSheet = writableWorkbook.createSheet("结果表", 0)
            writeSheet.addCell(Label(0, 0, sampleTitleInfo?.name, arial10format))
            writeSheet.addCell(Label(1, 0, "总计Rest Qty", arial10format))

            for (i in 0 until result.size) {
                writeSheet.addCell(Label(0, i + 1, result[i].name, arial12format))
                writeSheet.addCell(Label(1, i + 1, result[i].restQty, arial12format))
            }

            writableWorkbook?.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                writableWorkbook?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return outFile
    }


}