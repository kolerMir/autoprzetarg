package ai.makeitright.utilities.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DBConnector {

    public static List<String> getAuctionsToUpdate() throws SQLException {
        Dao<Favourites, String> favouritesDataDao = null;
        try (ConnectionSource connectionSource = getConnectionSource()) {
            favouritesDataDao = DaoManager.createDao(connectionSource, Favourites.class);
        } catch (IOException e) {
            log.atError().log(e.toString());
        }
        List<Favourites> favourites = favouritesDataDao.queryForAll();
        List<String> urlsOfFavouritedAuctions = new ArrayList<>();
        for (Favourites favourite : favourites) {
            if (favourite.getZrodlo().equals(System.getProperty("inputParameters.title")) && favourite.isFavourite()) {
                urlsOfFavouritedAuctions.add(favourite.getId());
            }
        }
        return urlsOfFavouritedAuctions;
    }

    public static void sendScrappedAuctionDatas(final List<AuctionData> auctionDatas) throws SQLException, IOException {
        Dao<AuctionData, String> auctionDataDao = null;
        try (ConnectionSource connectionSource = getConnectionSource()) {
            auctionDataDao = DaoManager.createDao(connectionSource, AuctionData.class);
        } catch (IOException e) {
            log.atError().log(e.toString());
        }
        for (AuctionData auctionData : auctionDatas) {
            auctionDataDao.createOrUpdate(auctionData);
        }
    }

    private static ConnectionSource getConnectionSource() throws SQLException {
        return new JdbcConnectionSource(System.getProperty("inputParameters.dbo"), System.getProperty("inputParameters.dbuser"), System.getProperty("inputParameters.dbpassword"));
    }

}