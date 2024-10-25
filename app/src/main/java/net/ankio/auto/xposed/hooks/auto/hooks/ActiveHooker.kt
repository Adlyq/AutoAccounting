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

package net.ankio.auto.xposed.hooks.auto.hooks

import android.app.Application
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import net.ankio.auto.xposed.core.api.HookerManifest
import net.ankio.auto.xposed.core.api.PartHooker
import net.ankio.auto.xposed.core.hook.Hooker
import java.lang.reflect.Field

class ActiveHooker : PartHooker() {

    override fun hook(
        hookerManifest: HookerManifest,
        application: Application?,
        classLoader: ClassLoader
    ) {
        val activeUtils = Hooker.loader("net.ankio.auto.xposed.common.ActiveInfo")
        Hooker.replaceReturn(
            activeUtils,
            "isModuleActive",
            true
        )
        // hook激活方法


        Hooker.after(
            activeUtils,
            "getFramework"
        ){
            // 获取TAG字段
            val tagField: Field = XposedBridge::class.java.getDeclaredField("TAG")
            // 设置字段可访问
            tagField.isAccessible = true
            // 获取TAG字段的值
            it.result = (tagField.get(null) as String).replace("-Bridge", "")
        }
    }
}