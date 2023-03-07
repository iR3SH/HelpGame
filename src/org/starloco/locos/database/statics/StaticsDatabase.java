package org.starloco.locos.database.statics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.Database;
import org.starloco.locos.database.statics.data.*;
import org.starloco.locos.kernel.Main;
import org.slf4j.LoggerFactory;

public class StaticsDatabase {
    //connection
    private HikariDataSource dataSource;
    private Logger logger = (Logger) LoggerFactory.getLogger(StaticsDatabase.class);
    //data
    private AccountData accountData;
    private CommandData commandData;
    private PlayerData playerData;
    private ServerData serverData;
    private BanIpData banIpData;
    private AreaData areaData;
    private SubAreaData subAreaData;
    private GroupData groupData;
    private HouseData houseData;
    private TrunkData trunkData;
    private MountParkData mountParkData;
    private ObvejivanData obvejivanData;
    private PubData pubData;
    private PrestigeData prestigeData;
    private PrestigeBonusData prestigeBonusData;

    public void initializeData() {
        this.accountData = new AccountData(this.dataSource);
        this.commandData = new CommandData(this.dataSource);
        this.playerData = new PlayerData(this.dataSource);
        this.serverData = new ServerData(this.dataSource);
        this.banIpData = new BanIpData(this.dataSource);
        this.areaData = new AreaData(this.dataSource);
        this.subAreaData = new SubAreaData(this.dataSource);
        this.groupData = new GroupData(this.dataSource);
        this.houseData = new HouseData(this.dataSource);
        this.trunkData = new TrunkData(this.dataSource);
        this.mountParkData = new MountParkData(this.dataSource);
        this.obvejivanData = new ObvejivanData(this.dataSource);
        this.pubData = new PubData(this.dataSource);
        this.prestigeData = new PrestigeData(this.dataSource);
        this.prestigeBonusData = new PrestigeBonusData(this.dataSource);
    }

    public boolean initializeConnection() {
        try {
            logger.setLevel(Level.ALL);
            logger.trace("Reading database config");

            HikariConfig config = new HikariConfig();
            config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            config.addDataSourceProperty("serverName", Main.loginHostDB);
            config.addDataSourceProperty("port", Main.loginPortDB);
            config.addDataSourceProperty("databaseName", Main.loginNameDB);
            config.addDataSourceProperty("user", Main.loginUserDB);
            config.addDataSourceProperty("password", Main.loginPassDB);
            config.setAutoCommit(true); // AutoCommit, c'est cool
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(1);
            this.dataSource = new HikariDataSource(config);

            if (!Database.tryConnection(this.dataSource)) {
                logger.error("Please check your username and password and database connection");
                Main.stop("statics try connection failed");
                return false;
            }
            logger.info("Database connection established");
            initializeData();
            logger.info("Database data loaded");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public BanIpData getBanIpData() {
        return banIpData;
    }

    public AreaData getAreaData() {
        return areaData;
    }

    public SubAreaData getSubAreaData() {
        return subAreaData;
    }

    public GroupData getGroupData() {
        return groupData;
    }

    public HouseData getHouseData() {
        return houseData;
    }

    public TrunkData getTrunkData() {
        return trunkData;
    }

    public MountParkData getMountParkData() {
        return mountParkData;
    }

    public ObvejivanData getObvejivanData() {
        return obvejivanData;
    }

    public PubData getPubData() {
        return pubData;
    }

	public PrestigeData getPrestigeData() {
		return prestigeData;
	}

	public PrestigeBonusData getPrestigeBonusData() {
		return prestigeBonusData;
	}
}
