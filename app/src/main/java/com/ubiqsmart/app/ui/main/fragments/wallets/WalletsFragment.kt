package com.ubiqsmart.app.ui.main.fragments.wallets

import com.ubiqsmart.app.ui.base.BaseFragment

@Deprecated("Moving logic to WalletsFragment for easy refactoring")
class WalletsFragment : BaseFragment() {//, View.OnClickListener, View.OnCreateContextMenuListener {

//  private val preferences: SharedPreferences by instance()
//
//  private val exchangeCalculator: ExchangeCalculator by instance()
//  private val etherscanApi: EtherscanAPI by instance()
//  private val addressNameConverter: AddressNameConverter by instance()
//  private val walletStorage: WalletStorage by instance()
//
//  private var wallets = ArrayList<WalletAdapter>()
//  private var walletAdapter: WalletAdapter? = null
//
//  private var ac: MainActivity? = null
//
//  private var balance = 0.0
//
//  val displayedWalletCount: Int
//    get() = wallets.size
//
//  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//    return inflater!!.inflate(R.layout.fragment_wallets, container, false)
//  }
//
//  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//    ac = this.activity as MainActivity
//
//    walletAdapter = WalletAdapter(wallets, activity, this, this)
//
//    swipe_refresh_layout?.setColorSchemeColors(ContextCompat.getColor(context, R.color.primary))
//    swipe_refresh_layout?.setOnRefreshListener {
//      balance = 0.0
//      try {
//        update()
//      } catch (e: IOException) {
//        if (ac != null) {
//          ac!!.snackError("Can't fetch account balances. No connection?")
//        }
//        e.printStackTrace()
//      }
//    }
//
//    exchangeCalculator.index = preferences.getInt("main_index", 0)
//
//    left_arrow?.setOnClickListener {
//      val (name, rate) = exchangeCalculator.previous()
//      balance_view.text = String.format("%s %s", exchangeCalculator.displayBalanceNicely(exchangeCalculator.convertRate(balance, rate)), name)
//      ac!!.broadCastDataSetChanged()
//      walletAdapter?.notifyDataSetChanged()
//      preferences.edit().putInt("main_index", exchangeCalculator.index).apply()
//    }
//
//    right_arrow?.setOnClickListener {
//      val (name, rate) = exchangeCalculator.next()
//      balance_view.text = String.format("%s %s", exchangeCalculator.displayBalanceNicely(exchangeCalculator.convertRate(balance, rate)), name)
//      ac!!.broadCastDataSetChanged()
//      walletAdapter?.notifyDataSetChanged()
//      preferences.edit().putInt("main_index", exchangeCalculator.index).apply()
//    }
//
//    val mgr = LinearLayoutManager(activity)
//    recycler_view.layoutManager = mgr
//    recycler_view.itemAnimator = DefaultItemAnimator()
//    recycler_view.adapter = walletAdapter
//
//    val dividerItemDecoration = DividerItemDecoration(context, mgr.orientation)
//    recycler_view.addItemDecoration(dividerItemDecoration)
//
//    walletAdapter?.notifyDataSetChanged()
//
//    gen_fab.setOnClickListener { generateDialog() }
//
//    scan_fab.setOnClickListener {
//      val scanQR = Intent(context, QRScanActivity::class.java).apply {
//        putExtra("TYPE", QRScanActivity.SCAN_ONLY)
//      }
//      activity.startActivityForResult(scanQR, QRScanActivity.REQUEST_CODE)
//    }
//
//    add_fab.setOnClickListener { DialogFactory.addWatchOnly(ac, walletStorage) }
//
//    import_fab.setOnClickListener {
//      try {
//        walletStorage.importingWalletsDetector(activity as MainActivity)
//      } catch (e: Exception) {
//        e.printStackTrace()
//      }
//    }
//
//    //if (ac != null && ac.getAppBar() != null) {
//    //  ac.getAppBar().addOnOffsetChangedListener(new AppBarStateChangeListener() {
//    //    @Override public void onStateChanged(AppBarLayout appBarLayout, State state) {
//    //      if (state == State.COLLAPSED) {
//    //        fabmenu.hideMenu(true);
//    //      } else {
//    //        fabmenu.showMenu(true);
//    //      }
//    //    }
//    //  });
//    //}
//
//    try {
//      update()
//    } catch (e: IOException) {
//      ac?.snackError("Can't fetch account balances. No connection?")
//    }
//  }
//
//  @Throws(IOException::class)
//  fun update() {
//    if (ac == null) {
//      return
//    }
//
//    wallets.clear()
//    balance = 0.0
//
//    val storedWallets = ArrayList(walletStorage.get())
//
//    if (storedWallets.isEmpty()) {
//      nothing_found.visibility = View.VISIBLE
//      onItemsLoadComplete()
//    } else {
//      nothing_found.visibility = View.GONE
//      etherscanApi.getBalances(storedWallets, object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//          ac?.snackError("Can't fetch account balances. Invalid response.")
//
//          val w = storedWallets.map { WalletAdapter(addressNameConverter.get(it.pubKey), it.pubKey, BigInteger("-1"), WalletAdapter.CONTACT) }
//
//          ac?.runOnUiThread {
//            wallets.addAll(w)
//            walletAdapter?.notifyDataSetChanged()
//            onItemsLoadComplete()
//          }
//        }
//
//        @Throws(IOException::class)
//        override fun onResponse(call: Call, response: Response) {
//          val w: List<WalletAdapter>
//          try {
//            w = ResponseParser.parseWallets(response.body()!!.string(), storedWallets, ac)
//          } catch (e: Exception) {
//            e.printStackTrace()
//            return
//          }
//
//          ac?.runOnUiThread {
//            wallets.addAll(w)
//            walletAdapter?.notifyDataSetChanged()
//            wallets.indices.forEach { i -> balance += wallets[i].balance }
//            balance_view.text = String.format("%s %s",
//                exchangeCalculator.displayBalanceNicely(exchangeCalculator.convertRate(balance, exchangeCalculator.current.rate)),
//                exchangeCalculator.current.name)
//            onItemsLoadComplete()
//          }
//        }
//      })
//    }
//  }
//
//  override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
//    menu.setHeaderTitle(R.string.wallet_menu_title)
//    menu.add(0, 200, 0, R.string.wallet_menu_changename)
//    menu.add(0, 201, 0, R.string.wallet_menu_copyadd)
//    menu.add(0, 202, 0, R.string.wallet_menu_share)
//    menu.add(0, 203, 0, R.string.wallet_menu_export)
//    menu.add(0, 204, 0, R.string.wallet_menu_delete)
//  }
//
//  override fun onContextItemSelected(item: MenuItem?): Boolean {
//    val position: Int
//    try {
//      position = walletAdapter?.position ?: 0
//    } catch (e: Exception) {
//      e.printStackTrace()
//      return super.onContextItemSelected(item)
//    }
//
//    when (item?.itemId) {
//      200 -> setName(wallets[position].publicKey) // Change address name
//
//      201 -> {
//        if (ac == null) {
//          return true
//        }
//        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip = ClipData.newPlainText("", wallets[position].publicKey)
//        clipboard.primaryClip = clip
//
//        Toast.makeText(ac, R.string.wallet_menu_action_copied_to_clipboard, Toast.LENGTH_SHORT).show()
//      }
//
//      202 -> {
//        val i = Intent(android.content.Intent.ACTION_SEND).apply {
//          type = "text/plain"
//          putExtra(Intent.EXTRA_TEXT, wallets[position].publicKey)
//        }
//
//        startActivity(Intent.createChooser(i, getString(R.string.share_via)))
//      }
//
//      203 -> {
//        if (wallets[position].type == WalletAdapter.NORMAL) {
//          DialogFactory.exportWallet(activity) { dialog, _ ->
//            walletStorage.setWalletForExport(wallets[position].publicKey)
//            export()
//            dialog.dismiss()
//          }
//        } else {
//          DialogFactory.cantExportNonWallet(activity)
//        }
//      }
//
//      204 -> confirmDelete(wallets[position].publicKey, wallets[position].type)
//    }
//
//    return super.onContextItemSelected(item)
//  }
//
//  fun export() {
//    val suc = walletStorage.exportWallet(activity)
//    ac?.snackError(if (suc) getString(R.string.wallet_suc_exported) else getString(R.string.wallet_no_permission))
//  }
//
//  fun generateDialog() {
//    if (!Settings.walletBeingGenerated) {
//      val intent = Intent(ac, WalletGenActivity::class.java)
//      activity.startActivityForResult(intent, WalletGenActivity.REQUEST_CODE)
//    } else {
//      val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= 24) AlertDialog.Builder(activity, R.style.Ubiq_Dialog_Alert) else AlertDialog.Builder(activity)
//      builder.setTitle(R.string.wallet_one_at_a_time)
//      builder.setMessage(R.string.wallet_one_at_a_time_text)
//      builder.setNeutralButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }
//      builder.show()
//    }
//  }
//
//  fun confirmDelete(address: String, type: Byte) {
//    val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= 24) AlertDialog.Builder(activity, R.style.Ubiq_Dialog_Alert) else AlertDialog.Builder(activity)
//    builder.setTitle(R.string.wallet_removal_title)
//
//    when (type) {
//      WalletAdapter.WATCH_ONLY -> builder.setMessage(R.string.wallet_removal_sure)
//      WalletAdapter.NORMAL -> builder.setMessage(getString(R.string.wallet_removal_privkey) + address)
//      java.lang.Byte.MAX_VALUE -> builder.setMessage(getString(R.string.wallet_removal_last_warning) + address)
//    }
//
//    builder.setPositiveButton(R.string.button_yes) { dialog, _ ->
//      when (type) {
//        WalletAdapter.WATCH_ONLY, java.lang.Byte.MAX_VALUE -> {
//          walletStorage.removeWallet(address, ac)
//          dialog.dismiss()
//          try {
//            update()
//          } catch (e: IOException) {
//            e.printStackTrace()
//          }
//
//        }
//        else -> confirmDelete(address, java.lang.Byte.MAX_VALUE)
//      }
//    }
//    builder.setNegativeButton(R.string.button_no) { dialog, _ -> dialog.dismiss() }
//    builder.show()
//  }
//
//  fun setName(address: String) {
//    val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= 24) {
//      AlertDialog.Builder(activity, R.style.Ubiq_Dialog_Alert)
//    } else {
//      AlertDialog.Builder(activity)
//    }
//    builder.setTitle(R.string.name_your_wallet)
//
//    val input = EditText(activity)
//    input.setText(addressNameConverter.get(address))
//    input.setSingleLine()
//
//    val container = FrameLayout(activity)
//    val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//    params.topMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//    params.bottomMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//    params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//    input.layoutParams = params
//    input.setSelection(input.text.length)
//
//    input.inputType = InputType.TYPE_CLASS_TEXT
//    container.addView(input)
//
//    builder.setView(container)
//
//    input.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
//      if (hasFocus) {
//        val inputMgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
//      }
//    }
//
//    builder.setPositiveButton(R.string.button_ok) { dialog, _ ->
//      addressNameConverter.put(address, input.text.toString())
//      val inputMgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//      inputMgr.hideSoftInputFromWindow(input.windowToken, 0)
//      notifyDataSetChanged()
//      dialog.dismiss()
//    }
//    builder.setNegativeButton(R.string.button_cancel) { dialog, _ ->
//      val inputMgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//      inputMgr.hideSoftInputFromWindow(input.windowToken, 0)
//      dialog.cancel()
//    }
//
//    builder.show()
//  }
//
//  internal fun onItemsLoadComplete() {
//    swipe_refresh_layout?.isRefreshing = false
//  }
//
//  fun notifyDataSetChanged() {
//    walletAdapter?.apply {
//      notifyDataSetChanged()
//      updateBalanceText()
//    }
//  }
//
//  fun updateBalanceText() {
//    balance_view?.text = String.format("%s %s", exchangeCalculator.displayBalanceNicely(exchangeCalculator.convertRate(balance, exchangeCalculator.current.rate)), exchangeCalculator.current.name)
//  }
//
//  override fun onClick(view: View) {
//    val itemPosition = recycler_view.getChildLayoutPosition(view)
//    if (itemPosition >= wallets.size) {
//      return
//    }
//
//    val detail = Intent(ac, AddressDetailActivity::class.java).apply {
//      putExtra("ADDRESS", wallets[itemPosition].publicKey)
//      putExtra("BALANCE", wallets[itemPosition].balance)
//      putExtra("TYPE", AddressDetailActivity.OWN_WALLET)
//    }
//    startActivity(detail)
//  }
}