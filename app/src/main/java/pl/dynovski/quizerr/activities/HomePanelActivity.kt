package pl.dynovski.quizerr.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.dynovski.quizerr.databinding.ActivityHomePanelBinding

class HomePanelActivity: AppCompatActivity() {
    private val LOG_TAG = "HOME_PANEL"

    private lateinit var homePanelBinding: ActivityHomePanelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homePanelBinding = ActivityHomePanelBinding.inflate(layoutInflater)
        setContentView(homePanelBinding.root)
    }
}