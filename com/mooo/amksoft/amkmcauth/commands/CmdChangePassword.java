package com.mooo.amksoft.amkmcauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mooo.amksoft.amkmcauth.AuthPlayer;
import com.mooo.amksoft.amkmcauth.Config;
import com.mooo.amksoft.amkmcauth.Hasher;
import com.mooo.amksoft.amkmcauth.Language;
import com.mooo.amksoft.amkmcauth.AmkAUtils;
import com.mooo.amksoft.amkmcauth.AmkMcAuth;

import java.security.NoSuchAlgorithmException;

public class CmdChangePassword implements CommandExecutor {

    @SuppressWarnings("unused") // Despite "unused": IT IS NEEDED in then onEnable Event !!!!!
	private final AmkMcAuth plugin;

    public CmdChangePassword(AmkMcAuth instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
        if (args.length < 2) {
        	//cs.sendMessage(cmd.getDescription());
        	cs.sendMessage(String.format(Language.USAGE_CHANGEPAS2.toString(), new Object[] { cmd }));
        	cs.sendMessage(Language.USAGE_CHANGEPAS1.toString());
        	
            return false;
        }
  		String oldPassword = AmkAUtils.getFinalArg(args, 0).trim(); // support spaces
        String newPassword = AmkAUtils.getFinalArg(args, 1).trim();
  		return ChgMyPswd((Player) cs, cmd.getName(), oldPassword, newPassword);
  	}


    public static boolean CmdChgMyPswd(CommandSender cs, String cmd, String args)
    {
        String [] passwords = args.split(",") ;
        //String newPassword = args[1];

    	//if (oldPassword.trim()=="" || newPassword.trim()=="")
        if(passwords.length<2 )
    	{
    		cs.sendMessage(String.format(Language.USAGE_CHANGEPAS0.toString(), new Object[] { cmd }));
    		cs.sendMessage(Language.USAGE_CHANGEPAS1.toString());

              return false;
    	}

  		return ChgMyPswd((Player) cs, cmd, passwords[0].trim(), passwords[1].trim());
    }
    
    private static boolean ChgMyPswd(Player cs, String cmd, String oldRawPassword, String newRawPassword) {
        if (cmd.equalsIgnoreCase("changepassword")) {
            if (!cs.hasPermission("amkauth.changepassword")) {
                AmkAUtils.dispNoPerms(cs);
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + AmkAUtils.colorize(Language.COMMAND_NO_CONSOLE.toString()));
                return true;
            }
            Player p = (Player) cs;
            AuthPlayer ap = AuthPlayer.getAuthPlayer(p);
            if (!ap.isLoggedIn()) {
                cs.sendMessage(ChatColor.RED + AmkAUtils.colorize(Language.YOU_MUST_LOGIN.toString()));
                return true;
            }
            String oldPassword;
            String newPassword;
            for (String disallowed : Config.disallowedPasswords) {
                if (!newRawPassword.equalsIgnoreCase(disallowed)) continue;
                cs.sendMessage(ChatColor.RED + AmkAUtils.colorize(Language.DISALLOWED_PASSWORD.toString()));
                return true;
            }
            try {
                oldPassword = Hasher.encrypt(oldRawPassword, ap.getHashType());
                newPassword = Hasher.encrypt(newRawPassword, Config.passwordHashType);
            } catch (NoSuchAlgorithmException e) {
                cs.sendMessage(ChatColor.RED + AmkAUtils.colorize(Language.ADMIN_SET_UP_INCORRECTLY.toString()));
                cs.sendMessage(ChatColor.RED + AmkAUtils.colorize(Language.YOUR_PASSWORD_COULD_NOT_BE_CHANGED.toString()));
                return true;
            }
            if (!ap.getPasswordHash().equals(oldPassword)) {
                cs.sendMessage(ChatColor.RED + AmkAUtils.colorize(Language.OLD_PASSWORD_INCORRECT.toString()));
                return true;
            }
            ap.setHashedPassword(newPassword, Config.passwordHashType);
            cs.sendMessage(ChatColor.BLUE + AmkAUtils.colorize(Language.YOUR_PASSWORD_CHANGED.toString()));
            return true;
        }
        return false;
    }

}
