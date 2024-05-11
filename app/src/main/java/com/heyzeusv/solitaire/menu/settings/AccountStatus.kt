package com.heyzeusv.solitaire.menu.settings

import androidx.annotation.StringRes
import com.heyzeusv.solitaire.R

sealed class AccountStatus(@StringRes val message: Int) {
    data object Idle : AccountStatus(0)
    class EmailCheck : AccountStatus(R.string.account_status_email_check)
    class UsernameCheck : AccountStatus(R.string.account_status_username_check)
    class CreateAccount : AccountStatus(R.string.account_status_creating_account)
}