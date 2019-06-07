package me.doppey.tjbot.logging;

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
        } else if (level.isGreaterOrEqual(Level.WARN)) {
            return FilterReply.ACCEPT;
        } else if (logger.getName().contains("mongodb")) {
            return FilterReply.DENY;
        } else return FilterReply.NEUTRAL;
    }
}
