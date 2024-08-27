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

package net.ankio.auto.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CacheManager(private val context: Context) {
    /**
     * 将数据保存到缓存中，并设置过期时间
     * @param key 缓存的键
     * @param data 要缓存的数据
     * @param expiryTimeInMinutes 过期时间，单位为分
     */
    suspend fun saveToCacheWithExpiry(
        key: String,
        data: ByteArray,
        expiryTimeInMinutes: Long = 0,
    ) = withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, key)
        FileOutputStream(file).use { fos ->
            fos.write(data)
            val expiryTimeMillis = expiryTimeInMinutes * 60 * 1000 + System.currentTimeMillis()
            fos.write(expiryTimeMillis.toString().toByteArray())
        }
    }

    /**
     * 从缓存中读取数据
     * @param key 缓存的键
     * @return 缓存的数据，如果没有找到或已过期则返回 null
     */
    suspend fun readFromCache(key: String): ByteArray =
        withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, key)
            if (file.exists()) {
                return@withContext FileInputStream(file).use { fis ->
                    val data = fis.readBytes()
                    val expiryTime =
                        data.takeLast(13).map { it.toInt().toChar() }.joinToString("").toLong()
                    val nowTime = System.currentTimeMillis()
                    if (nowTime > expiryTime) {
                        file.delete()
                        return@use ByteArray(0)
                    }
                    data.dropLast(13).toByteArray()
                }
            } else {
                return@withContext ByteArray(0)
            }
        }
}
