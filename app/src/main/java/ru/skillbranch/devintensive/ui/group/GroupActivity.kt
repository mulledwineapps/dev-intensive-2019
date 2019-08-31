package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.GroupViewModel
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class GroupActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViews()
        // один экран - одна вью модель
        // 2:24:50 5-го занятия - что-то про другой подход, про использование стэйтов во вью модели, что хорошо для
        // экранов, таких как профиль, но не очень применимо для списков из-за работы с коллекциями, а не с вью стэйтами
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                // анимация переходов между активностями реализована также через стиль ActivityTransitions
                overridePendingTransition(R.anim.idle, R.anim.bottom_down)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        userAdapter = UserAdapter {
            viewModel.handleSelectedItem(it.id)
        }
        val divider = DividerItemDecoration(this, RecyclerView.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.item_divider, theme))

        with(rv_user_list) {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@GroupActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(divider)
        }

        fab.setOnClickListener {
            viewModel.handleCreateGroup()
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        viewModel.getUsersData().observe(this, Observer { userAdapter.updateData(it) })
        viewModel.getSelectedData().observe(this, Observer {
            updateChips(it)
            toggleFab(it.size > 1)
        })
    }

    private fun toggleFab(isShow: Boolean) {
        if (isShow) fab.show() else fab.hide()
    }

    // ...2:57:50 5-го занятия это относится к слою представления, потому что нам неоходимо их адресовать
    private fun addChipToGroup(user: UserItem) {
        val chip = Chip(this).apply {
            text = user.fullName
            isCloseIconVisible = true
            tag = user.id
            closeIconTint = ColorStateList.valueOf(Utils.getThemeColor(R.attr.chipCloseIconTint, theme))
            chipBackgroundColor = ColorStateList.valueOf(Utils.getThemeColor(R.attr.chipBackgroundColor, theme))
            setTextColor(Color.WHITE)
        }

        val customTarget = object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) { }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                chip.chipIcon = resource
            }
        }

        if (user.avatar != null) {
            Glide.with(chip)
                .load(user.avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(customTarget)
        } else {
            chip.chipIcon = resources.getDrawable(R.drawable.avatar_default, theme)
        }

        chip.setOnCloseIconClickListener { viewModel.handleRemoveChip(it.tag.toString()) }
        chip_group.addView(chip)
    }

    private fun updateChips(listUsers: List<UserItem>) {
        chip_group.visibility = if (listUsers.isEmpty()) View.GONE else View.VISIBLE

        // преобразуем в map с помощью associate
        val users = listUsers.associate { user -> user.id to user }.toMutableMap()
        val views = chip_group.children.associate { view -> view.tag to view }

        for ((k, v) in views) {
            if (!users.containsKey(k)) chip_group.removeView(v)
            else users.remove(k)
        }

        users.forEach { (_, v) -> addChipToGroup(v) }
    }
}
