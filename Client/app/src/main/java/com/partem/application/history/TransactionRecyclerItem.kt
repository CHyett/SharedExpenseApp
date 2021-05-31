package com.partem.application.history

import com.partem.application.models.Transaction

data class TransactionRecyclerItem(val transaction: Transaction) { var isExpanded = false }