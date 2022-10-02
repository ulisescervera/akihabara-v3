package com.gmail.uli153.akihabara3.data.entities

import androidx.annotation.AttrRes
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "items", strict = false)
data class BggSearchResponse @JvmOverloads constructor(
    @field:ElementList(inline = true) var items: MutableList<SimpleBggSearchItem> = mutableListOf()
)

@Root(name = "item", strict = false)
data class SimpleBggSearchItem @JvmOverloads constructor(
    @field:Attribute(name = "type") var type: String = "",
    @field:Attribute(name = "id") var id: Int = -1
)

@Root(name = "items",strict = false)
data class BggItemResponse @JvmOverloads constructor(
    @field:ElementList(inline = true) var items: MutableList<BggItem> = mutableListOf()
)

@Root(name = "item", strict = false)
data class BggItem @JvmOverloads constructor(
    @field:Attribute var type: String = "",
    @field:Attribute var id: Long = -1,
    @field:Element var thumbnail: String = "",
    @field:Element var image: String = "",
    @field:ElementList(inline = true) var names: MutableList<Name> = mutableListOf(),
    @field:Element var description: String = "",
    @field:Element var yearpublished: YearPublished = YearPublished(),
    @field:Element var minplayers: MinPlayers = MinPlayers(),
    @field:Element var maxplayers: MaxPlayers = MaxPlayers(),
    @field:Element var playingtime: PlayingTime = PlayingTime(),
    @field:Element var minage: MinAge = MinAge(),
)

@Root(name = "name", strict = false)
data class Name @JvmOverloads constructor(
    @field:Attribute var type: String = "",
    @field:Attribute var value: String = "",
    @field:Attribute var sortindex: Int = -1
)

@Root(strict = false)
data class YearPublished @JvmOverloads constructor(
    @field:Attribute var value: Int = -1
)

@Root(strict = false)
data class MinPlayers @JvmOverloads constructor(
    @field:Attribute var value: Int = -1
)

@Root(strict = false)
data class MaxPlayers @JvmOverloads constructor(
    @field:Attribute var value: Int = -1
)

@Root(strict = false)
data class PlayingTime @JvmOverloads constructor(
    @field:Attribute var value: Int = -1
)

@Root(strict = false)
data class MinAge @JvmOverloads constructor(
    @field:Attribute var value: Int = -1
)
