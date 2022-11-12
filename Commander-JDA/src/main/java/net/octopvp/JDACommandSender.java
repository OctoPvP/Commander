package net.octopvp;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.octopvp.commander.sender.CoreCommandSender;

public interface JDACommandSender extends CoreCommandSender {
    User getUser();

    Member getMember();

    UserSnowflake getUserSnowflake();

    long getIDLong();

    String getID();

    String getAsMention();

    String getAsTag();
}
