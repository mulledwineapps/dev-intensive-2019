package ru.skillbranch.devintensive.ui.profile

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile_constraint.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        // set custom Theme this before super and setContentView
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity", "OnCreate")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity", "updateTheme")
        // при каждом вызове setLocalNightMode происходит пересоздание activity!
        delegate.setLocalNightMode(mode)
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
        }

        val firstName = et_first_name.text.toString().trim()
        val lastName = et_last_name.text.toString().trim()
        iv_avatar.setInitials(firstName, lastName)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickname" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        // btn_edit.setOnClickListener(object : View.OnClickListener {
        //     override fun onClick(v: View?) {
        //         isEditMode = isEditMode.not()
        //         showCurrentMode(isEditMode)
        //     }
        // })

        // btn_edit.setOnClickListener(View.OnClickListener {
        //     isEditMode = isEditMode.not()
        //     showCurrentMode(isEditMode)
        // })

        btn_edit.setOnClickListener {
            if (isEditMode) {
                if (!isRepositoryValid(et_repository.text.toString())) {
                    et_repository.text?.clear()
                }
                saveProfileInfo()
            }
            switchMode()
        }

        // et_repository.setOnFocusChangeListener { _, hasFocus ->
        //     if (hasFocus.not()) {
        //         if (!isRepositoryValid(et_repository.text.toString())) {
        //             wr_repository.error = "Невалидный адрес репозитория"
        //         }
        //     }
        // }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isRepositoryValid(s.toString())) {
                    wr_repository.error = null
                    wr_repository.isErrorEnabled = false
                } else {
                    wr_repository.error = "Невалидный адрес репозитория"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }
    }

    private fun switchMode() {
        isEditMode = isEditMode.not()
        showCurrentMode(isEditMode)
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for ((_, v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0 // прозрачность (255 - максимум прозрачности, 0 - непрозрачный)
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit // подсветка количества введённых символов

        wr_repository.isErrorEnabled = isEdit

        // https://stackoverflow.com/questions/13719103/how-to-retrieve-style-attributes-programmatically-from-styles-xml
        val a = obtainStyledAttributes(R.style.AppTheme, intArrayOf(android.R.attr.colorAccent))
        val colorAccent: Int = a.getColor(0, resources.getColor(R.color.color_accent, theme))
        a.recycle()

        iv_avatar.setAvatarBackgroundColor(colorAccent)

        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                PorterDuffColorFilter(
                    colorAccent,
                    PorterDuff.Mode.SRC_IN // режим наложения
                )
            } else {
                null
            }

            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_white_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable((icon))
        }
    }

    private fun isRepositoryValid(text: String): Boolean {

        // a*a+a?	0 or more, 1 or more, 0 or 1
        // .        any character except newline
        // \w\d\s	word, digit, whitespace
        // \W\D\S	not word, digit, whitespace

        val repository = text.trim().toLowerCase()
        val userName = repository.substringAfterLast('/')

        val pattern = "^(https://)?(www.)?github.com/[\\w\\d-]+$"

        val exclude = arrayOf(
            "enterprise", "features", "topics", "collections", "trending", "events", "marketplace",
            "pricing", "nonprofit", "customer-stories", "security", "login", "join"
        )

        return when {
            repository.isEmpty() -> true
            Regex(pattern).matches(repository).not() -> false
            exclude.contains(userName) -> false
            userName.contains(Regex("^a-zA-Z0-9-")) -> false
            userName.startsWith("-") || userName.endsWith("-") -> false
            else -> true
        }
    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileDate(this)
        } // apply - для того, чтобы обратиться к только что созданному инстансу Profile
    }

// мультиселект:
// alt или колесо мыши + проведение вертикальной черты
// ctrl + alt + shift -> клики мышкой по нужным позициям
}