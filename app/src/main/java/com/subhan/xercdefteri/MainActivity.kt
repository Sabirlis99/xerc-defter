package com.subhan.xercdefteri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.subhan.xercdefteri.data.AppDatabase
import com.subhan.xercdefteri.ui.ExpenseViewModel
import com.subhan.xercdefteri.ui.screens.HomeScreen
import com.subhan.xercdefteri.ui.theme.XercDefteriTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels {
        val dao = AppDatabase.getInstance(applicationContext).expenseDao()
        ExpenseViewModel.factory(dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XercDefteriTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
