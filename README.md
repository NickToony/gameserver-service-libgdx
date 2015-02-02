Gameserver Service Library for LibGDX
===================

<img src="https://github.com/NickToony/gameserver-service-libgdx/blob/master/screenshots/screenshot%201.png"/ width="50%">

About
-----
Provides a simple user interface for fetching and displaying a server list within a LibGDX game. It makes use of the Scene2d.ui to make it more responsive on various screen sizes. The server list itself can easily be inserted into your own existing UI stages.

Dependencies
----

**See the Laravel project**: https://github.com/NickToony/gameserver-service
**See the Java project**:

This project relies on the Java project for the fetching of the server list. You also need a web server running the laravel project for this to be useful.

**Other Dependencies (which are automatically included by Gradle)**
- LibGDX (http://libgdx.badlogicgames.com/

Installation
------------
1. Clone the repository into your project
2. Add the folder as a module for your project. Then add it to your dependencies
    - e.g. for gradle (replace module name)
`compile project(':module_name')`

Example
------
First, don't forget to set your Game Configuration (see the Java project for more info)
```java
GameserverConfig.setConfig(new GameConfig());
```

Then you can create the ServerList object, with a LibGDX skin of your choice
```java
serverList = new ServerList(new Skin(Gdx.files.internal("skins/default/uiskin.json")));
```

Finally, add it to your stage. The ServerList object is an extension of the Table actor, so treat it like any other actor.
```java
stage.addActor(serverList);
```

Planned Features
---------------
- Significantly improved appearance - it's basic right now!
- Searching, filtering, ordering.
- Resizable columns
- META support
