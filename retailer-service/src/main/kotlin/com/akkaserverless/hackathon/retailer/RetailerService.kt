/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.akkaserverless.hackathon.retailer

class RetailerService {
    val greeting: String
        get() {
            return "Hello World - Retailer service!"
        }
}

fun main() {
    println(RetailerService().greeting)
}
