package ai.makeitright.tests;

import ai.makeitright.utilities.db.AuctionData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ai.makeitright.utilities.crawler.Crawler.crawl;
import static ai.makeitright.utilities.db.DBConnector.getAuctionsToUpdate;
import static ai.makeitright.utilities.db.DBConnector.sendScrappedAuctionDatas;
import static ai.makeitright.utilities.inputparameters.InputParametersReader.getAuctionsPartialLinksFromJsonArrayOfInputParameter;
import static ai.makeitright.utilities.scraper.Scraper.scrape;
import static ai.makeitright.utilities.xlsx.XlsxCreator.convertArrayOfAuctionDatasToExcelFile;

@Slf4j
public class AutoPrzetarg {

    @Test
    public void doTest() throws IOException, InterruptedException, ParseException, SQLException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {

        boolean scrapeAllAuctions = Boolean.parseBoolean(System.getProperty("inputParameters.scrapeAllAuctions"));
        boolean scrapeFavouritedAuctions = Boolean.parseBoolean(System.getProperty("inputParameters.scrapeFavouritedAuctions"));
        boolean scrapeAuctionsFromInputParameter = Boolean.parseBoolean(System.getProperty("inputParameters.scrapeAuctionsFromInputParameter"));

        long start = System.currentTimeMillis();
        List<String> urlsOfAuctionsToScrape = new ArrayList<>();
        if (scrapeAllAuctions) {
            urlsOfAuctionsToScrape = crawl();
        }
        if (scrapeFavouritedAuctions) {
            urlsOfAuctionsToScrape = getAuctionsToUpdate();
        }
        if (scrapeAuctionsFromInputParameter) {
            urlsOfAuctionsToScrape = getAuctionsPartialLinksFromJsonArrayOfInputParameter();
        }
        List<AuctionData> auctionDatas = scrape(urlsOfAuctionsToScrape);
        convertArrayOfAuctionDatasToExcelFile(auctionDatas, System.getProperty("inputParameters.title"));
        sendScrappedAuctionDatas(auctionDatas);
        long stop = System.currentTimeMillis();
        log.atInfo().log("Czas:  " + (stop - start));
    }
}