package com.domenicportuesi.jfxwb;

// IMPORTS
// These are some classes that may be useful for completing the project.
// You may have to add others.
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.concurrent.Worker.State;
import javafx.concurrent.Worker;
import javafx.scene.text.Font;

/**
 * JavaFX Web browser
 * 
 * Date Modified: 11/15/16
 * @author Domenic Portuesi
 */
public class JavaFXWebBrowser extends Application
{
	// INSTANCE VARIABLES
	private double WIDTH = 600;
	private double HEIGHT = 400;
	private Stage stage = null;
	private BorderPane browserPane = null;
	
	private WebView view = null;
	private WebEngine webEngine = null;
	
	private TextField statusBarText = null;
	private HBox statusBarBox = null;
	
	private HBox toolPane;
	
	private TextField url;
	private HBox addressBar;
	
	//stores all previous urls for back button and forward button.
	private ArrayList<String>urls = new ArrayList<String>();
	
	//buttons
	private Button backBtn;
	private Button forwardBtn;
	private Button helpBtn;
	private Button reloadBtn;

	//index of the current url (for forward/back buttons).
	private int urlIndex = 0;
	
	private boolean isHelpPage = false;

	//Color for toolpane and status bar. I was planning on adding a color changer one day.
	private String browserColor = "#336699";

	// HELPER METHODS
	/**
	 * Retrieves the value of a command line argument specified by the index.
	 * 
	 * @param index
	 *            - position of the argument in the args list.
	 * @return The value of the command line argument.
	 */
	private String getParameter(int index)
	{
		Parameters params = getParameters();
		List<String> parameters = params.getRaw();
		return !parameters.isEmpty() ? parameters.get(0) : "";
	}

	/**
	 * Generates the status bar layout and text field.
	 * 
	 * @return statusbarPane - the HBox layout that contains the statusbar.
	 */
	private HBox makeStatusBar()
	{
		HBox statusbarPane = new HBox();
		statusbarPane.setPadding(new Insets(5, 4, 5, 4));
		statusbarPane.setSpacing(10);
		statusbarPane.setStyle("-fx-background-color: " + browserColor + ";");
		statusBarText = new TextField();
		HBox.setHgrow(statusBarText, Priority.ALWAYS);
		statusbarPane.getChildren().addAll(statusBarText);
		return statusbarPane;
	}
	
	/**
	 * Sets the main browser pane to have the toolpane and the webview.
	 */
	public void setWebView()
	{
		browserPane.setCenter(view);
		browserPane.setTop(toolPane);
		isHelpPage = false;
	}

	/**
	 * Sets the webpage based on the url. Adds the url to the history list and indexes urlIndex.
	 * @param url url to load.
	 */
	public void setWebpage(String url)
	{
		setWebpage(url, true);
	}
	
	/**
	 * Sets the webpage based on the url. Adds the url to the history list and indexes urlIndex.
	 * @param url url to load.
	 * @param load Should the page even load? This is for if you only want to index because the page is already loading.
	 */
	public void setWebpage(String url, boolean load)
	{		
    	browserPane.setBottom(null);

		if(isHelpPage)
		{
			setWebView();
		}
		
		if(urls.size() <= 0 )
			urlIndex=0;
		else
			urlIndex++;
		
		if(urls.size() > 0)
		{
			if(!webEngine.getLocation().equals( urls.get(urls.size() - urlIndex)))
			{
				if(url != "null" && url != null)
					urls.add(url);
				else
					urls.add(webEngine.getLocation());
				//Removes all urls past this one to "reset" the forward button
				for(int i = urlIndex + 1; i < urls.size(); i ++)
				{
					urls.remove(i);
				}
			}
			else
			{
				urlIndex--;
			}
		}
		else
		{
			if(url != "null" && url != null)
				urls.add(url);
			else
				urls.add(webEngine.getLocation());
			//Removes all urls past this one to "reset" the forward button
			for(int i = urlIndex + 1; i < urls.size(); i ++)
			{
				urls.remove(i);
			}
		}
		
		if(load)
		{
			webEngine.load(url);
		}
		stage.titleProperty().bind(webEngine.titleProperty());
		this.url.setText(webEngine.getLocation());
		
		backBtn.setDisable(backButtonDisableCheck());
		forwardBtn.setDisable(forwardButtonDisableCheck());
	}
	
	/**
	 * Checks if the back button should be disabled or not.
	 * @return disable it?
	 */
	private boolean backButtonDisableCheck()
	{
		if(urls.size() > 0 && urlIndex >= 1)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the forward button should be disabled or not.
	 * @return disable it?
	 */
	private boolean forwardButtonDisableCheck()
	{
		if(urls.size() > 0 && urlIndex < urls.size() - 1)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Creates the help page with its various texts in a VBox, then adds it to the browser pane.
	 */
	public void setHelpPage()
	{
		if(browserPane != null && toolPane != null)
		{
        	browserPane.setBottom(null);

			isHelpPage = true;
			BorderPane helpPage = new BorderPane();
			
			Label title = new Label("JavaFX Web Browser - Help");
			title.setFont(Font.font("Verdana", 35));
			title.setPrefSize(600, 85);
			
			Label text = new Label(
					"About: "
					);
			text.setFont(Font.font("Verdana", 20));
			text.setPrefSize(300, 50);
			
			Label text2 = new Label(
					"Use the address bar to type in a web address. Use the forward and back buttons to switch between previous pages."
					);
			text2.setFont(Font.font("Verdana", 20));
			
			Label text3 = new Label(
					 "By Domenic Portuesi - CS1131 - L01");
			text3.setFont(Font.font("Verdana", 20));
			
			Label text4 = new Label(
					"Features - Auto appends http and www, full address bar select, reload button, active/inactive back/forward buttons"
					);
			
			text.setAlignment(Pos.CENTER);
			title.setAlignment(Pos.CENTER);
			text2.setAlignment(Pos.CENTER);
			text3.setAlignment(Pos.CENTER);
			text4.setAlignment(Pos.BOTTOM_CENTER);
			
			VBox texts = new VBox();
			texts.setSpacing(30);
			texts.setAlignment(Pos.TOP_CENTER);
			texts.getChildren().addAll(title,text,text2,text3,text4);
			
			helpPage.setTop(toolPane);
			helpPage.setCenter(texts);
			
			browserPane.setCenter(helpPage);
		}
	}
	
	// REQUIRED METHODS
	/**
	 * The main entry point for all JavaFX applications. The start method is called after the init method has returned, and after the system is ready for the application to begin running.
	 * 
	 * NOTE: This method is called on the JavaFX Application Thread.
	 * 
	 * @param primaryStage
	 *            - the primary stage for this application, onto which the application scene can be set.
	 */
	@Override
	public void start(Stage stage)
	{
		//BASIC INTITS
		this.stage = stage;
		browserPane = new BorderPane(); // the root of the scene
		
		Scene scene = new Scene(browserPane, WIDTH, HEIGHT, Color.WHITE);
		stage.setScene(scene);
		stage.show();
		
		//html view
		view = new WebView();
		webEngine = view.getEngine();
		
		//status bar
		statusBarBox = makeStatusBar();
	
		//CREATION OF NODES
		
		//tool bar
		toolPane = new HBox();
		toolPane.setPrefHeight(20);
		toolPane.setPadding(new Insets(25,25,25,25));
		toolPane.setSpacing(20);
		toolPane.setStyle("-fx-background-color: " + browserColor + ";");
		
		//address bar
		url = new TextField();
		url.setPrefWidth(WIDTH);
		url.setOnMousePressed(e -> {
			//selects all of the text when its clicked.
			if(url.getSelectedText().length() > 1)
				url.selectEnd();
			if(url.getSelectedText().length() <= 1)
				url.selectAll();
		});
		url.setOnAction(e -> {
			//appends the correct stuff to make it a proper link.
			if(url.getText().startsWith("www."))
			{
				String link = url.getText();
				url.setText("https://" + link);
			}
			else if(!url.getText().startsWith("www.") && !url.getText().startsWith("https://"))
			{
				String link = url.getText();
				url.setText("https://www." + link);
			}
			setWebpage(url.getText());
		});
		
		//address bar
		addressBar = new HBox();
		addressBar.setPadding(new Insets(5, 4, 5, 4));
		addressBar.setSpacing(10);
		addressBar.getChildren().add(url);
		
		//back and forward and reload buttons
		backBtn = new Button("<-");
		backBtn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		backBtn.setDisable(true);
		backBtn.setOnAction(e->{
			urlIndex--;
			webEngine.load(urls.get(urlIndex));			
			backBtn.setDisable(backButtonDisableCheck());
			forwardBtn.setDisable(forwardButtonDisableCheck());
			this.url.setText(webEngine.getLocation());
			stage.titleProperty().bind(webEngine.titleProperty());
			if(isHelpPage)
			{
				setWebView();
			}
			
		});
		forwardBtn = new Button("->");
		forwardBtn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		forwardBtn.setDisable(true);
		forwardBtn.setOnAction(e->{
			urlIndex++;
			webEngine.load(urls.get(urlIndex));		
			forwardBtn.setDisable(forwardButtonDisableCheck());
			backBtn.setDisable(backButtonDisableCheck());
			this.url.setText(webEngine.getLocation());
			stage.titleProperty().bind(webEngine.titleProperty());
			if(isHelpPage)
			{
				setWebView();
			}
			
		});
		
		helpBtn = new Button("?");
		helpBtn.setOnAction(e -> {
			setHelpPage();
		});
		
		reloadBtn = new Button("@");
		reloadBtn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		reloadBtn.setOnAction(e -> {
			webEngine.reload();
		});
		
		//status bar
		statusBarText.setEditable(false);
		
		//hover text and link clicking. There is probably a much better way of doing this. Currently, i search for all anchor tags and display them when the mouse hovers.
		view.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() 
		{
            @Override
            public void changed(ObservableValue ov, State oldState, State newState) 
            {
            	backBtn.setDisable(backButtonDisableCheck());
    			forwardBtn.setDisable(forwardButtonDisableCheck());
    			
    			//In case the clicked link was a null anchor text, this changes the "null" added to the history to the correct web page.
    			if(urls.size() >= urlIndex + 2 )
        		{
        			if(urls.get(urlIndex + 1) == null || urls.get(urlIndex + 1).equals("null"))
        				urls.set(urlIndex + 1, webEngine.getLocation());
        		}
        		
    			//if the page was loaded, begin the hover text stuff.
                if (newState == Worker.State.SUCCEEDED) 
                {     	
            		url.setText(webEngine.getLocation());
            		
                    EventListener listener = new EventListener() 
                    {
                        @Override
                        public void handleEvent(Event ev) 
                        {
                            String domEventType = ev.getType();
                            String href = ((Element)ev.getTarget()).getAttribute("href");
                            
                            String base = "";
                            
                            //Appends the base URL if the href is not absolute. 
                            if(href != null)
                            {
	                            if(!href.startsWith("http"))
	                            {
	                            	base = ((Element)ev.getTarget()).getBaseURI();
	                            }
	                            else
	                            {
	                            	base = "";
	                            }
                            }
                            
                            if (domEventType.equals("click")) 
                            {          
                            	//Indexes for history purposes, but does not reload it as the webview already does that.
                                setWebpage(base + href, false);
                            }
                            else if(domEventType.equals("mouseover"))
                            {
                            	statusBarText.setText(base + href);
                            	browserPane.setBottom(statusBarBox);
                            }
                            else
                            {
                            	statusBarText.setText("");
                            	browserPane.setBottom(null);
                            }
                        }
                    };
 
                    //Scans the HTML document, creates a corresponding event with any anchor tag
                    Document doc = view.getEngine().getDocument();
                    NodeList nodeList = doc.getElementsByTagName("a");
                    for (int i = 0; i < nodeList.getLength(); i++) 
                    {
                        ((EventTarget) nodeList.item(i)).addEventListener("click", listener, false);
                        ((EventTarget) nodeList.item(i)).addEventListener("mouseover", listener, false);
                        ((EventTarget) nodeList.item(i)).addEventListener("mouseout", listener, false);
                    }
                }
            }
        });
		
		//FINILIZE ADDING THINGS
		
		//add everything to tool pane
		toolPane.getChildren().addAll(backBtn, forwardBtn,reloadBtn,addressBar,helpBtn);
		
		//sets up the web browser nodes.
		setWebView();
		
		//Sets default values
		if(getParameter(0).startsWith("http") || getParameter(0).startsWith("www."))
		{
			setWebpage(getParameter(0));
		}
		else
		{
			setHelpPage();
		}
		
		//Actively change width of url address bar
		scene.widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		        url.setPrefWidth((double)newSceneWidth);
		    }
		});
		
		stage.setMaximized(true);
	}
	
	/**
	 * The main( ) method is ignored in JavaFX applications. main( ) serves only as fallback in case the application is launched as a regular Java application, e.g., in IDEs with limited FX support.
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
		launch(args);
	}
}
