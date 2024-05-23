package com.msprysak.rentersapp.data.model

import java.util.Date

data class Invoice(
    var issuePlace: String? = null,
    var paymentMethod: String? = null,
    var sellerName: String? = null,
    var serviceName: String? = null,
    var nettoPrice: String? = null,
    var comments: String? = null,
    var sellDate: Date? = null,
    var nettoPriceTogether: Double? = null,
    var nettoPriceIn: Double? = null,
    var bruttoPrice: Double? = null,
    var bruttoPriceIn: Double? = null,
    var bruttoPriceTogether: Double? = null,
    var issueDate: String? = null,
    var paymentValue: Double? = null,
    var paymentValueInWords: String? = null,
    var sellerNip: String? = null,
    var sellerStreet: String? = null,
    var sellerPostalCode: String? = null,
    var sellerCity: String? = null,
    var buyerName: String? = null,
    var buyerPesel: String? = null,
    var buyerStreet: String? = null,
    var buyerPostalCode: String? = null,
    var buyerCity: String? = null,
    var invoiceName: String? = null,
    var sellerNameAndSurname: String? = null

)
