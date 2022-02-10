package com.shion1305.discord.wordsnake;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

@WebListener
public class WordSnakeMain implements ServletContextListener {
    static WordSnakeDiscord handler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initialization Started");
        handler = new WordSnakeDiscord(ConfigManager.getConfig("DiscordToken"), ConfigManager.getConfig("TargetChannel"));
    }
}
