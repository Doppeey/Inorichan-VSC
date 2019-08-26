package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class TagCommand extends Command {

    private MongoCollection tagCollection;
    private EventWaiter waiter;

    public TagCommand(MongoDatabase db, EventWaiter waiter) {
        this.waiter = waiter;
        tagCollection = db.getCollection("tags");
        this.name = "tag";
        this.aliases = new String[]{"tags"};
    }

    @Override
    protected void execute(CommandEvent event) {
        Role tagManagerRole;

        try {
            tagManagerRole = event.getGuild().getRolesByName("Tag Manager", true).get(0);

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            event.reply("A \"Tag Manager\" role must exist to use this command");
            return;
        }

        Runnable runnable = () -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Error || Timeout");
            embed.setDescription("30 seconds have passed without a response, tag creation has timed out");
            event.reply(embed.build());
        };

        final String contentRaw = event.getMessage().getContentRaw();

        //If the tag list is called
        if (contentRaw.toLowerCase().contains("tags")) {
            sendTagList(event);
            return;
        }

        //Creating a tag
        if (contentRaw.toLowerCase().contains("tag create")) {
            if (managerRoleMissing(event, tagManagerRole)) {
                return;
            }

            createTag(event, runnable);
            return;
        }

        //Displaying a tag
        if (contentRaw.split(" ").length == 2 && !contentRaw.contains("create") && !contentRaw.contains("edit") && !contentRaw.contains("delete")) {
            displayTag(event, contentRaw);
            return;
        }

        //Editing a tag
        if (contentRaw.contains("edit")) {
            if (managerRoleMissing(event, tagManagerRole)) {
                return;
            }

            //Incase user tries to edit in one line
            if (contentRaw.split(" ")[1].equalsIgnoreCase("edit") && contentRaw.split(" ").length >= 4) {
                if (updateTagOneLine(event, contentRaw)) {
                    return;
                }
                return;
            }

            //otherwise start dialog...
            editTag(event, runnable);
            return;
        }

        //Delete a tag
        if (contentRaw.contains("delete")) {
            if (managerRoleMissing(event, tagManagerRole)) {
                return;
            }
            deleteTag(event, runnable);
        }
    }

    private boolean updateTagOneLine(CommandEvent event, String contentRaw) {
        String name = contentRaw.split(" ")[2].toLowerCase();

        if (tagCollection.find(new Document("name", name)).first() == null) {
            EmbedBuilder alreadyExistsEmbed = new EmbedBuilder();
            alreadyExistsEmbed.setTitle("Error || Not found");
            alreadyExistsEmbed.setColor(Color.RED);
            alreadyExistsEmbed.setDescription("A tag with that name doesn't exist!");
            event.reply(alreadyExistsEmbed.build());
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setTitle("Success || Tag edit");
        embedBuilder.setDescription("The tag " + name + " has successfully been edited!");
        event.reply(embedBuilder.build());

        Document tag = new Document();
        tag.append("name", name);
        String splitRegex = ">tag edit " + contentRaw.split(" ")[2];
        String content = contentRaw.substring(splitRegex.length() + 1);

        tagCollection.updateOne(tag, new Document("$set", new Document("content", content)));
        return false;
    }

    // METHODS
    private void deleteTag(CommandEvent event, Runnable runnable) {
        event.reply("Which tag do you want to delete?");
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                received -> received.getAuthor().equals(event.getAuthor()) && received.getChannel().equals(event.getChannel()), confirmed -> {

                    String name = confirmed.getMessage().getContentRaw().toLowerCase().strip();
                    Document doc = new Document().append("name", name);

                    if (tagCollection.find(doc).first() == null) {
                        EmbedBuilder alreadyExistsEmbed = new EmbedBuilder()
                                .setTitle("Error || Not found")
                                .setColor(Color.RED)
                                .setDescription("A tag with that name doesn't exist!");
                        event.reply(alreadyExistsEmbed.build());
                        return;
                    }

                    tagCollection.deleteOne(doc);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.GREEN);
                    embedBuilder.setTitle("Success || Tag delete");
                    embedBuilder.setDescription("The tag " + name + " has successfully been deleted!");
                    event.reply(embedBuilder.build());
                }, 30, TimeUnit.SECONDS, runnable);
    }

    private void editTag(CommandEvent event, Runnable runnable) {
        event.reply("What tag do you want to edit?");
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                msg -> event.getChannel().equals(msg.getChannel()) && msg.getAuthor().equals(event.getAuthor()),
                correct -> {
                    String name = correct.getMessage().getContentDisplay().toLowerCase();

                    if (tagCollection.find(new Document("name", name)).first() == null) {
                        EmbedBuilder alreadyExistsEmbed = new EmbedBuilder();
                        alreadyExistsEmbed.setTitle("Error || Not found");
                        alreadyExistsEmbed.setColor(Color.RED);
                        alreadyExistsEmbed.setDescription("A tag with that name doesn't exist!");
                        event.reply(alreadyExistsEmbed.build());
                        return;
                    }

                    event.reply("Please tell me the new content.");

                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                            message -> event.getChannel().equals(message.getChannel()) && message.getAuthor().equals(event.getAuthor()), success -> {
                                EmbedBuilder embedBuilder = new EmbedBuilder();
                                embedBuilder.setColor(Color.GREEN);
                                embedBuilder.setTitle("Success || Tag edit");
                                embedBuilder.setDescription("The tag " + name + " has successfully been edited!");
                                event.reply(embedBuilder.build());

                                Document tag = new Document();
                                tag.append("name", name);

                                tagCollection.updateOne(tag, new Document("$set", new Document("content",
                                        success.getMessage().getContentRaw())));
                            }, 30, TimeUnit.SECONDS, runnable);
                }, 30, TimeUnit.SECONDS, runnable);
    }

    private void displayTag(CommandEvent event, String contentRaw) {
        Document doc = new Document("name", contentRaw.split(" ")[1].toLowerCase());
        if (tagCollection.find(doc).first() != null) {

            Document found = (Document) tagCollection.find(doc).first();
            event.reply(found.getString("content"));

        } else {
            EmbedBuilder noSuchTagEmbed = new EmbedBuilder();
            noSuchTagEmbed.setTitle("Error || Not found");
            noSuchTagEmbed.setColor(Color.RED);
            String name = contentRaw.split(" ")[1];
            noSuchTagEmbed.setDescription("A tag with the name " + name + " doesn't exist!");
            event.reply(noSuchTagEmbed.build());
        }
    }

    private void createTag(CommandEvent event, Runnable runnable) {
        event.reply("What do you want the tag to be named?");
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                msg -> event.getChannel().equals(msg.getChannel()) && msg.getAuthor().equals(event.getAuthor()),
                correct -> {
                    String name = correct.getMessage().getContentDisplay().toLowerCase();

                    if (tagCollection.find(new Document("name", name)).first() != null) {
                        EmbedBuilder alreadyExistsEmbed = new EmbedBuilder();
                        alreadyExistsEmbed.setTitle("Error || Duplicate tag");
                        alreadyExistsEmbed.setColor(Color.RED);
                        alreadyExistsEmbed.setDescription("A tag with that name already exists!");
                        event.reply(alreadyExistsEmbed.build());
                        return;
                    }

                    event.reply("Alright, the name will be " + name + ". Please tell me the content now.");
                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                            message -> event.getChannel().equals(message.getChannel()) && message.getAuthor().equals(event.getAuthor()), success -> {
                                EmbedBuilder embedBuilder = new EmbedBuilder();
                                embedBuilder.setColor(Color.GREEN);
                                embedBuilder.setTitle("Success || Tag creation");
                                embedBuilder.setDescription("The tag " + name + " has successfully been created!");
                                event.reply(embedBuilder.build());

                                Document tag = new Document();
                                tag.append("name", name);
                                tag.append("creatorId", event.getAuthor().getId());
                                tag.append("content", success.getMessage().getContentRaw());
                                tagCollection.insertOne(tag);
                            }, 30, TimeUnit.SECONDS, runnable);
                }, 30, TimeUnit.SECONDS, runnable);
    }

    private void sendTagList(CommandEvent event) {
        EmbedBuilder tagList = new EmbedBuilder();
        tagList.setTitle("A list of all tags");
        ArrayList<String> documentList = new ArrayList<>();

        for (Object doc : tagCollection.find()) {
            Document document = (Document) doc;
            documentList.add(document.getString("name"));
        }

        Collections.sort(documentList);

        for (int i = 0; i < documentList.size(); i++) {
            if (i == documentList.size() - 1) {
                tagList.appendDescription(documentList.get(i));
            } else {
                tagList.appendDescription(documentList.get(i)).appendDescription(", ");
            }
        }

        tagList.setColor(Color.GREEN);
        tagList.setFooter("Usage: >tag name", null);
        event.reply(tagList.build());
    }

    private boolean managerRoleMissing(CommandEvent event, Role tagManagerRole) {
        if (!event.getMember().getRoles().contains(tagManagerRole)) {
            event.reply("You need the Tag Manager role to do that.");
            return true;
        }
        return false;
    }
}
