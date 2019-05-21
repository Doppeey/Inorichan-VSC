package me.doppeey.tjbot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import me.doppeey.tjbot.InoriChan;

import java.util.Properties;

public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    private boolean started = false;

    @Override
    public void start() {
        Properties config = InoriChan.CONFIG;

        if (started || config == null) {
            return;
        }

        Context context = getContext();

        context.putProperty("HOST", config.getProperty("PT_HOST"));
        context.putProperty("PORT", config.getProperty("PT_PORT"));
        context.putProperty("NAME", config.getProperty("PT_NAME"));

        started = true;
    }

    @Override
    public void stop() {}

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext loggerContext) {}

    @Override
    public void onReset(LoggerContext loggerContext) {}

    @Override
    public void onStop(LoggerContext loggerContext) {}

    @Override
    public void onLevelChange(Logger logger, Level level) {}
}
