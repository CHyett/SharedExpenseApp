package com.example.sharedexpenseapp.enums

enum class Endpoints(val endpoint: String) {
    FIREBASE_TOKEN_ENDPOINT("https://ourapp.live/firebase_token"),
    LOGIN_ENDPOINT("https://ourapp.live/login"),
    REGISTER_ENDPOINT("https://ourapp.live/register"),
    CLIENT_TOKEN_ENDPOINT("https://ourapp.live/client_token"),
    CHECKOUT_ENDPOINT("https://ourapp.live/checkout"),
    PROFILE_PIC_ENDPOINT("https://ourapp.live/upload"),
    GROUP_INVITATION_ENDOINT("https://ourapp.live/group_invite")
}