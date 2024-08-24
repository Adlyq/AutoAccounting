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

package org.ezbook.server.server

import fi.iki.elonen.NanoHTTPD.AsyncRunner
import fi.iki.elonen.NanoHTTPD.ClientHandler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CustomAsyncRunner : AsyncRunner {
    private val executor: ExecutorService = Executors.newFixedThreadPool(10) // 创建线程池

    override fun closeAll() {

    }

    override fun closed(clientHandler: ClientHandler) {

    }

    override fun exec(code: ClientHandler?) {
        executor.submit(code)
    }
}