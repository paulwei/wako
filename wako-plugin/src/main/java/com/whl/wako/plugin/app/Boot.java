/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whl.wako.plugin.app;

import com.whl.wako.plugin.api.Greeting;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;
import java.util.List;

/**
 * A boot class that start the demo.
 *
 * @author Decebal Suiu
 */
public class Boot {

    public static void main(String[] args) {
        // jar插件管理器
        PluginManager pluginManager = new JarPluginManager();

        // 加载指定路径插件
        pluginManager.loadPlugin(Paths.get("plugins-0.0.1-SNAPSHOT.jar"));

        // 启动指定插件(也可以加载所有插件)
        pluginManager.startPlugin("welcome-plugin");

        // 执行插件
        List<Greeting> greetings = pluginManager.getExtensions(Greeting.class);
        for (Greeting greeting : greetings) {
            System.out.println(">>> " + greeting.getGreeting());
        }

        // 停止并卸载指定插件
        pluginManager.stopPlugin("welcome-plugin");
        pluginManager.unloadPlugin("welcome-plugin");

    }

}
