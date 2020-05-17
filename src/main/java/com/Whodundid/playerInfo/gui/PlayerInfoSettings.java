package com.Whodundid.playerInfo.gui;

import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.EMCApp;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.enhancedGui.guiObjects.actionObjects.EGuiButton;
import com.Whodundid.core.enhancedGui.guiObjects.advancedObjects.scrollList.EGuiScrollList;
import com.Whodundid.core.enhancedGui.guiObjects.basicObjects.EGuiLabel;
import com.Whodundid.core.enhancedGui.types.WindowParent;
import com.Whodundid.core.enhancedGui.types.interfaces.IEnhancedActionObject;
import com.Whodundid.core.util.renderUtil.EColors;
import com.Whodundid.playerInfo.PlayerInfoApp;
import com.Whodundid.playerInfo.util.PIResources;

public class PlayerInfoSettings extends WindowParent {
	
	PlayerInfoApp app = (PlayerInfoApp) RegisteredApps.getApp(AppType.PLAYERINFO);
	EGuiScrollList list;
	EGuiButton animateSkins, drawNames, randomBackgrounds;
	
	public PlayerInfoSettings() {
		super();
		aliases.add("pisettings");
		windowIcon = PIResources.icon;
	}
	
	@Override
	public void initGui() {
		setObjectName("Player Info Settings");
		defaultDims();
		setMinDims(170, 75);
		setResizeable(true);
	}
	
	@Override
	public void initObjects() {
		defaultHeader(this);
		
		list = new EGuiScrollList(this, startX + 2, startY + 20, width - 4, height - 22);
		list.setBackgroundColor(0xff303030);
		
		int sY = addSkinViewer(8);
		
		list.fitItemsInList(5, 10);
		
		addObject(list);
	}
	
	private int addSkinViewer(int yPos) {
		EGuiLabel skinViewerLabel = new EGuiLabel(list, 6, yPos, "Skin Viewer", EColors.orange.intVal);
		
		animateSkins = new EGuiButton(list, 6, skinViewerLabel.endY + 8, 60, 20, app.animateSkins);
		EGuiLabel animateSkinsLabel = new EGuiLabel(list, animateSkins.endX + 10, animateSkins.startY + 6, "Animate Player Skins");
		
		drawNames = new EGuiButton(list, 6, animateSkins.endY + 8, 60, 20, app.drawNames);
		EGuiLabel drawNamesLabel = new EGuiLabel(list, drawNames.endX + 10, drawNames.startY + 6, "Draw Player Names");
		
		randomBackgrounds = new EGuiButton(list, 6, drawNames.endY + 8, 60, 20, app.randomBackgrounds);
		EGuiLabel randomBackgroundsLabel = new EGuiLabel(list, randomBackgrounds.endX + 10, randomBackgrounds.startY + 6, "Draw Random Backgrounds");
		
		animateSkins.setActionReceiver(this);
		drawNames.setActionReceiver(this);
		randomBackgrounds.setActionReceiver(this);
		
		animateSkinsLabel.setDisplayStringColor(EColors.lgray);
		drawNamesLabel.setDisplayStringColor(EColors.lgray);
		randomBackgroundsLabel.setDisplayStringColor(EColors.lgray);
		
		animateSkinsLabel.setHoverText("Players will perform a walking animation");
		drawNamesLabel.setHoverText("Players will have their name drawn above them");
		randomBackgroundsLabel.setHoverText("Will draw a random Minecraft themed background behind the player being viewed");
		
		list.addObjectToList(skinViewerLabel);
		list.addObjectToList(animateSkins, animateSkinsLabel);
		list.addObjectToList(drawNames, drawNamesLabel);
		list.addObjectToList(randomBackgrounds, randomBackgroundsLabel);
		
		return randomBackgrounds.endY - list.getDimensions().startY;
	}
	
	@Override
	public void drawObject(int mXIn, int mYIn) {
		drawDefaultBackground();
		
		drawRect(startX + 1, startY + 19, endX - 1, endY - 1, -0x00cfcfcf); //grey background
		drawRect(startX, startY + 18, endX, startY + 19, 0xff000000); //top line
		
		drawCenteredStringWithShadow("Player Info App Settings", midX, startY + 6, 0xffbb00);
		
		super.drawObject(mXIn, mYIn);
	}
	
	@Override
	public void actionPerformed(IEnhancedActionObject object, Object... args) {
		if (object == animateSkins) { animateSkins.toggleTrueFalse(PlayerInfoApp.animateSkins, app, false); }
		if (object == drawNames) { drawNames.toggleTrueFalse(PlayerInfoApp.drawNames, app, false); }
		if (object == randomBackgrounds) { randomBackgrounds.toggleTrueFalse(PlayerInfoApp.randomBackgrounds, app, false); }
	}
	
	@Override
	public void sendArgs(Object... args) {
		if (args.length == 2) {
			if (args[0] instanceof String && args[1] instanceof EMCApp) {
				String msg = (String) args[0];
				EMCApp mod = (EMCApp) args[1];
				if (msg.equals("Reload") && mod instanceof PlayerInfoApp) {
					reInitObjects();
				}
			}
		}
	}

}