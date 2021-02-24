package com.partem.application.enums

enum class Endpoints(val endpoint: String) {
    FIREBASE_TOKEN_ENDPOINT("https://ourapp.live/firebase_token"),
    LOGIN_ENDPOINT("https://ourapp.live/login"),
    REGISTER_ENDPOINT("https://ourapp.live/register"),
    CLIENT_TOKEN_ENDPOINT("https://ourapp.live/client_token"),
    CHECKOUT_ENDPOINT("https://ourapp.live/checkout"),
    PROFILE_PIC_ENDPOINT("https://ourapp.live/upload"),
    GROUP_INVITATION_ENDPOINT("https://ourapp.live/group_invite"),
    GROUP_INVITATION_REPLY("https://ourapp.live/reply_group_inv"),
    USER_GROUP_LIST("https://ourapp.live/get_group_list"),
    ADD_GROUP_CHARGE("https://ourapp.live/add_group_charge"),
    PAY_GROUP_CHARGE("https://ourapp.live/pay_group_charge")
}