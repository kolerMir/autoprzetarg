package ai.makeitright.utilities.crawler;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public final class Crawler {

    public static List<String> crawl() throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {
        String uriString = System.getProperty("inputParameters.startPage");
        URI uRI = new URI(uriString);
        String startPageAsString;
        try (CloseableHttpResponse response0 = getClosableHttpClient().execute(new HttpGet(uRI.toString()))) {
            HttpEntity entity = response0.getEntity();
            startPageAsString = EntityUtils.toString(entity, "UTF-8");
            log.atInfo().log("Status of GET to: " + uRI.toString() + " is " + response0.getStatusLine());
        }
        Document mainPageDocument = Jsoup.parse(startPageAsString);
        String aInService = "div.auction-list-details a";
        Elements asFrom1stPage = mainPageDocument.select(aInService);
        Elements aSoFPaging = mainPageDocument.select("ul.pagination li:not(.active) a");
        Set<String> hrefsOfPaging = new HashSet<>();
        for (Element a : aSoFPaging) {
            String href = a.attr("href");
            hrefsOfPaging.add(href);
        }
        List<String> urlsOfSpecificAuctions = new ArrayList<>();
        List<String> hrefsOfAllAuctions = new ArrayList<>();
        for (Element a : asFrom1stPage) {
            hrefsOfAllAuctions.add(a.attr("href"));
        }
        for (String hrefOfPaging : hrefsOfPaging) {
            Thread.sleep(300);
            String uriForHttpGet = System.getProperty("inputParameters.prefix") + hrefOfPaging;
            log.atInfo().log("Going to: " + uriForHttpGet);
            String subpageDocumentAsString;
            try (CloseableHttpResponse response1 = getClosableHttpClient().execute(new HttpGet(uriForHttpGet))) {
                log.atInfo().log("Getting href values for auctions from " + hrefOfPaging + " page of search results");
                HttpEntity httpEntity = response1.getEntity();
                subpageDocumentAsString = EntityUtils.toString(httpEntity, "UTF-8");
            }
            Document document = Jsoup.parse(subpageDocumentAsString);
            Elements asFrom2ndPageAndSoOn = document.select(aInService);
            for (Element a : asFrom2ndPageAndSoOn) {
                hrefsOfAllAuctions.add(a.attr("href"));
            }
        }
        for (String hrefOfAuction : hrefsOfAllAuctions) {
            urlsOfSpecificAuctions.add(System.getProperty("inputParameters.prefix") + hrefOfAuction);
        }
        return urlsOfSpecificAuctions;
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