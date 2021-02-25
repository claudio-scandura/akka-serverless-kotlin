package com.akkaserverless.hackathon.retailer

import com.akkaserverless.hackathon.retailer.Retailers.*
import com.google.protobuf.empty.Empty
import io.cloudstate.javasupport.eventsourced.CommandContext
import io.cloudstate.javasupport.eventsourced.EventSourcedEntity
import io.cloudstate.kotlinsupport.annotations.EntityId
import io.cloudstate.kotlinsupport.annotations.eventsourced.CommandHandler
import io.cloudstate.kotlinsupport.annotations.eventsourced.EventHandler

@EventSourcedEntity
class RetailerEntity(@EntityId private val retailerId: String) {

    private val state = mutableMapOf<String, Item>()

    @CommandHandler
    fun findItem(command: FindItemCommand): Item? {
        return state[command.itemId]
    }

    @CommandHandler
    fun upsertItem(command: UpsertItemCommand, ctx: CommandContext): Empty {
        if(state.contains(command.item.itemId)) {
            ctx.emit(ItemUpdated.newBuilder().setItem(command.item).build())
        } else {
            ctx.emit(ItemAdded.newBuilder().setItem(command.item).build())
        }
        return Empty()
    }

    @EventHandler
    fun itemAdded(itemAdded: ItemAdded) {
        state[itemAdded.item.itemId] = itemAdded.item
    }

    @EventHandler
    fun itemUpdated(itemUpdated: ItemUpdated) {
        state[itemUpdated.item.itemId] = itemUpdated.item
    }

}