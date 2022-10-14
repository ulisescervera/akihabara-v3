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
    @field:Element(required = false) var thumbnail: String? = null,
    @field:Element(required = false) var image: String? = null,
    @field:ElementList(inline = true) var names: MutableList<Name> = mutableListOf(),
    @field:Element var description: String = "",
    @field:Element(required = false) var yearpublished: YearPublished? = null,
    @field:Element(required = false) var minplayers: MinPlayers? = null,
    @field:Element(required = false) var maxplayers: MaxPlayers? = null,
    @field:Element(required = false) var playingtime: PlayingTime? = null,
    @field:Element(required = false) var minage: MinAge? = null,
    @field:Element(required = false) var statistics: Statistics? = null,
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

@Root(strict = false)
data class Statistics @JvmOverloads constructor(
    @field:Element var ratings: Ratings? = null
)

@Root(strict = false)
data class Ratings @JvmOverloads constructor(
    @field:Element(required = false) var bayesaverage: GeekRating? = null,
    @field:Element(required = false) var usersrated: UsersRated? = null,
    @field:Element(required = false) var average: Average? = null,
    @field:Element(required = false) var averageweight: AverageWeight? = null,
    @field:ElementList(required = false) var ranks: MutableList<Rank>? = mutableListOf()
)

@Root(strict = false)
data class GeekRating @JvmOverloads constructor(
    @field:Attribute var value: Float = -1f
)

@Root(strict = false)
data class UsersRated @JvmOverloads constructor(
    @field:Attribute var value: Int = -1
)

@Root(strict = false)
data class Average @JvmOverloads constructor(
    @field:Attribute var value: Float? = null
)

@Root(strict = false)
data class AverageWeight @JvmOverloads constructor(
    @field:Attribute var value: Float? = null
)

@Root(strict = false)
data class Ranks @JvmOverloads constructor(
    @field:ElementList(inline = true) var names: MutableList<Rank> = mutableListOf(),
)

@Root(strict = false)
data class Rank @JvmOverloads constructor(
    @field:Attribute var id: Int = -1,
    @field:Attribute var type: String = "",
    @field:Attribute var friendlyname: String = "",
    @field:Attribute var value: String = "",
)
