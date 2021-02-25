package com.akkaserverless.hackathon.notification.entities

import com.akkaserverless.hackathon.notification.entities.*
import com.google.protobuf.Timestamp
import io.cloudstate.javasupport.eventsourced.CommandContext
import io.cloudstate.javasupport.eventsourced.EventSourcedContext
import io.cloudstate.javasupport.eventsourced.EventSourcedEntity
import io.cloudstate.kotlinsupport.annotations.EntityId
import io.cloudstate.kotlinsupport.annotations.eventsourced.CommandHandler
import io.cloudstate.kotlinsupport.annotations.eventsourced.EventHandler
import io.cloudstate.kotlinsupport.annotations.eventsourced.Snapshot
import io.cloudstate.kotlinsupport.annotations.eventsourced.SnapshotHandler

@EventSourcedEntity
class RetailerEntity(@EntityId val entityId: String, ctx: EventSourcedContext) {

    private val itemServiceCall = ctx.serviceCallFactory().lookup(ItemService::class.java.canonicalName, "UpdatePrice", UpdatePriceCommand::class.java)

    private val state = mutableMapOf<String, RetailItem>()

    @CommandHandler
    fun findItem(command: FindItemCommand): RetailItem? {
        return state[command.itemId]
    }

    @CommandHandler
    fun upsertItem(command: UpsertItemCommand, ctx: CommandContext) {
        if (state.contains(command.item.itemId)) {
            ctx.emit(ItemUpdated.newBuilder().setItem(command.item).build())
        } else {
            ctx.emit(ItemAdded.newBuilder().setItem(command.item).build())
        }
        val updatePriceCommand = UpdatePriceCommand.newBuilder()
                .setItemId(command.item.itemId)
                .setItem(Item.newBuilder().setDescription(command.item.description).setId(command.item.itemId))
                .setRetailPrice(RetailPrice.newBuilder().setRetailerId(entityId).setPrice(command.item.price))
                .setUpdatedAt(Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).setNanos(0))
                .build()
        ctx.forward(itemServiceCall.createCall(updatePriceCommand))
    }

    @EventHandler
    fun itemAdded(itemAdded: ItemAdded) {
        state[itemAdded.item.itemId] = itemAdded.item
    }

    @EventHandler
    fun itemUpdated(itemUpdated: ItemUpdated) {
        state[itemUpdated.item.itemId] = itemUpdated.item
    }

    @Snapshot
    fun snapshot(): Retailer {
        return Retailer.newBuilder()
                .setRetailerId(entityId)
                .addAllItems(state.values)
                .build()
    }

    @SnapshotHandler
    fun snapshotHandler(snapshot: Retailer) {
        state.putAll(snapshot.itemsList.map { Pair(it.itemId, it) })
    }
}