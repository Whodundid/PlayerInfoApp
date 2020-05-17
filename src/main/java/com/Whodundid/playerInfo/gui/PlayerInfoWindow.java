package com.Whodundid.playerInfo.gui;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.Whodundid.core.EnhancedMC;
import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.enhancedGui.guiObjects.actionObjects.EGuiButton;
import com.Whodundid.core.enhancedGui.guiObjects.actionObjects.EGuiTextField;
import com.Whodundid.core.enhancedGui.guiObjects.advancedObjects.textArea.EGuiTextArea;
import com.Whodundid.core.enhancedGui.guiObjects.advancedObjects.textArea.TextAreaLine;
import com.Whodundid.core.enhancedGui.guiObjects.utilityObjects.EGuiPlayerViewer;
import com.Whodundid.core.enhancedGui.guiObjects.windows.EGuiDialogueBox;
import com.Whodundid.core.enhancedGui.guiObjects.windows.EGuiDialogueBox.DialogueBoxTypes;
import com.Whodundid.core.enhancedGui.types.WindowParent;
import com.Whodundid.core.enhancedGui.types.interfaces.IEnhancedActionObject;
import com.Whodundid.core.util.EUtil;
import com.Whodundid.core.util.chatUtil.EChatUtil;
import com.Whodundid.core.util.mathUtil.NumberUtil;
import com.Whodundid.core.util.playerUtil.DummyPlayer;
import com.Whodundid.core.util.renderUtil.CenterType;
import com.Whodundid.core.util.renderUtil.EColors;
import com.Whodundid.core.util.resourceUtil.EResource;
import com.Whodundid.core.util.storageUtil.DynamicTextureHandler;
import com.Whodundid.core.util.storageUtil.EArrayList;
import com.Whodundid.core.util.storageUtil.EDimension;
import com.Whodundid.playerInfo.PlayerInfoApp;
import com.Whodundid.playerInfo.util.PIResources;
import com.Whodundid.playerInfo.util.SkinContainer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class PlayerInfoWindow extends WindowParent {
	
	private PlayerInfoApp app = (PlayerInfoApp) RegisteredApps.getApp(AppType.PLAYERINFO);
	
	private String inputName;
	private EGuiTextArea nameHistory;
	private EGuiPlayerViewer playerViewer;
	private EGuiButton closeBtn, textureBtn, downloadBtn;
	private boolean skinResponse = false, namesResponse = false;
	private long checkTimeOut = 4000l;
	private long startTime = 0l;
	private boolean timedOut = false;
	private boolean received = false;
	
	private String uuid = null;
	private String playerName = null;
	private DynamicTextureHandler skin;
	private DynamicTextureHandler cape;
	private boolean isAlex = false;
	private boolean hasCape = false;
	private boolean texture = false;
	private DummyPlayer playerEntity;
	
	public PlayerInfoWindow(String nameIn) {
		super();
		windowIcon = PIResources.viewerIcon;
		inputName = nameIn;
	}
	
	@Override
	public void initGui() {
		setDimensions(495, defaultHeight);
		setObjectName("Loading...");
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void initObjects() {
		defaultHeader(this);
		
		nameHistory = new EGuiTextArea(this, startX + 10, startY + 25, width - 30 - 201, height - 54);
		
		closeBtn = new EGuiButton(this, nameHistory.midX - 40, endY - 25, 80, 20, "Close");
		downloadBtn = new EGuiButton(this, nameHistory.endX + ((endX - nameHistory.endX) / 2) - 65, endY - 25, 60, 20, "Download").setDisplayStringColor(EColors.seafoam);
		textureBtn = new EGuiButton(this, nameHistory.endX + ((endX - nameHistory.endX) / 2) + 10, endY - 25, 60, 20, texture ? "3D Model" : "Texture").setDisplayStringColor(EColors.yellow);
		
		closeBtn.setVisible(false);
		nameHistory.setVisible(false);
		downloadBtn.setVisible(false);
		textureBtn.setVisible(false);
		
		app.fetchNameHistory(this, null, inputName, false);
		app.fetchSkin(this, null, inputName);
		
		addObject(closeBtn, downloadBtn, textureBtn, nameHistory);
	}
	
	@Override
	public void drawObject(int mXIn, int mYIn) {
		drawDefaultBackground();
		
		if (!received) {
			if (!timedOut) {
				drawStringCS("Loading player info...", midX, midY - 6, EColors.orange);
				
				if (skinResponse && namesResponse) {
					received = true;
					
					nameHistory.setVisible(true);
					playerViewer.setVisible(true);
					
					closeBtn.setVisible(true);
					downloadBtn.setVisible(true);
					textureBtn.setVisible(true);
					
					setObjectName(playerName + "'s Information");
					getHeader().setTitle(getObjectName());
				}
				else if (System.currentTimeMillis() - startTime >= checkTimeOut) {
					timedOut = true;
					
					closeBtn.setVisible(true);
					nameHistory.setVisible(true);
					
					if (!namesResponse) {
						nameHistory.addTextLine("Could not retrieve names data!", EColors.lred.intVal);
					}
					
					if (skinResponse && playerViewer != null) {
						playerViewer.setVisible(skinResponse);
						downloadBtn.setVisible(true);
						textureBtn.setVisible(true);
					}
					
					setObjectName(playerName != null ? playerName + "'s Information" : "Failed to Load Info!");
					getHeader().setTitle(getObjectName());
				}
			}
			else {
				//draw names header
				drawRect(nameHistory.startX, nameHistory.startY - 16, nameHistory.endX, nameHistory.startY, EColors.black);
				drawRect(nameHistory.startX + 1, nameHistory.startY - 15, nameHistory.endX - 1, nameHistory.startY, EColors.vdgray);
				drawStringCS("Player Information", nameHistory.midX, nameHistory.startY - 11, EColors.orange);
				
				if (skinResponse && playerViewer != null) {
					
					//draw skin header
					drawRect(playerViewer.startX, playerViewer.startY - 16, playerViewer.endX, playerViewer.startY, EColors.black);
					drawRect(playerViewer.startX + 1, playerViewer.startY - 15, playerViewer.endX - 1, playerViewer.startY, EColors.vdgray);
					drawStringCS("Skin", playerViewer.midX, playerViewer.startY - 11, EColors.orange);
					
					if (texture) {
						drawRect(playerViewer.startX, playerViewer.startY, playerViewer.endX, playerViewer.endY, EColors.black);
						drawRect(playerViewer.startX + 1, playerViewer.startY + 1, playerViewer.endX - 1, playerViewer.endY, 0xff878787);
						
						bindTexture(skin.getTextureLocation());
						GlStateManager.color(2.0f, 2.0f, 2.0f, 2.0f);
						drawTexture(playerViewer.startX + 1, playerViewer.startY + 1, playerViewer.width - 2, playerViewer.height - 2);
					}
				}
				
				if (!skinResponse) {
					drawRect(nameHistory.endX + 10, startY + 9, endX - 10, startY + 5 + (endY - 39 - startY + 5), EColors.black);
					drawRect(nameHistory.endX + 11, startY + 10, endX - 11, startY + 5 + (endY - 40 - startY + 5), EColors.vdgray);
					drawStringCS("Could not retrieve skin data!", endX - ((endX - nameHistory.endX) / 2), startY + 5 + ((endY - 39 - startY + 5) / 2) - 6, EColors.lred);
				}
				
			}
		}
		else {
			
			//draw names header
			drawRect(nameHistory.startX, nameHistory.startY - 16, nameHistory.endX, nameHistory.startY, EColors.black);
			drawRect(nameHistory.startX + 1, nameHistory.startY - 15, nameHistory.endX - 1, nameHistory.startY, EColors.vdgray);
			drawStringCS("Player Information", nameHistory.midX, nameHistory.startY - 11, EColors.orange);
			
			//draw skin header
			drawRect(playerViewer.startX, playerViewer.startY - 16, playerViewer.endX, playerViewer.startY, EColors.black);
			drawRect(playerViewer.startX + 1, playerViewer.startY - 15, playerViewer.endX - 1, playerViewer.startY, EColors.vdgray);
			drawStringCS("Skin Viewer", playerViewer.midX, playerViewer.startY - 11, EColors.orange);
			
			if (texture) {
				drawRect(playerViewer.startX, playerViewer.startY, playerViewer.endX, playerViewer.endY, EColors.black);
				drawRect(playerViewer.startX + 1, playerViewer.startY + 1, playerViewer.endX - 1, playerViewer.endY - 1, 0xff878787);
				
				bindTexture(skin.getTextureLocation());
				GlStateManager.color(2.0f, 2.0f, 2.0f, 2.0f);
				drawTexture(playerViewer.startX + 1, playerViewer.startY + 1, playerViewer.width - 2, playerViewer.height - 2);
			}
		}
		
		super.drawObject(mXIn, mYIn);
	}
	
	@Override
	public void actionPerformed(IEnhancedActionObject object, Object... args) {
		if (object == closeBtn) { close(); }
		if (object == downloadBtn) { download(); }
		
		if (object == textureBtn) {
			texture = !texture;
			playerViewer.setVisible(!texture);
			textureBtn.setDisplayString(texture ? "3D Model" : "Texture");
		}
	}
	
	@Override
	public void close() {
		if (playerEntity != null) { playerEntity.destroy(); }
		super.close();
	}
	
	public void onNamesResponse(String playerNameIn, String uuidIn, EArrayList<String> namesIn) {
		if (playerName == null) { playerName = playerNameIn; }
		if (uuid == null) { uuid = uuidIn; }
		
		if (uuid != null) {
			nameHistory.addTextLine("UUID: " + EnumChatFormatting.AQUA + uuid, EColors.yellow.intVal);
			nameHistory.addTextLine();
		}
		
		for (String l : namesIn) {
			nameHistory.addTextLine(l);
		}
		
		namesResponse = true;
	}
	
	public void onSkinResponse(SkinContainer containerIn) {
		if (playerName == null) { playerName = containerIn.getName(); }
		if (uuid == null) { uuid = containerIn.getUUID(); }
		
		setSkin(containerIn);
		
		playerViewer = new EGuiPlayerViewer(this, nameHistory.endX + 10, startY + 25, endX - 20 - nameHistory.endX, height - 54, playerEntity);
		if (PlayerInfoApp.randomBackgrounds.get()) {
			playerViewer.setBackground(getRandomBackground());
			playerViewer.setDrawBackground(true);
		}
		playerViewer.setVisible(false);
		
		addObject(playerViewer);
		
		skinResponse = true;
	}
	
	public void setSkin(SkinContainer containerIn) {
		
		//set skin
		if (containerIn.getSkinImage() != null) { skin = new DynamicTextureHandler(mc.renderEngine, containerIn.getSkinImage()); }
		else { skin = new DynamicTextureHandler(mc.renderEngine, new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)); }
		
		//set cape
		if (containerIn.getCapeImage() != null) { cape = new DynamicTextureHandler(mc.renderEngine, containerIn.getCapeImage()); }
		else { cape = new DynamicTextureHandler(mc.renderEngine, new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB)); }
		
		isAlex = containerIn.isAlex();
		hasCape = containerIn.hasCape();
		
		playerEntity = new DummyPlayer(skin.getTextureLocation(), cape.getTextureLocation(), isAlex, containerIn.getUUID(), containerIn.getName(), PlayerInfoApp.animateSkins.get());
		playerEntity.setDrawSkin(PlayerInfoApp.drawCapes.get());
		playerEntity.setDrawName(PlayerInfoApp.drawNames.get());
		
		//items
		ItemStack i = null;
		
		switch (uuid) {
		case "be8ba05926444f4ca5e788a38e555b1e": i = new ItemStack(Item.getItemById(398), 1); break; //Whodundid - carrot on stick
		case "d3dcdba8d6ee42778a810f359e4def25": i = new ItemStack(Item.getItemById(344), 1); break; //JamminOtter - egg
		case "fd260ca4df524211a44f90e6ae95596a": i = new ItemStack(Item.getItemFromBlock(Blocks.sponge)); break; //King_of_ducks - sponge
		}
		
		if (i != null) {
			playerEntity.inventory.mainInventory[playerEntity.inventory.currentItem] = i;
		}
	}
	
	private void download() {
		File skinDir = new File(RegisteredApps.getAppConfigBaseFileLocation(AppType.PLAYERINFO).getAbsolutePath() + "/Player Skins");
		
		if (!skinDir.exists()) {
			try {
				if (!skinDir.mkdir()) {
					openErrorBox("PlayerInfoApp Error", "Cannot create the 'Player Skins' directory!");
					EnhancedMC.error("PlayerInfoApp Error: Cannot create the 'Player Skins' directory!");
					return;
				}
				else {
					EnhancedMC.info("PlayerInfoApp: Successfully created 'Player Skins' directory at: " + skinDir);
					//EChatUtil.show(EnumChatFormatting.GREEN + "PlayerInfoApp: Successfully created 'Player Skins' directory");
					//EChatUtil.show("" + skinDir.getAbsolutePath());
				}
			}
			catch (Exception e) {
				openErrorBox("PlayerInfoApp Error", "Error thrown when attempting to create the 'Player Skins' directory!\n" + e.toString());
				EnhancedMC.error("PlayerInfoApp Error: Error thrown when attempting to create the 'Player Skins' directory!");
				e.printStackTrace();
				return;
			}
		}
		
		File playerSkinDir = new File(skinDir, playerName);
		if (playerSkinDir.mkdir());
		
		File skinFile = new File(playerSkinDir, playerName + " Skin.png");
		File capeFile = new File(playerSkinDir, playerName + " Cape.png");
		
		try {
			ImageIO.write(skin.GBI(), "png", skinFile);
			if (hasCape) { ImageIO.write(cape.GBI(), "png", capeFile); }
			
			openSuccessBox("Download Success", playerName + "'s skin data successfully downloaded!", playerSkinDir);
		}
		catch (Exception e) { e.printStackTrace(); }
		
	}
	
	//------------------------------
	//PlayerInfoWindow DialogueBoxes
	//------------------------------
	
	private void openErrorBox(String title, String message) {
		EGuiDialogueBox fail = new EGuiDialogueBox(DialogueBoxTypes.ok);
		fail.setTitle(title);
		fail.setTitleColor(EColors.lgray.intVal);
		fail.setMessage(EnumChatFormatting.RED + message);
		EnhancedMC.displayWindow(fail, this, true, false, false, CenterType.screen);
	}
	
	private void openSuccessBox(String title, String message, File path) {
		EGuiDialogueBox success = new EGuiDialogueBox(DialogueBoxTypes.custom) {
			EGuiButton openFolder = null, close = null;
			
			@Override
			public void initObjects() {
				defaultHeader(this);
				EDimension bdims = this.getDimensions();
				
				openFolder = new EGuiButton(this, bdims.midX - 90, bdims.endY - 30, 80, 20, "Open Folder");
				close = new EGuiButton(this, bdims.midX + 10, bdims.endY - 30, 80, 20, "Close");
				
				addObject(openFolder, close);
			}
			
			@Override
			public void actionPerformed(IEnhancedActionObject object, Object... args) {
				if (object == openFolder) {
					EUtil.openFile(path);
				}
				if (object == close) {
					close();
				}
			}
			
		};
		
		success.setTitle(title);
		success.setTitleColor(EColors.lgray.intVal);
		success.setMessage(message);
		success.setMessageColor(EColors.green.intVal);
		EnhancedMC.displayWindow(success, this, true, false, false, CenterType.screen);
	}
	
	private EResource getRandomBackground() {
		int num = NumberUtil.getRoll(0, 11);
		
		switch (num) {
		case 0: return PIResources.viewerBackground0;
		case 1: return PIResources.viewerBackground1;
		case 2: return PIResources.viewerBackground2;
		case 3: return PIResources.viewerBackground3;
		case 4: return PIResources.viewerBackground4;
		case 5: return PIResources.viewerBackground5;
		case 6: return PIResources.viewerBackground6;
		case 7: return PIResources.viewerBackground7;
		case 8: return PIResources.viewerBackground8;
		case 9: return PIResources.viewerBackground9;
		case 10: return PIResources.viewerBackground10;
		case 11: return PIResources.viewerBackground11;
		}
		
		return null;
	}

	public EGuiTextArea getTextArea() { return nameHistory; }
	public EGuiPlayerViewer getViewer() { return playerViewer; }
	
}
