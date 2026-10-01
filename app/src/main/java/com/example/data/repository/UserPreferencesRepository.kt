package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pocketledger_prefs", Context.MODE_PRIVATE)

    private val _currencySymbol = MutableStateFlow(prefs.getString(KEY_CURRENCY_SYMBOL, "₹") ?: "₹")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _currencyCode = MutableStateFlow(prefs.getString(KEY_CURRENCY_CODE, "INR") ?: "INR")
    val currencyCode: StateFlow<String> = _currencyCode.asStateFlow()

    private val _useIndianFormat = MutableStateFlow(prefs.getBoolean(KEY_INDIAN_FORMAT, true))
    val useIndianFormat: StateFlow<Boolean> = _useIndianFormat.asStateFlow()

    private val _startingBalance = MutableStateFlow(prefs.getFloat(KEY_STARTING_BALANCE, 0.0f).toDouble())
    val startingBalance: StateFlow<Double> = _startingBalance.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(prefs.getBoolean(KEY_PIN_ENABLED, false))
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _pinCode = MutableStateFlow(prefs.getString(KEY_PIN_CODE, "") ?: "")
    val pinCode: StateFlow<String> = _pinCode.asStateFlow()

    fun setCurrency(symbol: String, code: String) {
        prefs.edit().putString(KEY_CURRENCY_SYMBOL, symbol).putString(KEY_CURRENCY_CODE, code).apply()
        _currencySymbol.value = symbol
        _currencyCode.value = code
    }

    fun setIndianFormat(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_INDIAN_FORMAT, enabled).apply()
        _useIndianFormat.value = enabled
    }

    fun setStartingBalance(balance: Double) {
        prefs.edit().putFloat(KEY_STARTING_BALANCE, balance.toFloat()).apply()
        _startingBalance.value = balance
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
        _isOnboardingCompleted.value = true
    }

    fun setPin(pin: String) {
        prefs.edit().putString(KEY_PIN_CODE, pin).putBoolean(KEY_PIN_ENABLED, pin.isNotEmpty()).apply()
        _pinCode.value = pin
        _isPinEnabled.value = pin.isNotEmpty()
    }

    fun disablePin() {
        prefs.edit().putString(KEY_PIN_CODE, "").putBoolean(KEY_PIN_ENABLED, false).apply()
        _pinCode.value = ""
        _isPinEnabled.value = false
    }

    /**
     * Formats a financial amount according to user preference (Indian numbering system ₹1,25,000 or Western #,##0.00)
     */
    fun formatCurrency(amount: Double, includeSymbol: Boolean = true): String {
        val sym = if (includeSymbol) _currencySymbol.value else ""
        val isNegative = amount < 0
        val absAmount = kotlin.math.abs(amount)

        val formattedNumber = if (_useIndianFormat.value && _currencyCode.value == "INR") {
            formatIndianNumber(absAmount)
        } else {
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("#,##0.00", symbols)
            df.format(absAmount)
        }

        return if (isNegative) {
            "-$sym$formattedNumber"
        } else {
            "$sym$formattedNumber"
        }
    }

    private fun formatIndianNumber(amount: Double): String {
        val rounded = kotlin.math.round(amount * 100) / 100
        val longVal = rounded.toLong()
        val decimalPart = String.format(Locale.US, "%02d", kotlin.math.round((rounded - longVal) * 100).toInt())

        val str = longVal.toString()
        if (str.length <= 3) {
            return "$str.$decimalPart"
        }

        val lastThree = str.substring(str.length - 3)
        var remaining = str.substring(0, str.length - 3)
        val sb = StringBuilder()

        while (remaining.length > 2) {
            val part = remaining.substring(remaining.length - 2)
            sb.insert(0, ",$part")
            remaining = remaining.substring(0, remaining.length - 2)
        }
        if (remaining.isNotEmpty()) {
            sb.insert(0, "$remaining")
        }

        return "$sb,$lastThree.$decimalPart"
    }

    companion object {
        private const val KEY_CURRENCY_SYMBOL = "key_currency_symbol"
        private const val KEY_CURRENCY_CODE = "key_currency_code"
        private const val KEY_INDIAN_FORMAT = "key_indian_format"
        private const val KEY_STARTING_BALANCE = "key_starting_balance"
        private const val KEY_ONBOARDING_DONE = "key_onboarding_done"
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_PIN_CODE = "key_pin_code"
    }
}
