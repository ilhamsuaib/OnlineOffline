package id.ilhamsuaib.onlineoffline

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by ilham on 2/19/18.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
