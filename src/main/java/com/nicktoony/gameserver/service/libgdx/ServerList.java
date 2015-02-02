package com.nicktoony.gameserver.service.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.nicktoony.gameserver.service.client.Client;
import com.nicktoony.gameserver.service.client.models.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 01/02/2015.
 */
public class ServerList extends Table implements Client.ClientListener {
    private final Skin skin;
    private final Client client;

    private ShapeRenderer shapeRenderer;

    private List<String> columns = new ArrayList<>();

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
        client.refresh();

        // finally, setup
        setup();
    }

    private void setup() {
        clearChildren();

        setupColumnHeaders();
        setupRows();
    }

    private void setupColumnHeaders() {
        createRow(this, columns.toArray(new String[columns.size()]), true);
    }


    private void setupRows() {
        row();

        Table table = new Table();
        table.setDebug(getDebug());

        ScrollPane scrollPane = new ScrollPane(table);
        add(scrollPane).fillX().colspan(getColumns());

        for (Server server : client.getServers()) {
            createRow(table, new String[] {
                    server.getName(), server.getCurrentPlayers() + "/" + server.getMaxPlayers()
            }, false);
        }
    }

    private void createRow(Table table, String[] values, boolean header) {
        if (header) {
            table.row().padBottom(20);
        } else {
            table.row();
        }

        for (String value : values) {
            Label label = new Label(value, skin);
            table.add(label).expandX().align(Align.left);
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onRefreshed() {
        setup();
    }

    @Override
    public void onFail() {

    }
}
