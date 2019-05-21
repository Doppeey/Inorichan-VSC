package me.doppeey.tjbot.events.utility;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicInteger;

public class PollReactionListener extends ListenerAdapter {


    MongoCollection polls;

    public PollReactionListener(MongoDatabase db) {
        this.polls = db.getCollection("polls");
    }



    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {


        if (!event.getUser().isBot()) {

            AtomicInteger counter = new AtomicInteger(0);

            boolean isReactionToAPoll = polls.find(new Document("_id", event.getMessageId())).first() != null;

            if (isReactionToAPoll) {



                event.getChannel().getMessageById(event.getMessageId()).queue(message -> {

                    for (int i = 0; i < message.getReactions().size(); i++) {
                        message.getReactions().get(i).getUsers().queue(users -> {
                            if (users.contains(event.getUser())) {
                                counter.getAndIncrement();
                                if (counter.get() > 1) {
                                    event.getReaction().removeReaction(event.getUser()).queue();
                                }
                            }
                        });
                    }
                });


            }

        }


    }
}

