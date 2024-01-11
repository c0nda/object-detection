package com.example.cowdetection.utils.contentresolver

import android.content.ContentResolver
import android.content.Context
import javax.inject.Inject

class ContentResolverProviderImpl @Inject constructor(
    private val context: Context
) : ContentResolverProvider {

    override fun contentResolver(): ContentResolver {
        return context.contentResolver
    }
}