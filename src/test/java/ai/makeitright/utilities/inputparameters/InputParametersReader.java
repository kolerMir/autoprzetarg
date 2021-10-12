package ai.makeitright.utilities.inputparameters;

import java.util.ArrayList;
import java.util.List;

public class InputParametersReader {

    public static List<String> getAuctionsPartialLinksFromJsonArrayOfInputParameter() {
        List<String> urlsOfFavouritedAuctions = new ArrayList<>();

        String auctions = System.getProperty("inputParameters.endingAuctions");
        String[] str = auctions.split(";");
        for (String s : str) {
            urlsOfFavouritedAuctions.add(System.getProperty("inputParameters.prefix") + s);
        }
        return urlsOfFavouritedAuctions;
    }
}