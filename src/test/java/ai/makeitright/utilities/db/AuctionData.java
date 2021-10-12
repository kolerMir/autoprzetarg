package ai.makeitright.utilities.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "auctions")
public class AuctionData {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String marka;
    @DatabaseField
    private String model;
    @DatabaseField
    private String rokProdukcji;
    @DatabaseField
    private String numerRejestracyjny;
    @DatabaseField
    private String vin;
    @DatabaseField
    private String rodzajPaliwa;
    @DatabaseField
    private String klasaEuro;
    @DatabaseField
    private String kluczyki;
    @DatabaseField
    private String dowodRejestracyjny;
    @DatabaseField
    private String kartaPojazdu;
    @DatabaseField
    private Long przebieg;
    @DatabaseField
    private Long cena;
    @DatabaseField
    private String pdfUrl;
    @DatabaseField
    private String wyposazenie;
    @DatabaseField
    private Timestamp dataWyszukania;
    @DatabaseField
    private Timestamp doKoncaAukcji;
    @DatabaseField
    private String zrodlo;
    @DatabaseField
    private String typAukcji;

    public static AuctionData crateAuctionDataObjectFromJSoupDocument(final Document document, final String urlOfAuction) throws ParseException {
        Element divAC = document.selectFirst("#auction");
        AuctionData ad = new AuctionData();

        ad.setId(urlOfAuction);

        ad.setMarka(divAC.selectFirst("div.col-lg-5:contains(Marka:)").nextElementSibling().selectFirst("p").ownText());
        ad.setModel(divAC.selectFirst("div.col-lg-5:contains(Model:)").nextElementSibling().selectFirst("p").ownText());

        if (divAC.selectFirst("div.col-lg-5:contains(Rocznik:)") != null) {
            ad.setRokProdukcji(divAC.selectFirst("div.col-lg-5:contains(Rocznik:)").nextElementSibling().selectFirst("p").ownText());
        }

        if (divAC.selectFirst("div.col-lg-5:contains(Numer rejestracyjny:)") != null) {
            ad.setNumerRejestracyjny(divAC.selectFirst("div.col-lg-5:contains(Numer rejestracyjny:)").nextElementSibling().selectFirst("p").ownText());
        } else {
            ad.setNumerRejestracyjny("");
        }

        ad.setVin("");

        ad.setRodzajPaliwa("");

        ad.setKlasaEuro("");

        ad.setKluczyki("");

        ad.setDowodRejestracyjny("");

        ad.setKartaPojazdu("");

        if (divAC.selectFirst("div.col-lg-5:contains(Przebieg:)") != null) {
            String przebieg = divAC.selectFirst("div.col-lg-5:contains(Przebieg:)").nextElementSibling().selectFirst("p").ownText();
            ad.setPrzebieg(Long.valueOf(przebieg.replaceAll("km","").replaceAll(" ", "")));
        } else {
            ad.setPrzebieg(0L);
        }

        String cenaFromPage = divAC.selectFirst("div.group-title-text.auction-tile-value.auction-details-title-value:contains(Aktualna cena:)").selectFirst("p").ownText();
        String cena = cenaFromPage.replaceAll("\\s", "").substring(0, cenaFromPage.indexOf(44));
        ad.setCena(Long.valueOf(cena));

        ad.setPdfUrl("");

        ad.setWyposazenie("");

        Timestamp dataWyszukaniaTimestamp = new Timestamp(System.currentTimeMillis());
        ad.setDataWyszukania(dataWyszukaniaTimestamp);

        if (divAC.selectFirst("div.group-title-text.auction-details-inline.auction-details-value:contains(Data zako)") != null) {
            String doKoncaAukcji = divAC.selectFirst("div.group-title-text.auction-details-inline.auction-details-value:contains(Data zako)").selectFirst("#auctionEndDate").ownText();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(doKoncaAukcji);
            Timestamp koniecAukcjiTimestamp = new Timestamp(parsedDate.getTime());
            ad.setDoKoncaAukcji(koniecAukcjiTimestamp);
        } else {
            ad.setDoKoncaAukcji(new Timestamp(0L));
        }

        ad.setZrodlo(System.getProperty("inputParameters.title"));

        ad.setTypAukcji("licytacja");

        return ad;
    }
}