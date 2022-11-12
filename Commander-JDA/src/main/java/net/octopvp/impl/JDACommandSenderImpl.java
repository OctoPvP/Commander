package net.octopvp.impl;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.octopvp.JDACommandSender;

public class JDACommandSenderImpl implements JDACommandSender {
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
        return null;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public Member getMember() {
        return null;
    }

    @Override
    public UserSnowflake getUserSnowflake() {
        return null;
    }

    @Override
    public long getIDLong() {
        return 0;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public String getAsTag() {
        return null;
    }
}
