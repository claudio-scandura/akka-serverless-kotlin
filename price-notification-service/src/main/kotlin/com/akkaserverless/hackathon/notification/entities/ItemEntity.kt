package com.akkaserverless.hackathon.notification.entities

import com.akkaserverless.hackathon.notification.entities.*
import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import io.cloudstate.javasupport.crdt.CrdtCreationContext
import io.cloudstate.javasupport.crdt.LWWRegister
import io.cloudstate.javasupport.crdt.ORMap
import io.cloudstate.javasupport.crdt.StreamedCommandContext
import io.cloudstate.kotlinsupport.annotations.crdt.CommandHandler
import io.cloudstate.kotlinsupport.annotations.crdt.CrdtEntity
import java.time.Instant
import java.util.*

@CrdtEntity
class ItemEntity(context: CrdtCreationContext) {

    private val itemIdentity = mutableMapOf<String, Item>()

    // map[itemid -> map[retailer id -> price]]
    private val prices = context.newORMap<Item, ORMap<String, LWWRegister<Int>>>()

    @CommandHandler
    fun getPrices(command: GetPriceCommand, ctx: StreamedCommandContext<GetPriceResponse>): GetPriceResponse? {
        ctx.onChange {
            val item = itemIdentity[command.itemId]
            val response = prices[item]
                    ?.map { retailerPrices ->
                        RetailPrice.newBuilder().setRetailerId(retailerPrices.key).setPrice(retailerPrices.value.get())
                    }
                    ?.fold(GetPriceResponse.newBuilder().setItem(item)) { builder, price -> builder.addPrice(price) }

            response?.build()?.let { Optional.of(it) }
        }
        return GetPriceResponse.getDefaultInstance()
    }

    @CommandHandler
    fun updatePrice(command: UpdatePriceCommand): Empty {
        val item = itemIdentity.putIfAbsent(command.itemId, command.item) ?: command.item
        prices.getOrCreate(item) { crdtFactory ->
            crdtFactory.newORMap()
        }.getOrCreate(command.retailPrice.retailerId) { crdtFactory ->
            crdtFactory.newLWWRegister(command.retailPrice.price)
        }.set(command.retailPrice.price, LWWRegister.Clock.CUSTOM, toEpochMillis(command.updatedAt))

        return Empty.getDefaultInstance()
    }

    private fun toEpochMillis(ts: Timestamp) = Instant.ofEpochSecond(ts.seconds, ts.nanos.toLong()).toEpochMilli()

}