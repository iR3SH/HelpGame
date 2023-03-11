package org.starloco.locos.kernel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static final Config singleton = new Config();

    public final long startTime = System.currentTimeMillis();
    public boolean HALLOWEEN = false, NOEL = false, HEROIC = false;

    public String NAME, url, startMessage = "", colorMessage = "B9121B";
    public boolean autoReboot = true, allZaap = false, allEmote = false, onlyLocal = false;
    public int startMap = 0, startCell = 0;
    public int rateKamas = 1, rateDrop = 1, rateHonor = 1, rateJob = 1, rateFm = 1;
    public float rateXp = 1;
    public int erosion=10;
    public boolean prestige = false;

    public static int[] START_PANO = new int[0];
    public static int[] START_ITEM = new int[0];
    
    public int AIDelay=100, AIMovementCellDelay=180, AIMovementFlatDelay=700; //delay in ms


    public static Config getInstance() {
        return singleton;
    }

    public void load() {
        FileReader file = null;
        try {
            file = new FileReader("config.txt");
        } catch (FileNotFoundException ignored) {
        	this.generateNewConfigFile();
        	return;
        }
        try (final BufferedReader config = new BufferedReader(file);){
            String line;
            while ((line = config.readLine()) != null) {
            	final String[] lineSplit = line.split("=");
                if (lineSplit.length == 1)
                    continue;

                final String param = line.split("=")[0].trim();
                final String value = line.split("=")[1].trim();

                switch (param.toUpperCase()) {
                
                	// CONNECTION
                    case "IP":
                    	Main.Ip = value;
                        break;
                    case "GAME_PORT":
                        Main.gamePort = Integer.parseInt(value);
                        break;
                    case "EXCHANGE_PORT":
                        Main.exchangePort = Integer.parseInt(value);
                        break;
                    case "EXCHANGE_IP":
                        Main.exchangeIp = value;
                        break;
                        
                    // BDD
                    case "LOGIN_IP_DB":
                        Main.loginHostDB = value;
                        break;
                    case "LOGIN_NAME_DB":
                        Main.loginNameDB = value;
                        break;
                    case "LOGIN_USER_DB":
                        Main.loginUserDB = value;
                        break;
                    case "LOGIN_PASS_DB":
                        Main.loginPassDB = value;
                        break;
                    case "LOGIN_PORT_DB":
                        Main.loginPortDB = value;
                        break;
                
                 
                    case "GAME_IP_DB":
                        Main.hostDB = value;
                        break;
                    case "GAME_NAME_DB":
                        Main.nameDB = value;
                        break;
                    case "GAME_USER_DB":
                        Main.userDB = value;
                        break;
                    case "GAME_PASS_DB":
                        Main.passDB = value;
                        break;
                    case "GAME_PORT_DB":
                        Main.portDB = value;
                        break;
                
                    // RULE
                    case "START_LEVEL":
                        Main.startLevel = Integer.parseInt(value);
                        break;
                    case "START_KAMAS":
                        Main.startKamas = Integer.parseInt(value);
                        break;
                    case "ALLOW_PRESTIGE":
                        this.prestige = value.equalsIgnoreCase("true");
                        break;
                    case "SUBSCRIBER":
                        Main.useSubscribe = value.equalsIgnoreCase("true");
                        break;
                    case "START_PLAYER":
                        try {
                            this.startMap = Integer.parseInt(value.split("\\,")[0]);
                            this.startCell = Integer.parseInt(value.split("\\,")[1]);
                        } catch (Exception e) {
                            // ok
                        }
                        break;
                    case "START_PANO":
                		try {
                			START_PANO = this.convertArrayStringToInt(value.split(";"));
                		}catch(NumberFormatException e) {
                        	e.printStackTrace();
                        }
                		break;
                	case "START_ITEM":
                		try {
                			START_ITEM = value.isEmpty() ? new int[]{} : this.convertArrayStringToInt(value.split(";"));
                		}catch(NumberFormatException e) {
                        	e.printStackTrace();
                        }
                		break;
                    case "ALL_ZAAP":
                        this.allZaap = value.equalsIgnoreCase("true");
                        break;
                    case "ALL_EMOTE":
                        this.allEmote = value.equalsIgnoreCase("true");
                        break;
                    case "NOEL":
                        this.NOEL = value.equalsIgnoreCase("true");
                        break;
                    case "HALLOWEEN":
                        this.HALLOWEEN = value.equalsIgnoreCase("true");
                        break;
                    case "HEROIC":
                        this.HEROIC = value.equalsIgnoreCase("true");
                        break;
                
                    // CONFIGURATION

                    case "MESSAGE":
                        this.startMessage = value;
                        break;
                    case "URL":
                        this.url = value;
                        break;
                    case "NAME":
                        this.NAME = value;
                        break;
                    case "AUTO_REBOOT":
                        this.autoReboot = value.equalsIgnoreCase("true");
                        break;
                    case "SERVER_ID":
                        Main.serverId = Integer.parseInt(value);
                        break;
                    case "SERVER_KEY":
                        Main.key = value;
                        break;
                    case "DEBUG":
                        Main.modDebug = value.equalsIgnoreCase("true");
                        break;
                    case "USE_LOG":
                        Logging.USE_LOG = value.equalsIgnoreCase("true");
                        break;
                        
                    // RATE
                    case "RATE_XP":
                        this.rateXp = Float.parseFloat(value);
                        break;
                    case "RATE_DROP":
                        this.rateDrop = Integer.parseInt(value);
                        break;
                    case "RATE_JOB":
                        this.rateJob = Integer.parseInt(value);
                        break;
                    case "RATE_KAMAS":
                        this.rateKamas = Integer.parseInt(value);
                        break;
                    case "RATE_FM":
                        this.rateFm = Integer.parseInt(value);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

	private void generateNewConfigFile() {
		try(final BufferedWriter config = new BufferedWriter(new FileWriter("config.txt", true));) {
            final String str = "## Fichier de configuration pour StarLoco par Kevin#6537 ##\r\n"
            		+ "\r\n"
            		+ "## CONNECTION\r\n"
            		+ "IP = 127.0.0.1\r\n"
            		+ "GAME_PORT = 5555\r\n"
            		+ "\r\n"
            		+ "EXCHANGE_IP = 127.0.0.1\r\n"
            		+ "EXCHANGE_PORT = 666\r\n"
            		+ "\r\n"
            		+ "## BASE DE DONNEE\r\n"
            		+ "LOGIN_IP_DB = 127.0.0.1\r\n"
            		+ "LOGIN_NAME_DB = login\r\n"
            		+ "LOGIN_USER_DB = root\r\n"
            		+ "LOGIN_PASS_DB = \r\n"
            		+ "LOGIN_PORT_DB = 3306\r\n"
            		+ "\r\n"
            		+ "GAME_IP_DB = 127.0.0.1\r\n"
            		+ "GAME_NAME_DB = game\r\n"
            		+ "GAME_USER_DB = root\r\n"
            		+ "GAME_PASS_DB = \r\n"
            		+ "GAME_PORT_DB = 3306\r\n"
            		+ "\r\n"
            		+ "## RULES\r\n"
            		+ "SUBSCRIBER = false\r\n"
            		+ "START_LEVEL = 1\r\n"
            		+ "START_KAMAS = 10000\r\n"
            		+ "START_PLAYER = 164,298\r\n"
            		+ "## EXAMPLE FOR PANO AND ITEM : id;id;id;id\r\n"
            		+ "START_PANO = 5\r\n"
            		+ "START_ITEM = \r\n"
            		+ "ALL_ZAAP = true\r\n"
            		+ "ALL_EMOTE = true\r\n"
            		+ "NOEL = false\r\n"
            		+ "HALLOWEEN = false\r\n"
            		+ "HEROIC = false\r\n"
            		+ "\r\n"
            		+ "## CONFIGURATION\r\n"
            		+ "MESSAGE = Bienvenue dans l'�mulation FREE d'entraide\r\n"
            		+ "URL = nashira\r\n"
            		+ "NAME = Eratz\r\n"
            		+ "AUTO_REBOOT = true\r\n"
            		+ "\r\n"
            		+ "SERVER_ID = 601\r\n"
            		+ "SERVER_KEY = eratz\r\n"
            		+ "\r\n"
            		+ "DEBUG = true\r\n"
            		+ "USE_LOG = true\r\n"
            		+ "\r\n"
            		+ "## Server rate : \r\n"
            		+ "RATE_XP = 1\r\n"
            		+ "RATE_DROP = 1\r\n"
            		+ "RATE_JOB = 1\r\n"
            		+ "RATE_KAMAS = 1\r\n"
            		+ "RATE_FM = 2\r\n"
            		+ "\r\n"
            		+ "## Prestige :\r\n"
            		+ "ALLOW_PRESTIGE = false\r\n"
            		+ "";
            config.write(str);
            config.newLine();
            config.flush();
            Main.logger.info("The configuration file was created.");
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
    
	private int[] convertArrayStringToInt(final String[] arrayString) throws NumberFormatException
    {
    	final int size = arrayString.length;
    	final int[] array = new int[size];
    	for(int i = 0; i < size; ++i) 
    		array[i] = Integer.parseInt(arrayString[i].trim());
    	
    	return array;
    }

}