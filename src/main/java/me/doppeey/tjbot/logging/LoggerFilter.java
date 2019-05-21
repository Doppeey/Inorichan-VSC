package me.doppeey.tjbot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LoggerFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLevel() == Level.INFO) {
            return event.getLoggerName().startsWith("me.doppeey.tjbot") ? FilterReply.ACCEPT : FilterReply.DENY;
        }
        return event.getLevel().toInt() > Level.INFO_INT ? FilterReply.ACCEPT : FilterReply.DENY;
    }
}
