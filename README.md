# SpendPulse V1

Mobile-only Android prototype: detect debit bank SMS, store only debit transactions locally, sum the current calendar month's debits, and show the result in a Home Screen widget.

## Run
1. Install Android Studio.
2. Open this folder.
3. Let Gradle sync.
4. Connect an Android phone (USB debugging enabled) and Run `app`.
5. Grant SMS access.
6. Tap **Scan existing SMS** once to import qualifying historical debit messages.
7. Long-press the Android Home Screen -> Widgets -> SpendPulse -> place it.
8. Receive a qualifying debit SMS; the widget refreshes automatically.

## Important V1 behavior
- Debit only.
- Credits, refunds, cashback, salary and incoming transfers are ignored.
- Transactions are stored locally; SMS text is not uploaded anywhere.
- Duplicate SMS events are protected with a SHA-256 fingerprint + DB unique constraint.
- The total is based on the phone's current calendar month.
- Bank SMS formats differ; `TransactionParser.kt` is intentionally conservative and should be extended/tested with your bank's exact message formats.

## Example
`Your A/c XXXX1234 is debited by INR 1,250 towards AMAZON.` -> adds ₹1,250.

`Your A/c XXXX1234 is credited by INR 50,000.` -> ignored.

## Distribution note
SMS permissions are sensitive Android/Google Play permissions. This V1 is intended for local device testing/sideloading; Play Store distribution would require compliance with the applicable current SMS permission policy.
