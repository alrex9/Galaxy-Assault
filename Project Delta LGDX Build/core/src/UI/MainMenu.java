package UI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import framework.NewGame;

public class MainMenu {
	
	public Stage stage;
	private Viewport viewport;
	private TextureAtlas startAtlas, storeAtlas, creditsAtlas;
	private Button startButton, storeButton, creditsButton;
	
	public MainMenu(SpriteBatch sb) {
		viewport = new StretchViewport(NewGame.V_WIDTH, NewGame.V_HEIGHT);
		stage = new Stage(viewport, sb);
		Table table = new Table();
		table.top();
		table.setFillParent(true);
	}
}
