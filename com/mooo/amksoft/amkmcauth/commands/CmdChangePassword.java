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

//import static org.bukkit.Bukkit.getConsoleSender;//d, THIS AND other 'd' lines are things I added for debug, feel free to delete

public class CmdChangePassword implements CommandExecutor {

    @SuppressWarnings("unused") // Despite "unused": IT IS NEEDED in then onEnable Event !!!!!
	private final AmkMcAuth plugin;
    public CmdChangePassword(AmkMcAuth instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
//        getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize("onCommand ISSUED!!"));//d
//        for (String aaa : args)
//        {
//            getConsoleSender().sendMessage(ChatColor.RED + AmkAUtils.colorize(aaa));//d
//        }

        if (args.length < 2) {
        	//cs.sendMessage(cmd.getDescription());
        	cs.sendMessage(String.format(Language.USAGE_CHANGEPAS2.toString(), new Object[] { cmd }));
        	cs.sendMessage(Language.USAGE_CHANGEPAS1.toString());
        	
            return false;
        }

        //MDFM28: This wrong(?!), trim at 0 is same as not trimming at all
        //this return: "oldpassword newpassword" and "newpassword
        //Use split??
        //Maybe not necessary, The 'args' itself is an arrat which consist of oldpassword argument and newpassword argument
        //OLD:
  	//String oldPassword = AmkAUtils.getFinalArg(args, 0).trim(); // support spaces
        //String newPassword = AmkAUtils.getFinalArg(args, 1).trim();
        //NEW:
        String oldPassword = args[0]; //read directly to args[]
        String newPassword = args[1];
        //OK!

        //getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize(oldPassword));//d
        //getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize(newPassword));//d

  		return ChgMyPswd((Player) cs, cmd.getName(), oldPassword, newPassword);
  	}


    public static boolean CmdChgMyPswd(CommandSender cs, String cmd, String args)//when changes issued by command??
    {
        //getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize("CmdChgMyPswd ISSUED!!"));//d
        //getConsoleSender().sendMessage(ChatColor.RED + AmkAUtils.colorize(args));//d

        String [] passwords = args.split(",") ;
        //for (String aaa : passwords)
        //{
        //    getConsoleSender().sendMessage(ChatColor.YELLOW + AmkAUtils.colorize(aaa));//d
        //}
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
        //getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize("ChgMyPswd ISSUED!!"));

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
            //getConsoleSender().sendMessage(ChatColor.RED + AmkAUtils.colorize(oldRawPassword));//d
            //getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize(oldPassword));//d
            //getConsoleSender().sendMessage(ChatColor.RED + AmkAUtils.colorize(newRawPassword));//d
            //getConsoleSender().sendMessage(ChatColor.BLUE + AmkAUtils.colorize(newPassword));//d

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
