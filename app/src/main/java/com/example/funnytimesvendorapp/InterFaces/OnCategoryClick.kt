package com.example.funnytimesvendorapp.InterFaces

import com.example.funnytimesvendorapp.Models.FTPCategory

interface OnCategoryClick {
    fun OnCategoryClickListener(ftpCategory: FTPCategory)
}