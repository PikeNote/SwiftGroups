package org.swg.swiftgroups_app.CGAPI.AggregateEvents

import kotlinx.serialization.Serializable

@Serializable
data class AggregateAPIEventItem(
    val counter: String,
    val p0: String = "",
    val p1: String = "",
    val p10: String = "",
    val p11: String = "",
    val p12: String = "",
    val p13: String = "",
    val p14: String = "",
    val p15: String = "",
    val p16: String = "",
    val p17: String = "",
    val p18: String = "",
    val p19: String = "",
    val p2: String = "",
    val p20: String = "",
    val p21: String = "",
    val p22: String = "",
    val p23: String = "",
    val p24: String? = "", // co-host ID
    val p25: String = "",
    val p26: String = "",
    val p27: String = "",
    val p28: String = "",
    val p29: String = "",
    val p3: String = "",
    val p30: String = "",
    val p31: String = "",
    val p32: String = "",
    val p33: String = "",
    val p34: String = "",
    val p35: String = "",
    val p36: String = "",
    val p37: String? = "", // eventPhotoDescription
    val p38: String? = "", // eventFlyerDescription
    val p39: String? = "", //list_item_acc_label
    val p4: String = "",
    val p5: String = "",
    val p6: String = "",
    val p7: String = "",
    val p8: String = "",
    val p9: String = ""
)