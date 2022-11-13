package net.octopvp.impl;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.octopvp.JDACommandSender;

public class JDACommandSenderImpl implements JDACommandSender {

    private final User user;
    private Member member;

    public JDACommandSenderImpl(User user) {
        this.user = user;
    }

    public JDACommandSenderImpl(Member member) {
        this.member = member;
        this.user = member.getUser();
    }


    @Override
    public boolean hasPermission(String permissionStr) {
        Permission permission = null;
        try {
            permission = Permission.valueOf(permissionStr);
        } catch (IllegalArgumentException e) {
            for (Permission value : Permission.values()) {
                if (value.getName().equalsIgnoreCase(permissionStr))
                    permission = value;
            }
        }
        return permission != null;
    }

    @Override
    public Object getIdentifier() {
        return user.getIdLong();
    }

    @Override
    public void sendMessage(String message) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public UserSnowflake getUserSnowflake() {
        return user;
    }

    @Override
    public long getIDLong() {
        return user.getIdLong();
    }

    @Override
    public String getID() {
        return user.getId();
    }

    @Override
    public String getAsMention() {
        return user.getAsMention();
    }

    @Override
    public String getAsTag() {
        return user.getAsTag();
    }
}
