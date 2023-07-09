package com.example.publictransportationguidance.tracking.trackingModule.Reviews

import android.app.Application
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths

class MyApp : Application() {
    var paths: List<List<NearestPaths>>? = null
}