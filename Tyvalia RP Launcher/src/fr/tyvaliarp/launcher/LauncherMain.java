package fr.tyvaliarp.launcher;

import fr.trxyy.alternative.alternative_api.*;
import fr.trxyy.alternative.alternative_api.maintenance.GameMaintenance;
import fr.trxyy.alternative.alternative_api.maintenance.Maintenance;
import fr.trxyy.alternative.alternative_api_ui.LauncherBackground;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import fr.trxyy.alternative.alternative_api_ui.base.AlternativeBase;
import fr.trxyy.alternative.alternative_api_ui.base.LauncherBase;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
	Launcher by Charlito33
	made with Love <3
	PLEASE DON'T DELETE CREDITS
 */

public class LauncherMain extends AlternativeBase {
	private GameFolder gameFolder = new GameFolder("tyvaliaRP");
	private LauncherPreferences launcherPreferences = new LauncherPreferences("Tyvalia RP Launcher", 950, 600, true);
	private GameEngine gameEngine = new GameEngine(gameFolder, launcherPreferences, GameVersion.V_1_12_2, GameStyle.FORGE_1_8_TO_1_12_2);
	private GameLinks gameLinks = new GameLinks("http://charlito33.fr.nf/tyvalia-rp/launcher/", "1.12.2.json");
	private GameConnect gameConnect = new GameConnect("play.hypixel.net", "25565");
	private GameMaintenance gameMaintenance = new GameMaintenance(Maintenance.USE, gameEngine);
	private GameMemory gameMemory = GameMemory.RAM_4G;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(createContent());
		this.gameEngine.reg(primaryStage);
		this.gameEngine.reg(this.gameLinks);
		this.gameEngine.reg(this.gameConnect);
		this.gameEngine.reg(this.gameMaintenance);
		this.gameEngine.reg(this.gameMemory);
		LauncherBase launcherBase = new LauncherBase(primaryStage, scene, StageStyle.TRANSPARENT, gameEngine);
		launcherBase.setIconImage(primaryStage, getResourceLocation().loadImage(gameEngine, "favicon.png"));
	}
	
	private Parent createContent() {
		LauncherPane contentPane = new LauncherPane(gameEngine);
		Rectangle rectangle = new Rectangle(gameEngine.getLauncherPreferences().getWidth(), gameEngine.getLauncherPreferences().getHeight());
		rectangle.setArcWidth(15.0);
		rectangle.setArcHeight(15.0);
		contentPane.setClip(rectangle);
		new LauncherBackground(gameEngine, getResourceLocation().getMedia(gameEngine, "background.mp4"), contentPane);
		new LauncherPanel(contentPane, gameEngine);
		
		return contentPane;
	}
}
