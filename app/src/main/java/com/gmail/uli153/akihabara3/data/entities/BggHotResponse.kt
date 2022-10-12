package com.gmail.uli153.akihabara3.data.entities

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "items", strict = false)
data class BggHotResponse @JvmOverloads constructor(
    @field:ElementList(inline = true) var items: MutableList<BggHotItemResponse> = mutableListOf()
)

@Root(name = "item", strict = false)
data class BggHotItemResponse @JvmOverloads constructor(
    @field:Attribute(required = false) var rank: Int = -1,
    @field:Attribute(name = "id") var id: Int = -1,
    @field:Element(required = false) var thumbnail: ItemThumbnail? = null,
    @field:Element var name: ItemName = ItemName(""),
    @field:Element(required = false) var yearpublished: YearPublished? = null
)

@Root(name = "name", strict = false)
data class ItemName(
    @field:Attribute var value: String = "",
)

@Root(name = "thumbnail", strict = false)
data class ItemThumbnail(
    @field:Attribute var value: String = ""
)
