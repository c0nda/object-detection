package com.example.cowdetection.utils.contentresolver

import android.content.ContentResolver

interface ContentResolverProvider {

    fun contentResolver(): ContentResolver

}