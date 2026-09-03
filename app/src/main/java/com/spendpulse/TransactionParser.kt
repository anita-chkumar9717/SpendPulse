package com.spendpulse

import java.math.BigDecimal
import java.util.Locale

data class ParsedDebit(val amountPaise: Long)

object TransactionParser {
    private val amountPatterns = listOf(
        Regex("""(?:debited|debit(?:ed)?|spent|purchase|paid|payment).*?(?:INR|Rs\\.?|₹)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)""", RegexOption.IGNORE_CASE),
        Regex("""(?:INR|Rs\\.?|₹)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?).*?(?:debited|debit|spent|paid|purchase)""", RegexOption.IGNORE_CASE)
    )
    private val negative = listOf("credited","credit","refund","reversal","cashback","received","deposited","deposit","salary","inward")
    private val debit = listOf("debited","debit","spent","paid","payment","purchase","withdrawn","withdrawal")

    fun parse(body: String): ParsedDebit? {
        val normalized = body.replace('\n',' ').replace(Regex("\\s+")," ").trim().lowercase(Locale.US)
        if (negative.any { normalized.contains(it) }) return null
        if (debit.none { normalized.contains(it) }) return null
        val match = amountPatterns.firstNotNullOfOrNull { it.find(body) } ?: return null
        val amount = match.groupValues[1].replace(",","").toBigDecimalOrNull() ?: return null
        if (amount <= BigDecimal.ZERO) return null
        return ParsedDebit(amount.movePointRight(2).setScale(0).longValueExact())
    }
}
