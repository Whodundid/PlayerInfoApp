package com.Whodundid.playerInfo.gui;

import com.Whodundid.core.EnhancedMC;
import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.enhancedGui.guiObjects.actionObjects.EGuiButton;
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
import com.Whodundid.core.util.storageUtil.EDimension;
import com.Whodundid.playerInfo.PlayerInfoApp;
import com.Whodundid.playerInfo.util.PIResources;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class SkinDisplayWindow extends WindowParent {

	String uuid;
	String playerName;
	DynamicTextureHandler skin;
	DynamicTextureHandler cape;
	boolean isAlex = false;
	boolean hasCape = false;
	boolean texture = false;
	DummyPlayer playerEntity;
	
	EGuiPlayerViewer skinViewer;
	EGuiButton closeBtn, textureBtn, downloadBtn;
	
	//-----------------------------
	//SkinDisplayWindow Constructor
	//-----------------------------
	
	public SkinDisplayWindow(String uuidIn, String playerNameIn) {
		uuid = uuidIn;
		playerName = playerNameIn;
		windowIcon = PIResources.viewerIcon;
	}
	
	//----------------
	//Object Overrides
	//----------------
	
	@Override
	public void finalize() throws Throwable {
		if (skin != null) { skin.getDynamicTexture().deleteGlTexture(); }
		if (cape != null) { cape.getDynamicTexture().deleteGlTexture(); }
		
		super.finalize();
	}
	
	//----------------------
	//WindowParent Overrides
	//----------------------
	
	@Override public boolean showInLists() { return false; }
	
	//---------------------------
	//EnhancedGuiObject Overrides
	//---------------------------
	
	@Override
	public void initGui() {
		defaultPos();
		setObjectName(playerName + "'s Skin");
		
		super.initGui();
	}
	
	@Override
	public void initObjects() {
		defaultHeader(this);
		
		skinViewer = new EGuiPlayerViewer(this, startX + 10, startY + 10, width - 20, width - 20, playerEntity);
		if (PlayerInfoApp.randomBackgrounds.get()) {
			skinViewer.setBackground(getRandomBackground());
			skinViewer.setDrawBackground(true);
		}
		
		closeBtn = new EGuiButton(this, endX - 70, endY - 30, 60, 20, "Close");
		downloadBtn = new EGuiButton(this, startX + 10, endY - 30, 60, 20, "Download").setDisplayStringColor(EColors.seafoam);
		textureBtn = new EGuiButton(this, midX - 30, endY - 30, 60, 20, texture ? "3D Model" : "Texture").setDisplayStringColor(EColors.yellow);
		
		addObject(skinViewer, closeBtn, downloadBtn, textureBtn);
	}
	
	@Override
	public void drawObject(int mXIn, int mYIn) {
		drawDefaultBackground();
		
		if (texture) {
			drawRect(startX + 10, startY + 10, endX - 10, endY - 45, EColors.black);
			drawRect(startX + 11, startY + 11, endX - 11, endY - 46, 0xff878787);
			
			bindTexture(skin.getTextureLocation());
			GlStateManager.color(2.0f, 2.0f, 2.0f, 2.0f);
			drawTexture(startX + 11, startY + 11, width - 22, height - 57);
		}
		
		super.drawObject(mXIn, mYIn);
	}
	
	@Override
	public void actionPerformed(IEnhancedActionObject object, Object... args) {
		if (object == closeBtn) { close(); }
		if (object == downloadBtn) { download(); }
		
		if (object == textureBtn) {
			texture = !texture;
			skinViewer.setVisible(!texture);
			textureBtn.setDisplayString(texture ? "3D Model" : "Texture");
		}
	}
	
	@Override
	public void close() {
		super.close();
		
		if (skin != null) { skin.getDynamicTexture().deleteGlTexture(); }
		if (cape != null) { cape.getDynamicTexture().deleteGlTexture(); }
		
		playerEntity.destroy();
	}
	
	//-------------------------
	//SkinDisplayWindow Methods
	//-------------------------
	
	public void setSkin(BufferedImage skinIn, BufferedImage capeIn, boolean isAlexSkin, boolean hasCapeIn) {
		
		//set skin
		if (skinIn != null) { skin = new DynamicTextureHandler(mc.renderEngine, skinIn); }
		else { skin = new DynamicTextureHandler(mc.renderEngine, new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)); }
		
		//set cape
		if (capeIn != null) { cape = new DynamicTextureHandler(mc.renderEngine, capeIn); }
		else { cape = new DynamicTextureHandler(mc.renderEngine, new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB)); }
		
		isAlex = isAlexSkin;
		hasCape = hasCapeIn;
		
		playerEntity = new DummyPlayer(skin.getTextureLocation(), cape.getTextureLocation(), isAlex, uuid, playerName, PlayerInfoApp.animateSkins.get());
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
	
	//----------------------------------
	//SkinDisplayWindow Internal Methods
	//----------------------------------
	
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
					//EChatUtil.show(EnumChatFormatting.GREEN + "PlayerInfoApp: Successfully created 'Player Skins' directory at");
					//EChatUtil.show("" + skinDir);
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
	
	//-------------------------------
	//SkinDisplayWindow DialogueBoxes
	//-------------------------------
	
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
	
}
