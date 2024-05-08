package com.example.myapplication

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Person :RealmObject() {
    @PrimaryKey
    var id :Long=0
    var name:String?=null
    var age:Int?=null
    var job:String?=null
}