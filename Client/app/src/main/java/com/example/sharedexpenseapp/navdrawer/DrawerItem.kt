package com.example.sharedexpenseapp.navdrawer

data class DrawerItem(val itemName: String, val isGroup: Boolean, val hasChildren: Boolean, val destination: Int? = null)