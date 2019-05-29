package me.doppeey.tjbot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class InoriFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (level.isGreaterOrEqual(Level.WARN)) {
            return FilterReply.ACCEPT;
        }
        return logger.getName().startsWith("me.doppeey.tjbot") ? FilterReply.ACCEPT : FilterReply.DENY;
    }
}
