package com.partem.application.models

//TODO: Complete documentation in this file.
/**
 * This class represents a Transaction.
 *
 * @constructor
 */
data class Transaction(val target: String, val amount: Double, val description: String? = null)