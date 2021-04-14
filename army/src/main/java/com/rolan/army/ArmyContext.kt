package com.rolan.army

import android.content.Context
import android.content.ContextWrapper

/**
 * Created by Rolan.Wang on 1/28/21.6:48 PM
 * Global constant storage
 */
class ArmyContext(context: Context,
                           val defaultRequestListeners: List<RequestListener?>,
                           val engine: IEngine) : ContextWrapper(context.applicationContext)