package de.joshizockt.homesystem.api;

import de.joshizockt.homesystem.api.manager.HomeManager;
import de.joshizockt.homesystem.common.util.SQLConnector;
import de.joshizockt.homesystem.spigot.HomeSystemSpigot;

public class HomeSystemAPI {

    private static HomeSystemAPI instance;

    private SQLConnector sqlConnector;
    private HomeManager homeManager;

    public HomeSystemAPI(HomeSystemSpigot plugin, SQLConnector connector) {
        instance = this;
        this.sqlConnector = connector;
        this.homeManager = new HomeManager(connector);
    }

    public SQLConnector getSQLConnector() {
        return sqlConnector;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public static HomeSystemAPI getAPI() {
        return instance;
    }

}
