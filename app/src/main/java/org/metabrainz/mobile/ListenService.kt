package org.metabrainz.mobile

import android.content.ComponentName
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.widget.Toast
import androidx.annotation.RequiresApi
import org.metabrainz.mobile.presentation.UserPreferences.preferenceListenBrainzToken
import org.metabrainz.mobile.util.Log.d

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class ListenService : NotificationListenerService() {
    private var sessionManager: MediaSessionManager? = null
    private var handler: ListenHandler? = null
    private var sessionListener: ListenSessionListener? = null
    private var listenServiceComponent: ComponentName? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        d("Listen Service Started")
        return START_STICKY
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        if (Looper.myLooper() == null) Handler(Looper.getMainLooper()).post { initialize() } else initialize()
    }

    fun initialize() {
        d("Initializing Listener Service")
        val token = preferenceListenBrainzToken
        if (token == null || token.isEmpty()){
            d("ListenBrainz User token has not been set!")
        }
        handler = ListenHandler()
        sessionManager = applicationContext
                .getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        sessionListener = ListenSessionListener(handler!!)
        listenServiceComponent = ComponentName(this, this.javaClass)
        sessionManager!!.addOnActiveSessionsChangedListener(sessionListener!!, listenServiceComponent)
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager!!.removeOnActiveSessionsChangedListener(sessionListener!!)
        sessionListener!!.clearSessions()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }
}