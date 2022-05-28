package com.whl.wako.plugin.plugins;

import com.whl.wako.plugin.api.Greeting;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

@Extension
public class WelcomeGreeting extends Plugin implements Greeting {

    public WelcomeGreeting(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("WelcomePlugin.start()");
    }

    @Override
    public void stop() {
        System.out.println("WelcomePlugin.stop()");
    }

    @Override
    public void delete() {
        System.out.println("WelcomePlugin.delete()");
    }

    public String getGreeting() {
        return "Welcome";
    }
}