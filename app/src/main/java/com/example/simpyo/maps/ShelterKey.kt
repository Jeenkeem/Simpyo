package com.example.simpyo.maps

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

class ShelterKey(val id: Int, private val position: LatLng) : ClusteringKey {
    override fun getPosition() = position

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true

        if(other == null || javaClass != other.javaClass)
            return false

        val shelterKey = other as ShelterKey

        return id == shelterKey.id
    }

    override fun hashCode() = id
}