package com.nicktoony.gameserver.service.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nicktoony.gameserver.service.client.Client;
import com.nicktoony.gameserver.service.client.models.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Nick on 01/02/2015.
 */
public class ServerList extends Table implements Client.ClientListener {
    private final Skin skin;
    private final Client client;

    // Columns to display
    private List<String> columns = new ArrayList<>();
    private List<String> metaColumns = new ArrayList<>();

    // Stored values
    private ShapeRenderer shapeRenderer;
    private Table serversTable;
    private boolean updated;
    private boolean refreshing;

    // UI actors
    private Button buttonRefresh;
    private Label buttonRefreshText;
    private TextField textInputName;
    private Label labelName;
    private Label[] headerLabels;

    /**
     * Create a new ServerList, which is a libGDX table. It will handle the layout,
     * server list fetching. Just give it a skin.
     * @param skin
     */
    public ServerList(Skin skin) {
        this.skin = skin;
        
        // Setup the table layout
        setFillParent(true);
        align(Align.topLeft);

        // Define columns
        columns.add("Name");
        columns.add("Players");

        // For debug purposes
        shapeRenderer = new ShapeRenderer();

        // start the initial refresh
        client = new Client().setListener(this);
        refresh();

        // finally, setup
        //setup();
    }

    /**
     * Call this to recreate all views
     */
    public void setup() {
        clearChildren();

        setupColumnHeaders();
        setupRows();
        setupButtons();
    }

    private void update() {
        updated = true;
    }

    /**
     * The top headers are a seperate layout to the actual serverlist table,
     * so that they don't scroll away
     */
    private void setupColumnHeaders() {
        headerLabels = createRow(this, columns.toArray(new String[columns.size()]), true);
    }

    /**
     * uses the client's servers to populate the rows
     */
    private void setupRows() {
        if (serversTable == null) {
            row();

            serversTable = new Table();
            serversTable.setDebug(getDebug());
            serversTable.top();

            ScrollPane scrollPane = new ScrollPane(serversTable);
            add(scrollPane).expandY().fill().colspan(getColumns());
        }

        for (Server server : client.getServers()) {
            // Collect together all the meta values and such
            List<String> values = new ArrayList<String>();
            values.add(server.getName());
            values.add(server.getCurrentPlayers() + "/" + server.getMaxPlayers());
            for (String key : metaColumns) {
                if (server.getMeta().containsKey(key)) {
                    values.add(server.getMeta().get(key));
                } else {
                    values.add("null");
                }
            }

            // Convert the list to an array
            String[] valuesArray = new String[values.size()];
            // Create the row
            Label[] rowLabels = createRow(serversTable, values.toArray(valuesArray),false);
        }
    }

    /**
     * Any additional buttons and layouts are to be added here
     */
    private void setupButtons() {
        row().padTop(20);

        Table table = new Table();
        table.setDebug(getDebug());

        add(table).fillX().colspan(getColumns()).pad(5);

        buttonRefresh = new Button(skin);
        buttonRefreshText = new Label("Refresh", skin);
        buttonRefresh.add(buttonRefreshText);
        buttonRefresh.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!refreshing) {
                    refresh();
                }
            }
        });

        textInputName = new TextField("", skin);
        labelName = new Label("Server Name: ", skin);

        CheckBox checkEmpty = new CheckBox(" Empty Servers", skin);
        checkEmpty.setChecked(true);
        CheckBox checkFull = new CheckBox(" Full  Servers", skin);
        checkFull.setChecked(true);

        // States
        setRefreshing(refreshing);

        // Add to layouts
        table.row().left();
        table.add(labelName);
        table.add(textInputName).fillX().expandX();
        table.add(buttonRefresh).padLeft(50);

        table.row().left();
        table.add(checkEmpty);
        table.row().left();
        table.add(checkFull);
    }

    /**
     * To reduce redundant code, this method can be used to create a row
     * @param table
     * @param values
     * @param header
     */
    private Label[] createRow(Table table, String[] values, boolean header) {
        if (header) {
            table.row().padBottom(20);
        } else {
            table.row().expandX();
        }

        Label[] labels = new Label[values.length];
        for (int i = 0; i < values.length; i ++) {
            String value = values[i];
            Label label = new Label(value, skin);
            if (header) {
                table.add(label).expandX().align(Align.left);
            } else {
                table.add(label).align(Align.left).width(headerLabels[i].getWidth());
            }
            labels[i] = label;
        }
        return labels;
    }

    /**
     * Best to tidy up..
     */
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onRefreshed() {
        setRefreshing(false);
        update();
    }

    @Override
    public void onFail() {

    }

    /**
     * This updates the state of the button
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if (buttonRefresh != null) {
            if (refreshing) {
                buttonRefresh.setColor(Color.DARK_GRAY);
                buttonRefreshText.setText("Refreshing...");
            } else {
                buttonRefresh.setColor(Color.WHITE);
                buttonRefreshText.setText("Refresh");
            }
        }
    }

    /**
     *  Initiate a refresh
     */
    public void refresh() {
        client.refresh();
        setRefreshing(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (updated) {
            serversTable.clearChildren();
            setupRows();
            updated = false;
        }
    }

    public void addMetaColumn(String header, String column) {
        columns.add(header);
        metaColumns.add(column);
    }
}
