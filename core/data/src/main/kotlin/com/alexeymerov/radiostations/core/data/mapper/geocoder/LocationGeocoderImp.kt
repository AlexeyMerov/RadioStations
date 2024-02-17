package com.alexeymerov.radiostations.core.data.mapper.geocoder

import android.content.Context
import android.location.Geocoder
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.jetbrains.annotations.ApiStatus.Experimental
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

// Just for fun and demonstration. I'm not sure if this can be considered a valid solution.
@Experimental
class LocationGeocoderImp @Inject constructor(
    @ApplicationContext val context: Context
) : LocationGeocoder {

    private var geocoder = WeakReference<Geocoder?>(null)

    override suspend fun mapToListWithLocations(list: List<CategoryEntity>): List<CategoryEntity> {
        return list
            .filter { it.subTitle != null }
            .map(::mapToEntityWithLocation)
    }

    private fun mapToEntityWithLocation(it: CategoryEntity): CategoryEntity {
        if (geocoder.get() == null) geocoder = WeakReference(Geocoder(context))

        var result = it
        val location = it.subTitle
        if (location != null) {
            try {

                // deprecated since it asks to use async version
                // but it not allows to multiple requests at a time and we have to wait anyway
                // plus async works only on latest android SDKs
                @Suppress("DEPRECATION")
                val addressList = geocoder.get()?.getFromLocationName(location, 1)
                val address = addressList?.getOrNull(0)
                if (address != null && address.hasLatitude() && address.hasLongitude()) {
                    Timber.d("location = $location ## address = $address")

                    val newLocationText = when {
                        address.locality != null -> "${address.locality}, ${address.countryName}"
                        else -> address.countryName
                    }

                    result = result.copy(
                        latitude = address.latitude,
                        longitude = address.longitude,
                        subTitle = newLocationText
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "LocationGeocoderImp")
            }
        }

        geocoder.clear()
        return result
    }
}