package com.akkaserverless.hackathon.retailer

import com.akkaserverless.hackathon.retailer.Retailers.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class RetailerEntityTest : ShouldSpec({
    isolationMode = IsolationMode.InstancePerTest

    val retailerId = "Blackwells"
    val itemId = "9780141394633"
    val entity = RetailerEntity(retailerId)
    val item = Item.newBuilder()
            .setItemId(itemId)
            .setDescription("Hardback")
            .setName("Paradise Lost")
            .setPrice(1268)
            .build()

    context("ItemAdded") {
        should("add an item to state") {
            val findItemCommand = FindItemCommand.newBuilder().setRetailerId(retailerId).setItemId(itemId).build()
            entity.itemAdded(ItemAdded.newBuilder().setItem(item).build())
            val result = entity.findItem(findItemCommand)
            result shouldBe item

        }
    }

    context("findItem") {
        val findItemCommand = FindItemCommand.newBuilder().setRetailerId(retailerId).setItemId(itemId).build()

        should("return null if the item is not found") {
            entity.findItem(findItemCommand).shouldBeNull()
        }

        should("return the item if it exists") {
            entity.itemAdded(ItemAdded.newBuilder().setItem(item).build())
            entity.findItem(findItemCommand) shouldBe item
        }
    }

})