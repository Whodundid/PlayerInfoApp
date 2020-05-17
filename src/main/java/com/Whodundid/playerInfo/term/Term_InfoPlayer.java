package com.Whodundid.playerInfo.term;

import com.Whodundid.core.EnhancedMC;
import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.terminal.gui.ETerminal;
import com.Whodundid.core.terminal.terminalCommand.CommandType;
import com.Whodundid.core.terminal.terminalCommand.TerminalCommand;
import com.Whodundid.core.util.renderUtil.CenterType;
import com.Whodundid.core.util.storageUtil.EArrayList;
import com.Whodundid.playerInfo.PlayerInfoApp;
import com.Whodundid.playerInfo.gui.PlayerInfoWindow;

public class Term_InfoPlayer extends TerminalCommand {
	
	PlayerInfoApp mod = (PlayerInfoApp) RegisteredApps.getApp(AppType.PLAYERINFO);
	
	public Term_InfoPlayer() {
		super(CommandType.MOD_COMMAND);
	}

	@Override public String getName() { return "pinfo"; }
	@Override public boolean showInHelp() { return true; }
	@Override public EArrayList<String> getAliases() { return null; }
	@Override public String getHelpInfo(boolean runVisually) { return "Fetches a player's infomation"; }
	@Override public String getUsage() { return "ex: infoplayer notch"; }
	@Override public void handleTabComplete(ETerminal conIn, EArrayList<String> args) {}
	
	@Override
	public void runCommand(ETerminal termIn, EArrayList<String> args, boolean runVisually) {
		if (mod.isEnabled()) { 
			if (args.isEmpty()) { termIn.error("Not enough arguments!"); termIn.info(getUsage()); }
			else if (args.size() == 1) {
				mod.openInfoPlayer = args.get(0);
			}
			else { termIn.error("Too many arguments!"); termIn.info(getUsage()); }
		}
		else { termIn.error("PlayerInfo App is disabled! Enable it to use."); }
	}

}