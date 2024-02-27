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

package net.ankio.auto.api


/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.app.AndroidAppHelper
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.ankio.auto.HookMainApp
import net.ankio.auto.exceptions.AutoServiceException
import net.ankio.auto.utils.ActiveUtils
import net.ankio.auto.utils.HookUtils

abstract class Hooker : iHooker {
    abstract var partHookers: MutableList<PartHooker>
    private var TAG = "AutoAccounting"
    lateinit var hookUtils: HookUtils
    private fun hookMainInOtherAppContext() {
        var hookStatus = false
        XposedHelpers.findAndHookMethod(
            Application::class.java, "attach",
            Context::class.java, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    if(hookStatus){
                        return
                    }
                    hookStatus = true
                    val context = param.args[0] as Context
                    val application = param.thisObject as Application
                    runCatching {
                        initLoadPackage(context.classLoader,application)
                    }.onFailure {
                        XposedBridge.log("自动记账Hook异常..${it.message}.")
                        Log.e("AutoAccountingError", it.message.toString())
                        it.printStackTrace()
                    }
                }
        })
    }

    fun initLoadPackage(classLoader: ClassLoader?,application: Application){
        XposedBridge.log("[$TAG] Welcome to AutoAccounting")
        if(classLoader==null){
            XposedBridge.log("[AutoAccounting]"+this.appName+"hook失败: classloader 或 context = null")
            return
        }
        ActiveUtils.APPLICATION_ID = packPageName

        hookLoadPackage(classLoader,application)
        try{
            hookUtils = HookUtils(application, packPageName)
        }catch (e: AutoServiceException){
            XposedBridge.log("[AutoAccounting]自动记账服务未启动: "+e.message)
            return
        }

        hookUtils.logD(HookMainApp.getTag(appName,packPageName),"欢迎使用自动记账，该日志表示 $appName App 已被hook。")
        for (hook in partHookers) {
            try {
                hook.onInit(classLoader,application)
            }catch (e:Exception){
                e.message?.let { Log.e("AutoAccountingError", it) }
                println(e)
                hookUtils.log(HookMainApp.getTag(),"自动记账Hook异常..${e.message}.")
            }
        }
    }

    @Throws(ClassNotFoundException::class)
    abstract fun hookLoadPackage(classLoader: ClassLoader?,context: Context?)
    override fun onLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val pkg = lpparam?.packageName
        val processName = lpparam?.processName
        if (lpparam != null) {
            if (!lpparam.isFirstApplication) return
        }
        if (pkg != packPageName || processName != packPageName) return
        hookMainInOtherAppContext()
    }

}