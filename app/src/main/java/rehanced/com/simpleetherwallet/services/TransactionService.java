package rehanced.com.simpleetherwallet.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.request.RawTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import rehanced.com.simpleetherwallet.R;
import rehanced.com.simpleetherwallet.activities.MainActivity;
import rehanced.com.simpleetherwallet.network.EtherscanAPI;
import rehanced.com.simpleetherwallet.utils.WalletStorage;

public class TransactionService extends IntentService {

    private NotificationCompat.Builder builder;
    final int mNotificationId = 153;

    public TransactionService() {
        super("Transaction Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification();
        try {
            String fromAddress = intent.getStringExtra("FROM_ADDRESS");
            final String toAddress = intent.getStringExtra("TO_ADDRESS");
            final String amount = intent.getStringExtra("AMOUNT");
            final String gas_price = intent.getStringExtra("GAS_PRICE");
            final String gas_limit = intent.getStringExtra("GAS_LIMIT");
            String password = intent.getStringExtra("PASSWORD");

            final Credentials keys = WalletStorage.getInstance(getApplicationContext()).getFullWallet(getApplicationContext(), password, fromAddress);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final long currentNonceForAddress = preferences.getLong("NONCE"+fromAddress, -1) + 1 ; // -1 weil 0 auch gültig ist, sprich keine Transaktion und im nächsten schritt +1 = 0 CHECK;

            EtherscanAPI.getInstance().getNonceForAddress(fromAddress, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    error("Can't connect to network, retry it later");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        JSONObject o = new JSONObject(response.body().string());
                        BigInteger nonce = new BigInteger(o.getString("result").substring(2), 16);

                        RawTransaction tx = RawTransaction.createEtherTransaction(
                                nonce,
                                new BigInteger(gas_price),
                                new BigInteger(gas_limit),
                                toAddress,
                                new BigDecimal(amount).multiply(new BigDecimal("1000000000000000000")).toBigInteger()
                        );

                        Log.d("txx",
                                "Nonce: "+tx.getNonce()+"\n"+
                                        "gasPrice: "+tx.getGasPrice()+"\n"+
                                        "gasLimit: "+tx.getGasLimit()+"\n"+
                                        "To: "+tx.getTo()+"\n"+
                                        "Amount: "+tx.getValue()
                        );

                        byte [] signed = TransactionEncoder.signMessage(tx, (byte) 1, keys);


                        forwardTX(signed);

                    } catch(Exception e){
                        e.printStackTrace();
                        error("Can't connect to network, retry it later");
                    }
                }
            });

        } catch (Exception e) {
            error("Invalid Wallet Password!");
            e.printStackTrace();
        }
    }

    private void forwardTX(byte [] signed) throws IOException {
        EtherscanAPI.getInstance().forwardTransaction("0x" + Hex.toHexString(signed), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                error("Can't connect to network, retry it later");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try{
                    suc(new JSONObject(response.body().string()).getString("result"));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void suc(String hash){
        builder
                .setContentTitle("Transfer successfull")
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText("");

        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("STARTAT", 2);
        main.putExtra("TXHASH", hash);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void error(String err){
        builder
                .setContentTitle("Transfer Failed")
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText(err);

        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("STARTAT", 2);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void sendNotification(){
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(0x2d435c)
                .setTicker("Transfering Ether...")
                .setContentTitle("Transfering Ether")
                .setContentText("This might take a minute")
                .setOngoing(true)
                .setProgress(0, 0, true);
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

}
