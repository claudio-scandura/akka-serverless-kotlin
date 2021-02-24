package com.akkaserverless.hackathon.item

import com.akkaserverless.hackathon.item.Items.ItemIdSearchCommand
import com.akkaserverless.hackathon.item.Items.ItemSearchResponse
import io.cloudstate.javasupport.Context
import io.cloudstate.javasupport.crdt.StreamedCommandContext
import io.cloudstate.javasupport.eventsourced.SnapshotContext
import io.cloudstate.kotlinsupport.annotations.EntityId
import io.cloudstate.kotlinsupport.annotations.eventsourced.*

@EventSourcedEntity
class ItemEntity(@EntityId private val entityId: String) {

    @CommandHandler
    fun search(command: ItemIdSearchCommand, ctx: StreamedCommandContext<ItemSearchResponse>): ItemSearchResponse {
        return ItemSearchResponse.getDefaultInstance()
    }

    @EventHandler
    fun foo(context: Context): Unit {
    }

    @Snapshot
    fun snapshot(): Unit {
    }

    @SnapshotHandler
    fun snapshotHandler(ctx: SnapshotContext): Unit {
    }

}