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
package net.ankio.auto.database.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BookName {
    //账户列表
    @PrimaryKey(autoGenerate = true)
    var id = 0

    /**
     * 账户名
     */
    var name: String? = null

    /**
     * 图标是url或base64编码字符串
     */
    var icon: String? = null //图标

    /**
     * 该账户对应的远程账户id
     */
    var remoteId: String? = null
}
