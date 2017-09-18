package com.ubiqsmart.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.ubiqsmart.R;
import com.ubiqsmart.ui.detail.AddressDetailActivity;
import com.ubiqsmart.repository.data.TransactionDisplay;
import com.ubiqsmart.network.EtherscanAPI;
import com.ubiqsmart.utils.RequestCache;
import com.ubiqsmart.utils.ResponseParser;

import java.io.*;
import java.util.*;

import static android.view.View.GONE;

public class TransactionsFragment extends TransactionsAbstractFragment {

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = super.onCreateView(inflater, container, savedInstanceState);
    send.setVisibility(GONE);
    requestTx.setVisibility(GONE);
    fabmenu.setVisibility(View.GONE);
    return rootView;
  }

  public void update(boolean force) {
    if (ac == null) return;
    resetRequestCount();
    getWallets().clear();
    if (swipeLayout != null) swipeLayout.setRefreshing(true);

    try {
      EtherscanAPI.getInstance().getNormalTransactions(address, new Callback() {
        @Override public void onFailure(Call call, IOException e) {
          if (isAdded()) {
            ac.runOnUiThread(new Runnable() {
              @Override public void run() {
                onItemsLoadComplete();
                ((AddressDetailActivity) ac).snackError(getString(R.string.err_no_con));
              }
            });
          }
        }

        @Override public void onResponse(Call call, Response response) throws IOException {
          String restring = response.body().string();
          if (restring != null && restring.length() > 2) RequestCache.getInstance().put(RequestCache.TYPE_TXS_NORMAL, address, restring);
          final List<TransactionDisplay> w =
              new ArrayList<TransactionDisplay>(ResponseParser.parseTransactions(restring, "Unnamed Address", address, TransactionDisplay.NORMAL));
          if (isAdded()) {
            ac.runOnUiThread(new Runnable() {
              @Override public void run() {
                onComplete(w);
              }
            });
          }
        }
      }, force);
      EtherscanAPI.getInstance().getInternalTransactions(address, new Callback() {
        @Override public void onFailure(Call call, IOException e) {
          if (isAdded()) {
            ac.runOnUiThread(new Runnable() {
              @Override public void run() {
                onItemsLoadComplete();
                ((AddressDetailActivity) ac).snackError(getString(R.string.err_no_con));
              }
            });
          }
        }

        @Override public void onResponse(Call call, Response response) throws IOException {
          String restring = response.body().string();
          if (restring != null && restring.length() > 2) RequestCache.getInstance().put(RequestCache.TYPE_TXS_INTERNAL, address, restring);
          final List<TransactionDisplay> w =
              new ArrayList<TransactionDisplay>(ResponseParser.parseTransactions(restring, "Unnamed Address", address, TransactionDisplay.CONTRACT));
          if (isAdded()) {
            ac.runOnUiThread(new Runnable() {
              @Override public void run() {
                onComplete(w);
              }
            });
          }
        }
      }, force);
    } catch (IOException e) {
      if (ac != null) ((AddressDetailActivity) ac).snackError("Can't fetch account balances. No connection?");
      onItemsLoadComplete();
      e.printStackTrace();
    }
  }

  private void onComplete(List<TransactionDisplay> w) {
    addToWallets(w);
    addRequestCount();
    if (getRequestCount() >= 2) {
      onItemsLoadComplete();
      nothingToShow.setVisibility(wallets.size() == 0 ? View.VISIBLE : View.GONE);
      walletAdapter.notifyDataSetChanged();
    }
  }

}