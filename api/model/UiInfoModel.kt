package com.buisness.bonuscards.api.model

data class UiInfoModel(
    val mainLogo: String = "",
    val signInBoldText: String = "Войдите или зарегистрируйтесь",
    val signInAdditionalText: String = "Чтобы пользоваться персональными скидками и бонусами",
    val signInCouldNotConnectText: String = "Не можете войти?  Связаться с нами",
    val signInUserAgreementURL: String = "",
    val menuMainLink: String = "",
    val menuCatalogueLink: String = "",
    val menuCartLink: String = "",
    val menuMenuLink: String = "",
    val bonusProgramLink: String = "",
    val userAgreementLink: String = "",
    val contactsLink: String = "",
    val shopsLink: String = "",
    val smallCardLogo: String = "",
    val bonusProgramText: String = "",
    val userAgreementText: String = ""
)
