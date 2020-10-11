package fr.tyvaliarp.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api.account.AccountType;
import fr.trxyy.alternative.alternative_api.auth.GameAuth;
import fr.trxyy.alternative.alternative_api.updater.GameUpdater;
import fr.trxyy.alternative.alternative_api.utils.FontLoader;
import fr.trxyy.alternative.alternative_api.utils.Mover;
import fr.trxyy.alternative.alternative_api_ui.base.IScreen;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherButton;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherImage;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherLabel;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherPasswordField;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherRectangle;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import javax.sound.sampled.*;

public class LauncherPanel extends IScreen {
	
	private LauncherRectangle topRectangle;
	
	private LauncherLabel titleLabel;
	private LauncherImage titleImage;
	
	private LauncherTextField usernameField;
	private LauncherPasswordField passwordField;
	
	private LauncherButton loginButton;
	private LauncherButton settingsButton;
	private LauncherButton websiteButton;
	private LauncherButton musicButton;

	private Slider musicSlider;
	
	private LauncherButton closeButton;
	private LauncherButton reduceButton;
	
	private Timeline timeline;
	private DecimalFormat decimalFormat = new DecimalFormat(".#");
	private Thread updateThread;
	private GameUpdater gameUpdater = new GameUpdater();
	private LauncherRectangle updateRectangle;
	private LauncherLabel updateLabel;
	private LauncherLabel currentFileLabel;
	private LauncherLabel percentageLabel;
	private LauncherLabel currentStep;

	private Media music;
	private MediaPlayer musicPlayer;
	
	public LauncherPanel(Pane root, GameEngine engine) {
		music = new Media(LauncherPanel.class.getResource("/music.wav").toString());
		musicPlayer = new MediaPlayer(music);
		musicPlayer.setVolume(0.5);
		musicPlayer.play();

		this.topRectangle = new LauncherRectangle(root, 0, 0, engine.getWidth(), 31);
		this.topRectangle.setFill(Color.rgb(0, 0, 0, 0.70));
		
		this.drawLogo(engine, getResourceLocation().loadImage(engine, "logo.png"), engine.getWidth() / 2 - 165, 100, 330, 100, root, Mover.DONT_MOVE);
		
		this.titleLabel = new LauncherLabel(root);
		this.titleLabel.setText("Tyvalia RP Launcher - 0.5.0d");
		this.titleLabel.setFont(FontLoader.loadFont("Roboto-Light.tff", "Robota Light", 18f));
		this.titleLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.titleLabel.setPosition(engine.getWidth() / 2 - 120, -4);
		this.titleLabel.setOpacity(0.7);
		this.titleLabel.setSize(500, 40);
		
		this.titleImage = new LauncherImage(root);
		this.titleImage.setImage(getResourceLocation().loadImage(engine, "favicon.png"));
		this.titleImage.setSize(25, 25);
		this.titleImage.setPosition(engine.getWidth() / 3, 3);
		
		this.usernameField = new LauncherTextField(root);
		this.usernameField.setPosition(engine.getWidth() / 2 - 135, engine.getHeight() / 2 - 57);
		this.usernameField.setSize(270, 50);
		this.usernameField.setFont(FontLoader.loadFont("Roboto-Light.tff", "Robota Light", 14f));
		this.usernameField.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		this.usernameField.setVoidText("Pseudo / Email");
		
		this.passwordField = new LauncherPasswordField(root);
		this.passwordField.setPosition(engine.getWidth() / 2 - 135, engine.getHeight() / 2);
		this.passwordField.setSize(270, 50);
		this.passwordField.setFont(FontLoader.loadFont("Roboto-Light.tff", "Robota Light", 14f));
		this.passwordField.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		this.passwordField.setVoidText("Mot de passe");
		
		this.loginButton = new LauncherButton(root);
		this.loginButton.setText("Se Connecter");
		this.loginButton.setFont(FontLoader.loadFont("Roboto-Light.tff", "Robota Light", 22f));
		this.loginButton.setPosition(engine.getWidth() / 2 - 135, engine.getHeight() / 2 + 60);
		this.loginButton.setSize(270, 45);
		this.loginButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		this.loginButton.setAction(event -> {
			if (this.usernameField.getText().length() < 3 || this.usernameField.getText().length() > 16) {
				new LauncherAlert("Connexion échouée", "La case du pseudo / email doit entre 3 et 16 charactères.");
			} else if (!NicknameUtils.check(this.usernameField.getText()) && this.passwordField.getText().isEmpty()) {
				new LauncherAlert("Connexion échouée", "Le pseudo contient des caractères spéciaux.");
			} else if (this.usernameField.getText().length() >= 3 && this.passwordField.getText().isEmpty()) {
				GameAuth auth = new GameAuth(this.usernameField.getText(), this.passwordField.getText(), AccountType.OFFLINE);
				if (auth.isLogged()) {
					musicPlayer.stop();
					musicButton.setVisible(false);
					musicSlider.setVisible(false);
					this.update(engine, auth);
				}
				//new LauncherAlert("Connexion échouée", "Les Cracks ne sont pas acceptés !");
			} else if (this.usernameField.getText().length() >= 3 && !this.passwordField.getText().isEmpty()) {
				GameAuth auth = new GameAuth(this.usernameField.getText(), this.passwordField.getText(), AccountType.MOJANG);
				if (auth.isLogged()) {
					musicPlayer.stop();
					musicButton.setVisible(false);
					musicSlider.setVisible(false);
					this.update(engine, auth);
				} else {
					new LauncherAlert("Connexion échouée", "Identifiants incorrects");
				}
			} else {
				new LauncherAlert("Connexion échouée", "La connexion à échouée, contactez un Administateur");
			}
		});

		/*
		this.settingsButton = new LauncherButton(root);
		this.settingsButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		LauncherImage settingsImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "settings.png"));
		settingsImg.setSize(40, 40);
		this.settingsButton.setGraphic(settingsImg);
		this.settingsButton.setPosition(10, engine.getHeight() - 55);
		this.settingsButton.setSize(60, 45);
		this.settingsButton.setAction(event -> {
			new LauncherAlert("Action Impossible", "Cette fonction n'est pas encore implémentée");
		});
		 */

		this.websiteButton = new LauncherButton(root);
		this.websiteButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		LauncherImage websiteImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "website.png"));
		websiteImg.setSize(40, 40);
		this.websiteButton.setGraphic(websiteImg);
		this.websiteButton.setPosition(10, engine.getHeight() - 55);
		this.websiteButton.setSize(60, 45);
		this.websiteButton.setAction(event -> {
			try {
				java.awt.Desktop.getDesktop().browse(new URI("https://tyvalia-rp.fr.nf/?from=launcher"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		});

		this.musicButton = new LauncherButton(root);
		this.musicButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		LauncherImage musicImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "music-playing.png"));
		musicImg.setSize(40, 40);
		this.musicButton.setGraphic(musicImg);
		this.musicButton.setPosition(engine.getWidth() - 70, engine.getHeight() - 55);
		this.musicButton.setSize(60, 45);
		this.musicButton.setAction(event -> {
			if (musicPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
				musicPlayer.stop();
				LauncherImage tempImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "music-muted.png"));
				tempImg.setSize(40, 40);
				musicButton.setGraphic(tempImg);
			} else {
				musicPlayer.play();
				LauncherImage tempImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "music-playing.png"));
				tempImg.setSize(40, 40);
				musicButton.setGraphic(tempImg);
			}
		});

		this.musicSlider = new Slider();
		this.musicSlider.setMin(0);
		this.musicSlider.setMax(100);
		this.musicSlider.setValue(50);
		/*
		this.musicSlider.setShowTickLabels(true);
		this.musicSlider.setShowTickMarks(true);
		 */
		this.musicSlider.setBlockIncrement(10);
		this.musicSlider.setVisible(true);
		this.musicSlider.setLayoutX(engine.getWidth() - 220);
		this.musicSlider.setLayoutY(engine.getHeight() - 40);

		this.musicSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				musicPlayer.setVolume(newValue.doubleValue() / 100);
			}
		});

		root.getChildren().add(musicSlider);
		
		this.closeButton = new LauncherButton(root);
		this.closeButton.setPosition(engine.getWidth() - 35, 2);
		this.closeButton.setInvisible();
		this.closeButton.setSize(15, 15);
		this.closeButton.setBackground(null);
		LauncherImage closeImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "close.png"));
		closeImg.setSize(15, 15);
		this.closeButton.setGraphic(closeImg);
		this.closeButton.setOnAction(event -> {
			System.exit(0);
		});
		
		this.reduceButton = new LauncherButton(root);
		this.reduceButton.setPosition(engine.getWidth() - 55, 2);
		this.reduceButton.setInvisible();
		this.reduceButton.setSize(15, 15);
		this.reduceButton.setBackground(null);
		LauncherImage reduceImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "minimize.png"));
		reduceImg.setSize(15, 15);
		this.reduceButton.setGraphic(reduceImg);
		this.reduceButton.setOnAction(event -> {
			Stage stage = (Stage) ((LauncherButton) event.getSource()).getScene().getWindow();
			stage.setIconified(true);
		});
		
		this.updateRectangle = new LauncherRectangle(root, engine.getWidth() / 2 - 175, engine.getHeight() / 2 - 60, 350, 180);
		this.updateRectangle.setArcWidth(10.0);
		this.updateRectangle.setArcHeight(10.0);
		this.updateRectangle.setFill(Color.rgb(0, 0, 0, 0.60));
		this.updateRectangle.setVisible(false);
		
		this.updateLabel = new LauncherLabel(root);
		this.updateLabel.setText("Mise à jour...");
		this.updateLabel.setAlignment(Pos.CENTER);
		this.updateLabel.setFont(FontLoader.loadFont("Roboto-Light.tff", "Roboto Light",22f));
		this.updateLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.updateLabel.setPosition(engine.getWidth() / 2 - 95, engine.getHeight() / 2 - 55);
		this.updateLabel.setSize(190, 40);
		this.updateLabel.setVisible(false);
		
		this.currentStep = new LauncherLabel(root);
		this.currentStep.setText("Préparation de la mise à jour...");
		this.currentStep.setFont(Font.font("Verdana", FontPosture.ITALIC, 18f));
		this.currentStep.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.currentStep.setAlignment(Pos.CENTER);
		this.currentStep.setPosition(engine.getWidth() / 2 - 160, engine.getHeight() / 2 + 83);
		this.currentStep.setOpacity(0.4);
		this.currentStep.setSize(320, 40);
		this.currentStep.setVisible(false);
		
		this.currentFileLabel = new LauncherLabel(root);
		this.currentFileLabel.setText("");
		this.currentFileLabel.setAlignment(Pos.CENTER);
		this.currentFileLabel.setFont(FontLoader.loadFont("Roboto-Light.tff", "Roboto Light",18f));
		this.currentFileLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.currentFileLabel.setPosition(engine.getWidth() / 2 - 160, engine.getHeight() / 2 + 25);
		this.currentFileLabel.setSize(320, 40);
		this.currentFileLabel.setVisible(false);
		
		this.percentageLabel = new LauncherLabel(root);
		this.percentageLabel.setText("0%");
		this.percentageLabel.setAlignment(Pos.CENTER);
		this.percentageLabel.setFont(FontLoader.loadFont("Roboto-Light.tff", "Roboto Light",30f));
		this.percentageLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.percentageLabel.setPosition(engine.getWidth() / 2 - 50, engine.getHeight() / 2 - 5);
		this.percentageLabel.setOpacity(0.8);
		this.percentageLabel.setSize(100, 40);
		this.percentageLabel.setVisible(false);
	}
	
	public void update(GameEngine engine, GameAuth auth) {
		this.usernameField.setDisable(true);
		this.usernameField.setVisible(false);
		this.passwordField.setDisable(true);
		this.passwordField.setVisible(false);
		this.loginButton.setDisable(true);
		this.loginButton.setVisible(false);
		//this.settingsButton.setDisable(true);
		//this.settingsButton.setVisible(false);
		
		this.updateRectangle.setVisible(true);
		this.updateLabel.setVisible(true);
		this.currentStep.setVisible(true);
		this.currentFileLabel.setVisible(true);
		this.percentageLabel.setVisible(true);
		
		gameUpdater.reg(engine);
		gameUpdater.reg(auth.getSession());
		engine.reg(this.gameUpdater);
		this.updateThread = new Thread() {
			public void run() {
				engine.getGameUpdater().run();
			}
		};
		this.updateThread.start();
		
		this.timeline = new Timeline(new KeyFrame[] {
			new KeyFrame(javafx.util.Duration.seconds(0.0D), e -> updateDownload(engine),
			new javafx.animation.KeyValue[0]),
			new KeyFrame (javafx.util.Duration.seconds(0.1D),
			new javafx.animation.KeyValue[0])
		});
		
		this.timeline.setCycleCount(Animation.INDEFINITE);
		this.timeline.play();
	}

	private void updateDownload(GameEngine engine) {
		if (engine.getGameUpdater().downloadedFiles > 0) {
			this.percentageLabel.setText(decimalFormat.format(engine.getGameUpdater().downloadedFiles * 100.0d / engine.getGameUpdater().filesToDownload) + "%");
		}
		this.currentFileLabel.setText(engine.getGameUpdater().getCurrentFile());
		this.currentStep.setText(engine.getGameUpdater().getCurrentInfo());
	}
}
