package org.metabrainz.mobile.presentation.features.onboarding

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aemerse.onboard.OnboardAdvanced
import com.aemerse.onboard.OnboardFragment
import com.aemerse.onboard.OnboardPageTransformerType
import com.aemerse.onboard.model.SliderPage
import org.metabrainz.mobile.R
import org.metabrainz.mobile.presentation.UserPreferences
import org.metabrainz.mobile.presentation.features.dashboard.DashboardActivity

class FeaturesActivity : OnboardAdvanced() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(OnboardFragment.newInstance(
            "Tag",
            "Fix your Audio Metadata",
            resourceId = R.raw.tagger,
            isLottie = true,
            backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
            titleColor = ContextCompat.getColor(applicationContext, R.color.white),
            descriptionColor = ContextCompat.getColor(applicationContext, R.color.white)
        ))

        addSlide(OnboardFragment.newInstance(
            SliderPage(
                "Search",
                "Explore MusicBrainz Data",
                resourceId = R.raw.search,
                backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
                titleColor = ContextCompat.getColor(applicationContext, R.color.white),
                descriptionColor = ContextCompat.getColor(applicationContext, R.color.white),
                isLottie = true
            )
        ))

        addSlide(OnboardFragment.newInstance(
            "Scan",
            "Barcodes Search",
            resourceId = R.raw.scan,
            backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
            titleColor = ContextCompat.getColor(applicationContext, R.color.white),
            descriptionColor = ContextCompat.getColor(applicationContext, R.color.white),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Collections",
            "Explore your MusicBrainz Collection",
            resourceId = R.raw.collections,
            backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
            titleColor = ContextCompat.getColor(applicationContext, R.color.white),
            descriptionColor = ContextCompat.getColor(applicationContext, R.color.white),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Cross Platform",
            "Transfer music data across devices",
            resourceId = R.raw.laptop_and_stuff,
            backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
            titleColor = ContextCompat.getColor(applicationContext, R.color.white),
            descriptionColor = ContextCompat.getColor(applicationContext, R.color.white),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Listens",
            "Track your music listening habits ",
            resourceId = R.raw.teen,
            backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
            titleColor = ContextCompat.getColor(applicationContext, R.color.white),
            descriptionColor = ContextCompat.getColor(applicationContext, R.color.white),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Critiques",
            "Read and write about an album or event",
            resourceId = R.raw.review,
            backgroundColor =  ContextCompat.getColor(applicationContext, R.color.app_bg),
            titleColor = ContextCompat.getColor(applicationContext, R.color.white),
            descriptionColor = ContextCompat.getColor(applicationContext, R.color.white),
            isLottie = true
        ))

        setTransformer(OnboardPageTransformerType.Parallax())
    }

    public override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Skip?")
        builder.setMessage("Some of the functionalities are specially available through the settings.")
        builder.setPositiveButton("Understood") { dialog: DialogInterface?, which: Int ->
            UserPreferences.setOnBoardingCompleted()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Cancel") {dialog: DialogInterface?, which: Int ->}
        builder.create().show()
    }

    public override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        UserPreferences.setOnBoardingCompleted()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}