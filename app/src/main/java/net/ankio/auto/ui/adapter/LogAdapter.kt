/*
 * Copyright (C) 2023 ankio(ankio@ankio.net)
 * Licensed under the Apache License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-3.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.ankio.auto.ui.adapter

import com.hjq.toast.Toaster
import net.ankio.auto.R
import net.ankio.auto.databinding.AdapterLogBinding
import net.ankio.auto.ui.viewModes.LogViewModel
import net.ankio.auto.utils.AppUtils
import net.ankio.auto.models.LogModel

class LogAdapter(
    private val viewModel: LogViewModel,
) : BaseAdapter<AdapterLogBinding, LogModel>(viewModel) {
    override fun onInitView(holder: BaseViewHolder<AdapterLogBinding, LogModel>) {
        val binding = holder.binding as AdapterLogBinding
        binding.log.setOnClickListener {
            AppUtils.copyToClipboard( binding.log.text.toString())
            Toaster.show(R.string.copy_command_success)
        }
    }

    override fun onBindView(
        holder: BaseViewHolder<AdapterLogBinding, LogModel>,
        item: Any,
    ) {
        val binding = holder.binding
        val it = item as LogModel
        val level = it.level
        binding.logDate.text = it.date
        binding.app.text = it.app
        binding.logThread.text = it.thread
        binding.logFile.text = it.line
        // 数据需要进行截断处理，防止过长导致绘制ANR
        binding.log.text = it.log
        when (level) {
            LogModel.LOG_LEVEL_DEBUG -> binding.log.setTextColor(holder.context.getColor(R.color.success))
            LogModel.LOG_LEVEL_INFO -> binding.log.setTextColor(holder.context.getColor(R.color.info))
            LogModel.LOG_LEVEL_WARN -> binding.log.setTextColor(holder.context.getColor(R.color.warning))
            LogModel.LOG_LEVEL_ERROR -> binding.log.setTextColor(holder.context.getColor(R.color.danger))
            else -> binding.log.setTextColor(holder.context.getColor(R.color.info))
        }
    }

    override fun getViewBindingClazz(): Class<AdapterLogBinding> {
        return AdapterLogBinding::class.java
    }
}
