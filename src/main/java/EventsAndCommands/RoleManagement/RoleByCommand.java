package EventsAndCommands.RoleManagement;

// --Commented out by Inspection START (28.12.2018 17:57):
// --Commented out by Inspection START (28.12.2018 17:57):
////class RoleByCommand extends ListenerAdapter {
////
////
////    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
////
////        String[] messageArray = event.getMessage().getContentRaw().split(" ");
////        GuildController gc = new GuildController(event.getGuild());
////        if (!event.getAuthor().isBot()) {
////
////
////            if (messageArray[0].equalsIgnoreCase(">beginner")) {
////                if (!event.getMember().getRoles().contains(gc.getGuild().getRolesByName("beginner", true).get(0))) {
////                    gc.addRolesToMember(event.getMember(), event.getGuild().getRolesByName("beginner", true).get(0)).queue();
////                    event.getChannel().sendMessage("Role given!").queue();
////                } else {
////                    gc.removeRolesFromMember(event.getMember(), gc.getGuild().getRolesByName("beginner", true).get(0)).queue();
////                    event.getChannel().sendMessage("Role removed!").queue();
// --Commented out by Inspection STOP (28.12.2018 17:57)
//
//                }
//            }
//        }
//
//
//    }
//
//
//}
// --Commented out by Inspection STOP (28.12.2018 17:57)
