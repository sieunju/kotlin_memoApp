package com.hmju.memo.widget

import android.os.Bundle
import android.view.Menu
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import java.lang.ref.WeakReference

/**
 * Description :
 * Open Source Library 사용
 *
 * Created by juhongmin on 2020/08/30
 */
class NavigationComponentHelper {
    companion object {

        fun setupWithNavController(
            menu: Menu,
            toolbar: BottomToolbar,
            navController: NavController
        ) {
            toolbar.onItemSelectedListener = object : BottomToolbar.OnItemSelectedListener {
                override fun onItemSelect(pos: Int): Boolean {
                    return NavigationUI.onNavDestinationSelected(menu.getItem(pos), navController)
                }
            }

            val weakReference = WeakReference(toolbar)

            navController.addOnDestinationChangedListener(object :
                NavController.OnDestinationChangedListener {

                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()

                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }

                    for (h in 0 until menu.size()) {
                        val menuItem = menu.getItem(h)
                        if (matchDestination(destination, menuItem.itemId)) {
                            menuItem.isChecked = true
                            toolbar.itemActiveIndex = h
                        }
                    }
                }
            })
        }

        /**
         * Determines whether the given `destId` matches the NavDestination. This handles
         * both the default case (the destination's id matches the given id) and the nested case where
         * the given id is a parent/grandparent/etc of the destination.
         */
        fun matchDestination(
            destination: NavDestination,
            @IdRes destId: Int
        ): Boolean {
            var currentDestination: NavDestination? = destination
            while (currentDestination!!.id != destId && currentDestination.parent != null) {
                currentDestination = currentDestination.parent
            }
            return currentDestination.id == destId
        }
    }
}