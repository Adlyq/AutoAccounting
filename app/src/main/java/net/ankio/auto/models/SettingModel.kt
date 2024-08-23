/*
 * Copyright (C) 2024 ankio(ankio@ankio.net)
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

package net.ankio.auto.models

import com.google.gson.JsonPrimitive
import kotlinx.coroutines.launch
import net.ankio.auto.utils.AppUtils
import net.ankio.auto.utils.Logger

class SettingModel {
    var id  = 0
    var app = ""
    var key = ""
    var value = ""

    companion object {
        fun set(settingModel: SettingModel) {
            AppUtils.getScope().launch {
                AppUtils.getService().sendMsg("setting/set", settingModel)
            }
        }

        suspend fun get(
            app: String,
            key: String,
        ): String {
            val data = AppUtils.getService().sendMsg("setting/get", mapOf("app" to app, "key" to key))
            return runCatching { (data as JsonPrimitive).asString }.onFailure { Logger.e("Error",it) }.getOrNull() ?: ""
        }
    }
}
