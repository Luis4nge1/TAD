package com.optic.deliveryapp.activities.client.payments.mercado_pago.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import com.optic.deliveryapp.R
import com.optic.deliveryapp.activities.client.payments.mercado_pago.installments.ClientPaymentsInstallmentsActivity
import com.optic.deliveryapp.models.Cardholder
import com.optic.deliveryapp.models.MercadoPagoCardTokenBody
import com.optic.deliveryapp.providers.MercadoPagoProvider
import io.stormotion.creditcardflow.CardFlowState
import io.stormotion.creditcardflow.CreditCard
import io.stormotion.creditcardflow.CreditCardFlow
import io.stormotion.creditcardflow.CreditCardFlowListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientPaymentFormActivity : AppCompatActivity() {

    val TAG = "ClientPaymentForm"

    var creditCardFlow: CreditCardFlow? = null
    var cvv = ""
    var cardHolder = ""
    var cardNumber = ""
    var cardExpiration = ""

    var mercadoPagoProvider: MercadoPagoProvider = MercadoPagoProvider()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payment_form)

        creditCardFlow = findViewById(R.id.credit_card_flow)

        creditCardFlow?.setCreditCardFlowListener(object: CreditCardFlowListener{
            override fun onActiveCardNumberBeforeChangeToNext() {

            }

            override fun onActiveCardNumberBeforeChangeToPrevious() {

            }

            override fun onCardCvvBeforeChangeToNext() {

            }

            override fun onCardCvvBeforeChangeToPrevious() {

            }

            override fun onCardCvvValidatedSuccessfully(cvv: String) {

            }

            override fun onCardCvvValidationFailed(cvv: String) {

            }

            override fun onCardExpiryDateBeforeChangeToNext() {

            }

            override fun onCardExpiryDateBeforeChangeToPrevious() {

            }

            override fun onCardExpiryDateInThePast(expiryDate: String) {

            }

            override fun onCardExpiryDateValidatedSuccessfully(expiryDate: String) {

            }

            override fun onCardExpiryDateValidationFailed(expiryDate: String) {

            }

            override fun onCardHolderBeforeChangeToNext() {

            }

            override fun onCardHolderBeforeChangeToPrevious() {

            }

            override fun onCardHolderValidatedSuccessfully(cardHolder: String) {

            }

            override fun onCardHolderValidationFailed(cardholder: String) {

            }

            override fun onCardNumberValidatedSuccessfully(cardNumber: String) {

            }

            override fun onCardNumberValidationFailed(cardNumber: String) {

            }

            override fun onCreditCardFlowFinished(creditCard: CreditCard) {
                //SE OBTIENE TODA LA INFO DE LA CREDIT CARD

                cvv = creditCard.expiryDate.toString()
                cardHolder = creditCard.cvc.toString()
                cardNumber = creditCard.number.toString()
                cardExpiration = creditCard.holderName.toString()

                createCardToken()

                Log.d(TAG, "CVV: $cvv")
                Log.d(TAG, "cardHolder: $cardHolder")
                Log.d(TAG, "cardNumber: $cardNumber")
                Log.d(TAG, "cardExpiration: $cardExpiration")

            }

            override fun onFromActiveToInactiveAnimationStart() {

            }

            override fun onFromInactiveToActiveAnimationStart() {

            }

            override fun onInactiveCardNumberBeforeChangeToNext() {

            }

            override fun onInactiveCardNumberBeforeChangeToPrevious() {

            }
        })
    }

    private fun createCardToken(){

        val expiration = cardExpiration.split("/").toTypedArray()
        val month = expiration[0]
        val year = "20${expiration[1]}"

        cardNumber = cardNumber.replace(" ","")

        val ch = Cardholder(name = cardHolder)

        val mercadoPagoCardTokenBody = MercadoPagoCardTokenBody(
            securityCode = cvv,
            expirationYear = year,
            expirationMonth = month.toInt(),
            cardNumber = cardNumber,
            cardHolder = ch
        )

        mercadoPagoProvider.createCardToken(mercadoPagoCardTokenBody)?.enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(response.body() != null){
                    val cardToken = response.body()?.get("id")?.asString
                    val firstSixDigits = response.body()?.get("first_six_digits")?.asString
                    goToInstallments(cardToken!!, firstSixDigits!!)
                }

                Log.d(TAG, "Response: $response")
                Log.d(TAG, "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@ClientPaymentFormActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun goToInstallments(cardToken: String, firstSixDigits: String){
        val i = Intent( this, ClientPaymentsInstallmentsActivity::class.java)
        i.putExtra("cardToken", cardToken)
        i.putExtra("firstSixDigits", firstSixDigits)
        startActivity(i)
    }

    override fun onBackPressed() {
        if (creditCardFlow?.currentState() == CardFlowState.ACTIVE_CARD_NUMBER || creditCardFlow?.currentState() == CardFlowState.INACTIVE_CARD_NUMBER){
            finish()
        }
        else{
            creditCardFlow?.previousState()
        }
    }
}