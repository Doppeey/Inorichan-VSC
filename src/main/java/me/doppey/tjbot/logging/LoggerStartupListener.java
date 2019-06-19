package me.doppey.tjbot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import me.doppey.tjbot.Config;
import me.doppey.tjbot.InoriChan;

public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    private boolean started = false;

    @Override
    public void start() {
        if (started) {
            return;
        }

        Config config = InoriChan.getConfig();
        Context context = getContext();

        context.putProperty("HOST", config.getProperty("PT_HOST"));
        context.putProperty("PORT", config.getProperty("PT_PORT"));

        started = true;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public void onReset(LoggerContext context) {
    }

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
    }
}