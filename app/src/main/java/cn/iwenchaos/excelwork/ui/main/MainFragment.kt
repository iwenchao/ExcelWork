package cn.iwenchaos.excelwork.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.iwenchaos.excelwork.R
import cn.iwenchaos.excelwork.constant.Constant
import cn.iwenchaos.excelwork.util.AvoidDoubleClickUtil
import cn.iwenchaos.excelwork.util.ExcelHelper
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.File

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        etInputFile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                fileName = s?.toString()?.trim()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        tvInputFile.setOnClickListener {

            if (fileName.isNullOrBlank() || fileName?.trim()?.isBlank() == true) {
                Toast.makeText(activity, "请先输入文件名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (AvoidDoubleClickUtil.avoidFastClick(1500)) {
                clearConsole()
                consoleAppend("开始...")
                if (validFile(Constant.File.IN_FILE_DIR.plus(fileName))) {
                    consoleAppend("校验文件成功！")
                    doWork()
                } else {
                    consoleAppend("校验文件失败！")
                }
            } else {
                Toast.makeText(activity, "请勿频繁点击", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun validFile(filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) {
            Toast.makeText(activity, "请输入正确的文件路径", Toast.LENGTH_SHORT).show()
            return false
        }
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(activity, "未找到指定文件", Toast.LENGTH_SHORT).show()
            return false
        }
        if (file.exists() && file.isDirectory) {
            Toast.makeText(activity, "请输入正确的文件路径", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun doWork() {
        consoleAppend("开始解析文件！")
        var resultFile = ExcelHelper.execute(File(Constant.File.IN_FILE_DIR.plus(fileName)))
        consoleAppend("结束！")
        consoleAppend(resultFile?.name ?: "文件解析失败！")
    }

    private fun clearConsole() {
        tvConsole.text = "控制台信息："
    }

    private fun consoleAppend(text: String?) {
        tvConsole.run {
            append("\n")
            append(text)
        }

    }

}
