package rehanced.com.simpleetherwallet.utils;


import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import rehanced.com.simpleetherwallet.data.CurrencyEntry;
import rehanced.com.simpleetherwallet.interfaces.NetworkUpdateListener;
import rehanced.com.simpleetherwallet.network.EtherscanAPI;

public class ExchangeCalculator {

    private static ExchangeCalculator instance;
    private long lastUpdateTimestamp = 0;
    private double rateForChartDisplay = 1;

    private ExchangeCalculator(){}

    public static ExchangeCalculator getInstance(){
        if(instance == null)
            instance = new ExchangeCalculator();
        return instance;
    }

    private CurrencyEntry[] conversionNames = new CurrencyEntry []{
            new CurrencyEntry("ETH", 1, "Ξ"),
            new CurrencyEntry("BTC", 0.07, "฿"),
            new CurrencyEntry("USD", 0, "$")
    };

    private int index = 0;

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public double getRateForChartDisplay(){
        return rateForChartDisplay;
    }

    public CurrencyEntry next(){
        index = (index + 1) % conversionNames.length;
        return conversionNames[index];
    }

    public CurrencyEntry getCurrent(){
        return conversionNames[index];
    }

    public CurrencyEntry previous(){
        index = index > 0 ? index - 1 : conversionNames.length - 1;
        return conversionNames[index];
    }

    public CurrencyEntry getMainCurreny(){
        return conversionNames[2];
    }

    public String getCurrencyShort(){
        return conversionNames[index].getShorty();
    }

    public String displayBalanceNicely(double d){
        if(d == (long) d && index == 2)
            return String.format(Locale.US, "%d",(long)d);
        else
            return String.format(Locale.US, "%s",d);
    }

    public double convertRate(double balance, double rate){
        if(index == 2) {
            if (balance * rate > 100000) // dont display cents if bigger than 100k
                return (int)Math.floor(balance * rate);
            return Math.floor(balance * rate * 100) / 100;
        } else
            return Math.floor(balance * rate * 1000) / 1000;
    }

    public String convertRateExact(BigDecimal balance, double rate){
        if(index == 2) {
            return (Math.floor(balance.doubleValue() * rate * 100) / 100) + "";
        } else
            return balance.multiply(new BigDecimal(rate)).setScale(7, RoundingMode.CEILING).toPlainString();
    }

    public double convertToUsd(double balance){
        return Math.floor(balance * getUSDPrice() * 100) / 100;
    }

    public double getUSDPrice(){
        return Math.floor(conversionNames[2].getRate() * 100) / 100;
    }

    public double getBTCPrice(){
        return Math.floor(conversionNames[1].getRate() * 10000) / 10000;
    }

    public void updateExchangeRates(final String currency, final NetworkUpdateListener update) throws IOException {
        if(lastUpdateTimestamp + 40*60*1000 > System.currentTimeMillis() && currency.equals(conversionNames[2].getName())){ // Dont refresh if not older than 40 min and currency hasnt changed
            return;
        }
        if(! currency.equals(conversionNames[2].getName())){
            conversionNames[2].setName(currency);
            if(currency.equals("USD"))
                conversionNames[2].setShorty("$");
            else if(currency.equals("EUR"))
                conversionNames[2].setShorty("€");
            else if(currency.equals("GPB"))
                conversionNames[2].setShorty("£");
            else if(currency.equals("AUD"))
                conversionNames[2].setShorty("$");
            else if(currency.equals("RUB"))
                conversionNames[2].setShorty("р");
            else if(currency.equals("CHF"))
                conversionNames[2].setShorty("Fr");
            else if(currency.equals("CAD"))
                conversionNames[2].setShorty("$");
            else if(currency.equals("JPY"))
                conversionNames[2].setShorty("¥");

            else
                conversionNames[2].setShorty(currency);
        }

        //Log.d("updateingn", "Initialize price update");
        EtherscanAPI.getInstance().getEtherPrice(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {}

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject data = new JSONObject(response.body().string()).getJSONObject("result");

                    conversionNames[1].setRate(data.getDouble("ethbtc"));
                    conversionNames[2].setRate(data.getDouble("ethusd"));
                    if(!currency.equals("USD"))
                        convert(currency, update);
                    else
                        update.onUpdate(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void convert(final String currency, final NetworkUpdateListener update) throws IOException {
        EtherscanAPI.getInstance().getPriceConversionRates("USD"+currency, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {}

            @Override
            public void onResponse(Response response) throws IOException {

                rateForChartDisplay = ResponseParser.parsePriceConversionRate(response.body().string());
                conversionNames[2].setRate(Math.floor(conversionNames[2].getRate() * rateForChartDisplay * 100) / 100);
                update.onUpdate(response);
            }});
    }

}
