package ai.makeitright.utilities.scraper;

import ai.makeitright.utilities.db.AuctionData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ai.makeitright.utilities.db.AuctionData.crateAuctionDataObjectFromJSoupDocument;

@Slf4j
public final class Scraper {

    public static List<AuctionData> scrape(final List<String> urlsOfAllAuctions) throws InterruptedException, IOException, ParseException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        log.atInfo().log("Quantity of urls of all auctions: " + urlsOfAllAuctions.size());
        List<AuctionData> auctionDatas = new ArrayList<>();
        for (String urlOfAuction : urlsOfAllAuctions) {
            log.atInfo().log("Downloading url: " + urlOfAuction);
            Thread.sleep(500);
            String auctionDocumentAsString;
            try (CloseableHttpResponse responseFromSpecificAuction = getClosableHttpClient().execute(new HttpGet(urlOfAuction))) {
                HttpEntity httpEntity = responseFromSpecificAuction.getEntity();
                auctionDocumentAsString = EntityUtils.toString(httpEntity, "UTF-8");
            }
            Document document = Jsoup.parse(auctionDocumentAsString);
            AuctionData ad = crateAuctionDataObjectFromJSoupDocument(document, urlOfAuction);
            auctionDatas.add(ad);
        }
        return auctionDatas;
    }

    private static CloseableHttpClient getClosableHttpClient() throws
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        BasicCookieStore cookieStore = new BasicCookieStore();
        return HttpClients.custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCookieStore(cookieStore)
                .build();
    }

}