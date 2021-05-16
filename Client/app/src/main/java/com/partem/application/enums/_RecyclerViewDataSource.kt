package com.partem.application.enums

import com.partem.application.models.Group
import com.partem.application.models.Transaction

val RECYCLER_DATA = arrayOf(Group(1, "myGroup1", 100.0f, 0.3f), Group(2, "myGroup2", 200.0f, 0.3f),
        Group(3, "myGroup3", 300.0f, 0.3f), Group(4, "myGroup4", 400.0f, 0.3f),
        Group(5, "myGroup5", 500.0f, 0.3f), Group(6, "myGroup6", 600.0f, 0.3f),
        Group(7, "myGroup7", 700.0f, 0.3f), Group(8, "myGroup8", 800.0f, 0.3f),
        Group(9, "myGroup9", 900.0f, 0.3f), Group(10, "myGroup10", 1000.0f, 0.3f),)

val HISTORY_TRANSACTIONS_DATA = arrayOf(Transaction("Nick T.", -123.45), Transaction("Cy Cy", 42.31),
        Transaction("Cancun", -1812.77), Transaction("Dinner", 20.38), Transaction("Chris", 5.08),
        Transaction("Noah", -35.09), Transaction("Emily", 10.00), Transaction("Rent", -500.00),
        Transaction("Lake Isabella", 500.65), Transaction("Europe", 1000.0))