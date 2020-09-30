package com.example.sharedexpenseapp.enums

enum class Endpoints(val endpoint: String) {
    FIREBASE_TOKEN_ENDPOINT("https://ourapp.live/firebase_token"),
    LOGIN_ENDPOINT("https://ourapp.live/login"),
    REGISTER_ENDPOINT("https://ourapp.live/register"),
    CREATE_GROUP_ENDPOINT("https://ourapp.live/"),
    CLIENT_TOKEN_ENDPOINT("https://ourapp.live/client_token"),
    CHECKOUT_ENDPOINT("https://ourapp.live/checkout")
}