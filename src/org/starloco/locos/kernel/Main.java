package org.starloco.locos.kernel;

import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.database.Database;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.exchange.ExchangeClient;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.scheduler.entity.WorldPub;
import org.starloco.locos.game.scheduler.entity.WorldPlayerOption;
import org.starloco.locos.game.scheduler.entity.WorldSave;
import org.starloco.locos.game.world.World;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static Logger logger = (Logger) LoggerFactory.getLogger(Main.class);
    public static final List<Runnable> runnables = new LinkedList<>();

    public static boolean isRunning = false, isSaving = false;
    public static boolean modDebug = true;
    public static boolean allowMulePvp = false, useSubscribe = false;
    public static int startLevel = 1, startKamas = 50000000;
    public static boolean mapAsBlocked = false, fightAsBlocked = false, tradeAsBlocked = false;

    //Exchange
    public static String key = "jiva";
    public static int serverId = 1, exchangePort = 666;
    public static String exchangeIp = "127.0.0.1";
    public static String loginHostDB = "127.0.0.1", loginNameDB = "login", loginUserDB = "root", loginPassDB = "", loginPortDB = "3306";
    //Game
    public static int gamePort = 5555;
    public static String hostDB = "127.0.0.1", nameDB = "", userDB = "root", passDB = "", portDB = "3306";
    public static String Ip = "127.0.0.1";

    public static GameServer gameServer;
    public static ExchangeClient exchangeClient;

    public static void main(String[] args) throws SQLException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (Main.isRunning) {
                    Main.isRunning = false;

                    GameServer.setState(0);
                    WorldSave.cast(0);
                    GameServer.setState(0);

                    if(Main.gameServer != null) Main.gameServer.kickAll(true);
                    Logging.getInstance().stop();
                    Database.getStatics().getServerData().loggedZero();
                    //TimerWaiter.stop();
                    /*if(Main.exchangeClient != null) Main.exchangeClient.stop();
                    if(Main.gameServer != null) Main.gameServer.close();*/
                }
                Main.logger.info("The server is now closed.");
            }
        });

        try {
            System.setOut(new PrintStream(System.out, true, "IBM850"));
            if (!new File("Logs/Error").exists()) new File("Logs/Error").mkdir();
            System.setErr(new PrintStream(Files.newOutputStream(Paths.get("Logs/Error/" + new SimpleDateFormat("dd-MM-yyyy - HH-mm-ss", Locale.FRANCE).format(new Date()) + ".log"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Main.start();
    }


    public static void start() {
        Main.setTitle("StarLoco - Loading data..");
        Main.logger.info("You use " + System.getProperty("java.vendor") + " with the version " + System.getProperty("java.version"));
        Main.logger.debug("Starting of the server : " + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.FRANCE).format(new Date()));
        Main.logger.debug("Current timestamp : " + System.currentTimeMillis());

        Config.getInstance().load();
        Logging.getInstance().initialize();

        if(Database.launchDatabase()) {
            Main.isRunning = true;
	        World.world.createWorld();

            new GameServer().initialize();
            new ExchangeClient().initialize();

            Main.refreshTitle();
            Main.logger.info("The server is ready ! Waiting for connection..\n");

	        
	        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	        root.setLevel(Level.ERROR);
	        

            while (Main.isRunning) {
                try {
                    WorldSave.updatable.update();
                    GameMap.updatable.update();
                    InteractiveObject.updatable.update();
                    Mount.updatable.update();
                    WorldPlayerOption.updatable.update();
                    WorldPub.updatable.update();

                    if(!Main.runnables.isEmpty()) {
                        for (Runnable runnable : new LinkedList<>(Main.runnables)) {
                            try {
                                if(runnable != null) {
                                    runnable.run();
                                    Main.runnables.remove(runnable);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Main.logger.error("An error occurred when the server have try a connection on the Mysql server. Please check your identification.");
        }
    }

    public static void stop(String reason) {
        Logging.getInstance().write("Error", reason);
        System.exit(0);
    }

    private static void setTitle(String title) {
        AnsiConsole.out.printf("%c]0;%s%c", '\033', title, '\007');
    }

    public static void refreshTitle() {
        if (Main.isRunning)
            Main.setTitle(Config.getInstance().NAME + " - Port : " + Main.gamePort + " | " + Main.key + " | " + Main.gameServer.getClients().size() + " Joueur(s)");
    }

    public static void clear() { //~30ms
        AnsiConsole.out.print("\033[H\033[2J");
    }
}
