package com.Whodundid.playerInfo.window;

import com.Whodundid.core.EnhancedMC;
import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.util.EUtil;
import com.Whodundid.core.util.renderUtil.CenterType;
import com.Whodundid.core.util.renderUtil.EColors;
import com.Whodundid.core.util.renderUtil.ScreenLocation;
import com.Whodundid.core.util.storageUtil.EDimension;
import com.Whodundid.core.windowLibrary.windowObjects.actionObjects.WindowButton;
import com.Whodundid.core.windowLibrary.windowObjects.actionObjects.WindowTextField;
import com.Whodundid.core.windowLibrary.windowObjects.advancedObjects.scrollList.WindowScrollList;
import com.Whodundid.core.windowLibrary.windowObjects.basicObjects.WindowLabel;
import com.Whodundid.core.windowLibrary.windowObjects.basicObjects.WindowRect;
import com.Whodundid.core.windowLibrary.windowTypes.ActionObject;
import com.Whodundid.core.windowLibrary.windowTypes.WindowParent;
import com.Whodundid.core.windowLibrary.windowTypes.interfaces.IActionObject;
import com.Whodundid.playerInfo.PlayerInfoApp;
import com.Whodundid.playerInfo.util.PIResources;
import java.io.File;
import net.minecraft.util.MathHelper;

public class PlayerInfoSettings extends WindowParent {
	
	PlayerInfoApp app = (PlayerInfoApp) RegisteredApps.getApp(AppType.PLAYERINFO);
	WindowScrollList list;
	WindowButton animateSkins, drawCapes, drawNames, randomBackgrounds;
	WindowButton searchBtn;
	WindowButton skinsDir;
	WindowTextField searchField;
	
	File skinDir = new File(RegisteredApps.getAppConfigBaseFileLocation(AppType.PLAYERINFO).getAbsolutePath() + "/Player Skins");
	
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
		setMaximizable(true);
	}
	
	@Override
	public void initObjects() {
		defaultHeader(this);
		
		list = new WindowScrollList(this, startX + 2, startY + 20, width - 4, height - 22);
		list.setBackgroundColor(0xff303030);
		
		int searchY = addSearch(8);
		int skinViewerY = addSkinViewer(searchY);
		int skinDirY = addSkinDir(skinViewerY);
		
		list.fitItemsInList(5, 10);
		
		addObject(list);
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
	public void actionPerformed(IActionObject object, Object... args) {
		if (object == animateSkins) { animateSkins.toggleTrueFalse(PlayerInfoApp.animateSkins, app, false); }
		if (object == drawCapes) { drawCapes.toggleTrueFalse(PlayerInfoApp.drawCapes, app, false); }
		if (object == drawNames) { drawNames.toggleTrueFalse(PlayerInfoApp.drawNames, app, false); }
		if (object == randomBackgrounds) { randomBackgrounds.toggleTrueFalse(PlayerInfoApp.randomBackgrounds, app, false); }
		if (object == searchField || object == searchBtn) { openInfoWindow(searchField.getText()); }
		if (object == skinsDir) { EUtil.openFile(skinDir); }
	}
	
	@Override
	public PlayerInfoSettings resize(int xIn, int yIn, ScreenLocation areaIn) {
		if (getMaximizedPosition() != ScreenLocation.center && (xIn != 0 || yIn != 0)) {
			int vPos = list.getVScrollBar().getScrollPos();
			int hPos = list.getHScrollBar().getScrollPos();
			
			super.resize(xIn, yIn, areaIn);
			
			list.getVScrollBar().onResizeUpdate(vPos, xIn, yIn, areaIn);
			list.getHScrollBar().onResizeUpdate(hPos, xIn, yIn, areaIn);
			
			setPreMax(getDimensions());
		}
		return this;
	}
	
	private int addSearch(int yPos) {
		WindowLabel searchLabel = new WindowLabel(list, 6, yPos, "Search Player", EColors.orange.intVal);
		
		searchField = new WindowTextField(list, 6, searchLabel.endY + 8, 130, 18) {
			@Override
			public void keyPressed(char typedChar, int keyCode) {
				super.keyPressed(typedChar, keyCode);
				
				if (searchBtn != null && app.isEnabled()) { searchBtn.setEnabled(getText().length() >= 3); }
			}
		};
		if (app.isEnabled()) {
			searchField.setTextWhenEmptyColor(EColors.dgray.intVal);
			searchField.setTextWhenEmpty("player name");
		}
		else {
			searchField.setTextWhenEmptyColor(EColors.lred.intVal);
			searchField.setTextWhenEmpty("Player Info disabled!");
		}
		searchField.setClickable(app.isEnabled());
		searchField.setActionReceiver(this);
		
		searchBtn = new WindowButton(list, searchField.endX + 10, searchField.startY - 1, 60, 20, "Search");
		searchBtn.setEnabled(false);
		
		WindowRect divider = new WindowRect(list, 0, searchField.endY + 8, list.getDimensions().endX, searchField.endY + 9, EColors.black);
		divider.setClickable(false);
		
		list.addToIgnoreList(divider);
		
		list.addObjectToList(searchLabel, searchField, searchBtn, divider);
		
		return (searchField.endY + 16) - list.getDimensions().startY;
	}
	
	private int addSkinViewer(int yPos) {
		WindowLabel skinViewerLabel = new WindowLabel(list, 6, yPos, "Skin Viewer", EColors.orange.intVal);
		
		animateSkins = new WindowButton(list, 6, skinViewerLabel.endY + 8, 60, 20, app.animateSkins);
		WindowLabel animateSkinsLabel = new WindowLabel(list, animateSkins.endX + 10, animateSkins.startY + 6, "Animate Player Skins");
		
		drawCapes = new WindowButton(list, 6, animateSkins.endY + 8, 60, 20, app.drawCapes);
		WindowLabel drawCapesLabel = new WindowLabel(list, drawCapes.endX + 10, drawCapes.startY + 6, "Draw Player Capes");
		
		drawNames = new WindowButton(list, 6, drawCapes.endY + 8, 60, 20, app.drawNames);
		WindowLabel drawNamesLabel = new WindowLabel(list, drawNames.endX + 10, drawNames.startY + 6, "Draw Player Names");
		
		randomBackgrounds = new WindowButton(list, 6, drawNames.endY + 8, 60, 20, app.randomBackgrounds);
		WindowLabel randomBackgroundsLabel = new WindowLabel(list, randomBackgrounds.endX + 10, randomBackgrounds.startY + 6, "Draw Random Backgrounds");
		
		ActionObject.setActionReceiver(this, animateSkins, drawCapes, drawNames, randomBackgrounds);
		WindowLabel.setColor(EColors.lgray, animateSkinsLabel, drawCapesLabel, drawNamesLabel, randomBackgroundsLabel);
		
		animateSkinsLabel.setHoverText("Players will perform a walking animation");
		drawCapesLabel.setHoverText("Players who have a cape will have their cape drawn");
		drawNamesLabel.setHoverText("Players will have their name drawn above them");
		randomBackgroundsLabel.setHoverText("Will draw a random Minecraft themed background behind the player being viewed");
		
		list.addObjectToList(skinViewerLabel);
		list.addObjectToList(animateSkins, animateSkinsLabel);
		list.addObjectToList(drawCapes, drawCapesLabel);
		list.addObjectToList(drawNames, drawNamesLabel);
		list.addObjectToList(randomBackgrounds, randomBackgroundsLabel);
		
		return randomBackgrounds.endY - list.getDimensions().startY;
	}
	
	private int addSkinDir(int yPos) {
		EDimension ld = list.getListDimensions();
		int w = MathHelper.clamp_int(ld.width - (ld.width / 3), 100, 200);
		
		WindowRect divider = new WindowRect(list, 0, yPos + 8, list.getDimensions().endX, yPos + 9, EColors.black);
		WindowRect back = new WindowRect(list, 0, yPos + 9, list.getDimensions().endX, list.getDimensions().endY, EColors.steel);
		skinsDir = new WindowButton(list, ld.midX - (w / 2), divider.endY + 12, w, 20, "Open Skin Folder");
		
		skinsDir.setStringColor(EColors.seafoam);
		skinsDir.setEnabled(skinDir.exists());
		skinsDir.setHoverText(skinDir.exists() ? "Opens the folder where player skins are downloaded to." : "The player skins folder has not yet been made. Download a skin first!");
		
		skinsDir.setActionReceiver(this);
		
		divider.setClickable(false);
		back.setClickable(false);
		
		list.addToIgnoreList(divider, back);
		
		list.addObjectToList(divider, back, skinsDir);
		
		return skinsDir.endY - list.getDimensions().startY;
	}
	
	private void openInfoWindow(String name) {
		EnhancedMC.displayWindow(new PlayerInfoWindow(name), this, CenterType.object);
	}

}