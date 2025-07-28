package studiplayer.ui;


import javafx.scene.control.Button;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SampledFile;
import studiplayer.audio.SortCriterion;
import studiplayer.basic.BasicPlayer;

public class Player extends Application {
	
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	
	private static final String PLAYLIST_DIRECTORY = "playlists";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = " - ";
	
	private static boolean useCertPlayList = false;
	private static PlayList playList;
	private Button playButton  = null;
	private Button pauseButton  = null;
	private Button stopButton  = null;
	private Button nextButton  = null;
	private Label playListLabel = new Label(PLAYLIST_DIRECTORY);
	private Label playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
	private Label currentSongLabel = new Label(NO_CURRENT_SONG);
	private ChoiceBox<SortCriterion> sortChoiceBox = new ChoiceBox();
	private TextField searchTextField = new TextField();
	private Button filterButton = new Button("display");
	
	private TimerThread timerThread = new TimerThread();
	private PlayerThread playerThread = new PlayerThread();
	
	
	public void setUseCertPlayList(boolean useCertPlayList) {
		this.useCertPlayList = useCertPlayList;
	}
	
	public static void loadPlayList(String pathname) {
		if (pathname == null || pathname.equals("") ) {
			playList = new PlayList(DEFAULT_PLAYLIST);
		} else {
			playList = new PlayList(pathname);
		}
	}
	
	public void start(Stage stage) throws Exception{
		
		String pathname = "";
		if (!useCertPlayList) {
			final FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Open Resource File");
	        fileChooser.setInitialDirectory(new File(PLAYLIST_DIRECTORY));
	        pathname = fileChooser.showOpenDialog(stage).getAbsolutePath();
		}
		loadPlayList(pathname);
		
		stage.setTitle("APA Player");
		BorderPane mainPane = new BorderPane();
		
		
		// Filer and search are of the application
		GridPane gridPaneTop = new GridPane();
		gridPaneTop.setHgap(10);
		
		sortChoiceBox.getItems().addAll(
				SortCriterion.DEFAULT,
				SortCriterion.AUTHOR,
				SortCriterion.ALBUM, 
				SortCriterion.TITLE,
				SortCriterion.DURATION
				);
		
		sortChoiceBox.setValue(SortCriterion.DEFAULT);
		
		
		gridPaneTop.add(new Label("Search text"), 0, 0);
		gridPaneTop.add(searchTextField, 1, 0);
		gridPaneTop.add(new Label("Sort by"), 0, 1);
		gridPaneTop.add(sortChoiceBox, 1, 1);
		gridPaneTop.add(filterButton, 2, 1);
		
		TitledPane titledPane = new TitledPane("Filter", gridPaneTop);
		
		mainPane.setTop(titledPane);
		
		// Displaying the playlist
		TableView<Song> table = new SongTable(playList);
		
		table.setOnMouseClicked(event -> {
			if (event.getClickCount() == 1) {
				try {
					terminateThreads(false);
					playList.currentAudioFile().stop();
				} catch (Exception e) {}
				playList.jumpToAudioFile(table.getSelectionModel().getSelectedItem().getAudioFile());
				
				updateSongInfo(playList.currentAudioFile());
				playButton.setDisable(true);
				pauseButton.setDisable(false);
				stopButton.setDisable(false);
				startThreads(playerThread.isAlive());
			}
		});
		
		mainPane.setCenter(table);
		
		// Bottom area
        VBox vBox = new VBox();
        GridPane gridPaneBottom = new GridPane();
        
        gridPaneBottom.setHgap(10); 
        
        gridPaneBottom.add(new Label("PlayList"), 0, 0);
		gridPaneBottom.add(playListLabel, 1, 0);
		gridPaneBottom.add(new Label("Current Song"), 0, 1);
		gridPaneBottom.add(currentSongLabel, 1, 1);
		gridPaneBottom.add(new Label("Playtime"), 0, 2);
		gridPaneBottom.add(playTimeLabel, 1, 2);
		
		
        
        HBox hBox = new HBox();
        hBox.getChildren().addAll(createPlayButton("play.jpg"),
        						  createPauseButton("pause.jpg"),
        						  createStopButton("stop.jpg"),
        						  createNextButton("next.jpg")
        						  );
        
        vBox.getChildren().addAll(gridPaneBottom, hBox);
        
        StackPane stackPane = new StackPane(vBox);
        stackPane.setAlignment(hBox, Pos.BOTTOM_CENTER);
		
        mainPane.setBottom(stackPane);
        
        playButton.setDisable(false);
		pauseButton.setDisable(true);
		stopButton.setDisable(true);
        
		filterButton.setOnAction(event -> {
			playList.setSearch(searchTextField.getText());
			playList.setSortCriterion(sortChoiceBox.getValue());
			if (table instanceof SongTable) {
				((SongTable) table).refreshSongs();
			}
			
		});
		
		playButton.setOnAction(event -> {
			try {
				playCurrentSong();
			} catch (NotPlayableException e) {
				System.out.println(e.getMessage());
			}
			
		});
		
		pauseButton.setOnAction(event -> {
			try {
				pauseCurrentSong();
			} catch (NotPlayableException e) {
				System.out.println(e.getMessage());
			}
			
		});
		
		stopButton.setOnAction(event -> {
			try {
				stopCurrentSong();
			} catch (NotPlayableException e) {
				System.out.println(e.getMessage());
			}
			
		});
		
		nextButton.setOnAction(event -> {
			try {
				forwardToTheNextSong();
			} catch (NotPlayableException e) {
				System.out.println(e.getMessage());
			}
			
		});
        
		Scene scene = new Scene(mainPane, 600, 400);
		stage.setScene(scene);
		stage.show();
	}
	
	private Button createPlayButton(String iconfile) { 
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			playButton = new Button("", imageView);
			playButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			playButton.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) { 
			System.out.println("Image " + "icons/" + iconfile + " not found!"); System.exit(-1);
		}
		return playButton;
	}
	
	private Button createPauseButton(String iconfile) { 
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			pauseButton = new Button("", imageView);
			pauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			pauseButton.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) { 
			System.out.println("Image " + "icons/" + iconfile + " not found!"); System.exit(-1);
		}
		return pauseButton;
	}
	
	private Button createStopButton(String iconfile) { 
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			stopButton = new Button("", imageView);
			stopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			stopButton.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) { 
			System.out.println("Image " + "icons/" + iconfile + " not found!"); System.exit(-1);
		}
		return stopButton;
	}
	
	private Button createNextButton(String iconfile) { 
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			nextButton = new Button("", imageView);
			nextButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			nextButton.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) { 
			System.out.println("Image " + "icons/" + iconfile + " not found!"); System.exit(-1);
		}
		return nextButton;
	}
	
	private void playCurrentSong() throws NotPlayableException {
		updateSongInfo(playList.currentAudioFile());
		playButton.setDisable(true);
		stopButton.setDisable(false);
		pauseButton.setDisable(false);
		startThreads(playerThread.isAlive());
		
	}
	
	private void pauseCurrentSong() throws NotPlayableException {
		playList.currentAudioFile().togglePause();
		if (timerThread.isAlive()) {
			terminateThreads(true);
		} else {
			startThreads(playerThread.isAlive());
		}
	}
	
	private void stopCurrentSong() throws NotPlayableException {
		terminateThreads(false);
		playList.currentAudioFile().stop();
		updateSongInfo(playList.currentAudioFile());
		playButton.setDisable(false);
		pauseButton.setDisable(true);
		stopButton.setDisable(true);
	}
	
	private void forwardToTheNextSong() throws NotPlayableException {
		try {
			terminateThreads(false);
			playList.currentAudioFile().stop();
		} catch (Exception e) {}
		
		playList.nextSong();
		
		updateSongInfo(playList.currentAudioFile());
		playButton.setDisable(true);
		pauseButton.setDisable(false);
		stopButton.setDisable(false);
		startThreads(playerThread.isAlive());
	}
	
	private void updateSongInfo(AudioFile audioFile) {
		Platform.runLater(() -> {
			if (audioFile == null) {
				currentSongLabel.setText(NO_CURRENT_SONG);
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			} else {
				currentSongLabel.setText(audioFile.getTitle());
				playTimeLabel.setText(((SampledFile) playList.currentAudioFile()).formatPosition());
			}
		});
	}
	
	public class PlayerThread extends Thread{
		
		private boolean stopped = false;
		
		public void run() {
			
			while (!stopped) {	
				try {
					playList.currentAudioFile().play();
				} catch (NotPlayableException e) {
				}
				if (playList.currentAudioFile() instanceof SampledFile) {
					if (((SampledFile) playList.currentAudioFile()).getDuration() == BasicPlayer.getPosition()) {
						playList.nextSong();
					}
				}
			}
		}
		
		public void terminate() {
			stopped = true;
		}
	}
	
	public class TimerThread extends Thread{
		
		private boolean stopped = false;
		
		public void run() {
			while (!stopped) {
				if (playList.currentAudioFile() instanceof SampledFile) {
					updateSongInfo(playList.currentAudioFile());
				}
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
			}
		}
		
		public void terminate() {
			stopped = true;
		}
	}
	
	private void startThreads(boolean playerThreadActive) {
		timerThread = new TimerThread();
		timerThread.start();
		if(!playerThreadActive) {
			playerThread = new PlayerThread();
			playerThread.start();
		}
	}
	private void terminateThreads(boolean onlyTimer) {
		timerThread.terminate();
		if(!onlyTimer) {
			playerThread.terminate();
		}

	}
	
	public static void main(String[] args) {
		launch();

	}

}


