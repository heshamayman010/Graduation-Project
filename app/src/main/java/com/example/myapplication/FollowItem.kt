package com.example.myapplication
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
@RealmClass
open class FollowItem :RealmObject() {
    @PrimaryKey
    var id :String=""
    var name:String?=null
    var date:String?=null
    var price:String?=null
    var city:String?=null
    var market:String?=null
    var Item_id:String?=null
    var Email_Created:String?=null
    var Added_Email:String?=null
}