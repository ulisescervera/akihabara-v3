package com.gmail.uli153.akihabara3.data.entities

import androidx.annotation.AttrRes
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root
data class BggSearchResponse(
    @ElementList(inline = true) val items: List<SimpleBggSearchItem>
)

@Root(name = "item")
data class SimpleBggSearchItem(
    @Attribute(name = "type") val type: String,
    @Attribute(name = "id") val id: Long
)

@Root()
data class BggItemResponse(
    @Attribute(name= "type") val type: String,
    @Attribute(name = "id") val id: Long,
    @ElementList(inline = true) val items: List<BggItem>
)

@Root(name = "item")
data class BggItem(
    @Element val thumbnail: String,
    @Element val image: String,
    @ElementList(inline = true) val names: List<Name>,
    @Element val description: String,
    @Element val yearpublished: YearPublished,
    @Element val minplayers: MinPlayers,
    @Element val maxplayers: MaxPlayers,
    @Element val playingtim: PlayingTime,
    @Element val minage: MinAge,
)

@Root(name = "name")
data class Name(
    @Attribute val type: String,
    @Attribute val value: String,
)

@Root
data class YearPublished(
    @Attribute val value: Int
)

@Root
data class MinPlayers(
    @Attribute val value: Int
)

@Root
data class MaxPlayers(
    @Attribute val value: Int
)

@Root
data class PlayingTime(
    @Attribute val value: Int
)

@Root
data class MinAge(
    @Attribute val value: Int
)
