package pl.dynovski.quizerr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.dynovski.quizerr.databinding.ActivityCreateTestBinding

class CreateTestActivity : AppCompatActivity() {

    private lateinit var createTestBinding: ActivityCreateTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createTestBinding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(createTestBinding.root)
    }
}